# Quick Start Guide - Deploy to EC2

## 🚀 Fast Deploy (5 minutes)

### 1. SSH into your EC2 instance
```bash
ssh -i your-key.pem ubuntu@YOUR_EC2_IP
```

### 2. Run automated setup script
```bash
curl -fsSL https://get.docker.com -o get-docker.sh && \
sudo sh get-docker.sh && \
sudo usermod -aG docker $USER && \
sudo curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose && \
sudo chmod +x /usr/local/bin/docker-compose && \
sudo apt install git nginx -y

# Logout and login again
exit
```

### 3. Clone and configure
```bash
git clone https://github.com/YOUR_USERNAME/pg-backend.git
cd pg-backend

# Copy environment template
cp docs/deployment/.env.example .env

# Edit with your values
nano .env
```

### 4. Generate secrets
```bash
# Generate JWT secret
echo "JWT_SECRET=$(openssl rand -base64 64 | tr -d '\n')"

# Generate database password
echo "DB_PASSWORD=$(openssl rand -base64 32 | tr -d '\n')"
```

Update these values in your `.env` file.

### 5. Deploy!
```bash
docker-compose -f docker-compose.prod.yml up -d --build
```

### 6. Verify
```bash
# Check if running
docker ps

# Check logs
docker-compose -f docker-compose.prod.yml logs -f

# Test health endpoint
curl http://localhost:8085/pg/actuator/health
```

## 📋 Environment Variables Checklist

Make sure to configure these in your `.env` file:

- [ ] `DB_PASSWORD` - Strong database password
- [ ] `JWT_SECRET` - Strong JWT secret (256+ bits)
- [ ] `AWS_S3_BUCKET` - Your S3 bucket name
- [ ] `AWS_ACCESS_KEY` - AWS access key
- [ ] `AWS_SECRET_KEY` - AWS secret key
- [ ] `CORS_ALLOWED_ORIGINS` - Your frontend domain(s)

## 🔒 Setup Nginx + SSL (Optional but Recommended)

```bash
# Create Nginx config
sudo nano /etc/nginx/sites-available/pg-backend
```

Paste this configuration:
```nginx
server {
    listen 80;
    server_name your-domain.com;
    
    client_max_body_size 25M;
    
    location / {
        proxy_pass http://localhost:8085;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

Enable and start:
```bash
sudo ln -s /etc/nginx/sites-available/pg-backend /etc/nginx/sites-enabled/
sudo nginx -t
sudo systemctl restart nginx

# Setup SSL with Let's Encrypt
sudo apt install certbot python3-certbot-nginx -y
sudo certbot --nginx -d your-domain.com
```

## 📊 Monitoring Commands

```bash
# View application logs
docker-compose -f docker-compose.prod.yml logs -f app

# Check container status
docker ps

# Check resource usage
docker stats

# Access database
docker exec -it pg-backend-db psql -U postgres -d pg_backend
```

## 🔄 Update Application

```bash
cd pg-backend
git pull
docker-compose -f docker-compose.prod.yml up -d --build
```

## 🆘 Troubleshooting

### Application won't start
```bash
# Check application logs
docker-compose -f docker-compose.prod.yml logs app

# Check database logs
docker-compose -f docker-compose.prod.yml logs postgres
```

### Database connection failed
- Verify `.env` credentials match
- Check if database container is running: `docker ps`
- Check database logs: `docker-compose -f docker-compose.prod.yml logs postgres`

### Port 8085 already in use
```bash
# Find what's using the port
sudo lsof -i :8085

# Kill the process
sudo kill -9 <PID>
```

## 📦 Backup Database

```bash
# Create backup
docker exec pg-backend-db pg_dump -U postgres pg_backend > backup_$(date +%Y%m%d).sql

# Restore backup
cat backup_20250101.sql | docker exec -i pg-backend-db psql -U postgres -d pg_backend
```

## 🔐 Security Checklist

- [ ] Change default database password
- [ ] Generate strong JWT secret
- [ ] Enable firewall: `sudo ufw enable && sudo ufw allow 22,80,443/tcp`
- [ ] Install fail2ban: `sudo apt install fail2ban -y`
- [ ] Setup SSL certificate
- [ ] Configure CORS for your frontend domain only
- [ ] Set up automated backups

## 🌐 API Endpoints

Once deployed, your API will be available at:

- Health check: `http://YOUR_DOMAIN/pg/actuator/health`
- Send OTP: `http://YOUR_DOMAIN/pg/v1/user/auth/send-otp`
- Swagger UI: `http://YOUR_DOMAIN/pg/swagger-ui.html`

## 📞 Support

For issues or questions:
1. Check application logs
2. Review the full [EC2 Deployment Guide](./EC2_DEPLOYMENT_GUIDE.md)
3. Check Docker container status

Good luck! 🚀
