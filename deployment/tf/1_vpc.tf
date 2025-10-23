variable "public_subnet_cidrs" {
  type        = list(string)
  description = "public subnet cidr values"
  default     = ["10.0.1.0/24", "10.0.2.0/24"]
  # default = ["10.0.1.0/24"]
}

variable "private_subnet_cidrs" {
  type        = list(string)
  description = "private subnet cidr values"
  default     = ["10.0.3.0/24", "10.0.4.0/24"]
  # default = ["10.0.2.0/24"]
}

variable "azs" {
  type        = list(string)
  description = "AZs"
  default     = ["us-east-1a", "us-east-1b"]
  # default = ["us-east-1a"]
}

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

variable "jar_url" {
  description = "S3 URL of the Spring Boot JAR file"
  default     = "https://curecart-db.s3.us-east-1.amazonaws.com/Meds-0.0.1-SNAPSHOT.jar"
}



resource "aws_iam_role" "ssm_role" {
  name = "curecart-SSMRole"
  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Action = "sts:AssumeRole"
        Effect = "Allow"
        Principal = {
          Service = "ec2.amazonaws.com"
        }
      },
    ]
  })
  tags = { Name = "curecart-SSM-Role" }
}


resource "aws_iam_role_policy_attachment" "ssm_policy" {
  role       = aws_iam_role.ssm_role.name
  policy_arn = "arn:aws:iam::aws:policy/AmazonSSMManagedInstanceCore"
}


resource "aws_iam_instance_profile" "ssm_profile" {
  name = "curecart-SSMProfile"
  role = aws_iam_role.ssm_role.name
}

resource "aws_vpc" "curecart_vpc" {
  cidr_block           = "10.0.0.0/16"
  enable_dns_hostnames = true
  enable_dns_support   = true
  tags = {
    Name = "curecart_vpc"
  }
}

resource "aws_subnet" "curecart_public_subnet" {
  vpc_id                  = aws_vpc.curecart_vpc.id
  count                   = length(var.public_subnet_cidrs)
  cidr_block              = element(var.public_subnet_cidrs, count.index)
  map_public_ip_on_launch = true
  availability_zone       = element(var.azs, count.index)

  tags = {
    Name = "Public Subnets ${count.index + 1}"
  }
}

resource "aws_subnet" "curecart_private_subnet" {
  vpc_id            = aws_vpc.curecart_vpc.id
  count             = length(var.private_subnet_cidrs)
  cidr_block        = element(var.private_subnet_cidrs, count.index) # loops through the cidr list
  availability_zone = element(var.azs, count.index)

  tags = {
    Name = "Private Subnets ${count.index + 1}"
  }
}

resource "aws_internet_gateway" "curecart_igw" {
  vpc_id = aws_vpc.curecart_vpc.id
  tags = {
    Name = "curecart_igw"
  }
}

resource "aws_eip" "nat_gateway_eip" {
  depends_on = [aws_internet_gateway.curecart_igw]
  tags = {
    Name = "curecart-ngw-eip"
  }
}

# public submet
resource "aws_nat_gateway" "ngw" {
  allocation_id = aws_eip.nat_gateway_eip.id
  subnet_id     = aws_subnet.curecart_public_subnet[0].id 
  depends_on    = [aws_internet_gateway.curecart_igw]
  tags = {
    Name = "curecart-NGW"
  }
}

resource "aws_route_table" "curecart_public_rt" {
  vpc_id = aws_vpc.curecart_vpc.id

  route {
    cidr_block = "0.0.0.0/0"
    gateway_id = aws_internet_gateway.curecart_igw.id
  }

  tags = {
    Name = "curecart_public_rt"
  }
}

resource "aws_route_table" "curecart_private_rt" {
  vpc_id = aws_vpc.curecart_vpc.id
  route {
    cidr_block     = "0.0.0.0/0"
    nat_gateway_id = aws_nat_gateway.ngw.id
  }
  tags = {
    Name = "curecart_private_rt"
  }
}

resource "aws_route_table_association" "curecart_public_subnet_association" {
  count          = length(var.public_subnet_cidrs)
  subnet_id      = element(aws_subnet.curecart_public_subnet[*].id, count.index)
  route_table_id = aws_route_table.curecart_public_rt.id
}

resource "aws_route_table_association" "curecart_private_subnet_association" {
  count          = length(var.private_subnet_cidrs)
  subnet_id      = element(aws_subnet.curecart_private_subnet[*].id, count.index)
  route_table_id = aws_route_table.curecart_private_rt.id
}

