# Campus Circular Market — College Java Mini-Project

A complete campus marketplace web application built with **HTML5, CSS3, Vanilla JavaScript, Java Spring Boot, and MySQL**.

Students can discover, search, and buy second-hand academic items (textbooks, calculators, drawing instruments, lab coats, and electronics) from fellow students within their college campus.

---

## 1. System Architecture

```
┌────────────────────────────────────────────────────────┐
│             Frontend (HTML5 / CSS3 / Vanilla JS)       │
│  - Deep Navy (#14243A) & Teal (#16B8A6) visual design  │
│  - Responsive Product Grid, Live Filter & Search       │
│  - Photo Upload with live preview, Item Management     │
└───────────────────────────┬────────────────────────────┘
                            │ REST API (JSON / Multipart)
┌───────────────────────────▼────────────────────────────┐
│              Java Spring Boot 3 Backend                │
│  - Controllers: AuthController, ItemController         │
│  - Services: AuthService, ItemService, FileStorage     │
│  - Repositories: UserRepository, ItemRepository        │
│  - Database Access: Spring Data JPA & Hibernate        │
│  - Security: Role-Based Access Control (RBAC)          │
└───────────────────────────┬────────────────────────────┘
                            │ JDBC Connection (Port 3306)
┌───────────────────────────▼────────────────────────────┐
│                    MySQL Database                      │
│  - Database: campus_market_db                          │
│  - Tables: users, items                                │
└────────────────────────────────────────────────────────┘
```

---

## 2. Key Features & Role-Based Permissions

1. **Everyone (Guests & Students)**:
   - Browse all available listings with photos, prices in Indian Rupees (₹), and descriptions.
   - Live search by keyword (searches name, category, and description).
   - Filter by category (Books, Electronics, Engineering Tools, Lab Supplies, Stationery, Accessories, Other).
   - Filter by minimum and maximum price (₹).
   - Sort by newest or price (low to high / high to low).
   - View item details and see if an item is available or sold.

2. **Registered Students (`ROLE_USER`)**:
   - Sell an item: Upload product photos (JPG, PNG, WebP $\le 10\text{ MB}$), set price, description, and contact info.
   - View verified seller contact details (email / phone number).
   - View their own listings under **My Listings**.
   - Edit only their personally posted listings.
   - Toggle status of their items between **Available** and **Sold**.
   - Delete only their own listings.

3. **Administrator (`ROLE_ADMIN`)**:
   - Everything a normal user can do.
   - **Manage All Listings**: Can edit or delete any listing across the entire campus.
   - **Reset Demo Data**: Restore the 8 original demonstration items and images at any time.

---

## 3. Technology Stack

* **Frontend**: Pure HTML5, CSS3, Vanilla JavaScript (ES6+). Zero build steps or npm packages required.
* **Backend**: Java 17, Spring Boot 3.2.5.
* **ORM & Database**: Spring Data JPA, Hibernate, MySQL Connector/J.
* **Database**: MySQL 8.0+ (or MariaDB / XAMPP).
* **Storage**: Local filesystem storage under `./uploads/` with pre-packaged demo assets in `uploads/demo/`.

---

## 4. Default Demo Accounts

On the first application launch, the database automatically pre-seeds:

| Account Type | Email | Password | Role / Access |
| :--- | :--- | :--- | :--- |
| **Administrator** | `admin@campus.edu` | `admin123` | Can manage all listings & reset demo data |
| **Student** | `student@campus.edu` | `student123` | Can post listings & manage own items |

---

## 5. Prerequisites & Setup

### A. Prerequisites
1. **Java Development Kit (JDK 17 or higher)**
   - Verify with: `java -version`
   - Download from: [Eclipse Adoptium Temurin 17](https://adoptium.net/temurin/releases/?version=17)
2. **MySQL Server (or XAMPP / WampServer)**
   - Ensure MySQL service is running on port `3306`.
3. **Apache Maven** (Optional if using IDE or Maven wrapper).

### B. Database Configuration
By default, the application connects to MySQL using standard local credentials:
* **Host**: `localhost:3306`
* **Database**: `campus_market_db` (Spring Boot will auto-create this if it doesn't exist)
* **Username**: `root`
* **Password**: *(blank)*

To change database credentials, open `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/campus_market_db?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=your_mysql_password
```

---

## 6. How to Run the Project

### Option 1: In IntelliJ IDEA / Eclipse / VS Code
1. Open the `campus-market-java` folder as a Maven project.
2. Allow your IDE to import Maven dependencies.
3. Open `src/main/java/com/campus/market/CampusMarketApplication.java`.
4. Click **Run** / **Debug**.
5. Open your browser and navigate to:
   ```
   http://localhost:8080
   ```

### Option 2: Using Command Prompt / Terminal
From inside the `campus-market-java` folder:
```bash
# If Maven is installed:
mvn spring-boot:run

# Or on Windows using run.bat:
run.bat
```

---

## 7. Project File Structure

```
campus-market-java/
├── pom.xml                                  # Maven dependencies and build configuration
├── run.bat                                  # 1-click startup script for Windows
├── README.md                                # Project documentation
└── src/
    └── main/
        ├── java/com/campus/market/
        │   ├── CampusMarketApplication.java # Spring Boot entry point
        │   ├── config/                      # Web MVC and demo data initializer
        │   ├── controller/                  # AuthController, ItemController
        │   ├── dto/                         # Request & Response Data Transfer Objects
        │   ├── entity/                      # User and Item JPA entities
        │   ├── exception/                   # Global exception handler
        │   ├── repository/                  # Spring Data JPA repositories
        │   ├── service/                     # AuthService, ItemService, FileStorageService
        │   └── util/                        # Secure password hashing (SHA-256 + salt)
        └── resources/
            ├── application.properties       # Database and server configuration
            ├── schema.sql                   # MySQL table definitions
            └── static/                      # Frontend HTML, CSS, JS and uploaded images
                ├── index.html               # Home discovery page
                ├── browse.html              # Search, filter and sort marketplace
                ├── item-details.html        # Single product view and contact reveal
                ├── sell.html                # Listing upload form
                ├── edit.html                # Edit listing form
                ├── my-listings.html         # User listings & admin management
                ├── auth.html                # Sign in / Register tabbed view
                ├── css/style.css            # Navy (#14243A) & Teal (#16B8A6) stylesheet
                ├── js/api.js                # Centralized REST client and utilities
                ├── js/auth.js               # Session auth state and header updater
                └── uploads/demo/            # The 8 original high-res demo photos
```

---

## 8. Common Viva Questions & Answers

**Q1: How does the application prevent temporary browser storage issues?**
> All listings and user accounts are permanently stored in a relational MySQL database (`campus_market_db`). The browser frontend only renders data retrieved dynamically from the Spring Boot REST API (`/api/items`).

**Q2: How are permissions enforced securely?**
> Permissions are enforced on the backend in `ItemService.java`. When an edit (`PUT`) or delete (`DELETE`) request arrives, the backend retrieves the logged-in user from the session and checks whether `user.isAdmin()` or `item.getUser().equals(currentUser)`. Modifying frontend code or sending a fabricated request cannot bypass this server-side validation.

**Q3: How are uploaded product photos handled?**
> When a student submits a listing with a photo, `FileStorageService.java` validates the file type (`.jpg`, `.jpeg`, `.png`, `.webp`) and file size ($\le 10\text{ MB}$). It generates a unique UUID filename to prevent overwriting and path traversal attacks, saves the image to the local `./uploads/` directory, and stores the relative URL path in MySQL.

