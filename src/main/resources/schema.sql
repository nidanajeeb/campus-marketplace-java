-- ========================================================
-- Campus Circular Market - Database Schema (MySQL)
-- ========================================================

CREATE DATABASE IF NOT EXISTS campus_market_db
  CHARACTER SET utf8mb4 
  COLLATE utf8mb4_unicode_ci;

USE campus_market_db;

-- 1. Users table (Stores registered students and admins)
CREATE TABLE IF NOT EXISTS users (
  user_id BIGINT AUTO_INCREMENT PRIMARY KEY,
  email VARCHAR(100) NOT NULL UNIQUE,
  password_hash VARCHAR(255) NOT NULL,
  full_name VARCHAR(100) NOT NULL,
  role VARCHAR(20) NOT NULL DEFAULT 'ROLE_USER',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- 2. Items table (Marketplace listings)
CREATE TABLE IF NOT EXISTS items (
  item_id BIGINT AUTO_INCREMENT PRIMARY KEY,
  item_name VARCHAR(120) NOT NULL,
  category VARCHAR(50) NOT NULL,
  price DECIMAL(10, 2) NOT NULL,
  description TEXT NOT NULL,
  seller_name VARCHAR(100) NOT NULL,
  seller_contact VARCHAR(120) NOT NULL,
  image_url VARCHAR(255) NULL,
  status VARCHAR(20) NOT NULL DEFAULT 'AVAILABLE',
  is_demo BOOLEAN NOT NULL DEFAULT FALSE,
  user_id BIGINT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_items_user FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE SET NULL,
  INDEX idx_category (category),
  INDEX idx_status (status),
  INDEX idx_price (price)
) ENGINE=InnoDB;

