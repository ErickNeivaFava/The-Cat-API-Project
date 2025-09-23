locals {
  vpc_name              = "thecatapi-vpc"
  rds_identifier        = "thecatapi-db"
  rds_engine            = "postgres"
  rds_engine_version    = "17.6"
  rds_db_name           = "thecatapidb"
  ecs_cluster_name      = "thecatapi-ecs"
  ecs_task_family       = "thecatapi-task"
  ecs_network_mode      = "awsvpc"
  ecs_container_name    = "thecatapi"
  ecs_container_port    = 8080
  ecs_service_name      = "thecatapi-service"
}