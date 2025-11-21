#!/bin/bash

###########################################
# PG Backend EC2 Deployment Script
# This script automates the deployment
# of PG Backend to AWS EC2
###########################################

set -e

echo "================================================"
echo "  PG Backend EC2 Deployment Script"
echo "================================================"
echo ""

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Function to print colored output
print_success() {
    echo -e "${GREEN}✓ $1${NC}"
}

print_error() {
    echo -e "${RED}✗ $1${NC}"
}

print_info() {
    echo -e "${YELLOW}ℹ $1${NC}"
}

# Check if running on Ubuntu
if [ ! -f /etc/lsb-release ]; then
    print_error "This script is designed for Ubuntu. Exiting."
    exit 1
fi

print_info "Step 1: Updating system packages..."
sudo apt update && sudo apt upgrade -y
print_success "System packages updated"

print_info "Step 2: Installing Docker..."
if command -v docker &> /dev/null; then
    print_info "Docker is already installed"
else
    curl -fsSL https://get.docker.com -o get-docker.sh
    sudo sh get-docker.sh
    sudo usermod -aG docker $USER
    rm get-docker.sh
    print_success "Docker installed successfully"
fi

print_info "Step 3: Installing Docker Compose..."
if command -v docker-compose &> /dev/null; then
    print_info "Docker Compose is already installed"
else
    sudo curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
    sudo chmod +x /usr/local/bin/docker-compose
    print_success "Docker Compose installed successfully"
fi

print_info "Step 4: Installing Git..."
if command -v git &> /dev/null; then
    print_info "Git is already installed"
else
    sudo apt install git -y
    print_success "Git installed successfully"
fi

print_info "Step 5: Installing Nginx..."
if command -v nginx &> /dev/null; then
    print_info "Nginx is already installed"
else
    sudo apt install nginx -y
    sudo systemctl enable nginx
    print_success "Nginx installed successfully"
fi

echo ""
print_success "Basic setup completed!"
echo ""
print_info "Next steps:"
echo "1. Clone your repository: git clone https://github.com/YOUR_USERNAME/pg-backend.git"
echo "2. cd pg-backend"
echo "3. Create .env file with your configuration"
echo "4. Run: docker-compose -f docker-compose.prod.yml up -d --build"
echo ""
print_info "For group changes to take effect, logout and login again, or run: newgrp docker"
