# EC2 Deployment Guide for PG Backend

This guide will help you deploy the PG Backend application on an AWS EC2 instance using Docker.

## Prerequisites

- AWS EC2 instance (Ubuntu 22.04 LTS recommended)
- Minimum recommended: t3.medium (2 vCPU, 4 GB RAM)
- Security group with ports open:
  - 22 (SSH)
  - 80 (HTTP)
  - 443 (HTTPS)
  - 8085 (Application - optional, for direct access)

## Step 1: Connect to Your EC2 Instance

```bash
ssh -i your-key.pem ubuntu@your-ec2-public-ip
```

## Step 2: Initial Server Setup

### Update system packages
```bash
sudo apt update && sudo apt upgrade -y
```

### Install Docker
```bash
# Install Docker
curl -fsSL https://get.docker.com -o get-docker.sh
sudo sh get-docker.sh

# Add your user to docker group
sudo usermod -aG docker $USER

# Logout and login again for group changes to take effect
# Or run: newgrp docker

# Start Docker service
sudo systemctl enable docker
sudo systemctl start docker

# Verify installation
docker --version
```

### Install Docker Compose
```bash
# Download Docker Compose
sudo curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose

# Make it executable
sudo chmod +x /usr/local/bin/docker-compose

# Verify installation
docker-compose --version
```

### Install Git
```bash
sudo apt install git -y
```

## Step 3: Clone Your Repository

```bash
# Clone your repository
git clone https://github.com/YOUR_USERNAME/pg-backend.git
cd pg-backend
```

## Step 4: Configure Environment Variables

Create a `.env` file with production settings:

```bash
nano .env
```

Add the following (replace with your actual values):

```env
# Application Configuration
SPRING_PROFILES_ACTIVE=prod
APP_PORT=8085
SERVER_CONTEXT_PATH=/pg

# Database Configuration
DB_NAME=pg_backend
DB_USERNAME=postgres
DB_PASSWORD=YOUR_STRONG_PASSWORD_HERE

# JWT Configuration (generate a strong random secret)
JWT_SECRET=YOUR_JWT_SECRET_KEY_HERE_MUST_BE_AT_LEAST_256_BITS_LONG
JWT_ACCESS_TOKEN_EXPIRATION=86400000
JWT_REFRESH_TOKEN_EXPIRATION=604800000

# AWS S3 Configuration
AWS_S3_BUCKET=your-s3-bucket-name
AWS_REGION=us-east-1
AWS_ACCESS_KEY=your-aws-access-key
AWS_SECRET_KEY=your-aws-secret-key
AWS_CLOUDFRONT_DOMAIN=your-cloudfront-domain.cloudfront.net

# CORS Configuration (add your frontend domains)
CORS_ALLOWED_ORIGINS=https://yourdomain.com,https://www.yourdomain.com

# File Upload Configuration
FILE_MAX_SIZE=5MB
FILE_MAX_REQUEST_SIZE=25MB
```

**Generate strong secrets:**
```bash
# Generate JWT secret
openssl rand -base64 64

# Generate database password
openssl rand -base64 32
```

**Secure the .env file:**
```bash
chmod 600 .env
```

## Step 5: Build and Deploy

```bash
# Build and start all services
docker-compose -f docker-compose.prod.yml up -d --build

# View logs (press Ctrl+C to exit)
docker-compose -f docker-compose.prod.yml logs -f

# Check status
docker-compose -f docker-compose.prod.yml ps
```

## Step 6: Verify Deployment

```bash
# Check if application is running
curl http://localhost:8085/pg/actuator/health

# Expected response: {"status":"UP"}

# Test the OTP endpoint
curl -X POST http://localhost:8085/pg/v1/user/auth/send-otp \
  -H 'Content-Type: application/json' \
  -d '{"mobile":"1234567890","userType":"USER"}'
```

## Step 7: Setup Nginx Reverse Proxy (Recommended)

### Install Nginx
```bash
sudo apt install nginx -y
```

### Configure Nginx
```bash
sudo nano /etc/nginx/sites-available/pg-backend
```

Add the following configuration:

```nginx
server {
    listen 80;
    server_name your-domain.com www.your-domain.com;

    client_max_body_size 25M;

    location / {
        proxy_pass http://localhost:8085;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;

        # WebSocket support
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection "upgrade";

        # Timeouts
        proxy_connect_timeout 60s;
        proxy_send_timeout 60s;
        proxy_read_timeout 60s;
    }

    # Health check endpoint
    location /pg/actuator/health {
        proxy_pass http://localhost:8085/pg/actuator/health;
        access_log off;
    }
}
```

### Enable the site
```bash
sudo ln -s /etc/nginx/sites-available/pg-backend /etc/nginx/sites-enabled/
sudo nginx -t
sudo systemctl restart nginx
```

## Step 8: Setup SSL with Let's Encrypt

```bash
# Install Certbot
sudo apt install certbot python3-certbot-nginx -y

# Obtain SSL certificate
sudo certbot --nginx -d your-domain.com -d www.your-domain.com
```

## Useful Commands

### Application Management
```bash
# View logs
docker-compose -f docker-compose.prod.yml logs -f app

# Restart application
docker-compose -f docker-compose.prod.yml restart app

# Stop all services
docker-compose -f docker-compose.prod.yml down

# Update application
git pull
docker-compose -f docker-compose.prod.yml up -d --build
```

### Database Management
```bash
# Access database
docker exec -it pg-backend-db psql -U postgres -d pg_backend

# Backup database
docker exec pg-backend-db pg_dump -U postgres pg_backend > backup.sql

# Restore database
cat backup.sql | docker exec -i pg-backend-db psql -U postgres -d pg_backend
```

## Security Configuration

```bash
# Setup firewall
sudo ufw enable
sudo ufw allow 22
sudo ufw allow 80
sudo ufw allow 443

# Install fail2ban
sudo apt install fail2ban -y
sudo systemctl enable fail2ban
```

## Monitoring

```bash
# Check container status
docker ps

# Check resource usage
docker stats

# Check disk space
df -h

# Check memory
free -h
```

## Troubleshooting

### Application won't start
```bash
docker-compose -f docker-compose.prod.yml logs app
```

### Database connection issues
```bash
docker-compose -f docker-compose.prod.yml logs postgres
```

### Port conflicts
```bash
sudo lsof -i :8085
```

For more details, refer to the comprehensive troubleshooting section in the full documentation.
