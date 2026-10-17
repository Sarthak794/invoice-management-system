# 🔥 Invoice Management System

A server-rendered **Invoice Management System** built using Java, Spring Boot, Thymeleaf, and MySQL. This application helps businesses manage customers, products, invoices, inventory, payments, and invoice delivery through PDF downloads and email.

## ✨ Features

### 📊 Dashboard
- View invoice and payment summaries.
- Track invoice payment statuses.

### 🧾 Invoice Management
- Create and view invoices with automatically generated invoice numbers.
- Add multiple products and quantities to a single invoice.
- Calculate invoice totals and apply percentage discounts.
- Check product stock availability before creating invoices.
- Automatically update inventory when invoices are created.
- Download invoices as PDF documents.
- Email invoice PDFs to customers.

### 👥 Customer Management
- Add new customers.
- View customer details.
- Edit existing customer information.
- Delete customers.

### 🛒 Product & Inventory Management
- Add, edit, view, and delete products.
- Manage product quantities and stock availability.
- Update inventory when invoices are generated.

### 💳 Payment Management
- Record customer payments.
- Track outstanding invoice balances.
- Manage payment statuses:
  - `DUE`
  - `PARTIAL`
  - `PAID`

### 📄 PDF & Email Integration
- Generate downloadable invoice PDFs using OpenPDF.
- Send invoice PDFs to customers through email.
- Configure email delivery using Gmail SMTP.

## 🧰 Tech Stack

| Technology | Purpose |
|---|---|
| Java 8 | Programming language |
| Spring Boot 2.2.1 | Backend framework |
| Spring MVC | Web request handling |
| Thymeleaf | Server-side HTML rendering |
| Spring Data JPA | Database operations |
| Hibernate | ORM |
| MySQL | Relational database |
| OpenPDF 1.3.30 | PDF generation |
| Spring Boot Mail | Email integration |
| Maven | Dependency management |
| Bootstrap 5.3.3 | Responsive UI |
| Bootstrap Icons 1.11.3 | Icons |

## ⚙️ Prerequisites

Before running the project, install:

- JDK 8
- Maven
- MySQL Server
- Git
- An SMTP-enabled email account for sending invoices

## 🚀 Getting Started

### 1. Clone the Repository

```bash
git clone https://github.com/Sarthak794/invoice-management-system.git
cd invoice-management-system
```

### 2. Create the MySQL Database

Open MySQL and execute:

```sql
CREATE DATABASE project;
```

### 3. Configure Database Credentials

Open:

```text
src/main/resources/application.properties
```

Update the database configuration to match your local MySQL setup.

The documented development defaults are:

| Property | Default |
|---|---|
| Host | `localhost` |
| Port | `3306` |
| Database | `project` |
| Username | `root` |
| Password | Empty |

**Important:** Use the credentials configured on your own machine. Avoid committing production database credentials to GitHub.

### 4. Configure Email Credentials

The application uses Gmail SMTP with port `587` and STARTTLS.

For Gmail, generate an **App Password** if your account supports it. Do not use your regular Gmail password.

**Windows PowerShell**

```powershell
$env:MAIL_USERNAME = "your-gmail-address@gmail.com"
$env:MAIL_PASSWORD = "your-gmail-app-password"
```

**macOS / Linux**

```bash
export MAIL_USERNAME="your-gmail-address@gmail.com"
export MAIL_PASSWORD="your-gmail-app-password"
```

Make sure your application configuration reads these environment variables before sending emails.

### 5. Run the Application

From the project directory containing `pom.xml`, execute:

```bash
mvn spring-boot:run
```

Once the application starts, open:

**http://localhost:9092**

The development configuration uses Hibernate's `ddl-auto=update` setting to update the database schema when the application starts.

### 6. Build the Application

To create a packaged JAR file:

```bash
mvn clean package
```

Run the packaged application:

```bash
java -jar target/invoice-system-0.0.1-SNAPSHOT.jar
```

Ensure the configured port and database are available before starting the application.

## 🌐 Production Deployment

The project includes a production configuration in:

```text
src/main/resources/application-prod.properties
```

