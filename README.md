# 💳 Card Fraud Monitoring App

## Project Overview
A Java application for managing bank cards and detecting fraud.  
The app allows banks to manage card lifecycles, monitor transactions, detect suspicious behavior, and generate alerts or block cards when necessary.

---

## Features
- Manage clients and their cards (debit, credit, prepaid)  
- Record and track transactions (purchase, withdrawal, online payment)  
- Detect fraudulent activities automatically  
- Generate alerts and block suspicious cards  
- Import/export cards and transactions via Excel  
- Generate basic reports and statistics

---

## Technical Details
- Java 17 (records, sealed classes, Stream API, Optional)  
- JDBC + MySQL for database management  
- Layered architecture: Entity, DAO, Service, UI  
- Packaged as an executable JAR  

---

## Installation
1. Clone the repository:
```bash
git clone https://github.com/<your-username>/card-fraud-monitoring-app.git
cd card-fraud-monitoring-app
