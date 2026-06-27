# 🚀 QueueUp

<p align="center">
  <strong>A modern real-time virtual queue management system built with Spring Boot, WebSockets, and Vanilla JavaScript.</strong>
</p>

<p align="center">
Eliminate physical waiting lines, improve customer experience, and manage queues in real time from any device.
</p>

---

<p align="center">

<a href="https://queueup-ipw1.onrender.com">
    <img src="https://img.shields.io/badge/🚀_Live_Application-Visit_QueueUp-success?style=for-the-badge">
</a>
</p>



---

<p align="center">

![Java](https://img.shields.io/badge/Java-21-orange?style=for-the-badge)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.5.15-brightgreen?style=for-the-badge)
![WebSocket](https://img.shields.io/badge/WebSocket-Real--Time-blue?style=for-the-badge)
![H2 Database](https://img.shields.io/badge/H2-Embedded_Database-lightgrey?style=for-the-badge)

</p>

---

## 📖 Overview

QueueUp is a lightweight, real-time queue management platform that replaces traditional physical waiting lines with a digital queue.

Businesses can instantly create a queue, while customers join using a simple queue code from any browser—no downloads, registration, or mobile application required.

Powered by WebSockets, QueueUp synchronizes queue updates instantly across all connected devices, ensuring customers always know their live position and estimated waiting time.

Whether it's a clinic, restaurant, bank, café, pharmacy, or government office, QueueUp simplifies customer flow while providing administrators with complete control over queue operations.

---

## ✨ Key Features

### 🎫 Customer Experience

- Join queues instantly using a unique queue code
- No registration or login required
- Live queue position updates
- Estimated waiting time
- Browser notifications when called
- Mobile vibration support
- Works directly from any modern browser

---

### 🏢 Administrator Features

- Create queues instantly
- Secure admin management using unique tokens
- Call the next customer
- Mark customers as served
- Handle no-shows automatically
- Pause or resume queues
- Close queues when operations finish
- View live queue analytics

---

### ⚡ Real-Time Capabilities

- WebSocket-powered live synchronization
- Instant queue position updates
- Automatic queue advancement
- Live status broadcasting
- Multiple clients synchronized simultaneously

---

### 📊 Analytics

- Customers served
- Average waiting time
- Queue length
- No-show statistics
- Active customer count

---

## 🎯 Why QueueUp?

Traditional waiting systems often suffer from:

| Problem | QueueUp Solution |
|----------|------------------|
| Long physical lines | Virtual queue accessible anywhere |
| Customers miss their turn | Real-time notifications |
| Unknown waiting times | Live wait-time estimation |
| Manual queue handling | Automated queue management |
| Expensive hardware | Browser-based solution |
| Dedicated mobile apps | No installation required |

---

## 🌟 Highlights

- ⚡ Real-time communication using WebSockets
- 🚀 Zero-login customer experience
- 🔐 Token-based admin authorization
- 📱 Mobile-friendly responsive interface
- 🖥️ Single Page Application (SPA)
- 💾 Embedded database (no external DB required)
- 📦 Lightweight deployment
- ☁️ Ready for cloud hosting
- 🔄 Automatic no-show detection
- 📈 Queue performance metrics

---

## 🏗 System Architecture

```text
                    +-----------------------+
                    |      Web Browser      |
                    |                       |
                    |  Admin / Customer UI  |
                    +-----------+-----------+
                                |
                       HTTP + WebSocket
                                |
                +---------------+---------------+
                |      Spring Boot Server       |
                |                               |
                | REST API | WebSocket Handler  |
                | Business Services            |
                +---------------+---------------+
                                |
                     Spring Data JPA
                                |
                     Embedded H2 Database
```

### Architecture Components

| Layer | Responsibility |
|--------|----------------|
| Frontend | User interface and interactions |
| REST API | Queue operations |
| WebSocket | Real-time updates |
| Service Layer | Business logic |
| Repository Layer | Data persistence |
| Scheduler | Auto no-show and queue cleanup |
| Database | Queue and ticket storage |

---

## 🛠 Technology Stack

### Backend

| Technology | Purpose |
|------------|---------|
| Java 21 | Programming language |
| Spring Boot | Backend framework |
| Spring MVC | REST APIs |
| Spring Data JPA | ORM |
| Spring WebSocket | Real-time communication |
| Undertow | Embedded web server |
| Bucket4j | Rate limiting |
| H2 Database | Embedded persistence |
| Maven | Dependency management |

### Frontend

| Technology | Purpose |
|------------|---------|
| HTML5 | Structure |
| CSS3 | Styling |
| Vanilla JavaScript | Application logic |
| Fetch API | REST communication |
| WebSocket API | Live updates |
| Notification API | Browser notifications |

### DevOps

| Technology | Purpose |
|------------|---------|
| Git | Version Control |
| GitHub | Source Repository |
| Docker | Containerization |
| Render  | Deployment |

---
## 📂 Project Structure

The project follows a layered architecture based on Spring Boot best practices to ensure clean separation of concerns and maintainability.

```text
queueup/
├── src/
│   ├── main/
│   │   ├── java/com/queueup/
│   │   │   ├── config/             # Application configuration
│   │   │   ├── controller/         # REST Controllers
│   │   │   ├── dto/                # Request & Response DTOs
│   │   │   ├── exception/          # Global exception handling
│   │   │   ├── model/              # JPA Entities
│   │   │   ├── repository/         # Spring Data JPA repositories
│   │   │   ├── scheduler/          # Background scheduled tasks
│   │   │   ├── service/            # Business logic
│   │   │   ├── util/               # Utility classes
│   │   │   ├── websocket/          # WebSocket handlers
│   │   │   └── QueueUpApplication.java
│   │   │
│   │   └── resources/
│   │       ├── static/
│   │       │   └── index.html
│   │       ├── application.properties
│   │       └── application-prod.properties
│   │
│   └── test/
│
├── Dockerfile
├── pom.xml
└── README.md
```

---

# 🚀 Getting Started

Follow these steps to run QueueUp locally.

## Prerequisites

Before running the application, ensure you have the following installed:

| Software | Version |
|----------|---------|
| Java | 17 or above |
| Maven | 3.9+ |
| Git | Latest |
| Browser | Chrome, Edge, Firefox, Safari |

---

## Clone the Repository

```bash
git clone https://github.com/<your-username>/queueup.git
```

```bash
cd queueup
```

---

## Build the Project

Using Maven Wrapper:

```bash
./mvnw clean install
```

Or using Maven:

```bash
mvn clean install
```

---

## Run the Application

Using Maven Wrapper

```bash
./mvnw spring-boot:run
```

Or

```bash
mvn spring-boot:run
```

The application will start on:

```
http://localhost:8080
```

---

## Verify Installation

Open your browser and visit

```
http://localhost:8080
```

You should see the QueueUp landing page where you can either:

- Create a new queue
- Join an existing queue

---

# ⚙️ Configuration

The application works out of the box with zero configuration.

### Default Configuration

| Property | Value |
|----------|-------|
| Server Port | 8080 |
| Database | Embedded H2 |
| WebSocket Endpoint | `/ws/queue/{queueCode}` |
| API Base URL | `/api` |

---

### application.properties

Example configuration:

```properties
server.port=8080

spring.datasource.url=jdbc:h2:file:./data/queueup
spring.datasource.driverClassName=org.h2.Driver

spring.jpa.hibernate.ddl-auto=update

spring.h2.console.enabled=true

spring.jpa.show-sql=false
```

---

# 🎮 Using QueueUp

## Creating a Queue

1. Open the application.
2. Click **Create Queue**.
3. A unique queue code is generated.
4. Save the generated Admin Token securely.
5. Share the queue code with customers.

---

## Joining a Queue

Customers simply:

1. Open the application.
2. Click **Join Queue**.
3. Enter the queue code.
4. Optionally enter their name.
5. Receive a queue token.
6. Track their live position.

No registration or login is required.

---

## Managing Customers

Administrators can:

- Call the next customer
- Serve customers
- Mark no-shows
- Pause the queue
- Resume operations
- Close the queue

All connected users receive updates instantly.

---

# 📡 REST API Overview

## Public Endpoints

| Method | Endpoint | Description |
|----------|----------------------------|--------------------------------|
| POST | `/api/admin/queues` | Create a new queue |
| POST | `/api/queues/{code}/join` | Join a queue |
| GET | `/api/queues/{code}/status` | Get customer queue status |
| GET | `/api/health` | Health check |

---

## Admin Endpoints

Authentication is performed using the generated Admin Token.

| Method | Endpoint | Description |
|----------|----------------------------------|------------------------|
| GET | `/api/admin/queues/{code}` | Queue details |
| POST | `/call` | Call next customer |
| POST | `/serve` | Mark customer served |
| POST | `/pause` | Pause queue |
| POST | `/resume` | Resume queue |
| POST | `/close` | Close queue |


---

## Sample Response

```json
{
  "code": "KX89B2",
  "status": "OPEN",
  "waitingCustomers": 7,
  "estimatedWait": 12
}
```

---

# 🔄 Real-Time Communication

QueueUp uses **WebSockets** to synchronize queue information across every connected client.

Whenever the queue changes, all users immediately receive updated information without refreshing the page.

### Real-Time Events

- Customer joins
- Customer served
- Queue paused
- Queue resumed
- Queue closed
- Customer called
- No-show detected
- Queue position updates

---

## WebSocket Endpoint

```
ws://localhost:8080/ws/queue/{queueCode}
```

Production

```
wss://your-domain/ws/queue/{queueCode}
```

---

## Event Flow

```text
Customer Joins
       │
       ▼
REST API
       │
       ▼
Queue Updated
       │
       ▼
WebSocket Broadcast
       │
       ▼
All Connected Clients Updated
```

---

# 🔐 Security Model

QueueUp intentionally avoids traditional username/password authentication.

Instead, each queue generates two identifiers:

| Identifier | Purpose |
|------------|---------|
| Queue Code | Public identifier for customers |
| Admin Token | Secure management access |

This approach provides:

- Faster onboarding
- Simpler user experience
- No password storage
- No session management
- Secure queue isolation
- Stateless API design

Only administrators possessing the Admin Token can perform management operations.

---

# 💾 Database

QueueUp uses an embedded **H2 Database**, allowing the application to run without installing MySQL or PostgreSQL.

The database automatically stores:

- Queues
- Tickets
- Customer status
- Waiting times
- Analytics

For production deployments, switching to PostgreSQL or MySQL requires only datasource configuration changes.

---
# ☁️ Deployment

QueueUp is designed to be lightweight and can be deployed on most cloud platforms with minimal configuration.

## Docker

### Build Image

```bash
docker build -t queueup .
```

### Run Container

```bash
docker run -p 8080:8080 queueup
```

The application will be available at:

```
http://localhost:8080
```

---

## Deploy on Render

1. Push the project to GitHub.
2. Create a new **Web Service** on Render.
3. Connect your GitHub repository.
4. Configure:

| Setting | Value |
|----------|-------|
| Build Command | `./mvnw clean package` |
| Start Command | `java -jar target/*.jar` |

5. Deploy.

---

## Deploy on Railway

1. Push the repository to GitHub.
2. Create a new Railway project.
3. Select **Deploy from GitHub**.
4. Railway automatically detects the Spring Boot application.
5. Deploy.


---

## Environment Variables

For production deployments, you may configure:

| Variable | Description |
|----------|-------------|
| `SERVER_PORT` | Application port |
| `SPRING_PROFILES_ACTIVE` | Active Spring profile |
| `DATABASE_URL` | External database connection |
| `JAVA_OPTS` | JVM configuration |

---

# 📸 Screenshots

## Home Page


<img width="1907" height="1045" alt="image" src="https://github.com/user-attachments/assets/3283a757-e65d-4bad-ab1f-16047515013d" />



---

## Admin Dashboard


<img width="1917" height="1048" alt="image" src="https://github.com/user-attachments/assets/7189201f-5f56-417d-a850-96d99417090c" />



---

## Customer Queue View


<img width="1912" height="1046" alt="image" src="https://github.com/user-attachments/assets/7fa15505-003e-4753-90ff-845107cba9cd" />


---

# 💼 Business Use Cases

QueueUp is suitable for a wide range of industries where organized customer flow is essential.

| Industry | Example Use Case |
|----------|------------------|
| ☕ Coffee Shops | Order pickup queues |
| 🍽 Restaurants | Waiting list management |
| 💈 Barber Shops | Virtual appointment queues |
| 💊 Pharmacies | Medicine pickup |
| 🏥 Clinics | Patient waiting queues |
| 🏦 Banks | Counter token system |
| 🏛 Government Offices | Citizen service queues |
| 📱 Service Centers | Device repair collection |
| 🎓 Universities | Student service counters |
| 🛒 Retail Stores | Customer assistance queues |

---

# 🚀 Performance Highlights

- Real-time WebSocket communication
- Lightweight embedded database
- No frontend frameworks
- Minimal memory footprint
- Fast application startup
- Stateless API architecture
- Efficient queue synchronization
- Automatic background scheduling
- Horizontal deployment ready

---

# 🧩 Design Principles

QueueUp was built around a few core engineering principles.

### Simplicity

No unnecessary dependencies or complex infrastructure.

### Scalability

Business logic is isolated into dedicated service layers, making future expansion straightforward.

### Maintainability

The project follows Spring Boot's layered architecture with clean separation between controllers, services, repositories, and models.

### User Experience

Customers can join queues in seconds without creating accounts or downloading applications.

### Real-Time Communication

Every queue update is immediately synchronized across connected clients using WebSockets.

---

# 🛣 Roadmap

Future improvements planned for QueueUp include:

- [ ] QR Code-based queue joining
- [ ] SMS notifications
- [ ] Email notifications
- [ ] Multiple service counters
- [ ] Priority queues
- [ ] Appointment scheduling
- [ ] Customer feedback system
- [ ] Dashboard analytics charts
- [ ] PostgreSQL support
- [ ] Redis caching
- [ ] Spring Security integration
- [ ] JWT authentication
- [ ] Docker Compose support
- [ ] Kubernetes deployment
- [ ] CI/CD pipeline
- [ ] Automated testing suite
- [ ] Dark mode
- [ ] Progressive Web App (PWA)

---

# 🧪 Testing

Run the test suite using Maven.

```bash
./mvnw test
```

or

```bash
mvn test
```

---

# 🤝 Contributing

Contributions are welcome!

If you would like to improve QueueUp:

1. Fork the repository.
2. Create a feature branch.

```bash
git checkout -b feature/my-feature
```

3. Commit your changes.

```bash
git commit -m "Add new feature"
```

4. Push to your branch.

```bash
git push origin feature/my-feature
```

5. Open a Pull Request.

Please ensure your code follows the project's coding standards and includes appropriate documentation.

---

# 📚 Documentation

Additional documentation can be organized inside a dedicated **docs/** directory.

```text
docs/
├── architecture.md
├── api-reference.md
├── websocket.md
├── database.md
├── deployment.md
├── security.md
└── contributing.md
```

---

# 📄 License

Feel free to use, modify, and distribute it for personal or commercial purposes.

---

# 👨‍💻 Author

**Praveen Verma**

Computer Science Engineering Student

- Passionate about Software Engineering
- Problem Solving
- Java Backend Development
- Spring Boot
- Data Structures & Algorithms
- Artificial Intelligence

GitHub:
```
(https://github.com/praveen8848)
```


---

# 🙏 Acknowledgements

Special thanks to the open-source community and the technologies that made this project possible.

- Spring Boot
- Spring Data JPA
- Spring WebSocket
- H2 Database
- Undertow
- Maven
- Java
- GitHub

---

# ⭐ Support

If you found this project useful, consider giving it a ⭐ on GitHub.

Your support helps increase the visibility of the project and encourages further development.

---

<p align="center">


*Making queue management smarter, faster, and accessible to everyone.*

</p>
