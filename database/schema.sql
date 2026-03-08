-- database/schema.sql
CREATE DATABASE IF NOT EXISTS fitness_ai;
USE fitness_ai;

-- Users table for authentication
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(50),
    last_name VARCHAR(50),
    user_id VARCHAR(50) UNIQUE,
    enabled BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_login TIMESTAMP NULL,
    INDEX idx_username (username),
    INDEX idx_email (email)
);

-- Roles table
CREATE TABLE IF NOT EXISTS roles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL,
    description VARCHAR(255)
);

-- User roles junction table
CREATE TABLE IF NOT EXISTS user_roles (
    user_id BIGINT,
    role_id BIGINT,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);

-- Insert default roles
INSERT IGNORE INTO roles (name, description) VALUES 
('ROLE_USER', 'Regular user'),
('ROLE_ADMIN', 'Administrator');

-- User profiles table
CREATE TABLE IF NOT EXISTS user_profiles (
    user_id VARCHAR(50) PRIMARY KEY,
    name VARCHAR(100),
    age INT,
    weight DECIMAL(5,2),
    height DECIMAL(5,2),
    fitness_goal VARCHAR(50),
    fitness_level VARCHAR(20),
    medical_conditions TEXT,
    weekly_workout_goal INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- Chat messages table
CREATE TABLE IF NOT EXISTS chat_messages (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id VARCHAR(50),
    user_message TEXT,
    ai_response TEXT,
    user_rating DECIMAL(2,1),
    context TEXT,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user_id (user_id),
    INDEX idx_timestamp (timestamp),
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- Workout plans table
CREATE TABLE IF NOT EXISTS workout_plans (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id VARCHAR(50),
    plan_name VARCHAR(100),
    exercises TEXT,
    duration INT,
    intensity VARCHAR(20),
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    completed BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- Learning patterns table (for AI self-learning)
CREATE TABLE IF NOT EXISTS learning_patterns (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    intent VARCHAR(50),
    pattern TEXT,
    weight INT DEFAULT 1,
    success_count INT DEFAULT 0,
    usage_count INT DEFAULT 0,
    last_used TIMESTAMP,
    INDEX idx_intent (intent)
);

-- User feedback table
CREATE TABLE IF NOT EXISTS user_feedback (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    message_id BIGINT,
    rating INT,
    feedback_text TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (message_id) REFERENCES chat_messages(id) ON DELETE CASCADE
);