# 🚀 MyBank - Digital Banking System

## 📌 Overview

A secure backend banking system built using Spring Boot that simulates real-world banking operations like account creation, fund transfer, and authentication.

## 🛠️ Tech Stack

* Java, Spring Boot
* PostgreSQL
* Spring Security (JWT)
* Docker

## ✨ Features

* JWT-based authentication
* Role-based access (Admin/User)
* Fund transfer with transaction history
* Secure password hashing (BCrypt)
* Input validation and exception handling

## 🔗 API Endpoints

* POST /auth/login
* POST /accounts/create
* POST /transactions/transfer

## 📊 Performance

* Handles 10K+ records
* Optimized DB queries using indexing

## 🧠 Architecture

Client → Controller → Service → Repository → Database

## ▶️ Run Locally

1. Clone repo
2. Configure PostgreSQL
3. Run Spring Boot app

## 📌 Future Improvements

* Add Kafka for async processing
* Add monitoring/logging

## 🔗 GitHub Repository

https://github.com/shubham-sharma-03/My-Bank
