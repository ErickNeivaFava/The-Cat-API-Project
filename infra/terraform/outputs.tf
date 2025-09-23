output "ecs_service_name" {
  value = aws_ecs_service.thecatapi.name
}

output "rds_endpoint" {
  value = module.rds.db_instance_endpoint
}