# PacePlate — Fitness Tracker API

> Spring Boot backend powering PacePlate — India's fitness tracker with a built-in Indian food database.

---

## Tech Stack

| Component | Technology |
|-----------|------------|
| Framework | Spring Boot 3.5 (Java 17) |
| Database  | MongoDB (Atlas or local) |
| Auth      | JWT (JJWT 0.12.3) |
| Security  | Spring Security + BCrypt + Rate Limiting (Bucket4j) |
| Payments  | Razorpay |
| Email     | Spring Mail (SMTP) |
| Deploy    | Docker |

---

## Prerequisites

- Java 17+
- Maven 3.8+
- MongoDB (local or [MongoDB Atlas](https://www.mongodb.com/atlas))
- A Razorpay account (for payment features)
- SMTP email credentials (Gmail, Brevo, SendGrid, etc.)

---

## Getting Started

### 1. Clone and navigate

```bash
git clone https://github.com/your-org/fitness-tracker.git
cd fitness-tracker/fitness-app-API
```

### 2. Set up environment variables

Copy the example env file and fill in your values:

```bash
cp .env.example .env
```

Edit `.env`:

```env
# MongoDB
MONGO_URI=mongodb://localhost:27017/fitnessdb
# Or MongoDB Atlas:
# MONGO_URI=mongodb+srv://<user>:<password>@cluster0.xxxxx.mongodb.net/fitnessdb

# JWT
JWT_SECRET=your-minimum-256-bit-random-secret-here
JWT_EXPIRATION_MS=86400000

# CORS — set to your frontend URL in production
CORS_ALLOWED_ORIGINS=http://localhost:5173

# Email (SMTP)
SPRING_MAIL_HOST=smtp.gmail.com
SPRING_MAIL_PORT=587
SPRING_MAIL_USERNAME=your@gmail.com
SPRING_MAIL_PASSWORD=your-app-password
APP_PASSWORD_RESET_URL=http://localhost:5173/reset-password

# Razorpay
RAZORPAY_KEY_ID=rzp_test_xxxx
RAZORPAY_KEY_SECRET=your-secret
```

### 3. Run the application

**Using Maven (development):**
```bash
./mvnw spring-boot:run
```

**Using the compiled JAR:**
```bash
./mvnw clean package -DskipTests
java -jar target/FitnessTracker-1.0.0.jar
```

**Using Docker:**
```bash
docker build -t paceplate-api .
docker run -p 8080:8080 --env-file .env paceplate-api
```

The API will start on `http://localhost:8080`.

---

## API Endpoints

### Auth (`/api/auth`)
| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| POST | `/register` | ❌ | Register new user |
| POST | `/login` | ❌ | Login, returns JWT |
| GET  | `/me` | ✅ | Get current user |
| POST | `/forgot-password` | ❌ | Send password reset email |
| POST | `/reset-password` | ❌ | Reset password with token |
| DELETE | `/delete-account` | ✅ | Permanently delete account (DPDPA) |

### Profiles (`/api/users/{userId}/profiles`)
| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| GET | `/` | ✅ | Get user profiles |
| POST | `/` | ✅ | Create profile |
| PUT | `/{profileId}` | ✅ | Update profile |
| PUT | `/{profileId}/goals` | ✅ | Update daily goals |
| PUT | `/{profileId}/water` | ✅ | Set water intake |
| POST | `/{profileId}/water/add` | ✅ | Add water intake |

### Food Database (`/api/foods`)
| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| GET | `/` | ❌ | List all foods (paginated) |
| GET | `/search?q=` | ❌ | Search foods |
| GET | `/category/{cat}` | ❌ | Foods by category |
| GET | `/barcode/{code}` | ❌ | Food by barcode |
| GET | `/categories` | ❌ | List all categories |
| POST | `/` | 🔒 ADMIN | Create food item |
| PUT | `/{id}` | 🔒 ADMIN | Update food item |
| DELETE | `/{id}` | 🔒 ADMIN | Delete food item |

### Food Log (`/api/profiles/{profileId}/food-log`)
| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| GET | `/` | ✅ | Today's food log |
| GET | `/{date}` | ✅ | Log for specific date |
| GET | `/range?start=&end=` | ✅ | Log for date range |
| GET | `/all` | ✅ | All logs |
| POST | `/` | ✅ | Add food item |
| DELETE | `/{itemId}` | ✅ | Remove food item |

### Workout Log (`/api/profiles/{profileId}/workout-log`)
Similar to food log — GET today, date, range, all; POST add; PUT update; DELETE remove.

### Payments (`/api/payment`)
| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| POST | `/create-order` | ✅ | Create Razorpay order |
| POST | `/verify` | ✅ | Verify payment signature |

### Restaurants & Orders (`/api/restaurants`, `/api/orders`)
Full CRUD for restaurant management and order tracking (admin routes protected).

---

## Health Check

```bash
curl http://localhost:8080/actuator/health
# {"status":"UP"}
```

Used by Docker and container orchestration platforms for liveness probes.

---

## Security

- **JWT Auth**: All protected endpoints require `Authorization: Bearer <token>` header
- **Passwords**: BCrypt hashed, never stored in plain text
- **Rate Limiting**: 20 req/min on auth endpoints, 5 req/hour on password reset (per IP)
- **CORS**: Restricted to `CORS_ALLOWED_ORIGINS` — set your production domain here
- **Input Validation**: `@Valid` annotations on all request bodies

---

## Data Seeding

On first startup, `DataInitializer.java` seeds the database with the Indian food database (300+ items) and sample exercises if the database is empty.

---

## Deployment (Docker Compose)

From the project root:

```bash
docker-compose up --build
```

This starts both the API (port 8080) and serves the frontend via Nginx (port 80).

Set production environment variables in `docker-compose.yml` or use a `.env` file at the root.

---

## Contact

Questions? Email [dev@paceplate.in](mailto:dev@paceplate.in)
