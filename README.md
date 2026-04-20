<div align="center">

<img src="assets/Naukarisathi.png" alt="NaukariSathi Logo" width="200"/>

# 🎓 NaukariSathi

### *Your AI-Powered Job Companion*

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-336791?style=for-the-badge&logo=postgresql&logoColor=white)](https://www.postgresql.org/)
[![Groq AI](https://img.shields.io/badge/Groq%20AI-Powered-F55036?style=for-the-badge&logo=ai&logoColor=white)](https://groq.com/)
[![Cloudinary](https://img.shields.io/badge/Cloudinary-Storage-3448C5?style=for-the-badge&logo=cloudinary&logoColor=white)](https://cloudinary.com/)
[![Render](https://img.shields.io/badge/Deployed%20on-Render-46E3B7?style=for-the-badge&logo=render&logoColor=black)](https://render.com/)

> NaukariSathi is a full-stack AI-powered job application platform that helps job seekers parse resumes, match with job listings using AI scoring, chat with an intelligent assistant, and manage their entire job hunt — all in one place.

</div>

---

## 📸 Tech Stack Overview

| Layer | Technology |
|---|---|
| **Backend** | Java Spring Boot |
| **Frontend** | HTML, CSS, JavaScript |
| **Database** | PostgreSQL |
| **File Storage** | Cloudinary (PDF/Resume) |
| **AI Engine** | Groq API (LLaMA) |
| **Email Service** | Email (OTP + Notifications) |
| **Deployment** | Render |

---

## ✨ Features

### 🤖 AI-Powered Capabilities (Groq API)
- **Resume Parser** — Automatically extracts skills, experience, education, and contact info from uploaded PDFs
- **AI Chatbot** — Intelligent assistant to guide users through job applications, resume tips, and career advice
- **AI Score Match** — Compares a candidate's resume against job descriptions and returns a compatibility score with reasoning

### 🔐 Authentication & Security
- OTP-based Email Verification for registration
- Email Notifications for application status updates, alerts, and reminders
- JWT-secured REST APIs

### 📄 Resume Management
- Upload resume as PDF
- Stored securely on **Cloudinary**
- Parsed and structured using Groq AI

### 💼 Job Applications
- Browse and apply for jobs
- Track application status (Applied → Reviewed → Interview → Offer)
- AI score match before applying to improve chances

### 📬 Email Services
- OTP delivery for account verification
- Application confirmation emails
- Status update notifications

---

## 🗂️ Project Structure

```
naukarisathi/
├── src/
│   └── main/
│       ├── java/com/naukarisathi/
│       │   ├── controller/          # REST Controllers
│       │   ├── service/             # Business Logic
│       │   │   ├── EmailService.java
│       │   │   ├── ResumeParserService.java
│       │   │   ├── ChatbotService.java
│       │   │   ├── AiScoreService.java
│       │   │   └── CloudinaryService.java
│       │   ├── model/               # JPA Entities
│       │   ├── repository/          # Spring Data Repos
│       │   ├── dto/                 # Request/Response DTOs
│       │   ├── config/              # App Configuration
│       │   └── NaukariSathiApp.java
│       └── resources/
│           └── application.properties
├── frontend/
│   ├── index.html
│   ├── style.css
│   └── script.js
├── .env.example
├── pom.xml
└── README.md
```

---

## ⚙️ Environment Variables

Create a `.env` file in the root directory based on the `.env.example` template:

```env
# ── Database ──────────────────────────────────────
SPRING_DATASOURCE_URL=jdbc:postgresql://<host>:<port>/<dbname>
SPRING_DATASOURCE_USERNAME=your_db_username
SPRING_DATASOURCE_PASSWORD=your_db_password

# ── Email Service ─────────────────────────────────
SPRING_MAIL_HOST=smtp.gmail.com
SPRING_MAIL_PORT=587
SPRING_MAIL_USERNAME=your_email@gmail.com
SPRING_MAIL_PASSWORD=your_app_password

# ── Cloudinary ────────────────────────────────────
CLOUDINARY_CLOUD_NAME=your_cloud_name
CLOUDINARY_API_KEY=your_api_key
CLOUDINARY_API_SECRET=your_api_secret

# ── Groq AI ───────────────────────────────────────
GROQ_API_KEY=your_groq_api_key
GROQ_MODEL=llama3-8b-8192

# ── JWT ───────────────────────────────────────────
JWT_SECRET=your_super_secret_key
JWT_EXPIRY_MS=86400000

# ── App ───────────────────────────────────────────
SERVER_PORT=8080
```

> ⚠️ **Never commit your `.env` file or actual API keys to version control.**

---

## 🚀 Getting Started

### Prerequisites

- Java 17+
- Maven 3.8+
- PostgreSQL 14+
- JavaScript (optional, for frontend tooling)
- A [Groq API Key](https://console.groq.com/)
- A [Cloudinary Account](https://cloudinary.com/)

### 1. Clone the Repository

```bash
git clone https://github.com/your-username/naukarisathi.git
cd naukarisathi
```

### 2. Set Up PostgreSQL

```sql
CREATE DATABASE naukarisathi_db;
CREATE USER naukarisathi_user WITH PASSWORD 'your_password';
GRANT ALL PRIVILEGES ON DATABASE naukarisathi_db TO naukarisathi_user;
```

### 3. Configure Environment

```bash
cp .env.example .env
# Fill in your values in .env
```

### 4. Run the Application

```bash
mvn clean install
mvn spring-boot:run
```

The API will be live at `http://localhost:8080`

---

## 🌐 API Endpoints

### 🔑 Auth
| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/auth/register` | Register with OTP verification |
| `POST` | `/api/auth/verify-otp` | Verify OTP from email |
| `POST` | `/api/auth/login` | Login and get JWT |

### 📄 Resume
| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/resume/upload` | Upload PDF to Cloudinary |
| `GET` | `/api/resume/parse/{userId}` | Parse resume with Groq AI |
| `GET` | `/api/resume/{userId}` | Get stored resume data |

### 🤖 AI Services
| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/ai/chat` | Chat with AI assistant |
| `POST` | `/api/ai/score-match` | Match resume to job description |

### 💼 Jobs & Applications
| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/api/jobs` | List all jobs |
| `POST` | `/api/jobs/{jobId}/apply` | Apply for a job |
| `GET` | `/api/applications/{userId}` | Get user's applications |

---

## ☁️ Deployment on Render

This project is deployed using [Render](https://render.com/) — a modern cloud platform with zero-config deploys.

### Steps to Deploy

1. Push your code to a GitHub repository
2. Go to [render.com](https://render.com/) and create a **New Web Service**
3. Connect your GitHub repo
4. Set the following:
   - **Environment**: `Java`
   - **Build Command**: `mvn clean package -DskipTests`
   - **Start Command**: `java -jar target/naukarisathi-*.jar`
5. Add all environment variables from your `.env` file under **Environment → Add Environment Variable**
6. Click **Deploy**

### Render PostgreSQL (optional)
You can provision a free PostgreSQL database directly in Render and link it to your web service using the auto-populated `DATABASE_URL`.

---

## 📧 Email Service Setup

NaukariSathi uses Spring Mail (SMTP) to send:

- ✅ **OTP emails** for account verification during signup
- 📩 **Application confirmation** emails when a user applies for a job
- 🔔 **Status update notifications** as applications move through stages

> For Gmail, enable **2-Factor Authentication** and generate an **App Password** from [myaccount.google.com/apppasswords](https://myaccount.google.com/apppasswords). Use this as `SPRING_MAIL_PASSWORD`.

---

## 🧠 Groq AI Integration

NaukariSathi uses the [Groq API](https://groq.com/) for ultra-fast inference with LLaMA models.

### How it's used:

| Feature | Prompt Strategy |
|---|---|
| **Resume Parser** | Sends raw PDF text → asks Groq to return structured JSON (name, skills, experience, etc.) |
| **Chatbot** | Multi-turn conversation with system role as a "career counselor" |
| **AI Score Match** | Sends resume + job description → returns a score (0–100) with reasoning |

### Sample Groq API Call (Java)

```java
String prompt = "Parse this resume and return JSON: " + resumeText;

HttpRequest request = HttpRequest.newBuilder()
    .uri(URI.create("https://api.groq.com/openai/v1/chat/completions"))
    .header("Authorization", "Bearer " + groqApiKey)
    .header("Content-Type", "application/json")
    .POST(HttpRequest.BodyPublishers.ofString(buildRequestBody(prompt)))
    .build();
```

---

## 🖼️ Cloudinary — Resume PDF Storage

Resumes are uploaded as PDFs and stored in Cloudinary under the `naukarisathi/resumes/` folder.

```java
Map uploadResult = cloudinary.uploader().upload(file.getBytes(),
    ObjectUtils.asMap(
        "resource_type", "raw",
        "folder", "naukarisathi/resumes",
        "public_id", userId + "_resume"
    ));
String resumeUrl = (String) uploadResult.get("secure_url");
```

The `secure_url` is saved to PostgreSQL and linked to the user profile.

---

## 🤝 Contributing

Contributions are welcome! Please follow these steps:

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/your-feature-name`
3. Commit your changes: `git commit -m 'Add: your feature description'`
4. Push to the branch: `git push origin feature/your-feature-name`
5. Open a Pull Request

---



<div align="center">

Made with ❤️ for job seekers everywhere

**NaukariSathi** — *Sathi matlab saath, hamesha.*

</div>
