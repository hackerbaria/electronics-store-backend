# 🛍️ Electronics Store API

## 🚀 Getting Started

### 1. Requirements
- Java 17+
- Maven
- Docker (optional)

### 2. Running the App (Local)
```bash
mvn spring-boot:run
```

App will be accessible at: http://localhost:8080

### 3. Running all test cases
```bash
mvn test
```

### 4. Access Swagger UI
```bash
http://localhost:8080/swagger-ui/index.html
```

## 📦 API Overview
All endpoints are prefixed with `/v1/api`.

### Admin APIs

| Method | Endpoint                    | Description                   |
|--------|-----------------------------|-------------------------------|
| POST   | /v1/api/admin/products      | Create product                |
| DELETE | /v1/api/admin/products/{id} | Remove product                |
| POST   | /v1/api/admin/deals         | Add a discount deal           |
| GET    | /v1/api/admin/products      | Paginated product list        |
| POST   | /v1/api/admin/customers     | Add a new customer (optional) |

### Customer APIs

| Method | Endpoint                                      | Description                         |
|--------|-----------------------------------------------|-------------------------------------|
| POST   | /v1/api/customers/{customerId}/basket/items   | Add product with quantity to basket |
| DELETE | /v1/api/customers/{customerId}/basket/items   | Remove product from basket          |
| GET    | /v1/api/customers/{customerId}/basket/receipt | Calculate receipt with deals        |
| GET    | /v1/api/customer/products                     | Filterable, paginated products      |

## 🛡️ Concurrency and Performance

- Safe concurrent usage ensured via synchronized operations and atomic structures.
- Transactional rollback mechanisms in place to avoid partial updates.
- Read-optimized access for catalog browsing.

## 📁 Project Structure
```
src/
├── main/
│   ├── java/com/altech/store
│   │   ├── controller
│   │   ├── service
│   │   ├── model
│   │   ├── dto
│   │   └── repository
│   └── resources/
├── test/
│   └── java/com/altech/store
└── Dockerfile
```

## 📝 Notes

This project uses an in-memory H2 database for demonstration. It can be swapped with PostgreSQL or any other DB.


## 📦 Deployment

Deployment, CI, and metrics are out of scope per the original requirements.

## 📎 License

MIT License
