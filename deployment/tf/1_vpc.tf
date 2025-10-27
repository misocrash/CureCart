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
  subnets            = [for subnet in aws_subnet.curecart_public_subnet : subnet.id]
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

output "application_load_balancer_dns" {
  description = "The DNS name of the Application Load Balancer (for Angular app to call)."
  value       = aws_lb.alb.dns_name
}
