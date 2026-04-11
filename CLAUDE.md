# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

YST is a management system with a Spring Boot 3 backend and Vue 3 frontend. It implements a Ruoyi-style RBAC (Role-Based Access Control) system with data scope permissions.

## Architecture

### Backend (yst-server/)

Spring Boot 3.5.9 + Java 17 + MyBatis-Plus + MySQL + Redis

**Key architectural patterns:**

1. **Layered Architecture**: controller → service → impl → mapper → entity
2. **RBAC Security**: JWT-based authentication with Spring Security
3. **Data Scope**: Custom permission annotations control data visibility
4. **API Response**: Standardized `ApiResponse<T>` wrapper for all responses
5. **Entity Naming**: All entities use `Sys` prefix (e.g., `SysUser`, `SysRole`)

**Key packages:**
- `com.yst.security` - JWT, Spring Security config, permission evaluation
- `com.yst.common` - Global exception handler, API response wrapper
- `com.yst.entity` - MyBatis-Plus entities with `@TableName`
- `com.yst.vo` - Request/response DTOs for controllers
- `com.yst.mapper` - MyBatis-Plus mappers

**Security annotations:**
- `@PreAuthorize("@ss.hasPermi('system:user:list')")` - Method-level permission
- `@DataScope` - Data scope filtering (implementation in progress)

### Frontend (yst-ui/)

Vue 3 + TypeScript + Vite + Element Plus + Pinia + Vue Router

**Key architectural patterns:**

1. **Single-SPA ready**: Qiankun integration for micro-frontends
2. **Permission Directives**: Custom `v-hasPermi` directive for button-level permissions
3. **API Layer**: Axios-based HTTP client with request/response interceptors
4. **Store Pattern**: Pinia stores for auth state management

**Key directories:**
- `src/api/` - API modules (http.ts, user.ts, system.ts)
- `src/views/` - Page components (UserView.vue, RoleView.vue, etc.)
- `src/stores/` - Pinia stores (auth.ts)
- `src/directives/` - Custom Vue directives (hasPermi.ts)
- `src/router/` - Vue Router configuration

## Common Commands

### Backend

```bash
cd yst-server

# Run with Maven
./mvnw spring-boot:run

# Build
./mvnw clean package

# Run tests
./mvnw test

# Run single test class
./mvnw test -Dtest=UserServiceTest

# Run single test method
./mvnw test -Dtest=UserServiceTest#testMethod

# Checkstyle
./mvnw checkstyle:check

# SpotBugs
./mvnw spotbugs:check

# SonarQube
./mvnw sonar:sonar -Dsonar.token=<token>
```

### Frontend

```bash
cd yst-ui

# Development server
npm run dev

# Build for production
npm run build

# Preview production build
npm run preview

# Lint
npm run lint
npm run lint:fix

# Format
npm run format
npm run format:write

# Check all (lint + format)
npm run check
```

## Database & Infrastructure

- **MySQL**: Database for application data (configured in `application.yml`)
- **Redis**: Used for caching and session storage
- **Database initialization**: `yst-server/src/main/resources/sql/init.sql`

## API Documentation

When the backend is running, API docs are available at:
- Swagger UI: http://localhost:38080/swagger-ui.html
- Knife4j: http://localhost:38080/doc.html

## Code Quality Tools

The project enforces code quality through Maven plugins:

1. **Checkstyle**: Validates code style (config in `checkstyle/checkstyle.xml`)
2. **SpotBugs**: Static analysis for bug patterns
3. **SonarQube**: Code quality metrics and coverage

All checks run during the build lifecycle - fix issues before committing.

## Key Dependencies

**Backend:**
- Spring Boot 3.5.9 (Web, Security, Validation, Data Redis)
- MyBatis-Plus 3.5.15 (ORM)
- JJWT 0.11.5 (JWT handling)
- SpringDoc/Knife4j (API docs)
- EasyExcel 3.3.3 (Excel export)

**Frontend:**
- Vue 3.5 + TypeScript
- Element Plus 2.11.7 (UI component library)
- Vite 5.4 (build tool)
- Pinia 2.1.7 (state management)
- Axios (HTTP client)
- Qiankun 2.10 (micro-frontend framework)
- VXE-Table 4.13 (data table component)