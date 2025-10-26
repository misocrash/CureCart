
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

resource "aws_instance" "springboot_server" {
  ami                         = "ami-0341d95f75f311023"
  instance_type               = "t3.micro"
  subnet_id                   = aws_subnet.curecart_private_subnet[0].id
  vpc_security_group_ids      = [aws_security_group.ec2_sg.id]
  iam_instance_profile        = aws_iam_instance_profile.ssm_profile.name
  

  user_data = <<-EOF
      #!/bin/bash

      # Update system and install Java
      sudo dnf update -y
      sudo dnf install -y java-17-amazon-corretto

      # Download Spring Boot JAR
      cd /home
      sudo wget https://curecart-db.s3.us-east-1.amazonaws.com/Meds-0.0.1-SNAPSHOT.jar

      # Create systemd service
      cat <<EOT > /etc/systemd/system/meds.service
      [Unit]
      Description=Meds Spring Boot App
      After=network.target

      [Service]
      User=root
      ExecStart=/usr/bin/java -jar /home/Meds-0.0.1-SNAPSHOT.jar --spring.datasource.url=jdbc:mysql://${aws_db_instance.curecart_rds.endpoint}:3306/${var.rds_db_name} --spring.datasource.username=${var.rds_username} --spring.datasource.password=${var.rds_password}
      Restart=always

      [Install]
      WantedBy=multi-user.target
      EOT

      # Enable and start the service
      systemctl daemon-reexec
      systemctl daemon-reload
      systemctl enable meds
      systemctl start meds
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