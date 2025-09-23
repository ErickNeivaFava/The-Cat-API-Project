resource "aws_iam_role" "ecs_task_execution" {
  name = "ecsTaskExecutionRole-${local.ecs_service_name}"
  assume_role_policy = file("${path.module}/roles/ecs_task_execution_role.json")
}

resource "aws_iam_policy" "ecs_task_execution_custom" {
  name   = "ecsTaskExecutionCustomPolicy-${local.ecs_service_name}"
  policy = file("${path.module}/roles/ecs_task_execution_policy_custom.json")
}

resource "aws_iam_role_policy_attachment" "ecs_task_execution_custom_attach" {
  role       = aws_iam_role.ecs_task_execution.name
  policy_arn = aws_iam_policy.ecs_task_execution_custom.arn
}
