# Smart Grid Electricity Management System

## Overview

A backend-based Smart Grid Electricity Management System developed using Java, Spring Boot and PostgreSQL. The system manages customers, electricity billing, energy generation records, etc while supporting role-based access for customers and employees.

---

## Features

### Authentication & Authorization

* Customer login
* Employee login
* Session-based role validation
* Customer-only and employee-only API access

### Customer Management

* Add new customers
* Automatic wallet creation
* Connection status management

### Virtual Money Concept

* Earn virtual money from energy generation
* View wallet balance
* Pay bills using Virtual money
* Convert virtual money into real bank money

### Billing System

* Create electricity bills
* View pending and paid bills
* Pay bills using wallet balance and/or bank account
* Automatic connection restoration after all dues are cleared

### Energy Management

* Record energy generation/consumption
* View customer energy history
* Solar panel support


### Exception Handling

* Global exception handling
* Validation checks
* Resource not found handling
* Duplicate resource protection
* Insufficient balance protection

---

## Tech Stack

* Java
* Spring Boot
* Spring Data JPA
* Hibernate
* PostgreSQL
* Maven
* Postman

---

## Project Structure

```text
Controller
    ↓
Service
    ↓
Repository
    ↓
PostgreSQL Database
```

---

## Running the Project

### 1. Clone Repository

```bash
git clone https://github.com/ashishwho/SMART_GRID_ELECTRICITY_SYSTEM.git
```

### 2. Create Database

```sql
CREATE DATABASE smartgrid;
```

### 3. Configure Database

Update:

```properties
src/main/resources/application.properties
```

with your PostgreSQL credentials.

### 4. Run Application

Run:

```text
SmartGridElectricitySystemApplication
```

or

```bash
mvn spring-boot:run
```

---

## Key Concepts

* Hybrid bill payment using wallet and bank balance.
* Virtual money in a customer's wallet represents money owed by the Electricity Department to that customer.
* Transaction-safe operations using Spring Transactions.
* Role-based access control for customers and employees.
* Utility-bank account separation for realistic financial modeling.

---

## Future Enhancements

* JWT-based authentication
* Smart meter integration

