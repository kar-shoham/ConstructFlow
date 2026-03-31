# ConstructFlow CRM

A modern, microservices-based Construction Project Management and HR Solution built with Spring Boot, React, and cloud-native technologies.

## 📋 Table of Contents

- [Business Logic](#business-logic)
- [Architecture & Services](#architecture--services)
- [Technology Stack](#technology-stack)
- [Deployment](#deployment)
- [Getting Started](#getting-started)
- [Environment Variables](#environment-variables)

---

## 🎯 Business Logic

ConstructFlow is a comprehensive Construction Resource Management (CRM) platform designed to streamline HR and project management operations within construction companies. The application focuses on:

### Core Features

1. **User Management**
   - User registration and authentication
   - Role-based access control
   - JWT-based token management
   - User profile management

2. **Entity Management**
   - Management of organizational entities (companies, departments)
   - Project and entity hierarchy
   - Resource and cost code management
   - Task categorization and management

3. **Timesheet Management**
   - Employee time tracking
   - Project-based time entry
   - Task and cost code assignment
   - Status tracking (submitted, approved, rejected)
   - Advanced filtering (by employee, company, date range)

4. **Payroll Processing**
   - Payroll calculation based on timesheets
   - Salary processing
   - Integration with timesheet data

### Business Workflow

```
User Logs In → Creates/Edits Timesheets → Timesheet Submitted →
Validation & Approval → Payroll Processing → Payment Completion
```

---

## 🏗️ Architecture & Services

ConstructFlow uses a **microservices architecture** with the following components:

### Architecture Overview

The system is composed of distinct layers:

**Client Layer**: React frontend (Vite) served via Nginx with SSL/TLS termination on ports 80/443

**API Gateway Layer**: Spring Cloud Gateway routing all requests with JWT validation

**Service Layer**: 5 independent microservices:
- User Service (authentication & authorization)
- Entity Service (organizational data)
- Timesheet Service (time tracking)
- Payroll Service (payroll processing)
- Config Service (centralized configuration)

**Infrastructure Layer**:
- Discovery Service (Eureka) for service registration
- PostgreSQL databases (4 independent instances)
- Kafka for asynchronous event publishing

All services communicate via Docker network and are discoverable through Eureka.

### Services Description

#### 1. **Discovery Service** (Eureka)
- **Port**: 8580 (external), 8080 (internal)
- **Purpose**: Service registry and discovery
- **Role**: Maintains a registry of all microservices
- **Technology**: Spring Cloud Netflix Eureka

#### 2. **Config Service**
- **Port**: 8590 (external), 8080 (internal)
- **Purpose**: Centralized configuration management
- **Role**: Provides configuration to all microservices from a Git repository
- **Dependencies**: Discovery Service
- **Technology**: Spring Cloud Config Server

#### 3. **API Gateway Service**
- **Port**: 8680 (external), 8080 (internal)
- **Purpose**: Single entry point for all client requests
- **Features**:
  - Request routing to appropriate microservices
  - JWT token validation and authorization
  - Load balancing
  - CORS handling
- **Technology**: Spring Cloud Gateway with WebFlux

#### 4. **User Service**
- **Database**: cf_user_db (PostgreSQL on port 5531)
- **Purpose**: User authentication and management
- **Features**:
  - User registration and login
  - JWT token generation
  - Password management
  - Role-based access control (RBAC)
- **Dependencies**: Config Service, PostgreSQL

#### 5. **Entity Service**
- **Database**: cf_entity_db (PostgreSQL on port 5532)
- **Purpose**: Manage organizational entities
- **Features**:
  - Company and department management
  - Project management
  - Cost code and task configuration
  - Organizational hierarchy
- **Dependencies**: Config Service, PostgreSQL

#### 6. **Timesheet Service**
- **Database**: cf_timesheet_db (PostgreSQL on port 5533)
- **Purpose**: Handle timesheet operations
- **Features**:
  - Create, read, update timesheets
  - Validate timesheet data (calls Entity Service)
  - Filter by employee, company, date range
  - Publish timesheet events to Kafka
- **Dependencies**: Config Service, Entity Service, PostgreSQL, Kafka
- **Key Integration**: Uses Kafka to publish timesheet submission events

#### 7. **Payroll Service**
- **Database**: cf_payroll_db (PostgreSQL on port 5534)
- **Purpose**: Process payroll based on timesheets
- **Features**:
  - Calculate payroll from timesheet data
  - Generate payroll reports
  - Integration with timesheet data
- **Dependencies**: Config Service, PostgreSQL

#### 8. **Frontend (React + Vite)**
- **Port**: 80 (HTTP), 443 (HTTPS)
- **Purpose**: Web-based user interface
- **Features**:
  - Responsive UI for timesheet management
  - Authentication and authorization
  - Real-time data display
  - RESTful API integration
- **Server**: Nginx (handles SSL/TLS and routing)

### Data Flow

1. **Service-to-Service Communication**
   - Synchronous: REST calls via Feign Client (e.g., Timesheet Service validates data with Entity Service)
   - Asynchronous: Kafka events (e.g., timesheet submission events)

2. **Client Communication**
   - HTTPS requests to Nginx
   - Nginx routes `/api/*` to API Gateway
   - API Gateway routes to appropriate microservice based on Eureka registry

---

## 🛠️ Technology Stack

### Backend
- **Framework**: Spring Boot 4.0.1
- **Cloud**: Spring Cloud 2025.1.0
  - Spring Cloud Gateway (API routing)
  - Spring Cloud Config (configuration management)
  - Eureka (service discovery)
  - Feign (inter-service communication)
- **Language**: Java 17
- **Build**: Gradle
- **ORM**: Spring Data JPA
- **Database Migrations**: Liquibase
- **Security**: Spring Security with JWT (JJWT)
- **Message Queue**: Apache Kafka
- **Observability**: Micrometer (metrics), Brave (distributed tracing)
- **Validation**: Jakarta Validation
- **Object Mapping**: ModelMapper

### Frontend
- **Framework**: React 18.3.1
- **Build Tool**: Vite 6.0.1
- **Language**: TypeScript 5.7.0
- **Routing**: React Router DOM 7.0.2
- **HTTP Client**: Axios 1.7.9
- **UI Components**: Lucide React 1.7.0
- **Linting**: ESLint with TypeScript support

### Infrastructure & DevOps
- **Containerization**: Docker
- **Orchestration**: Docker Compose
- **Web Server**: Nginx (Alpine)
- **Database**: PostgreSQL (latest)
- **Message Broker**: Apache Kafka (Confluent)
- **SSL/TLS**: Let's Encrypt (LetsEncrypt certificates)
- **Deployment**: GCP Compute Engine (e1-small instances)
- **Container Registry**: Docker images built locally or in CI/CD pipeline

---

## 🚀 Deployment

### Local Development

The application is fully containerized and uses Docker Compose for orchestration.

#### Prerequisites

- Docker and Docker Compose installed
- Git credentials configured (for Config Service to access config repository)

#### Running Locally

```bash
# Set up environment variables
export GIT_PASSWORD=<your_github_token>

# Start all services
docker-compose up --build

# Access the application
# Frontend: http://localhost (or https://localhost for SSL)
# API Gateway: http://localhost:8680
# Discovery Service: http://localhost:8580
# Config Service: http://localhost:8590
```

#### Service Ports (Local)

| Service | Internal Port | External Port |
|---------|---------------|---------------|
| Discovery Service | 8080 | 8580 |
| Config Service | 8080 | 8590 |
| API Gateway | 8080 | 8680 |
| User DB | 5432 | 5531 |
| Entity DB | 5432 | 5532 |
| Timesheet DB | 5432 | 5533 |
| Payroll DB | 5432 | 5534 |
| Kafka | 9092 | 9092 |
| Frontend | 80/443 | 80/443 |

### Production Deployment

ConstructFlow is designed to be deployed on **GCP Compute Engine** or any Docker-capable infrastructure.

#### Deployment Architecture

```
┌─────────────────────────────────────────────┐
│     GCP Compute Engine Instance             │
│  (e1-small: 2 vCPU, 2GB RAM)               │
│                                             │
│  ┌────────────────────────────────────────┐ │
│  │   Docker Compose Stack                 │ │
│  │                                        │ │
│  │  Services (7) + Databases (4) + Kafka │ │
│  │                                        │ │
│  │  Volumes: /db-volumes/*                │ │
│  │  Network: constructflow-network        │ │
│  └────────────────────────────────────────┘ │
│                                             │
│  ┌────────────────────────────────────────┐ │
│  │   SSL Certificates                     │ │
│  │   /etc/letsencrypt/archive/            │ │
│  │   www.constructflow.space/             │ │
│  └────────────────────────────────────────┘ │
└─────────────────────────────────────────────┘
           │ Port 80 (HTTP)
           │ Port 443 (HTTPS)
           ▼
      ┌────────────┐
      │  Internet  │
      └────────────┘
```

#### SSL/TLS Configuration

- **Certificate Authority**: Let's Encrypt (LetsEncrypt)
- **Certificate Path**: `/etc/letsencrypt/archive/www.constructflow.space/`
- **Nginx Configuration**:
  - HTTP (port 80) → redirects to HTTPS
  - HTTPS (port 443) → serves frontend and proxies API
  - SSL Protocols: TLSv1.2, TLSv1.3
  - Cipher Suites: HIGH:!aNULL:!MD5

#### Nginx Configuration

The frontend Dockerfile includes an embedded Nginx configuration:

```nginx
# HTTP to HTTPS redirect
server {
    listen 80;
    server_name www.constructflow.space;
    return 301 https://$host$request_uri;
}

# HTTPS server
server {
    listen 443 ssl http2;
    server_name www.constructflow.space;

    # Static assets
    location / {
        root /usr/share/nginx/html;
        try_files $uri $uri/ /index.html;
    }

    # API routing
    location /api {
        proxy_pass http://api-gateway-service:8080;
        proxy_set_header X-Forwarded-Proto https;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    }

    location /auth {
        proxy_pass http://api-gateway-service:8080;
    }
}
```

#### Persistent Storage

- **Database Volumes**: `/db-volumes/user-volume`, `/db-volumes/entity-volume`, etc.
- **Kafka Volume**: `/db-volumes/kafka-volume`
- **Recommendation**: Mount volumes to persistent GCP storage (GCP Persistent Disks) for production

#### Scaling Considerations

For production with higher load:
- Use GCP Cloud SQL for managed PostgreSQL
- Use GCP Pub/Sub instead of Kafka (or use a managed Kafka service)
- Use Container Registry (GCR) and deploy with GKE (Google Kubernetes Engine)
- Add load balancing and auto-scaling policies

---

## 🔧 Getting Started

### Prerequisites

- Docker and Docker Compose
- Git
- GitHub Personal Access Token (for config server)

### Quick Start

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd ConstructFlow
   ```

2. **Configure environment**
   ```bash
   # Create local.env with your GitHub token
   echo "GIT_PASSWORD=<your_token>" > local.env
   ```

3. **Start the application**
   ```bash
   docker-compose up --build
   ```

4. **Access the application**
   - Frontend: http://localhost
   - API Gateway: http://localhost:8680/api
   - Discovery Service: http://localhost:8580

### Development

#### Frontend Development

```bash
cd frontend
npm install
npm run dev  # Starts Vite dev server on port 5173
npm run build  # Production build
npm run lint  # ESLint checks
```

#### Backend Services

Each service can be built independently:

```bash
cd user-service
./gradlew clean build  # Build with tests
./gradlew clean build -x test  # Build without tests
```

---

## 📝 Environment Variables

### Docker Compose (.env / local.env)

| Variable | Description | Example |
|----------|-------------|---------|
| `GIT_PASSWORD` | GitHub PAT for config repository | `github_pat_...` |

### Service Configuration

Services receive configuration from:
1. **Local `application.properties`** (defaults)
2. **Config Service** (Git-based configuration)
3. **Environment Variables** (Docker Compose)
4. **JVM Options** (memory, GC settings)

### Key Service Variables

```yaml
# API Gateway
SPRING_CONFIG_IMPORT: configserver:http://config-service:8080
EUREKA_CLIENT_SERVICEURL_DEFAULTZONE: http://discovery-service:8080/eureka/

# Microservices
SPRING_DATASOURCE_URL: jdbc:postgresql://<db-host>:5432/<db-name>
SPRING_DATASOURCE_USERNAME: myuser
SPRING_DATASOURCE_PASSWORD: mypass
```

---

## 🔐 Security

- **Authentication**: JWT tokens issued by User Service
- **Authorization**: API Gateway validates JWT tokens
- **Database**: PostgreSQL with encrypted passwords
- **Network**: All inter-service communication via internal Docker network
- **Frontend-to-Backend**: HTTPS only in production
- **Secrets**: Environment variables for sensitive data (GitHub token, DB passwords)

---

## 📊 Monitoring

The application includes observability features:

- **Metrics**: Micrometer with Actuator endpoints
- **Distributed Tracing**: Brave integration (optional Zipkin)
- **Logging**: SLF4J with Logback
- **Health Checks**: Spring Boot Actuator (`/actuator/health`)

### Actuator Endpoints

Available on each service at `http://service:8080/actuator/`

- `/health` - Application health
- `/metrics` - Application metrics
- `/env` - Environment configuration
- `/loggers` - Logging configuration

---

## 📚 API Documentation

All API endpoints are prefixed with `/api` and routed through the API Gateway.

### Example Endpoints

- `POST /api/auth/login` - User login
- `GET /api/users/{id}` - Get user details
- `POST /api/timesheets` - Create timesheet
- `GET /api/timesheets?filters` - List timesheets
- `POST /api/payroll/process` - Process payroll

Detailed API documentation can be found in individual service HELP.md files.

---

## 🐛 Troubleshooting

### Services Not Starting

```bash
# Check service logs
docker-compose logs <service-name>

# Common issues:
# 1. Port conflicts - ensure ports 80, 443, 5531-5534, 8580, 8590, 8680, 9092 are available
# 2. Config server not responding - check GIT_PASSWORD is set
# 3. Database connection issues - wait for DB container to be ready
```

### Kafka Connection Issues

- Ensure `kafka:9092` is used for internal (Docker) communication
- Port `9094` is for external access from localhost

### SSL Certificate Issues

- Verify certificate files exist at `/etc/letsencrypt/archive/www.constructflow.space/`
- Check Nginx logs: `docker-compose logs frontend`
- Certificate permissions must be readable by Nginx container

---

## 📄 License

[Add your license information here]

---

## 🤝 Contributing

[Add contribution guidelines here]

---

## 📧 Support

For issues and questions, please open a GitHub issue or contact the development team.

---

**Last Updated**: March 2025 | **Version**: 0.0.1-SNAPSHOT