The production profile expects the following environment variables:

| Environment Variable | Description |
|---|---|
| `PORT` | Application server port |
| `MYSQLHOST` | MySQL server hostname |
| `MYSQLPORT` | MySQL server port |
| `MYSQLDATABASE` | Database name |
| `MYSQLUSER` | Database username |
| `MYSQLPASSWORD` | Database password |
| `MAIL_PASSWORD` | SMTP app password |

Configure the mail username in `application-prod.properties` and ensure the configuration uses environment variables for sensitive values.

### Activate the Production Profile

```bash
java -jar target/invoice-system-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```

**Deployment checklist:**

- Configure all required environment variables.
- Verify that the production database is accessible.
- Confirm that the application listens on the port assigned by your hosting provider.
- Set `PORT` explicitly. The current documented fallback is `3306`, which is normally used for MySQL rather than the web application.
- Keep database and email credentials out of source control.
- Review database schema management before deploying updates.

## 📁 Project Structure

```text
invoice-management-system/
├── pom.xml
└── src/
    ├── main/
    │   ├── java/
    │   │   └── invoice/
    │   │       ├── InvoiceApplication.java
    │   │       ├── controller/
    │   │       ├── entity/
    │   │       ├── repository/
    │   │       └── service/
    │   └── resources/
    │       ├── application.properties
    │       ├── application-prod.properties
    │       ├── static/
    │       │   └── css/
    │       └── templates/
    └── test/
        └── resources/
            └── application.properties
```

### Package Responsibilities

- **controller/** — Handles web requests and application routes.
- **entity/** — Contains Customer, Product, Invoice, InvoiceItem, and Payment entities.
- **repository/** — Provides database access through Spring Data JPA.
- **service/** — Contains business logic.
- **templates/** — Stores Thymeleaf pages and reusable layout fragments.
- **static/css/** — Contains application stylesheets.
- **application.properties** — Development configuration.
- **application-prod.properties** — Production configuration.

## 📝 Business Rules & Notes

- Invoice amounts are displayed using the Indian Rupee symbol (`₹`).
- Percentage discounts are supported.
- Tax is not included in the current invoice calculations.
- Inventory is updated when invoices are created.
- Payment statuses distinguish unpaid, partially paid, and fully paid invoices.
- The included source does not contain authentication or authorization configuration.
- Spring Boot testing dependencies are included, but no test source files were present in the supplied project archive.

## 🔐 Security Considerations

Before using the application in production:

- Implement authentication and role-based authorization.
- Validate user input on the server.
- Protect sensitive configuration using environment variables or a secret manager.
- Use HTTPS in production.
- Review stock updates and payment calculations for transaction safety.
- Add automated tests for invoice creation, discounts, inventory updates, and payment balances.
- Configure database backups and appropriate database permissions.

## 🛠️ Troubleshooting

**Application fails to connect to MySQL**
- Confirm MySQL is running.
- Check the database name, hostname, port, username, and password.
- Ensure the `project` database exists.

**Invoice emails are not being sent**
- Verify the SMTP username and Gmail App Password.
- Confirm that the application reads the configured environment variables.
- Check the application logs for SMTP authentication or connection errors.

**Application starts on the wrong port**
- Check the active Spring Boot profile and configured server port.
- Ensure the production `PORT` environment variable is set correctly.

**PDF generation fails**
- Check the OpenPDF dependency in `pom.xml`.
- Review the application logs for PDF generation exceptions.

## 🚧 Future Enhancements

Potential improvements for future versions:

- JWT or Spring Security authentication.
- Role-based access for administrators and staff.
- Tax calculation and configurable invoice templates.
- Invoice search, filtering, and pagination.
- Automated payment reminders.
- Monthly revenue and outstanding-payment reports.
- Unit and integration testing.
- Docker-based deployment.

## 👨‍💻 Author

**Sarthak Jadhav**

- GitHub: [@Sarthak794](https://github.com/Sarthak794)

## 📄 License

This project does not specify a license. Add a `LICENSE` file if you intend to distribute or reuse the project under specific terms.
