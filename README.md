# AI Mock Interview Agent

A full-stack mock interview practice app. Users sign up, pick a role and tech
stack, get AI-generated interview questions (via Google Gemini), answer them,
and receive AI-generated feedback with a score, strengths, and areas to improve.

## Tech Stack

- **Backend:** Java 17, Spring Boot 3, Spring Data JPA, H2 (file-based) database
- **AI:** Google Gemini API (`gemini-1.5-flash`)
- **Frontend:** Plain HTML, CSS, JavaScript (no framework/build step)
- **Containerization:** Docker + Docker Compose

## Project Structure

```
AI-Mock-Interview-Agent/
├── backend/     # Spring Boot REST API
├── frontend/    # Static HTML/CSS/JS client
├── docker/      # Dockerfile + docker-compose.yml
└── README.md
```

## Prerequisites

- Java 17+
- Maven 3.9+
- A Gemini API key: https://aistudio.google.com/app/apikey
- (Optional) Docker + Docker Compose

## Running Locally (without Docker)

1. **Set your Gemini API key** as an environment variable:
   ```bash
   export GEMINI_API_KEY=your_key_here
   ```

2. **Run the backend:**
   ```bash
   cd backend
   mvn spring-boot:run
   ```
   The API will be available at `http://localhost:8080`.
   H2 console (optional): `http://localhost:8080/h2-console`
   (JDBC URL: `jdbc:h2:file:./data/ai_interview_db`, user: `sa`, no password)

3. **Serve the frontend.** Since it's static files, any simple server works, e.g.:
   ```bash
   cd frontend
   python3 -m http.server 3000
   ```
   Then open `http://localhost:3000`.

## Running with Docker Compose

```bash
export GEMINI_API_KEY=your_key_here
cd docker
docker compose up --build
```

- Backend: `http://localhost:8080`
- Frontend: `http://localhost:3000`

## API Overview

| Method | Endpoint                          | Description                          |
|--------|------------------------------------|---------------------------------------|
| POST   | `/api/auth/register`              | Create a new user                     |
| POST   | `/api/auth/login`                 | Log in                                |
| POST   | `/api/interview/start`            | Generate questions, start an interview|
| GET    | `/api/interview/{id}`             | Get a single interview                |
| GET    | `/api/interview/user/{userId}`    | List a user's interviews              |
| POST   | `/api/interview/{id}/submit`      | Submit answers, trigger AI evaluation |
| GET    | `/api/feedback/{interviewId}`     | Get stored feedback for an interview  |

## Switching to MySQL

Update `backend/src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/ai_interview_db
spring.datasource.username=root
spring.datasource.password=your_password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
```

And add the MySQL connector dependency to `backend/pom.xml`:

```xml
<dependency>
    <groupId>com.mysql</groupId>
    <artifactId>mysql-connector-j</artifactId>
    <scope>runtime</scope>
</dependency>
```

## Notes

- Passwords are hashed with BCrypt.
- CORS is open (`*`) for local development — restrict `CorsConfig` before deploying publicly.
- This is a demo/portfolio-style scaffold — add proper auth (JWT/session), input
  validation, and rate limiting on the Gemini calls before using in production.
