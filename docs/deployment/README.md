# PG Backend Deployment Documentation

This folder contains all the documentation and scripts needed to deploy the PG Backend application to AWS EC2.

## 📚 Documentation

### [Quick Start Guide](./QUICK_START.md) ⚡
**Start here!** 5-minute deployment guide with essential commands.
- Fast setup instructions
- Environment configuration checklist
- Basic troubleshooting

### [Complete EC2 Deployment Guide](./EC2_DEPLOYMENT_GUIDE.md) 📖
Comprehensive step-by-step guide covering:
- Detailed EC2 instance setup
- Docker and Docker Compose installation
- Nginx reverse proxy configuration
- SSL certificate setup with Let's Encrypt
- Monitoring and maintenance
- Security best practices
- Backup and recovery procedures

## 🛠 Scripts

### [deploy-to-ec2.sh](./deploy-to-ec2.sh)
Automated deployment script that:
- Installs Docker and Docker Compose
- Sets up Nginx
- Configures the system for deployment

**Usage:**
```bash
chmod +x deploy-to-ec2.sh
./deploy-to-ec2.sh
```

## 📋 Configuration Files

### [.env.example](./.env.example)
Template for environment variables. Copy this to `.env` and fill in your values:
```bash
cp .env.example ../../.env
nano ../../.env
```

## 🚀 Deployment Options

### Option 1: Docker Compose (Recommended)
Best for single-server deployments. Uses the included `docker-compose.prod.yml`.

**Pros:**
- Simple setup
- Easy to manage
- Good for small to medium workloads

**Setup:**
```bash
docker-compose -f docker-compose.prod.yml up -d --build
```

### Option 2: AWS ECS/EKS
For production-scale deployments with auto-scaling.

**Pros:**
- Auto-scaling
- High availability
- Managed infrastructure

**Cons:**
- More complex setup
- Higher cost

### Option 3: Manual Deployment
Build JAR locally and run on EC2 without Docker.

**Not recommended** - Docker deployment is preferred.

## 🏗 Architecture

```
Internet
    ↓
  Nginx (Port 80/443)
    ↓
  Spring Boot App (Port 8085)
    ↓
  PostgreSQL (Port 5432)
    ↓
  AWS S3 (File Storage)
```

## 📊 Monitoring

### Application Health
```bash
curl http://localhost:8085/pg/actuator/health
```

### Logs
```bash
# Application logs
docker-compose -f docker-compose.prod.yml logs -f app

# Database logs
docker-compose -f docker-compose.prod.yml logs -f postgres

# Nginx logs
sudo tail -f /var/log/nginx/access.log
```

### Resource Usage
```bash
# Docker stats
docker stats

# System resources
htop
```

## 🔒 Security Checklist

Before going to production:

- [ ] Change all default passwords
- [ ] Generate strong JWT secret (256+ bits)
- [ ] Setup firewall (UFW)
- [ ] Install fail2ban for SSH protection
- [ ] Enable SSL with Let's Encrypt
- [ ] Configure CORS for specific domains only
- [ ] Restrict database port (5432) to localhost only
- [ ] Set up automated backups
- [ ] Enable CloudWatch or similar monitoring
- [ ] Configure log rotation
- [ ] Use AWS Secrets Manager for sensitive data

## 🆘 Common Issues

### Application won't start
```bash
docker-compose -f docker-compose.prod.yml logs app
```
Check for:
- Missing environment variables
- Database connection issues
- Port conflicts

### Database connection failed
- Verify `.env` credentials
- Check if PostgreSQL container is running
- Ensure Flyway migrations succeeded

### Out of memory
- Increase EC2 instance size
- Reduce JVM heap size in `docker-compose.prod.yml`
- Enable swap space

### SSL certificate issues
```bash
sudo certbot renew --dry-run
```

## 📞 Getting Help

1. Check the logs first
2. Review the troubleshooting section in the guides
3. Verify your environment configuration
4. Check Docker container status

## 🔄 Maintenance

### Updating the Application
```bash
cd pg-backend
git pull
docker-compose -f docker-compose.prod.yml up -d --build
```

### Database Backups
```bash
# Create backup
docker exec pg-backend-db pg_dump -U postgres pg_backend > backup.sql

# Restore backup
cat backup.sql | docker exec -i pg-backend-db psql -U postgres -d pg_backend
```

### Log Rotation
Logs are automatically rotated by Docker:
```yaml
logging:
  options:
    max-size: "10m"
    max-file: "3"
```

## 💰 Cost Optimization

- Use t3.medium for production (2 vCPU, 4 GB RAM) ≈ $30/month
- Consider AWS RDS for PostgreSQL for better reliability
- Use CloudFront with S3 for static assets
- Enable Auto Scaling for traffic spikes
- Use Reserved Instances for cost savings

## 📈 Scaling

As your application grows:

1. **Vertical Scaling**: Increase EC2 instance size
2. **Horizontal Scaling**: Use Application Load Balancer with multiple instances
3. **Database Scaling**: Move to RDS with read replicas
4. **Caching**: Add Redis for session management
5. **CDN**: Use CloudFront for static content

## 🎯 Next Steps

After successful deployment:

1. Set up monitoring (CloudWatch, Prometheus, Grafana)
2. Configure automated backups
3. Implement CI/CD pipeline
4. Set up staging environment
5. Configure alerting
6. Document your deployment process
7. Create runbooks for common operations

---

**Ready to deploy?** Start with the [Quick Start Guide](./QUICK_START.md)!
