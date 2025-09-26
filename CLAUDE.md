# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a Spring Cloud microservices-based family and classroom reward management system (小红花家庭奖励系统) built with Java 17, Spring Boot 3.x, and Vue 3. The system supports multi-child management, reward mechanisms, and educational scenarios.

## Development Commands

### Backend (Maven Multi-module)

```bash
# Build all modules
mvn clean package -DskipTests

# Run specific service (from service directory)
cd red-flower-user && mvn spring-boot:run
cd red-flower-flower && mvn spring-boot:run
cd red-flower-log && mvn spring-boot:run

# Run tests
mvn test

# Skip tests during build
mvn clean package -DskipTests
```

### Frontend (Vue 3)

```bash
cd red-flower-web

# Install dependencies
npm install

# Development server
npm run serve

# Build for production
npm run build

# Lint code
npm run lint
```

### Docker Deployment

```bash
# Start all services with Docker Compose
docker-compose up -d

# View service status
docker-compose ps

# Stop all services
docker-compose down

# Build and start
docker-compose build && docker-compose up -d
```

### Database

```bash
# Initialize database
mysql -u root -p < init.sql
```

#### Default User Accounts

All default accounts use the same password: **admin123**

| Username | Password | Role | Description |
|----------|----------|------|-------------|
| admin | admin123 | ADMIN | 系统管理员 |
| parent1 | admin123 | PARENT | 爸爸 |
| parent2 | admin123 | PARENT | 妈妈 |
| child1 | admin123 | CHILD | 小明 |
| child2 | admin123 | CHILD | 小红 |

**注意**: 生产环境请务必修改默认密码！

## Architecture

### Microservices Structure

- **red-flower-common**: Shared utilities, entities, enums, and response models
- **red-flower-user**: User management, authentication (JWT), family management (Port: 8081)
- **red-flower-flower**: Red flower currency system, transactions, account management (Port: 8082)
- **red-flower-log**: Activity logging, milestone system, growth reports (Port: 8086)
- **red-flower-gateway**: API Gateway (Port: 8080) - *Not yet implemented*
- **red-flower-task**: Task management - *Not yet implemented*
- **red-flower-reward**: Reward system - *Not yet implemented*
- **red-flower-class**: Class/school management - *Not yet implemented*
- **red-flower-message**: Messaging service - *Not yet implemented*
- **red-flower-web**: Vue 3 frontend application (Port: 80 in Docker, 8080 in dev)

### Technology Stack

**Backend:**
- Java 17 + Spring Boot 3.1.5 + Spring Cloud 2023.0.0
- MySQL 8.0 + MyBatis Plus + Redis 7.x
- RocketMQ 5.x + JWT Authentication
- Knife4j for API documentation

**Frontend:**
- Vue 3 + TypeScript + Element Plus
- Pinia (state management) + Vue Router + Axios
- ECharts for data visualization

### Key Design Patterns

**Common Module Structure:**
- `Result<T>`: Unified API response wrapper with success/error methods
- `BaseEntity`: Common entity fields (id, createTime, updateTime, deleted)
- Enums: UserRole, ActivityType, TaskType, TaskScope
- Exception handling with `BusinessException`

**Service Configuration:**
- All services run independently without service discovery
- Database connections via Druid connection pool
- Redis for caching with Lettuce client
- RocketMQ for asynchronous messaging
- MyBatis Plus with logical deletion (deleted=0/1)

**API Documentation:**
- Knife4j (Swagger) available at `/doc.html` for each service
- User service: http://localhost:8081/doc.html
- Flower service: http://localhost:8082/doc.html
- Log service: http://localhost:8086/doc.html

## Development Notes

### Service Ports
- Gateway: 8080 (not implemented)
- User Service: 8081
- Flower Service: 8082
- Log Service: 8086
- Frontend Dev: 8080 (npm run serve)
- MySQL: 3306
- Redis: 6379
- RocketMQ NameServer: 9876

### Configuration Files
Services use `application.yml` with profiles (dev/docker). Key configurations:
- Database: MySQL with Druid connection pool
- Cache: Redis with Lettuce
- Messaging: RocketMQ with named groups per service

### Current Implementation Status
- ✅ **Completed**: Common module, User service, Flower service, Log service, Frontend basic features
- 🚧 **In Progress**: Frontend enhancements
- ❌ **Not Started**: Task service, Reward service, Class service, Message service, Gateway service

### Testing
The project currently focuses on integration testing. Run services individually for testing:
1. Start infrastructure: MySQL, Redis, RocketMQ
2. Start backend services independently
3. Start frontend with `npm run serve`
4. Access frontend at http://localhost:8080

### Database Schema
Uses MySQL with comprehensive schema supporting:
- Multi-tenant family/school structure
- User management with role-based permissions
- Red flower currency system with transaction logging
- Activity logging and milestone tracking
- Prepared for task and reward management