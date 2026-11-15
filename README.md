# SmartQueue – Smart Queue Management System

A college demonstration project built with Java 17+, Spring Boot, Thymeleaf, JavaScript and a persistent H2 database.

## Features
- Customer token generation with unique daily token numbers
- Admin dashboard with total, waiting, serving and completed counts
- Call next customer (automatically completes the previous serving ticket)
- Complete, skip and cancel waiting/serving tickets
- Token tracking by token number and waiting position
- REST API endpoints
- H2 file database: data is stored locally and survives application restarts
- Responsive web interface

## Easiest way to run on Windows (Maven does not need to be installed globally)

1. Extract this ZIP completely.
2. Install **Java JDK 17 or newer** if it is not already installed: https://adoptium.net/temurin/releases/ (choose Windows x64, JDK).
3. Open the extracted `SmartQueue` folder.
4. Double-click **`RUN_SMARTQUEUE.bat`**.
5. On the first run, the helper downloads Maven into your Windows user profile (it does not change system PATH) and Maven downloads the project's dependencies. Internet is required for the first run.
6. Wait for `Started SmartQueueApplication` in the terminal.
7. Open http://localhost:8080
8. Keep the terminal window open while using the application. Press Ctrl+C to stop the server.

If Windows blocks the batch file, right-click it and choose **Open**, or open PowerShell in this folder and run:

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File .\run-smartqueue.ps1
```

### Alternative: run from a terminal
If Maven is already installed and available on PATH, open a terminal in the folder containing `pom.xml` and run:

```powershell
mvn spring-boot:run
```

## Pages
- Admin dashboard: http://localhost:8080/
- Join queue: http://localhost:8080/join
- Track token: http://localhost:8080/track
- H2 console: http://localhost:8080/h2-console

H2 console JDBC settings:
- JDBC URL: `jdbc:h2:file:./data/smartqueue`
- User: `sa`
- Password: leave blank

## REST API
- `GET /api/tickets` – list all tickets
- `GET /api/stats` – dashboard counts and current ticket
- `POST /api/tickets` – create ticket, JSON: `{"customerName":"Asha"}`
- `GET /api/tickets/{token}` – find ticket by token number
- `POST /api/call-next` – call next waiting ticket
- `PATCH /api/tickets/{id}/status` – JSON status: `COMPLETED`, `SKIPPED`, or `CANCELLED`

## Troubleshooting
- **`java` is not recognized:** install a JDK 17+ and reopen PowerShell/VS Code.
- **First run download fails:** check internet access, then double-click `RUN_SMARTQUEUE.bat` again.
- **Port 8080 is already in use:** stop the other app using port 8080 before starting SmartQueue.
- **Browser cannot connect:** check the terminal for `Started SmartQueueApplication`; keep the terminal open.

## Important
This is an academic demonstration project. The admin dashboard is not password-protected in this version; do not expose it to the public internet without adding authentication, authorization, input-rate limits and production database configuration.
