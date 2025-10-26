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


# variable "jar_url" {
#   description = "S3 URL of the Spring Boot JAR file"
#   default     = "https://curecart-db.s3.us-east-1.amazonaws.com/Meds-0.0.1-SNAPSHOT.jar"
# }


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


output "application_load_balancer_dns" {
  description = "The DNS name of the Application Load Balancer (for Angular app to call)."
  value       = aws_lb.alb.dns_name
}
