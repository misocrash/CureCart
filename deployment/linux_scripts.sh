# For installing sql in the rds ec2 instance
sudo yum -y update
sudo yum install https://dev.mysql.com/get/mysql84-community-release-el9-1.noarch.rpm
sudo rpm --import https://repo.mysql.com/RPM-GPG-KEY-mysql
sudo yum install mysql-community-server

# JAVA install
sudo yum install java-17-amazon-corretto -y
sudo java -jar Meds-0.0.1-SNAPSHOT.jar --server.port=80 --spring.datasource.url=jdbc:mysql://mydb.cad646k2uj4t.us-east-1.rds.amazonaws.com:3306/curecartdb   --spring.datasource.username=admin   --spring.datasource.password=adminpassword

mysql -h mydb.cad646k2uj4t.us-east-1.rds.amazonaws.com -P 3306 -u admin -p'adminpassword' curecartdb  < db-setup.sql