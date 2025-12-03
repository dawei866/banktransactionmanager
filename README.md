# Bank Transaction Manager

A comprehensive banking transaction management application built with Spring Boot that allows users to record, view, and manage financial transactions efficiently.

## Table of Contents
- [Features](#features)
- [Technologies Used](#technologies-used)
- [Getting Started](#getting-started)
  - [Prerequisites](#prerequisites)
  - [Local Development](#local-development)
  - [Docker Deployment](#docker-deployment)
- [API Documentation](#api-documentation)
- [Application Structure](#application-structure)
- [Testing](#testing)
- [Performance Considerations](#performance-considerations)
- [Security](#security)
- [Contributing](#contributing)
- [License](#license)

## Features

### Core Functionality
- Create, read, update, and delete transactions
- Support for multiple transaction types (Deposit, Withdrawal, Transfer, Payment, Fee)
- Account-specific transaction history and balance calculation
- Advanced search and filtering capabilities
- Pagination for efficient data retrieval

### User Interface
- Intuitive web interface with responsive design
- Dashboard with transaction statistics and balance overview
- Form-based transaction management
- Data visualization with transaction type indicators

### Technical Features
- RESTful API with comprehensive documentation
- In-memory database (H2) for development and testing
- Caching mechanism for improved performance
- Robust error handling and validation
- Unit and integration testing
- Docker containerization support
- Health monitoring endpoints

## Technologies Used

- **Backend**: Java 17, Spring Boot 3.1.x, Spring Data JPA, Spring MVC
- **Database**: H2 in-memory database
- **Caching**: Ehcache 3.x
- **API Documentation**: SpringDoc OpenAPI 3.0 (Swagger)
- **Testing**: JUnit 5, Mockito, Spring Boot Test
- **Build Tool**: Maven
- **Containerization**: Docker, Docker Compose
- **Frontend**: Thymeleaf, Bootstrap 5, HTML5, CSS3, JavaScript

## Getting Started

### Prerequisites

- JDK 17 or higher
- Maven 3.8.x or higher
- Docker and Docker Compose (for containerized deployment)
- IDE of your choice (IntelliJ IDEA, Eclipse, VS Code)

### Local Development

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd bank-transaction-manager
   ```

2. **Build the project**
   ```bash
   mvn clean package
   ```

3. **Run the application**
   ```bash
   mvn spring-boot:run
   ```

4. **Access the application**
   - Web Interface: http://localhost:8080/transactions
   - API Documentation: http://localhost:8080/swagger-ui.html
   - H2 Console: http://localhost:8080/h2-console
     - JDBC URL: jdbc:h2:mem:bankdb
     - Username: sa
     - Password: password

### Docker Deployment

1. **Build and run with Docker Compose**
   ```bash
   docker-compose up -d
   ```

2. **Verify the deployment**
   - The application will be available at http://localhost:8080
   - Check container status: `docker-compose ps`
   - View logs: `docker-compose logs -f`

3. **Stopping the application**
   ```bash
   docker-compose down
   ```

### Kubernetes Deployment

The application can also be deployed to a Kubernetes cluster using the provided manifests:

1. **Prerequisites**
   - Kubernetes cluster (minikube, k3s, or cloud provider cluster)
   - kubectl CLI configured
   - Docker images built and available in cluster registry

2. **Deployment Steps**
   ```bash
   # Apply all Kubernetes manifests
   kubectl apply -k k8s/
   
   # Or apply individual manifests
   kubectl apply -f k8s/namespace.yaml
   kubectl apply -f k8s/configmap.yaml
   kubectl apply -f k8s/secret.yaml
   kubectl apply -f k8s/deployment.yaml
   kubectl apply -f k8s/hpa.yaml
   kubectl apply -f k8s/ingress.yaml
   ```

3. **Verify the deployment**
   ```bash
   # Check pods status
   kubectl get pods -n bank-transaction-manager
   
   # Check service status
   kubectl get svc -n bank-transaction-manager
   
   # View logs
   kubectl logs -n bank-transaction-manager -l app=bank-transaction-manager
   ```

4. **Accessing the application**
   - Through service: `kubectl port-forward svc/bank-transaction-manager-service 8080:8080 -n bank-transaction-manager`
   - Through ingress (if configured): http://bank-transaction-manager.local

5. **Scaling the application**
   ```bash
   # Scale manually
   kubectl scale deployment bank-transaction-manager -n bank-transaction-manager --replicas=5
   
   # Check HPA status
   kubectl get hpa -n bank-transaction-manager
   ```

For detailed Kubernetes deployment instructions, see [Kubernetes Deployment Guide](README-k8s.md).

## API Documentation

The application provides a comprehensive RESTful API for transaction management. API documentation is available through Swagger UI at http://localhost:8080/swagger-ui.html when the application is running.

### Key API Endpoints

- **GET /api/transactions** - Get all transactions with pagination
- **POST /api/transactions** - Create a new transaction
- **GET /api/transactions/{id}** - Get transaction by ID
- **PUT /api/transactions/{id}** - Update an existing transaction
- **DELETE /api/transactions/{id}** - Delete a transaction
- **GET /api/transactions/account/{accountNumber}** - Get transactions by account number
- **GET /api/transactions/account/{accountNumber}/balance** - Get account balance

## Application Structure

```
src/main/java/com/example/banktransactionmanager/
├── config/             # Application configuration
├── controller/         # REST and Web controllers
├── dto/                # Data Transfer Objects
├── exception/          # Custom exceptions and handlers
├── model/              # JPA entities
├── repository/         # Data access layer
├── service/            # Business logic layer
│   └── impl/           # Service implementations
└── BankTransactionManagerApplication.java # Main application class

src/main/resources/
├── static/             # Static resources
├── templates/          # Thymeleaf templates
│   ├── layout.html     # Main layout template
│   └── transactions/   # Transaction-related templates
├── application.properties         # Default configuration
├── application-docker.properties  # Docker-specific configuration
└── ehcache.xml                    # Cache configuration
```

## Testing

The application includes comprehensive tests to ensure functionality and reliability:

### Running Tests

```bash
mvn test
```

### Test Coverage
- Unit tests for Service layer
- Integration tests for Repository layer
- Controller tests with MockMvc
- Performance tests for load and stress testing

### Performance Testing

The project includes performance tests to evaluate the system under load:

- **JMeter Tests**: GUI-based load testing scripts

See [Performance Test Documentation](src/test/performance/README.md) for details on how to run these tests.

## Performance Considerations

- **Caching**: Implemented with Ehcache to cache frequently accessed data
- **Pagination**: All list endpoints use pagination to limit memory usage
- **Database Indexes**: Properly indexed entity fields for faster queries
- **Container Optimization**: Docker image uses multi-stage build and JVM container awareness

## Security

### Current Security Measures
- Input validation and sanitization
- Proper error handling that doesn't expose sensitive information
- HTTPS support configuration
- SQL injection prevention through JPA

### Production Recommendations
- Implement proper authentication and authorization
- Configure HTTPS with valid certificates
- Set up rate limiting for API endpoints
- Implement audit logging
- Regular security updates

## Contributing

Contributions are welcome! Please follow these steps:

1. Fork the repository
2. Create a new branch (`git checkout -b feature/amazing-feature`)
3. Make your changes
4. Run tests to ensure everything works
5. Commit your changes (`git commit -m 'Add some amazing feature'`)
6. Push to the branch (`git push origin feature/amazing-feature`)
7. Open a Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

---

Built with ❤️ using Spring Boot and modern web technologies.
