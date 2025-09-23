terraform {
  backend "s3" {
    bucket         = "777184857830-statefile-tf"
    key            = "thecatapi/terraform.tfstate"
    region         = "sa-east-1"
    encrypt        = true
  }
}