# Personal Task & Duty Management System

**Author:** Yanamala Sanjay
**Institution:** Indian Institute of Technology Bhubaneswar
**Organization:** Reliance Industries Limited
**Tech Stack:** Spring Boot, MySQL, Scheduled Jobs, Java 17

---

## 📋 Project Overview

A comprehensive task management system built to track daily reporting duties and material data updates at Reliance Industries Ltd. The system features automated reminder notifications, a recurring task engine supporting daily, weekly, and monthly schedules, and an email digest system delivering task summaries and priority-based alerts for deadline management.

### Key Features

- ✅ **Task CRUD Operations** - Complete task lifecycle management
- ✅ **Recurring Task Engine** - Automated task generation using cron jobs
- ✅ **Daily/Weekly/Monthly Schedules** - Flexible recurrence patterns
- ✅ **Email Digest System** - Daily task summaries sent to users
- ✅ **Priority-Based Alerts** - Deadline reminders based on task priority
- ✅ **Automated Reminders** - Smart notification system
- ✅ **Task Templates** - Reusable patterns for recurring duties

---

## 🏗️ Architecture

```
┌─────────────────────────┐
│   Client / User         │
└────────┬────────────────┘
         │ REST API
┌────────▼────────────────┐
│   Controller Layer      │
└────────┬────────────────┘
         │
┌────────▼────────────────┐
│   Service Layer         │
│ - TaskService           │
│ - TaskTemplateService   │
│ - EmailService          │
└────────┬────────────────┘
         │
┌────────▼────────────────┐
│   Scheduler Layer       │  ⏰ Cron Jobs
│ - TaskSchedulerService  │
│ - ReminderScheduler     │
│ - DigestScheduler       │
└────────┬────────────────┘
         │
┌────────▼────────────────┐
│   Repository Layer      │
└────────┬────────────────┘
         │
┌────────▼────────────────┐
│   MySQL Database        │
└─────────────────────────┘
```

---

## 🛠️ Tech Stack

| Component | Technology |
|-----------|-----------|
| Backend Framework | Spring Boot 3.2.0 |
| Language | Java 17 |
| Database | MySQL 8.0 |
| Scheduling | Spring @Scheduled |
| Email | Spring Mail (SMTP) |
| Build Tool | Maven |
| ORM | Spring Data JPA / Hibernate |

---

## ⏰ Scheduled Jobs

### 1. Daily Task Generation
- **Schedule:** Every day at 6:00 AM
- **Cron:** `0 0 6 * * ?`
- **Purpose:** Generate tasks from DAILY templates

### 2. Weekly Task Generation
- **Schedule:** Every day at 6:00 AM (generates on specified day of week)
- **Cron:** `0 0 6 * * ?`
- **Purpose:** Generate tasks from WEEKLY templates

### 3. Monthly Task Generation
- **Schedule:** Every day at 6:00 AM (generates on specified day of month)
- **Cron:** `0 0 6 * * ?`
- **Purpose:** Generate tasks from MONTHLY templates

### 4. Overdue Task Check
- **Schedule:** Every hour
- **Rate:** `fixedRate = 3600000` (1 hour in milliseconds)
- **Purpose:** Update status of overdue tasks

### 5. Reminder System
- **Schedule:** Every 2 hours
- **Rate:** `fixedRate = 7200000`
- **Purpose:** Send reminders for upcoming deadlines

### 6. Daily Email Digest
- **Schedule:** Every day at 8:00 AM
- **Cron:** `0 0 8 * * ?`
- **Purpose:** Send task summary emails to users

---

## 🚀 Getting Started

### Prerequisites

- JDK 17 or higher
- MySQL 8.0 or higher
- Maven 3.6+
- Git

### Installation Steps

1. **Clone the repository**
   ```bash
   git clone https://github.com/yanamalasanjay/task-management-system.git
   cd task-management-system
   ```

2. **Configure MySQL Database**
   ```bash
   mysql -u root -p
   CREATE DATABASE task_management_db;
   ```

3. **Update application.properties**
   ```properties
   spring.datasource.username=YOUR_MYSQL_USERNAME
   spring.datasource.password=YOUR_MYSQL_PASSWORD
   ```

4. **Configure Email (Optional)**
   ```properties
   spring.mail.username=your-email@gmail.com
   spring.mail.password=your-app-password
   ```

5. **Build the project**
   ```bash
   mvn clean install
   ```

6. **Run the application**
   ```bash
   mvn spring-boot:run
   ```

7. **Access the API**
   - REST API: `http://localhost:8081/api`

---

## 📡 API Endpoints

### Task Management

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/tasks` | Create new task |
| GET | `/api/tasks/{id}` | Get task by ID |
| GET | `/api/tasks/user/{userId}` | Get all user tasks |
| GET | `/api/tasks/user/{userId}/pending` | Get pending tasks (sorted) |
| GET | `/api/tasks/overdue` | Get overdue tasks |
| GET | `/api/tasks/due-today` | Get tasks due today |
| PUT | `/api/tasks/{id}` | Update task |
| PUT | `/api/tasks/{id}/status` | Update task status |
| DELETE | `/api/tasks/{id}` | Delete task |
| GET | `/api/tasks/user/{userId}/stats` | Get task statistics |

### Task Templates (Recurring Tasks)

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/templates` | Create task template |
| GET | `/api/templates/{id}` | Get template by ID |
| GET | `/api/templates/user/{userId}` | Get user templates |
| GET | `/api/templates/active` | Get active templates |
| PUT | `/api/templates/{id}` | Update template |
| POST | `/api/templates/{id}/toggle` | Toggle active status |
| DELETE | `/api/templates/{id}` | Delete template |

