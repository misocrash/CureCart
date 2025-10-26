variable "rds_username" {
  description = "Master username for RDS"
  type        = string
  default     = "admin"
}

variable "rds_password" {
  description = "Master password for RDS"
  type        = string
  sensitive   = true
  default = "adminpassword"
}

variable "rds_db_name" {
  description = "Database name for RDS"
  type        = string
  default     = "curecartdb"
}


resource "aws_security_group" "rds_client" {
  name        = "curecart-RDS-Client-SG"
  description = "Allows no inbound connections. Access via SSM Session Manager only."
  vpc_id      = aws_vpc.curecart_vpc.id


  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

resource "aws_security_group" "rds" {
  name        = "curecart-RDS-SG"
  description = "Allow inbound traffic from EC2 App and EC2 Client SGs only"
  vpc_id      = aws_vpc.curecart_vpc.id

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

resource "aws_security_group_rule" "rds_ingress_from_app_server" {
  type                     = "ingress"
  from_port                = 3306
  to_port                  = 3306
  protocol                 = "tcp"
  security_group_id        = aws_security_group.rds.id
  source_security_group_id = aws_security_group.ec2_sg.id
  description              = "Allow MySQL access from App Server"
}


resource "aws_security_group_rule" "rds_ingress_from_client" {
  type                     = "ingress"
  from_port                = 3306
  to_port                  = 3306
  protocol                 = "tcp"
  security_group_id        = aws_security_group.rds.id
  source_security_group_id = aws_security_group.rds_client.id 
  description              = "Allow MySQL access from RDS Client"
}

resource "aws_security_group_rule" "rds_client_egress_to_rds" {
  type                     = "egress"
  from_port                = 3306
  to_port                  = 3306
  protocol                 = "tcp"
  security_group_id        = aws_security_group.rds_client.id # Client SG ID
  source_security_group_id = aws_security_group.rds.id       # References the RDS SG ID
  description              = "Allow outbound MySQL access to RDS"
}

resource "aws_instance" "rds_client_instance" {
  ami           = "ami-0341d95f75f311023"
  instance_type = "t3.micro"
  subnet_id     = aws_subnet.curecart_private_subnet[0].id
  vpc_security_group_ids = [aws_security_group.rds_client.id]

  iam_instance_profile = aws_iam_instance_profile.ssm_profile.name


  user_data = <<-EOF
    #!/bin/bash
    sudo dnf update -y
    sudo dnf install -y https://dev.mysql.com/get/mysql84-community-release-el9-1.noarch.rpm
    sudo rpm --import https://repo.mysql.com/RPM-GPG-KEY-mysql
    sudo dnf install -y mysql-community-server
    sudo cd /home
    sudo wget https://curecart-db.s3.us-east-1.amazonaws.com/db-setup.sql
    sudo mysql -h ${aws_db_instance.curecart_rds.endpoint} -P ${aws_db_instance.curecart_rds.port} -u ${var.rds_username} -p${var.rds_password} ${var.rds_db_name} < /db-setup.sql
    EOF

  tags = { Name = "curecart-EC2-RDS-Client" }
}

# RDS Subnet Group
resource "aws_db_subnet_group" "rds_subnet_group" {
  name       = "rds-subnet-group"
  subnet_ids = aws_subnet.curecart_private_subnet[*].id
  tags       = { Name = "rds-subnet-group" }
}

# RDS MySQL Instance
resource "aws_db_instance" "curecart_rds" {
  identifier             = "mydb"
  engine                 = "mysql"
  engine_version         = "8.0"
  instance_class         = "db.t3.micro"
  allocated_storage      = 20
  storage_type           = "gp2"
  username               = "admin"
  password               = "adminpassword"
  db_name                = "curecartdb"
  publicly_accessible    = false
  skip_final_snapshot    = true
  deletion_protection    = false
  multi_az               = false
  vpc_security_group_ids = [aws_security_group.rds.id]
  db_subnet_group_name   = aws_db_subnet_group.rds_subnet_group.name

  tags = { Name = "rds-mysql" }
}

output "rds_endpoint" {
  value = aws_db_instance.curecart_rds.endpoint
}

output "rds_port" {
  value = aws_db_instance.curecart_rds.port
}

output "rds_username" {
  value = var.rds_username
}

output "rds_password" {
  value = var.rds_password
  sensitive = true
}