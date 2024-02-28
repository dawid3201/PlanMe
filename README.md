# PlanMe
This is a final university project. It is still under development. 
## Description

Greetings and welcome to my project! I am pleased to introduce a Todo List Application developed using Java and the Spring Boot 3 framework. This application is designed to provide users with a seamless experience in efficiently managing tasks. Users can effortlessly add, delete, and update tasks, fostering a convenient and streamlined task management process. The application is versatile, accommodating both individual users and collaborative team environments. 


Users passwords are protected with BCrypt algorithm.

![image](https://github.com/dawid3201/PlanMe/assets/42513264/5282f80a-2dcd-4149-8a52-514c1b7549a4)

## How to Use

**Prerequisites**

* Java 20 or at least Java 11 installed on your device
* A modern web browser

**Dependencies used**

- Spring Framework: Provides the core foundation for Spring Boot applications and features like JPA, web development, security, and more.
- Spring Boot DevTools: Enhances development workflow with features like automatic restarts.
- MySQL Connector/J: Enables communication with a MySQL database.
- Thymeleaf: Server-side template engine for building Java web applications.
- Spring Security: Provides security features for authentication and authorization.
- Spring Websockets and related dependencies: Enable real-time features and messaging within the application.
- Lombok: Simplifies Java code reducing boilerplate.

**Installation**

1. Download zip file of the master branch

**Running the Application**

1) Import the project into your preferred IDE (IntelliJ, Eclipse, etc.)
2) My program uses MySQL database to store info, the exact copy of my database is included below. It will be neccessary to run the app.
3) Locate and run the main application class.
4) Access website through: http://localhost:8080/login


## Funcionality
Users can:
1) Register and Login

![image](https://github.com/dawid3201/PlanMe/assets/42513264/fb95c55e-9451-401c-992c-8b75e0d350ac)
![image](https://github.com/dawid3201/PlanMe/assets/42513264/0c3d2669-7351-4070-943c-912078d58821)

2) Add description to each task

![image](https://github.com/dawid3201/PlanMe/assets/42513264/2b67dc33-cc87-4f9a-86b6-4988557ddda5)

3) Assign users to tasks

![image](https://github.com/dawid3201/PlanMe/assets/42513264/52403b4f-dee4-4f13-b899-60a9ce40d271)

4) Move tasks and bars

![image](https://github.com/dawid3201/PlanMe/assets/42513264/9cf97e13-71bf-40d1-8679-11caf8f63fc9)
![image](https://github.com/dawid3201/PlanMe/assets/42513264/f44ced80-1e41-428c-aed0-b9d466a55e46)

5) Prioritise tasks based on the number assign

![image](https://github.com/dawid3201/PlanMe/assets/42513264/4363e48e-f4c6-4834-aa6c-4af96b1aaf37)

6) Update names

![image](https://github.com/dawid3201/PlanMe/assets/42513264/d7c3e206-d446-4d0a-88de-7fc4a6caa58d)

7) Search tasks by name

![image](https://github.com/dawid3201/PlanMe/assets/42513264/18926c13-cb71-4b7c-ab5d-70ff2383679f)

8) See changes other users made

## Tools and languages used 
1) Java 20
2) JavaScript - jQuery
3) JUnit 5
4) Postman
5) HTML & CSS
6) Sprint Boot 3
7) JDBC
8) Hibernate 
9) Thymeleaf
10) MySQL - Workbench 8.0
11) WebSocket

## Datbase

CREATE TABLE Users (
    first_name varchar(45) DEFAULT NULL,
    last_name varchar(45) DEFAULT NULL,
    email_address varchar(100) PRIMARY KEY,
    password varchar(68) default NULL
);

CREATE TABLE Homepages (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id VARCHAR(100),
    data TEXT,
    FOREIGN KEY (user_id) REFERENCES users(email_address)
);

create table Projects(
	id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name varchar(25) NOT NULL,
    deadline DATE,
    creator_id VARCHAR(100),
    FOREIGN KEY (creator_id) REFERENCES users(email_address)
);

CREATE TABLE Project_members (
    project_id BIGINT,
    user_id VARCHAR(100),
    PRIMARY KEY (project_id, user_id),
    FOREIGN KEY (project_id) REFERENCES projects(id),
    FOREIGN KEY (user_id) REFERENCES users(email_address)
);

CREATE TABLE Bars (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(70) NOT NULL,
    position BIGINT,
    project_id BIGINT,
    FOREIGN KEY (project_id) REFERENCES Projects(id)
);

CREATE TABLE Tasks(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    state VARCHAR(255),
    project_id BIGINT,
    bar_id BIGINT,
    FOREIGN KEY (bar_id) REFERENCES Bars(id),
    FOREIGN KEY (project_id) REFERENCES projects(id),
    priority INT,
    position BIGINT,
    description varchar(10000),
    assigned_user_email VARCHAR(100),
    FOREIGN KEY (assigned_user_email) REFERENCES users(email_address)
);




