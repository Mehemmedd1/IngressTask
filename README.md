# Meta Post Performance Analysis

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.15-brightgreen)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-17-orange)](https://www.oracle.com/java/)
[![License](https://img.shields.io/badge/License-MIT-blue)](LICENSE)

> A **Spring Boot** web application that fetches the last 20 posts from the **Meta Graph API**, analyzes engagement metrics (likes, comments, engagement score), and presents the results through a **terminal report**, **REST API**, and **Bootstrap-styled HTML dashboard**.

---

## Table of Contents

- [Features](#-features)
- [Tech Stack](#-tech-stack)
- [Project Structure](#-project-structure)
- [Getting Started](#-getting-started)
- [API Endpoints](#-api-endpoints)
- [Analysis Methodology](#-analysis-methodology)
- [Sample Results](#-sample-results)
- [Mock Data Mode](#-mock-data-mode)
- [Deployment (Railway)](#-deployment-railway)
- [Configuration Reference](#-configuration-reference)

---

## Features

| Feature | Description |
|---------|-------------|
| **Top 3 Posts** | Identifies the highest-engagement posts (likes + comments) |
| **Likes by Day of Week** | Aggregates total likes per weekday to find the best posting day |
| **Best Day Detection** | Automatically highlights the weekday with the most accumulated likes |
| **Average Engagement** | Computes the arithmetic mean of engagement across all posts |
| **Dynamic Insight** | Generates a human-readable Azerbaijani summary of the analysis |
| **Triple Output** | Terminal table + HTML dashboard (Bootstrap 5) + JSON API |
| **Secure Config** | Access token loaded from `.env` — never hardcoded in source code |

---

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Language | Java 17 |
| Framework | Spring Boot 3.5.15 |
| Templating | Thymeleaf |
| UI | Bootstrap 5.3.3 |
| Config Loading | spring-dotenv 4.0.0 |
| Boilerplate | Lombok |
| Build Tool | Maven (with Maven Wrapper) |
| External API | Meta Graph API v21.0 |

---

## Project Structure

```
meta-post-analysis/
├── .env                          # Environment variables (NOT committed to git)
├── .env.example                  # Template file for .env
├── .gitignore
├── pom.xml                       # Maven configuration
├── src/
│   ├── main/
│   │   ├── java/org/test/metapostanalysis/
│   │   │   ├── MetaPostAnalysisApplication.java   # Spring Boot entry point
│   │   │   ├── config/
│   │   │   │   └── MetaApiConfig.java             # @ConfigurationProperties for Meta API
│   │   │   ├── controller/
│   │   │   │   └── AnalyticsController.java       # REST + Thymeleaf endpoints
│   │   │   ├── dto/
│   │   │   │   ├── AnalyticsReportDto.java        # Analysis result DTO
│   │   │   │   ├── CommentsSummary.java           # Comments summary model
│   │   │   │   ├── LikesSummary.java              # Likes summary model
│   │   │   │   ├── MetaPostResponse.java          # API response wrapper
│   │   │   │   ├── Paging.java                    # Pagination model
│   │   │   │   └── PostData.java                  # Post entity with nested connections
│   │   │   ├── runner/
│   │   │   │   └── AnalyticsTerminalRunner.java   # CommandLineRunner for terminal output
│   │   │   └── service/
│   │   │       ├── MetaApiService.java            # Data provider (mock or real API)
│   │   │       └── PostAnalyticsService.java      # Stream API analysis engine
│   │   └── resources/
│   │       ├── application.properties             # Spring config
│   │       └── templates/
│   │           └── analytics.html                 # Bootstrap HTML report page
│   └── test/
│       └── java/org/test/metapostanalysis/
│           └── MetaPostAnalysisApplicationTests.java
```

---

## Getting Started

### Prerequisites

- **Java 17** or higher ([download](https://adoptium.net/))
- **Git** ([download](https://git-scm.com/))
- A **Meta Developer account** with a test app (for real API mode) — see [Mock Data Mode](#-mock-data-mode) if you don't have one

### Installation

**1. Clone the repository**
```bash
git clone https://github.com/Mehemmedd1/IngressTask.git
cd IngressTask
```

**2. Create the `.env` file**

Copy the template and edit it with your credentials:
```bash
# On Linux / macOS
cp .env.example .env

# On Windows (PowerShell)
Copy-Item .env.example .env
```

Edit `.env`:
```env
META_ACCESS_TOKEN=your_page_access_token_here
META_API_BASE_URL=https://graph.facebook.com
META_API_VERSION=v21.0
META_PAGE_ID=your_page_id_here
```

> **Note:** The `.env` file is **never committed** to version control (it is listed in `.gitignore`).

**3. Run the application**
```bash
# On Linux / macOS
./mvnw spring-boot:run

# On Windows
mvnw.cmd spring-boot:run
```

**4. Open the report**

Once the application starts, the terminal will display a structured analysis report. You can also access:

| URL | Description |
|-----|-------------|
| http://localhost:8080/ | HTML dashboard (Bootstrap 5) |
| http://localhost:8080/api/analytics | JSON response |
| http://localhost:8080/api/health | Health check |

---

## API Endpoints

| Method | Path | Content-Type | Description |
|--------|------|--------------|-------------|
| `GET` | `/` | `text/html` | Renders the Bootstrap-styled analytics dashboard |
| `GET` | `/api/analytics` | `application/json` | Returns the full analysis report as JSON |
| `GET` | `/api/health` | `application/json` | Simple liveness probe for deployment platforms |

**Example JSON response (`/api/analytics`):**
```json
{
  "topEngagementPosts": [
    {
      "message": "Bu gün komandamız ilə birlikdə startup ekosistemi haqqında...",
      "created_time": "2026-06-20T10:30:00+0000",
      "likeCount": 487,
      "commentCount": 95,
      "engagement": 582
    }
  ],
  "likesByDay": {
    "MONDAY": 213, "TUESDAY": 315, "WEDNESDAY": 201,
    "THURSDAY": 484, "FRIDAY": 625, "SATURDAY": 1298, "SUNDAY": 810
  },
  "bestDay": "SATURDAY",
  "bestDayLikes": 1298,
  "averageEngagement": 232.75,
  "insight": "Ən uğurlu günümüz Şənbə günüdür...",
  "totalPostsAnalysed": 20,
  "totalLikes": 3946,
  "totalComments": 709
}
```

---

## Analysis Methodology

The analysis pipeline is implemented entirely with the **Java Stream API** inside `PostAnalyticsService`:

```
MetaApiService (20 posts)
       │
       ▼
PostAnalyticsService.generateReport()
       │
       ├── findTopEngagementPosts()
       │   └── sorted(comparingLong(getEngagement).reversed()).limit(3)
       │
       ├── calculateLikesByDay()
       │   └── groupingBy(extractDayOfWeek, summingLong(getLikeCount))
       │
       ├── findBestDay()
       │   └── max(comparingLong(entry.getValue))
       │
       ├── calculateAverageEngagement()
       │   └── mapToLong(getEngagement).average()
       │
       └── buildInsight()
           └── Dynamic Azerbaijani summary string
       │
       ▼
AnalyticsReportDto
       │
       ├── AnalyticsController (HTML + JSON)
       ├── AnalyticsTerminalRunner (stdout table)
```

| Metric | Formula |
|--------|---------|
| **Engagement** | `likes + comments` |
| **Top 3 Posts** | Posts sorted descending by engagement, limited to 3 |
| **Likes by Day** | Sum of all likes grouped by `DayOfWeek` extracted from `created_time` |
| **Best Day** | The `DayOfWeek` with the maximum total likes |
| **Average Engagement** | Sum of all engagement values / total number of posts |

---

## Sample Results

Based on the 20 mock posts (see [Mock Data Mode](#-mock-data-mode)):

### Summary Statistics

| Metric | Value |
|--------|-------|
| Posts analysed | 20 |
| Total likes | 3,946 |
| Total comments | 709 |
| Average engagement | 232.75 |
| Best posting day | Saturday (1,298 likes) |

### Top 3 Posts by Engagement

| Rank | Message Preview | Date | Likes | Comments | Engagement |
|------|-----------------|------|-------|----------|------------|
| #1 | Bu gün komandamız ilə birlikdə startup ekosistemi... | 20 Jun 2026 | 487 | 95 | **582** |
| #2 | Müştərilərimizdən aldığımız ən yaxşı rəyləri... | 06 Jun 2026 | 455 | 88 | **543** |
| #3 | Bazar günü istirahət edə-edə növbəti böyük buraxılışın... | 14 Jun 2026 | 421 | 76 | **497** |

### Likes by Day of Week

| Day | Total Likes | Relative |
|-----|-------------|----------|
| Monday | 213 | ██░░░░░░░░ 16% |
| Tuesday | 315 | ███░░░░░░░ 24% |
| Wednesday | 201 | █░░░░░░░░░ 15% |
| Thursday | 484 | ████░░░░░░ 37% |
| Friday | 625 | █████░░░░░ 48% |
| **Saturday** | **1,298** | ██████████ 100% |
| Sunday | 810 | ██████░░░░ 62% |

### Generated Insight

> *"Ən uğurlu günümüz Şənbə günüdür (cəmi 1,298 like). Ümumi 20 post üzrə orta engagement göstəricisi 232.8-dir. Həftə sonları digər günlərə nəzərən daha yüksək qarşılıqlı təsir alır."*

---

## Mock Data Mode

Due to permission restrictions on the Meta Developer dashboard, this project currently runs in **mock mode**: the `MetaApiService` returns 20 hardcoded posts that **exactly mirror the real Meta Graph API JSON structure**:

```json
{
  "message": "...",
  "created_time": "2026-06-20T10:30:00+0000",
  "likes": { "summary": { "total_count": 487 } },
  "comments": { "summary": { "total_count": 95 } }
}
```

The mock dataset spans **20 days (June 1–20, 2026)** with realistic engagement patterns:
- **Weekends** (Saturday, Sunday) have significantly higher engagement
- **Likes** range from 23 to 487
- **Comments** range from 3 to 95

### Switching to Real API

Replace the `getRecentPosts()` method in `MetaApiService` with a real HTTP call to:
```
GET /{page-id}/posts?fields=message,created_time,likes.summary(true),comments.summary(true)&limit=20&access_token={token}
```

The required Graph API permissions are:
- `pages_show_list`
- `pages_read_engagement`

---

## Deployment (Railway)

### Step-by-Step Railway Setup

1. Go to [https://railway.app](https://railway.app) and sign in with GitHub
2. Click **"New Project"** → **"Deploy from GitHub repo"**
3. Select the **`IngressTask`** repository
4. Go to the **"Variables"** tab and add the environment variables:

   | Variable | Value |
   |----------|-------|
   | `META_ACCESS_TOKEN` | `your_page_access_token` |
   | `META_API_BASE_URL` | `https://graph.facebook.com` |
   | `META_API_VERSION` | `v21.0` |
   | `META_PAGE_ID` | `your_page_id` |

5. Railway will automatically detect the Spring Boot app and start deployment
6. Once deployed, click **"Generate Domain"** to get a public URL

### Railway Deploy Link

> **Deploy URL:** _[Add your Railway URL here after deployment]_

---

## Configuration Reference

All configuration is loaded from `.env` via `spring-dotenv`:

| Property | Env Variable | Default | Description |
|----------|--------------|---------|-------------|
| `meta.api.base-url` | `META_API_BASE_URL` | `https://graph.facebook.com` | Graph API base URL |
| `meta.api.version` | `META_API_VERSION` | `v21.0` | API version |
| `meta.api.access-token` | `META_ACCESS_TOKEN` | _(required)_ | Page access token |
| `meta.api.page-id` | `META_PAGE_ID` | `me` | Facebook Page ID |
| `server.port` | — | `8080` | Application port |

---

## License

This project was developed as part of a **Backend / Data Intern** application task.
