# ngrok Setup Guide for PG Backend

This guide explains how to expose your PG Backend application to the internet using ngrok.

## Prerequisites

- ngrok is already installed at `/opt/homebrew/bin/ngrok`
- Spring Boot application runs on port `8085`
- Application context path: `/pg`

## Quick Start

### 1. Configure ngrok (First Time Only)

If you have an ngrok account, add your authtoken to `ngrok.yml`:

```bash
# Get your authtoken from https://dashboard.ngrok.com/get-started/your-authtoken
# Then edit ngrok.yml and replace YOUR_NGROK_AUTHTOKEN_HERE with your actual token
```

Alternatively, set it via command line:

```bash
ngrok config add-authtoken YOUR_TOKEN_HERE
```

### 2. Start Your Spring Boot Application

```bash
# Using Maven
./mvnw spring-boot:run

# Or if you have a built JAR
java -jar target/pg-backend-*.jar
```

The application will start on port `8085`.

### 3. Start ngrok Tunnel

**Option A: Using the configuration file (Recommended)**

```bash
ngrok start pg-backend --config ngrok.yml
```

**Option B: Simple command (No config file needed)**

```bash
ngrok http 8085
```

**Option C: With HTTPS only**

```bash
ngrok http 8085 --scheme=https
```

### 4. Access Your Application

Once ngrok starts, you'll see output like:

```
Session Status                online
Account                       your-email@example.com
Version                       3.x.x
Region                        United States (us)
Forwarding                    https://abcd-1234-5678.ngrok-free.app -> http://localhost:8085
```

Your application is now accessible at:

```
https://abcd-1234-5678.ngrok-free.app/pg/v1/auth/...
https://abcd-1234-5678.ngrok-free.app/pg/...
```

**Important**: Don't forget the `/pg` context path!

## API Endpoints Examples

Assuming your ngrok URL is `https://abcd-1234-5678.ngrok-free.app`:

### Authentication
```bash
# Register
POST https://abcd-1234-5678.ngrok-free.app/pg/v1/auth/register

# Login
POST https://abcd-1234-5678.ngrok-free.app/pg/v1/auth/login
```

### PG Operations (Requires JWT)
```bash
# List all PGs
GET https://abcd-1234-5678.ngrok-free.app/pg/v1/pgs

# Create PG
POST https://abcd-1234-5678.ngrok-free.app/pg/v1/pgs
```

### Swagger UI
```
https://abcd-1234-5678.ngrok-free.app/pg/swagger-ui/index.html
```

## CORS Configuration

The application is configured to accept requests from:
- All ngrok domains (`*.ngrok.io`, `*.ngrok-free.app`)
- Localhost (for local development)

CORS settings in `src/main/java/org/recnos/pg/config/WebConfig.java`:
- Allowed methods: GET, POST, PUT, DELETE, OPTIONS, PATCH
- Credentials: Enabled
- Max age: 3600 seconds

## Environment Variables

You can customize CORS allowed origins:

```bash
export CORS_ALLOWED_ORIGINS="http://localhost:3000,https://your-frontend.com"
./mvnw spring-boot:run
```

## Tips and Best Practices

### 1. Keep ngrok Running
ngrok must stay running while you want external access. Keep it in a separate terminal window.

### 2. URL Changes on Restart
Free ngrok URLs change every time you restart ngrok. For a permanent URL, consider:
- ngrok paid plan (custom subdomain)
- Use environment variables for the frontend to configure the API base URL

### 3. Security Considerations

- JWT tokens still required for protected endpoints
- Free ngrok includes an interstitial page warning
- Consider adding basic auth for extra security (see ngrok.yml)
- Monitor ngrok's web interface at `http://127.0.0.1:4040` for request inspection

### 4. Inspect Traffic

ngrok provides a web interface to inspect all HTTP traffic:

```
http://127.0.0.1:4040
```

This shows all requests/responses going through the tunnel - very useful for debugging!

### 5. Rate Limits

Be aware of:
- ngrok free tier limits (check https://ngrok.com/pricing)
- Application rate limits (20 file uploads per hour)

## Troubleshooting

### "Tunnel not found" error
Make sure ngrok.yml is in the project root and the tunnel name matches:
```bash
ngrok start pg-backend --config ngrok.yml
```

### CORS errors
Verify WebConfig.java has the correct origin patterns. The wildcard patterns should cover all ngrok domains.

### Port already in use
Make sure no other application is using port 8085:
```bash
lsof -i :8085
```

### ngrok authtoken error
Free tier works without authtoken, but has limits. Sign up at https://ngrok.com and add your token.

## Local Network Access

Your application is also accessible on your local network:

1. Find your local IP:
```bash
ifconfig | grep "inet " | grep -v 127.0.0.1
```

2. Access from other devices on the same network:
```
http://YOUR_LOCAL_IP:8085/pg/v1/...
```

For example: `http://192.168.1.100:8085/pg/v1/auth/login`

## Stopping ngrok

Press `Ctrl+C` in the ngrok terminal to stop the tunnel.

## Additional Resources

- [ngrok Documentation](https://ngrok.com/docs)
- [ngrok Dashboard](https://dashboard.ngrok.com)
- [Spring Boot CORS Documentation](https://docs.spring.io/spring-framework/docs/current/reference/html/web.html#mvc-cors)
