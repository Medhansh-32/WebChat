# WebChat💭

**WebChat** is a real-time chat and image-sharing application built using Spring Boot and modern web technologies. This project demonstrates seamless integration of backend and frontend systems with cloud services to deliver a smooth and feature-rich user experience.

---

## Table of Contents


1. [Features](#features)
2. [Technologies Used](#technologies-used)
3. [Project Architecture](#project-architecture)
4. [Installation and Setup](#installation-and-setup)
5. [Usage](#usage)
6. [Endpoints](#endpoints)
7. [Contributing](#contributing)


---

## Features

- **Real-Time Chat:** Send and receive messages instantly using WebSocket.
- **Image Sharing:** Upload and share images seamlessly using Imgur's cloud services.
- **Contact Management:** Manage contacts to streamline conversations.
- **Responsive UI:** Built with HTML, CSS, and JavaScript for a modern and intuitive front-end experience.

---

## Technologies Used

### Backend:
- **Java:** Programming language used for the application.
- **Spring Boot:** Framework for rapid application development.
  - **Spring Web:** For RESTful APIs.
  - **Spring WebSocket:** For real-time communication.
  - **Spring Data JPA:** For database interactions.
- **Lombok:** Reduces boilerplate code.

### Database:
- **MySQL (Aiven Cloud):** Cloud-hosted MySQL for efficient and reliable database operations.

### Frontend:
- **HTML, CSS, JavaScript:** To design and create a responsive and interactive user interface.

### Cloud Services:
- **Imgur API:** Used for uploading and storing images in the cloud.

### Build Tool:
- **Maven:** Dependency management and project building.

---

## Project Architecture

**Frontend:**
- HTML/CSS/JavaScript
- Interacts with backend via REST APIs and WebSocket.

**Backend:**
- Java with Spring Boot
  - REST APIs
  - WebSocket endpoints
  - Database interactions using Spring Data JPA.

**Database:**
- MySQL hosted on Aiven Cloud.

**Cloud Integration:**
- Imgur API for image sharing and storage.

---

## Installation and Setup

### Prerequisites

- Java 17 or higher.
- Maven 3.8 or higher.
- MySQL database (details configured in the application).
- Imgur API credentials (client ID and client secret).

### Steps

1. Clone the repository:

   ```sh
   git clone https://github.com/your-username/webchat.git
   cd webchat

### Set up your database:

1. Create a MySQL database for the application.
2. Update the `application.properties` file with your database URL, username, and password:

    ```properties
    spring.datasource.url=jdbc:mysql://<hostname>:<port>/<dbname>
    spring.datasource.username=<username>
    spring.datasource.password=<password>
    spring.jpa.hibernate.ddl-auto=update
    ```

### Configure Imgur:

1. Obtain Imgur API credentials (client ID and client secret).
2. Add them to the `application.properties` file:

    ```properties
    imgur.client.id=your-client-id
    imgur.client.secret=your-client-secret
    ```

### Build the project:

1. Use Maven to build the project:

    ```sh
    mvn clean install
    ```

### Run the application:

1. Once the project is built, run the backend server:

    ```sh
    mvn spring-boot:run
    ```

    The application will be available at (http://localhost:8080).

### Usage

- Register and log in to the WebChat application.
- Manage your contacts by adding new ones or deleting old ones in the contact section.
- Start real-time chats using the chat interface.
- Share images via the Imgur-integrated upload feature.
- Download images by right-clicking on the image .

### Endpoints

#### Public Endpoints:

- **POST /users/register** - Register a new user.
- **POST /users/login** - Login to the application.

#### Protected Endpoints:

- **GET /users/data** - Retrieve user data.
- **POST /users/addContact** - Add a new contact.
- **DELETE /users/deleteContact/{contactName}** - Delete a specific contact.
- **POST /chat/sendMessage** - Send a message via WebSocket.
- **POST /images/upload** - Upload and share an image.

### Contributing

We welcome contributions! To contribute:

1. Fork the repository.
2. Create a feature branch (`git checkout -b feature-name`).
3. Commit your changes (`git commit -m 'Add feature'`).
4. Push to the branch (`git push origin feature-name`).
5. Open a pull request.

---

### Let's Interact!

❤️ **Contribute:** Every little bit helps. Whether it's a small fix or a major feature, we welcome all contributions. Don't hesitate to open an issue or submit a pull request.

🤝 **Collaborate:** Have ideas? Share them with us! Let's build something amazing together.

🌟 **Spread the Word:** If you like this project, please give it a star on GitHub and share it with others.

---

Thank you for your support! Let's make this project awesome together! 🚀
