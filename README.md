# PayPal Clone - Backend

This project is a backend clone of PayPal, built with a microservices architecture using Spring Boot. It demonstrates a robust, scalable, and event-driven system for handling users, wallets, transactions, and notifications.

## Features

* **User Management:** User registration, login, and profile management.
* **Wallet System:** Each user has a wallet with a balance.
* **Transactions:** Users can send and receive money.
* **Money Requests:** Users can request money from other users.
* **Real-time Notifications:** Users receive notifications for various events like transactions and money requests.
* **Microservices Architecture:** The application is divided into several independent services for better scalability and maintainability.
* **Containerized:** The entire application is containerized using Docker for easy setup and deployment.

## Architecture

The application is composed of the following microservices:

* **API Gateway:** The single entry point for all client requests. It routes requests to the appropriate microservice and handles cross-cutting concerns like CORS.
* **Service Discovery:** Uses Netflix Eureka for service registration and discovery, allowing services to find and communicate with each other dynamically.
* **User Service:** Manages user-related operations, including registration, authentication, and money requests.
* **Wallet Service:** Manages user wallets, including creating wallets, debiting, and adding money.
* **Transaction Service:** Handles all transactions between users.
* **Notification Service:** Manages and sends notifications to users in real-time.

The services communicate with each other both synchronously via REST APIs (with the help of the service discovery) and asynchronously using Apache Kafka.

## Technologies Used

* **Java 17**
* **Spring Boot 3**
* **Spring Cloud:**
    * Spring Cloud Gateway
    * Spring Cloud Netflix Eureka
* **Spring Data JPA (Hibernate)**
* **MySQL** (for User, Wallet, and Transaction data)
* **H2 Database** (for Notification data)
* **Apache Kafka** (for asynchronous communication)
* **Docker & Docker Compose**
* **Maven**

## Getting Started

### Prerequisites

* Java 17 or later
* Docker and Docker Compose
* Maven
* A MySQL database instance

### Installation & Running the application

1.  **Clone the repository:**
    ```bash
    git clone <repository-url>
    cd <repository-name>
    ```

2.  **Configure environment variables:**
    Create a `.env` file in the `backend` directory with the following content:
    ```
    DB_USER=your_mysql_username
    DB_PASSWORD=your_mysql_password
    ```

3.  **Build and run the application using Docker Compose:**
    ```bash
    docker-compose up --build
    ```
    This will build the Docker images for each microservice and start all the containers.

## API Endpoints

The API Gateway is the single entry point for all requests. It is accessible at `http://localhost:8080`.

### User Service (`/api/users`, `/api/requests`)

* `POST /api/users/register`: Register a new user.
* `POST /api/users/login`: Log in a user.
* `GET /api/users/me`: Get the authenticated user's details.
* `POST /api/requests/create`: Create a new money request.
* `PUT /api/requests/{id}/approve`: Approve a money request.
* `PUT /api/requests/{id}/reject`: Reject a money request.

### Wallet Service (`/api/wallets`)

* `GET /api/wallets/user/{userId}`: Get the wallet for a user.
* `POST /api/wallets/add`: Add money to a wallet.
* `POST /api/wallets/debit`: Debit money from a wallet.

### Transaction Service (`/api/transactions`)

* `POST /api/transactions`: Create a new transaction.
* `GET /api/transactions/user/{userId}`: Get all transactions for a user.

### Notification Service (`/api/notifications`)

* `GET /api/notifications/user?id={userId}`: Get all notifications for a user.
* `PUT /api/notifications/{id}/read`: Mark a notification as read.

## Environment Variables

The `docker-compose.yml` file uses environment variables for database credentials. You need to create a `.env` file in the `backend` directory:

* `DB_USER`: Your MySQL database username.
* `DB_PASSWORD`: Your MySQL database password.