# ALB
# Allows public HTTP access
resource "aws_security_group" "alb" {
  name        = "curecart-ALB-SG"
  description = "Allow all inbound HTTP/S traffic to ALB"
  vpc_id      = aws_vpc.curecart_vpc.id

  ingress {
    from_port   = 80
    to_port     = 80
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

# For SSH into EC2
resource "aws_security_group" "ec2_sg" {
  name   = "ec2-sg"
  vpc_id = aws_vpc.curecart_vpc.id

  ingress {
    from_port   = 80
    to_port     = 80
    protocol    = "tcp"
    security_groups = [aws_security_group.alb.id]
    # cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = { Name = "ec2-sg" }
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

resource "aws_lb" "alb" {
  name               = "curecart-ALB"
  internal           = false
  load_balancer_type = "application"
  security_groups    = [aws_security_group.alb.id]
  subnets            = [for subnet in aws_subnet.curecart_public_subnet: subnet.id]
  tags = {
    Name = "curecart-ALB"
  }
}

resource "aws_lb_target_group" "tg" {
  name     = "curecart-TG"
  port     = 80
  protocol = "HTTP"
  vpc_id   = aws_vpc.curecart_vpc.id
}

resource "aws_lb_listener" "http" {
  load_balancer_arn = aws_lb.alb.arn
  port              = "80"
  protocol          = "HTTP"

  default_action {
    type             = "forward"
    target_group_arn = aws_lb_target_group.tg.arn
  }
}

resource "aws_instance" "springboot_server" {
  ami                         = "ami-0341d95f75f311023"
  instance_type               = "t3.micro"
  subnet_id                   = aws_subnet.curecart_private_subnet[0].id
  vpc_security_group_ids      = [aws_security_group.ec2_sg.id]
  iam_instance_profile        = aws_iam_instance_profile.ssm_profile.name
  
  # user_data = <<-EOF
  #   #!/bin/bash
  #   sudo yum update -y
  #   sudo yum install httpd -y
  #   sudo systemctl start httpd
  #   sudo systemctl enable httpd
  #   echo "<h1>Backend Server Running in Private Subnet!</h1>" | sudo tee /var/www/html/index.html
  # EOF

  user_data = <<-EOF
    #!/bin/bash
    sudo su
    sudo yum update -y
    sudo yum install -y java-17-amazon-corretto aws-cli

  EOF

  tags = {
    Name = "springboot-server"
  }
}


resource "aws_lb_target_group_attachment" "ec2_attachment" {
  target_group_arn = aws_lb_target_group.tg.arn
  target_id        = aws_instance.springboot_server.id
  port             = 80
}

  # user_data = <<-EOF
  #             #!/bin/bash
  #             sudo dnf update -y
  #             sudo dnf install -y java-17-amazon-corretto aws-cli

  #             # Set environment variables
  #             export DB_HOST="${aws_db_instance.curecart_rds.endpoint}"
  #             export DB_PORT="${aws_db_instance.curecart_rds.port}"
  #             export DB_NAME="${var.rds_db_name}"
  #             export DB_USER="${var.rds_username}"
  #             export DB_PASS="${var.rds_password}"
  #             export SERVER_PORT="8080"

  #             # Download and run Spring Boot app
  #             mkdir -p /opt/app
  #             cd /opt/app
  #             aws s3 cp s3://curecart-db/curecart.jar ./curecart.jar

              # nohup java -jar curecart.jar \
              #   --server.port=$$SERVER_PORT \
              #   --spring.datasource.url=jdbc:mysql://$$DB_HOST:$$DB_PORT/$$DB_NAME \
              #   --spring.datasource.username=$$DB_USER \
              #   --spring.datasource.password=$$DB_PASS \
  #               > /var/log/app.log 2>&1 &
  #             EOF


resource "aws_instance" "rds_client_instance" {
  ami           = "ami-0341d95f75f311023"
  instance_type = "t3.micro"
  subnet_id     = aws_subnet.curecart_private_subnet[0].id
  vpc_security_group_ids = [aws_security_group.rds_client.id]

  iam_instance_profile = aws_iam_instance_profile.ssm_profile.name


  user_data = <<-EOF
    #!/bin/bash
    sudo yum update -y
    sudo yum install https://dev.mysql.com/get/mysql84-community-release-el9-1.noarch.rpm
    sudo rpm --import https://repo.mysql.com/RPM-GPG-KEY-mysql
    sudo yum install mysql-community-server
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

# # RDS EC2 Instance
# resource "aws_instance" "rds_client" {
#   ami                         = "ami-0341d95f75f311023"
#   instance_type               = "t3.micro"
#   subnet_id                   = aws_subnet.curecart_public_subnet[0].id
#   associate_public_ip_address = true
#   security_groups             = [aws_security_group.ec2_sg.id]

#   user_data = <<-EOF
#               #!/bin/bash
#               sudo wget https://dev.mysql.com/get/mysql80-community-release-el9-1.noarch.rpm
#               sudo rpm -Uvh mysql80-community-release-el9-1.noarch.rpm

#               sudo dnf clean all
#               sudo dnf makecache
#               sudo dnf config-manager --set-enabled mysql80-community

#               sudo dnf install -y mysql-community-server

#               sudo systemctl start mysqld
#               sudo systemctl enable mysqld

#               sudo dnf install -y awscli

#               # Set RDS connection details
#               DB_HOST="$$aws_db_instance.curecart_rds.endpoint"
#               DB_PORT="$$aws_db_instance.curecart_rds.port"
#               DB_NAME="$$var.rds_db_name"
#               DB_USER="$$var.rds_username"
#               DB_PASS="$$var.rds_password"

#               # Download SQL script from S3
#               aws s3 cp s3://curecart-db/db-setup.sql /tmp/db-setup.sql

#               # Run SQL script against RDS
#               mysql -h $DB_HOST -P $DB_PORT -u $DB_USER -p$DB_PASS $DB_NAME < /tmp/db-setup.sql
#               EOF


#   tags = { Name = "rds-client-ec2" }
# }

# Outputs
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

output "application_load_balancer_dns" {
  description = "The DNS name of the Application Load Balancer (for Angular app to call)."
  value       = aws_lb.alb.dns_name
}
