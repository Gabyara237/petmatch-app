#  PetMatch – Smart Adoption Platform (Backend API)

Welcome to the **PetMatch**.
PetMatch is a full-stack web platform that connects people who want to adopt pets with shelters or responsible owners. This solution combines a robust backend with Java + Spring Boot and a frontend (in progress) with React.

## About

This backend provides a secure, tested, and scalable foundation for:

- User registration and login (JWT-based authentication)
- Posting pets for adoption
- Requesting to adopt pets
- Managing pet and request statuses
- Unit test coverage for all core services

## Authentication

Authentication is handled using **JWT**. Users must login to receive a token, which is required to access protected routes.

## Features Implemented

### Users
- Registration with encrypted passwords
- Login with JWT generation
- Role support via `Role` enum (`USER`, `ADMIN`, `SHELTER`) [extendable]
- Authentication and authorization using security filters
- Exception handling (404s, unauthorized access, duplicate requests, etc.)

### Pets
- Full CRUD: create, update, delete, and list pets
- Fetch pets by user
- Change pet status (available, adopted, etc.)

### Adoption Requests
- Submit authenticated adoption requests
- List sent and received requests
- Approve or reject requests
- Automatically update pet status upon approval

### Unit Testing
Full test coverage of all service classes using **JUnit 5** and **Mockito**:
- `UserServiceTest`
- `AuthServiceTest`
- `PetServiceTest`
- `AdoptionRequestServiceTest`

### Testing

Tests are written using **JUnit 5** and **Mockito**. To run them:

```bash
./mvnw test
```

You’ll see green if all services pass!

## Getting Started

### Prerequisites

- Java 17+
- Maven 3.8+
- PostgreSQL or H2 (dev)
- Spring Boot 3.x

### Steps

1. Clone the repo

```bash
git clone https://github.com/yourusername/petmatch-backend.git
cd petmatch-backend
```

2. Configure your `.env` or `application.properties`:

```properties
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/petmatch
SPRING_DATASOURCE_USERNAME=your_user
SPRING_DATASOURCE_PASSWORD=your_password
JWT_SECRET=super_secret_key
```

3. Run the project

```bash
./mvnw spring-boot:run
```


## API Endpoints Overview

| Method | Endpoint                     | Description                                 |
|--------|------------------------------|---------------------------------------------|
| POST   | `/api/users/signup`          | Register new user                           |
| POST   | `/api/auth/login`            | Authenticate user and return JWT token      |
| GET    | `/api/pets`                  | List all pets                               |
| POST   | `/api/pets`                  | Create pet (logged-in user)                 |
| PUT    | `/api/pets/{id}`             | Update pet details                          |
| DELETE | `/api/pets/{id}`             | Delete pet                                  |
| POST   | `/api/adoption-requests`     | Create adoption request                     |
| GET    | `/api/adoption-requests/user`| View my adoption requests                   |
| GET    | `/api/adoption-requests/mine`| View requests for my pets                   |
| PUT    | `/api/adoption-requests/{id}/approve` | Approve request & reject others     |

(Coming soon: filters, roles, notifications)


## Project Structure

```
petmatch-app/
├── petmatch-backend/
│   ├── src/main/java/com/petmatch/
│   │   ├── model/                  → Entities (User, Pet, Request, Enums)
│   │   ├── dto/                    → Data Transfer Objects (DTO classes for requests and responses)
│   │   ├── repository/             → Spring Data JPA repositories
│   │   ├── service/                → Business logic
│   │   ├── security/               → JWT filters and utilities
│   │   ├── config/                 → Security configuration
│   │   └── controller/             → REST Controllers
│   ├── src/test/java/com/petmatch/
│   │   └── service/                → Unit testing with JUnit 5 and Mockito
```

## Design Philosophy

This backend follows:

- SOLID principles
- Layered architecture
- Exception-based validation
- Fully tested service layer
- Mocked dependencies for unit tests
- Stateless security via JWT

## Future Enhancements

- Notifications system for approved/rejected requests
- Filter pets by type, age, and status
- Role-based dashboards (Admin / Shelter)
- Frontend development in React with protected routes, forms, and custom dashboards.
- Integration of artificial intelligence to recommend pets to users based on their adoption preferences.


## Contributions

Open to ideas, suggestions, and pull requests!  
Let's build something beautiful
