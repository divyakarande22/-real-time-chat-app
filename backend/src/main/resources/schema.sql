-- Create the database if it doesn't exist
CREATE DATABASE IF NOT EXISTS chat_app;

-- Use the database
USE chat_app;

-- Create messages table
CREATE TABLE IF NOT EXISTS messages (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    sender VARCHAR(255) NOT NULL,
    text VARCHAR(1000) NOT NULL,
    timestamp DATETIME NOT NULL
);

-- Create users table (for future authentication)
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
); 