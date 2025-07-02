# Distributed Demo System

**A School Project Demonstrating Distributed Systems Concepts**

This project is an academic demonstration of distributed systems architecture developed as part of coursework to showcase key concepts in distributed computing. The system implements an order management solution using microservices architecture with Spring Boot (Java) and FastAPI (Python), demonstrating inter-service communication via RabbitMQ message queuing and data persistence with PostgreSQL. 

**Note:** This is a learning project created for educational purposes to demonstrate distributed systems patterns rather than a production-ready application.

## 📦 Project Components
The system demonstrates distributed architecture through the following components:

| Service Name     | Technology       | Educational Purpose                             |
| ---------------- | ---------------- | ----------------------------------------------- |
| order-management | Spring Boot      | Order and product management microservice       |
| product-service  | FastAPI          | Product catalog API and consumer service        |
| postgres         | PostgreSQL       | Shared database demonstrating data consistency  |
| rabbitmq         | RabbitMQ         | Message queuing for asynchronous communication |
| traefik          | Traefik          | Reverse proxy and load balancing                |
| visualizer       | Swarm visualizer | Cluster visualization tool                      |

---
## ➤ Work Queues Pattern (Learning Concept)
The Work Queues pattern demonstrates how to distribute time-consuming or intensive tasks among multiple workers (consumers) to balance load. RabbitMQ acts as a message distributor: a producer (publisher) sends a message to a queue, and one or more consumers (workers) handle each message processing.

## ➤ Implementation in This Demo
The Spring Boot API publishes a message to the `product_queue` whenever an order is created, modified, or deleted. A Python worker service consumes these messages via RabbitMQ to simulate background processing for updating global stock in the FastAPI service.

## ➤ Benefits of RabbitMQ (Academic Learning Points)
- **Asynchronous Processing**: The FastAPI remains responsive even during heavy processing
- **Reliability**: Messages are persistent (delivery_mode=2), so they survive worker failures  
- **Scalability**: Multiple workers can consume from the same queue to distribute load
- **Decoupling**: The API doesn't depend on downstream processing, allowing independent evolution

## 🎓 Key Learning Features

### ➤ Microservices Architecture Demonstration
- CRUD operations for Products and Orders (Create, Read, Update, Delete)
- RabbitMQ message publishing for each operation (create, update, delete)
- Inter-service communication patterns

### ➤ Python Message Consumer Service
- Consumes messages from the `product_queue`
- Simulates processing (logging and system updates)
- Demonstrates asynchronous message handling

## 🚀 Quick Start (Installation Instructions)
### 🧰 Prerequisites
* `Docker`
* `Docker Compose`
* `Docker Swarm` (for production deployment demonstration)
* (Optional for development) Java 17+, Python 3.10+

---
## 🐳 Running with Docker
### Using Docker Compose (Development Mode)
```bash
docker-compose up --build
```
This compose file starts the following services:

* order-management (Spring Boot)
* product-service (FastAPI)
* postgres
* rabbitmq

---
## ⚙️ Deployment with Docker Swarm
### 1. Initialize Swarm
```bash
docker swarm init
```
### 2. Deploy the stack
```bash
docker stack deploy -c docker-compose.yml ordersystem
```
### 3. Access Interfaces
| Service                | URL                                       | Credentials                     |
| ---------------------- | ----------------------------------------- | ------------------------------- |
| Traefik Dashboard      | http://localhost:8080                     |                                 |
| Spring Boot Swagger    | http://localhost:81/swagger-ui/index.html |                                 |
| FastAPI Swagger        | http://localhost:81/docs                  |                                 |
| Swarm Visualizer       | http://localhost:8085                     |                                 |
| RabbitMQ Management UI | http://localhost:15672                    | user: guest,<br>password: guest |

---
## 🔁 API Endpoints
### Spring Boot (Java)
* `GET /commandes` – List all orders
* `GET /commandes/{id}` - Get order details
* `POST /commandes` – Create a new order
* `PUT /commandes/{id}` - Update an existing order
* `DELETE /commandes/{id}` - Delete an order

* `GET /commandes-produits` – Get order product details
* `GET /commandes-produits/{id}` - Get specific order product details
* `POST /commandes-produits` – Create a new order product
* `PUT /commandes-produits/{id}` - Update an existing order product
* `DELETE /commandes-produits/{id}` - Delete an order product

---
## FastAPI (Python)
* `GET /produits/` – List all products
* `GET /produits/{id}` - Get product details
* `POST /produits/` – Add a new product
* `PUT /produits/{id}` - Edit an existing product
* `DELETE /produits/{id}` - Delete a product

---
## 📌 Environment Variables
The following variables can be defined in the .env file or directly in docker-compose.yml:

| Variable                              | Description                  |
| ------------------------------------- | ---------------------------- |
| SPRING_DATASOURCE_URL                 | JDBC URL to PostgreSQL      |
| SPRING_DATASOURCE_USERNAME / PASSWORD | Database credentials         |
| SPRING_RABBITMQ_HOST / PORT           | RabbitMQ address             |
| POSTGRES_USER / PASSWORD              | PostgreSQL connection        |

---
## ⚠️ Important Notes
RabbitMQ uses the default credentials `guest/guest`.

The FastAPI service depends on a consumer (`consumer.py`) that must be active to receive messages.

Traefik handles routing and reverse proxy management.

Both services use the same database for order management.

---
## RabbitMQ Usage Details

Each time a product is:
- Created: a message with action CREATED is sent
- Updated: message with action UPDATED
- Deleted: message with action DELETED

Example JSON message published:
```json
{
  "action": "UPDATED",
  "produit_id": 5,
  "quantite": 12,
  "commande_id": 0
}
```

The Python consumer service listens to the `product_queue`, extracts the information, and performs simulated processing with logging or business actions.

## 🤝 Contributing to this Academic Project

This is an educational project, but contributions that enhance the learning experience are welcome:

### How to Contribute
1. **Fork the Repository**: Create your own fork to experiment with changes
2. **Create a Feature Branch**: 
   ```bash
   git checkout -b feature/learning-enhancement
   ```
3. **Focus on Educational Value**: Ensure changes help demonstrate distributed systems concepts
4. **Test Your Changes**: Verify that the system still demonstrates the intended patterns
5. **Document Learning Outcomes**: Explain how your changes enhance the educational value
6. **Submit a Pull Request**: Include a clear description of the educational benefits

### Areas for Educational Enhancement
- Adding more comprehensive logging to show message flow
- Implementing additional distributed patterns (Circuit Breaker, Saga, etc.)
- Adding monitoring and observability features
- Enhancing error handling demonstrations
- Adding more detailed documentation of architectural decisions

### Code Style Guidelines
- Follow existing code formatting patterns
- Add comments explaining distributed systems concepts
- Ensure all code examples in documentation use proper markdown formatting
- Include educational comments in complex distributed logic

## 🔮 Future Learning Enhancements

- Add storage or database for tracing consumed message actions
- Add other event types (e.g., "product out of stock", "low stock alert")
- Integration monitoring with Prometheus + Grafana
- Implementation of additional distributed patterns
- Enhanced error handling and resilience patterns
- Performance testing and load balancing demonstrations