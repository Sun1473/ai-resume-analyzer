# AI Resume Analyzer

A full-stack web app that uses AI to compare your resume against a job description — giving you a match score, missing keywords, and specific suggestions to improve your chances of getting an interview call.

Built with **Spring Boot** (Java), **Spring Security + JWT authentication**, **MySQL**, the **Claude API** for the AI analysis, and a **React** frontend.

## Why this project

Most fresher resumes have a generic CRUD app (Student/Library/Employee Management). This project stands out because it:
- Integrates a real third-party AI API (a skill in high demand right now)
- Implements proper JWT-based authentication from scratch (not just a tutorial CRUD)
- Solves an actual real-world problem (you can literally use it on your own job applications)

## Tech Stack

**Backend:** Java 17, Spring Boot 3, Spring Security, JWT (jjwt), Spring Data JPA, Hibernate, MySQL, Maven, Lombok
**Frontend:** React 18, Axios
**AI:** Anthropic Claude API (`/v1/messages`)

## Features

- User registration & login with JWT-based authentication (stateless, industry-standard)
- Passwords hashed with BCrypt — never stored in plain text
- Submit resume text + job description → get an AI-generated match score (0-100), missing keywords, and improvement suggestions
- Analysis history saved per user
- Clean error handling (validation errors, auth errors, AI service failures all return proper JSON responses)

## Project Structure

```
ai-resume-analyzer/
├── backend/
│   └── src/main/java/com/ara/resumeanalyzer/
│       ├── controller/     # AuthController, ResumeController
│       ├── service/          # AiAnalysisService (Claude API call), ResumeAnalysisServiceImpl
│       ├── security/         # JwtUtil, JwtAuthFilter, CustomUserDetailsService
│       ├── config/            # SecurityConfig
│       ├── entity/             # User, ResumeAnalysis
│       ├── repository/       # Spring Data JPA repositories
│       ├── dto/                 # Request/response DTOs
│       └── exception/       # GlobalExceptionHandler
└── frontend/
    └── src/
        ├── components/    # AuthForm, AnalyzerForm, History
        └── services/         # api.js (axios + JWT interceptor)
```

## How to Run Locally

### 1. Claude API key (optional — the app also runs in Mock Mode for free)

This project can run in two modes:

- **Mock Mode (default, free):** If no real API key is set, `AiAnalysisService` automatically
  falls back to a local keyword-overlap analysis instead of calling the real Claude API.
  This lets you run and demo the entire app — auth, database, frontend, the full flow —
  without spending anything. Responses are prefixed with `[MOCK MODE]` so it's clear
  during a demo that this isn't a live AI call.
- **Live Mode:** Sign up at [console.anthropic.com](https://console.anthropic.com), create an
  API key, and set it as an environment variable (never hardcode it or commit it to GitHub):
  ```bash
  export ANTHROPIC_API_KEY=sk-ant-xxxxxxxx
  ```
  Once this is set, the app automatically switches to real AI-powered analysis — no code changes needed.

### 2. Backend
Prerequisites: Java 17+, Maven, MySQL (or skip MySQL and use the H2 profile below).

```bash
cd backend
# Edit src/main/resources/application.properties with your MySQL credentials
# Also change jwt.secret to your own random string before deploying anywhere public

mvn spring-boot:run
```
Runs at `http://localhost:8080`.

> No MySQL? Use the in-memory H2 database instead:
> `mvn spring-boot:run -Dspring-boot.run.profiles=h2`

### 3. Frontend
```bash
cd frontend
npm install
npm start
```
Runs at `http://localhost:3000`.

## API Endpoints

| Method | Endpoint | Auth Required | Description |
|---|---|---|---|
| POST | `/api/auth/register` | No | Create a new account |
| POST | `/api/auth/login` | No | Login, returns a JWT |
| POST | `/api/resume/analyze` | Yes | Submit resume + JD for AI analysis |
| GET | `/api/resume/history` | Yes | Get past analyses for the logged-in user |

## How Authentication Works (important for interviews!)

1. User registers/logs in → backend verifies credentials and generates a signed JWT containing the username + expiry.
2. Frontend stores this token and attaches it as `Authorization: Bearer <token>` on every subsequent request (see `frontend/src/services/api.js`).
3. `JwtAuthFilter` intercepts every backend request, validates the token, and sets the authenticated user in Spring Security's context — so protected endpoints know who's calling without needing a session.
4. This is **stateless** — the server doesn't store any session data, which is why JWT scales well for REST APIs.

## How the AI Integration Works

`AiAnalysisService` builds a prompt instructing Claude to respond in a strict JSON format (score, missing keywords, suggestions), calls the `/v1/messages` endpoint with the API key, and parses the JSON reply. If the AI API fails or is rate-limited, it gracefully falls back to an error message instead of crashing the whole request — worth mentioning in interviews as a resilience/error-handling decision.

## Deployment (recommended before adding to your resume)

- **Backend:** Render, Railway, or Fly.io (free tiers available)
- **Frontend:** Vercel or Netlify (free, very easy for React)
- **Database:** Railway/Render also offer free MySQL instances, or use PlanetScale

Having a **live demo link** on your resume (not just a GitHub link) makes a huge difference — recruiters can actually try it.

## Possible Future Improvements (good to mention in interviews as "next steps")

- Rate-limit the `/analyze` endpoint per user to control AI API costs
- Add resume file upload (PDF parsing) instead of plain text paste
- Add refresh tokens for longer-lived sessions
- Cache repeated JD analyses to reduce API calls
- Add role-based access if building an admin/recruiter view later

## Running Tests
```bash
cd backend
mvn test
```
