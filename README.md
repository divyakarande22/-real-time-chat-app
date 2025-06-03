# Real-Time Chat Application

A full-stack real-time chat application built with Spring Boot and React, featuring WhatsApp-like UI and real-time messaging capabilities.

## Features

### Backend (Spring Boot)
- JWT Authentication & Authorization
- Real-time messaging using WebSocket
- File upload and storage
- User management
- Chat room management
- Message persistence
- RESTful API endpoints

### Frontend (React)
- WhatsApp-like UI
- Real-time message updates
- User authentication
- Chat room management
- File sharing
- Responsive design

## Tech Stack

### Backend
- Java 11
- Spring Boot 2.7.0
- Spring Security
- Spring Data JPA
- WebSocket
- MySQL
- Maven

### Frontend
- React 18
- Material-UI
- Axios
- WebSocket
- CSS3

## Prerequisites
- Java 11 or higher
- Node.js 14 or higher
- MySQL 8.0 or higher
- Maven 3.6 or higher

## Installation

### Backend Setup
1. Clone the repository
2. Navigate to the backend directory
3. Configure database in `application.properties`
4. Run `mvn clean install`
5. Start the application: `mvn spring-boot:run`

### Frontend Setup
1. Navigate to the frontend directory
2. Install dependencies: `npm install`
3. Start the development server: `npm start`

## API Documentation

### Authentication
- POST `/api/auth/register` - Register new user
- POST `/api/auth/login` - User login
- GET `/api/auth/me` - Get current user

### Chat
- GET `/api/chat/rooms` - Get user's chat rooms
- GET `/api/chat/messages/{roomId}` - Get room messages
- POST `/api/chat/messages` - Send message
- POST `/api/chat/rooms` - Create chat room

### WebSocket
- `/ws/chat` - WebSocket endpoint for real-time messaging

## Project Structure

```
├── backend/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   └── com/
│   │   │   │       └── chatapp/
│   │   │   │           ├── config/
│   │   │   │           ├── controller/
│   │   │   │           ├── model/
│   │   │   │           ├── repository/
│   │   │   │           ├── security/
│   │   │   │           └── service/
│   │   │   └── resources/
│   │   └── test/
│   └── pom.xml
└── frontend/
    ├── public/
    ├── src/
    │   ├── components/
    │   ├── context/
    │   ├── services/
    │   └── App.js
    └── package.json
```

## Security
- JWT-based authentication
- Password encryption
- CORS configuration
- Secure WebSocket connections

## Contributing
1. Fork the repository
2. Create your feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## License
This project is licensed under the MIT License. 