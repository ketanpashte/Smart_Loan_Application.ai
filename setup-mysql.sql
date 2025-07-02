-- SmartLoan MySQL Database Setup Script
-- Run this script to set up the database for SmartLoan application

-- Create database
CREATE DATABASE IF NOT EXISTS smartloan_db;

-- Create user (optional - you can use root)
CREATE USER IF NOT EXISTS 'smartloan_user'@'localhost' IDENTIFIED BY 'smartloan_password';

-- Grant privileges
GRANT ALL PRIVILEGES ON smartloan_db.* TO 'smartloan_user'@'localhost';
FLUSH PRIVILEGES;

-- Use the database
USE smartloan_db;

-- The application will automatically create tables using JPA/Hibernate
-- No need to manually create tables as they will be auto-generated

-- Verify database creation
SHOW DATABASES;
SHOW TABLES;
