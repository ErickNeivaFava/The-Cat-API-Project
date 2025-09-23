variable "aws_region" {
  description = "Região AWS utilizada para os recursos."
  type        = string
}

variable "vpc_cidr" {
  description = "CIDR da VPC utilizada na infraestrutura."
  type        = string
}
variable "rds_allocated_storage" {
  description = "Tamanho do armazenamento do RDS (GB)"
  type        = number
}

variable "ecs_cpu" {
  description = "CPU para a task ECS (unidades)"
  type        = string
}

variable "ecs_memory" {
  description = "Memória para a task ECS (MB)"
  type        = string
}
variable "docker_image_url" {
  description = "URL da imagem Docker publicada no registry"
  type        = string
}

variable "db_user" {
  description = "Usuário do banco de dados RDS"
  type        = string
}

variable "db_instance_class" {
  description = "Tipo da instancia do RDS"
  type        = string
}

variable "ecs_desired_count" {
  description = "Numero de tasks ECS desejadas"
  type = number
}

variable "public_subnets" {
  description = "Subnets publicas a serem usadas na VPC, RDS e ECS"
  type = list(string)
}

variable "private_subnets" {
  description = "Subnets privadas a serem usadas na VPC, RDS e ECS"
  type = list(string)
}

variable "vpc_azs" {
  description = "AZs a serem usadas na aplicação"
  type = list(string)
  default = ["sa-east-1a","sa-east-1b","sa-east-1c"]
}

variable "ecs_launch_type" {
  description = "Tipo de provisionamento do ECS"
  type        = string
  validation {
    condition     = contains(["FARGATE", "FARGATE_SPOT"], var.ecs_launch_type)
    error_message = "O valor de ecs_launch_type deve ser 'FARGATE' ou 'FARGATE_SPOT'."
  }
}

variable "rds_family" {
  description = "Familia do DB Parameter Group"
  type = string
}

variable "aws_account_id" {
  description = "Id da conta AWS"
  type = string
}