---

## 🧪 Testing the Application

### Create a Task

```bash
curl -X POST http://localhost:8081/api/tasks \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "title": "Update Material Data",
    "description": "Daily material inventory update",
    "priority": "HIGH",
    "dueDate": "2024-12-25",
    "category": "Material Data Update"
  }'
```

### Create a Recurring Task Template

```bash
curl -X POST http://localhost:8081/api/templates \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "title": "Daily Reporting",
    "description": "Submit daily work report",
    "priority": "MEDIUM",
    "recurrenceType": "DAILY",
    "daysToComplete": 1,
    "category": "Daily Reporting"
  }'
```

### Get User's Pending Tasks

```bash
curl http://localhost:8081/api/tasks/user/1/pending
```

### Get Task Statistics

```bash
curl http://localhost:8081/api/tasks/user/1/stats
```

---

## 🎯 Key Design Decisions

### 1. Cron-Based Task Generation
**Why?** Need automated, time-based task creation for recurring duties.

**Implementation:**
- Spring's `@Scheduled` annotation
- Separate schedulers for daily, weekly, monthly patterns
- Template-based task generation
- Prevents duplicate generation with `lastGenerated` timestamp

**Cron Syntax:**
```
┌───────────── second (0-59)
│ ┌───────────── minute (0-59)
│ │ ┌───────────── hour (0-23)
│ │ │ ┌───────────── day of month (1-31)
│ │ │ │ ┌───────────── month (1-12)
│ │ │ │ │ ┌───────────── day of week (0-7)
│ │ │ │ │ │
* * * * * *
```

### 2. Email Digest System
**Why?** Users need a consolidated view of daily tasks.

**Implementation:**
- Scheduled daily at 8:00 AM
- Aggregates tasks due today, upcoming, and overdue
- Provides statistics (total, completed, pending)
- Asynchronous sending to avoid blocking

### 3. Priority-Based Reminders
**Why?** Critical tasks need earlier reminders.

**Logic:**
- CRITICAL tasks: Remind 2 days before
- Other tasks: Remind 1 day before
- Prevents spam with `reminderSent` flag

### 4. Task Template Pattern
**Why?** Recurring tasks follow same structure.

**Benefits:**
- DRY principle (Don't Repeat Yourself)
- Centralized management of recurring patterns
- Easy to enable/disable without deletion
- Tracks generation history

---

## 📊 Database Schema

### Key Tables

**users**
- Employee information
- Email digest preferences

**tasks**
- Task details
- Status tracking
- Due dates and priorities
- Links to templates for recurring tasks

**task_templates**
- Recurring task patterns
- Schedule configuration
- Cron expressions
- Generation tracking

---

## 🔄 Recurring Task Flow

```
1. Scheduler runs (6:00 AM daily)
   ↓
2. Fetch active templates by recurrence type
   ↓
3. For each template:
   - Check if already generated today
   - Check if matches schedule (day of week/month)
   ↓
4. Generate task from template
   - Set due date based on daysToComplete
   - Assign to user
   - Mark template as generated
   ↓
5. Task created and ready for user
```

---

## 📧 Email Notifications

### 1. Task Creation
- Sent when new task is assigned
- Contains task details and due date

### 2. Status Updates
- Sent when task status changes
- Especially for completion

### 3. Reminders
- Priority-based timing
- Urgent flag for critical tasks
- Contains days until deadline

### 4. Daily Digest
- Sent at 8:00 AM
- Includes:
  - Task statistics
  - Tasks due today
  - Upcoming tasks (next 7 days)
  - Overdue tasks (action required)

---

## ⚙️ Configuration

### Cron Expression Examples

```properties
# Every day at 6:00 AM
0 0 6 * * ?

# Every Monday at 9:00 AM
0 0 9 * * MON

# First day of every month at 9:00 AM
0 0 9 1 * ?

# Every hour
0 0 * * * ?

# Every 30 minutes
0 */30 * * * ?
```

### Email Configuration (Gmail)

1. Enable 2-Step Verification in Gmail
2. Generate App Password
3. Update application.properties:
```properties
spring.mail.username=your-email@gmail.com
spring.mail.password=your-16-digit-app-password
```

---

## 🐛 Troubleshooting

### Scheduled jobs not running
- Check logs for Spring Boot scheduler
- Verify `@EnableScheduling` is present
- Ensure application is running during scheduled time

### Email not sending
- Verify SMTP credentials
- Check Gmail App Password
- Enable "Less secure app access" (if not using App Password)
- Check firewall allows port 587

### Tasks not generating from templates
- Check template `isActive` status
- Verify recurrence configuration
- Check `lastGenerated` timestamp
- Review scheduler logs

---

## 🚀 Future Enhancements

- [ ] Task assignment to multiple users
- [ ] Task dependencies
- [ ] Gantt chart view
- [ ] Mobile app integration
- [ ] Slack/Teams integration
- [ ] Advanced analytics dashboard
- [ ] Task comments and attachments
- [ ] Subtasks and checklists

---

## 👨‍💻 Author

**Yanamala Sanjay**
- Email: sanjayyanamala07@gmail.com
- LinkedIn: [linkedin.com/in/sanjay-yanamala-1b45b9219](https://linkedin.com/in/sanjay-yanamala-1b45b9219/)
- GitHub: [github.com/yanamalasanjay](https://github.com/yanamalasanjay)
- Company: Reliance Industries Limited

---

## 📝 License

This project is created for educational and interview purposes.
