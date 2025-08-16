# PayPal Clone - A Microservices-Based Payment System

This project is a fully functional backend clone of a payment platform, built using a modern microservices architecture with **Java** and the **Spring Boot** framework. The system handles user registration, secure login, wallet management, financial transactions, and robust, asynchronous notifications using **Apache Kafka**.

This project showcases a deep understanding of distributed systems, backend development, and system design principles.

## Core Technologies Used
* **Java 17**
* **Spring Boot** & **Spring Cloud**
* **Spring Security** (for password hashing)
* **Spring Data JPA (Hibernate)**
* **Maven** for dependency management
* **MySQL** for database storage
* **Apache Kafka** for asynchronous messaging
* **Netflix Eureka** for Service Discovery
* **Spring Cloud Gateway** for API Gateway
* **Docker** for running Kafka and Zookeeper

## High-Level Architecture
The system is designed following a classic microservices pattern. An **API Gateway** provides a single, secure entry point for all client requests. It communicates with a **Service Discovery** server (Eureka) to dynamically route requests to the appropriate downstream microservice. Asynchronous communication for notifications is handled by a **Kafka** message broker, ensuring services are fully decoupled and resilient.

## Service Breakdown
* **Service Discovery (`service-discovery`):** A Eureka server that acts as a registry for all other microservices.
* **API Gateway (`api-gateway`):** The single entry point for all external traffic. It handles request routing and CORS.
* **User Service (`user-service`):** Manages user registration with hashed passwords and secure login.
* **Wallet Service (`wallet-service`):** Responsible for creating and managing user wallets and their balances.
* **Transaction Service (`transaction-service`):** Orchestrates financial transactions by communicating with the Wallet Service and publishing a notification event to Kafka upon completion.
* **Notification Service (`notification-service`):** Consumes events from a Kafka topic to process and log notifications asynchronously.

---

## Getting Started

### Prerequisites
* Java 17 (or newer)
* Apache Maven
* MySQL Server
* Docker Desktop (to run Kafka and Zookeeper)
* An API testing tool (like Postman)

### How to Run
1.  **Clone the repository:**
    ```bash
    git clone https://github.com/uzairahmad19/Paypal-clone.git
    ```
2.  **Start Kafka and Zookeeper:**
    Make sure Docker Desktop is running. Open a terminal in the project's root directory and run:
    ```bash
    docker-compose up
    ```
3.  **Set up Databases:**
    Log in to your MySQL server and create a separate database for each service:
    ```sql
    CREATE DATABASE paypal_users;
    CREATE DATABASE paypal_wallets;
    CREATE DATABASE paypal_transactions;
    ```
4.  **Update Database Credentials:**
    In the `application.yml` file for `user-service`, `wallet-service`, and `transaction-service`, update the `spring.datasource.username` and `spring.datasource.password` fields to match your local MySQL credentials.
5.  **Run the Services:**
    The services must be started in a specific order. Open a new terminal for each service.
    * Start the **Service Discovery**: Run `ServiceDiscoveryApplication.java`.
    * Start the **User Service**: Run `UserServiceApplication.java`.
    * Start the **Wallet Service**: Run `WalletServiceApplication.java`.
    * Start the **Transaction Service**: Run `TransactionServiceApplication.java`.
    * Start the **Notification Service**: Run `NotificationServiceApplication.java`.
    * Finally, start the **API Gateway**: Run `ApiGatewayApplication.java`.

    Verify that all services are registered by visiting the Eureka dashboard at **http://localhost:8761**.

---

## API Documentation
All requests should be sent to the **API Gateway** at `http://localhost:8080`.

### 1. User Service
**Base URL:** `/api/users`

* **Register a New User**
    * **Endpoint:** `POST /api/users/register`
    * **Description:** Creates a new user with a securely hashed password.
    * **Request Body:**
        ```json
        {
            "name": "Your Name",
            "email": "your.email@example.com",
            "password": "yourpassword"
        }
        ```
    * **Success Response (200 OK):** The newly created user object.

* **Log In a User**
    * **Endpoint:** `POST /api/users/login`
    * **Description:** Authenticates a user based on their email and password.
    * **Request Body:**
        ```json
        {
            "email": "your.email@example.com",
            "password": "yourpassword"
        }
        ```
    * **Success Response (200 OK):** The full user object.
    * **Failure Response (401 Unauthorized):** "Invalid credentials".

### 2. Wallet Service
**Base URL:** `/api/wallets`

* **Create a Wallet for a User**
    * **Endpoint:** `POST /api/wallets`
    * **Description:** Creates a new wallet with a zero balance, linked to a `userId`.
    * **Request Body:**
        ```json
        {
            "userId": 1
        }
        ```
    * **Success Response (200 OK):** The newly created wallet object.

* **Credit a Wallet (Add Funds)**
    * **Endpoint:** `POST /api/wallets/credit`
    * **Description:** Adds funds to a user's wallet.
    * **Request Body:**
        ```json
        {
            "userId": 1,
            "amount": 100.00
        }
        ```
    * **Success Response (200 OK):** An empty body.

* **Get Wallet by User ID**
    * **Endpoint:** `GET /api/wallets/user/{userId}`
    * **Description:** Retrieves the wallet details for a specific user.
    * **Success Response (200 OK):** The wallet object, including the current balance.

### 3. Transaction Service
**Base URL:** `/api/transactions`

* **Create a Transaction**
    * **Endpoint:** `POST /api/transactions`
    * **Description:** Creates and executes a transaction, moving funds from a sender to a recipient. Publishes a notification event to Kafka upon completion.
    * **Request Body:**
        ```json
        {
            "senderId": 1,
            "recipientId": 2,
            "amount": 25.00
        }
        ```
    * **Success Response (200 OK):** The completed transaction record.
