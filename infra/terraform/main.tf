resource "aws_security_group" "rds_sg" {
  name        = "rds-sg"
  description = "Security Group para RDS"
  vpc_id      = module.vpc.vpc_id

  ingress {
    from_port   = 5432
    to_port     = 5432
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}
resource "aws_cloudwatch_log_group" "ecs_logs" {
  name              = "/ecs/${local.ecs_service_name}"
  retention_in_days = 1
}
provider "aws" {
  region = var.aws_region
}

module "vpc" {
  source  = "terraform-aws-modules/vpc/aws"
  name    = local.vpc_name
  cidr    = var.vpc_cidr
  azs     = var.vpc_azs
  public_subnets  = var.public_subnets
  private_subnets = var.private_subnets
}

resource "aws_db_subnet_group" "rds_subnet_group" {
  name       = "rds-subnet-group"
  subnet_ids = module.vpc.public_subnets
  description = "Subnets para RDS"
}

resource "aws_security_group" "ecs_sg" {
  name        = "ecs-sg"
  description = "Security Group para ECS"
  vpc_id      = module.vpc.vpc_id

  ingress {
    from_port       = 8080
    to_port         = 8080
    protocol        = "tcp"
    security_groups = [aws_security_group.alb_sg.id]
  }
  ingress {
    from_port   = 8080
    to_port     = 8080
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

resource "aws_security_group" "alb_sg" {
  name        = "alb-sg"
  description = "Security Group para ALB"
  vpc_id      = module.vpc.vpc_id

  ingress {
    from_port   = 80
    to_port     = 80
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress {
    from_port   = 443
    to_port     = 443
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

resource "aws_lb" "main" {
  name               = "thecatapi-alb"
  internal           = false
  load_balancer_type = "application"
  security_groups    = [aws_security_group.alb_sg.id]
  subnets            = module.vpc.public_subnets

  enable_deletion_protection = false
}

resource "aws_lb_target_group" "ecs" {
  name     = "thecatapi-tg"
  port     = 8080
  protocol = "HTTP"
  vpc_id   = module.vpc.vpc_id
  target_type = "ip"

  health_check {
    enabled             = true
    healthy_threshold   = 2
    interval            = 30
    matcher             = "200"
    path                = "/actuator/health"
    port                = "traffic-port"
    protocol            = "HTTP"
    timeout             = 5
    unhealthy_threshold = 2
  }
}

resource "aws_lb_listener" "main" {
  load_balancer_arn = aws_lb.main.arn
  port              = "80"
  protocol          = "HTTP"

  default_action {
    type             = "forward"
    target_group_arn = aws_lb_target_group.ecs.arn
  }
}

resource "aws_kms_key" "rds_kms" {
  description         = "KMS key para criptografia do RDS e ECS"
  enable_key_rotation = true
}


resource "aws_sqs_queue" "breed_queue_dlq" {
  name                       = "breed-queue-dlq.fifo"
  fifo_queue                = true
  content_based_deduplication = true
  message_retention_seconds  = 604800  # 7 dias
}

resource "aws_sqs_queue" "breed_queue" {
  name                       = "breed-queue.fifo"
  fifo_queue                = true
  content_based_deduplication = true
  message_retention_seconds  = 259200  # 3 dias
  visibility_timeout_seconds = 30
  
  redrive_policy = jsonencode({
    deadLetterTargetArn = aws_sqs_queue.breed_queue_dlq.arn
    maxReceiveCount     = 3
  })
}

resource "aws_ses_domain_identity" "main_domain" {
  domain = "thecatapi.com"
}

resource "aws_ses_domain_dkim" "main_domain_dkim" {
  domain = aws_ses_domain_identity.main_domain.domain
}

resource "aws_ses_domain_mail_from" "main_domain_mail_from" {
  domain           = aws_ses_domain_identity.main_domain.domain
  mail_from_domain = "mail.thecatapi.com"
}

resource "aws_ses_email_identity" "email_gatos" {
  email = "9lives@thecatapi.com"
}

module "rds" {
  source = "terraform-aws-modules/rds/aws"
  identifier = local.rds_identifier
  engine = local.rds_engine
  engine_version = local.rds_engine_version
  instance_class = var.db_instance_class
  allocated_storage = var.rds_allocated_storage
  db_name      = local.rds_db_name
  username = var.db_user
  manage_master_user_password = true
  vpc_security_group_ids = [aws_security_group.rds_sg.id]
  subnet_ids = var.public_subnets
  publicly_accessible = true
  skip_final_snapshot = true
  family = var.rds_family
  db_subnet_group_name = aws_db_subnet_group.rds_subnet_group.name
  multi_az = false
  storage_encrypted = true
  kms_key_id        = aws_kms_key.rds_kms.arn
}

module "ecs" {
  source       = "terraform-aws-modules/ecs/aws"
  cluster_name = local.ecs_cluster_name
}

resource "aws_ecs_task_definition" "thecatapi" {
  family                   = local.ecs_task_family
  network_mode             = local.ecs_network_mode
  requires_compatibilities = [var.ecs_launch_type]
  cpu                      = var.ecs_cpu
  memory                   = var.ecs_memory
  execution_role_arn       = aws_iam_role.ecs_task_execution.arn
  task_role_arn            = aws_iam_role.ecs_task_execution.arn
  container_definitions    = jsonencode([
    {
      name      = local.ecs_container_name
      image     = var.docker_image_url
      essential = true
      portMappings = [{ containerPort = local.ecs_container_port, hostPort = local.ecs_container_port }]
      environment = [
        { name = "SPRING_PROFILES_ACTIVE", value = "aws" },
        { name = "SPRING_DATASOURCE_URL", value = "jdbc:postgresql://${module.rds.db_instance_endpoint}/${local.rds_db_name}" },
        { name = "SPRING_DATASOURCE_USERNAME", value = var.db_user }
      ]
      logConfiguration = {
        logDriver = "awslogs"
        options = {
          awslogs-group         = "/ecs/${local.ecs_service_name}"
          awslogs-region        = var.aws_region
          awslogs-stream-prefix = local.ecs_service_name
        }
      }
    }
  ])
}

resource "aws_ecs_service" "thecatapi" {
  name            = local.ecs_service_name
  cluster         = module.ecs.cluster_id
  task_definition = aws_ecs_task_definition.thecatapi.arn
  desired_count   = var.ecs_desired_count
  launch_type     = var.ecs_launch_type
  
  network_configuration {
    subnets          = module.vpc.public_subnets
    security_groups  = [aws_security_group.ecs_sg.id]
    assign_public_ip = true
  }

  load_balancer {
    target_group_arn = aws_lb_target_group.ecs.arn
    container_name   = local.ecs_container_name
    container_port   = local.ecs_container_port
  }

  depends_on = [aws_lb_listener.main]
}

output "load_balancer_url" {
  description = "URL do Load Balancer"
  value       = "http://${aws_lb.main.dns_name}"
}

