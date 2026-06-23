# Library Borrowing System — Full Tutorial

### MVC Architecture + SQLite Database with Java & Maven

---

## Table of Contents

1. [Introduction](#1-introduction)
2. [Prerequisites](#2-prerequisites)
3. [Installing Java JDK](#3-installing-java-jdk)
4. [Installing Maven](#4-installing-maven)
5. [Understanding pom.xml](#5-understanding-pomxml)
6. [Installing VS Code & Java Extensions](#6-installing-vs-code--java-extensions)
7. [Getting the Project](#7-getting-the-project)
8. [SQLite from Scratch](#8-sqlite-from-scratch)
9. [Project Structure](#9-project-structure)
10. [Understanding MVC Pattern](#10-understanding-mvc-pattern)
11. [Understanding the Database Layer](#11-understanding-the-database-layer)
12. [Code Walkthrough](#12-code-walkthrough)
13. [How Data Flows](#13-how-data-flows)
14. [Running the Project](#14-running-the-project)
15. [Troubleshooting](#15-troubleshooting)

---

## 1. Introduction

This project is a **console-based Library Borrowing System** written in Java.
It demonstrates two important concepts:

| Concept | What it means in this project |
|---|---|
| **MVC (Model-View-Controller)** | Code is split into 3 clear layers — data, display, and logic |
| **SQLite Database** | Data is saved to a real database file so it survives when the app restarts |

Before this version, all data was stored in **arrays in memory** — meaning every time you closed the app, all books, members, and borrow records were lost. Now they are saved to a file called `library.db`.

---

## 2. Prerequisites

Before you start, make sure you have:

- [ ] Java JDK 17 or higher
- [ ] Apache Maven 3.6 or higher
- [ ] Visual Studio Code
- [ ] VS Code Java Extension Pack
- [ ] Git (to clone the project)

---

## 3. Installing Java JDK

### Windows

1. Go to [https://adoptium.net](https://adoptium.net)
2. Download **Temurin JDK 21 (LTS)** for Windows x64
3. Run the `.msi` installer — it sets up `PATH` automatically
4. Open a new **Command Prompt** and verify:

```cmd
java -version
```

Expected output:

```
openjdk version "21.0.x" ...
```

### macOS

```bash
brew install openjdk@21
echo 'export PATH="/opt/homebrew/opt/openjdk@21/bin:$PATH"' >> ~/.zshrc
source ~/.zshrc
java -version
```

### Linux (Ubuntu / Debian)

```bash
sudo apt update
sudo apt install openjdk-21-jdk -y
java -version
```

### Linux (Fedora / RHEL / CentOS)

```bash
sudo dnf install java-21-openjdk-devel -y
java -version
```

---

## 4. Installing Maven

Maven is a **build tool** for Java. It handles downloading libraries (like the SQLite driver) automatically so you never need to download `.jar` files manually.

### Windows

**Step 1** — Download Maven

Go to [https://maven.apache.org/download.cgi](https://maven.apache.org/download.cgi) and download the **Binary zip archive** (e.g., `apache-maven-3.9.x-bin.zip`)

**Step 2** — Extract it

Extract the zip to a folder, for example:

```
C:\Program Files\Apache\maven
```

**Step 3** — Set Environment Variables

1. Press `Win + S`, search for **"Environment Variables"**, click **"Edit the system environment variables"**
2. Click **"Environment Variables..."**
3. Under **System variables**, click **New**:
   - Variable name: `MAVEN_HOME`
   - Variable value: `C:\Program Files\Apache\maven`
4. Find the **`Path`** variable, click **Edit**, then **New**, and add:

```
C:\Program Files\Apache\maven\bin
```

5. Click **OK** on all windows

**Step 4** — Open a **new** Command Prompt and verify:

```cmd
mvn -version
```

Expected output:

```
Apache Maven 3.9.x ...
Java version: 21.x.x ...
```

---

### macOS

```bash
brew install maven
mvn -version
```

### Linux (Ubuntu / Debian)

```bash
sudo apt update
sudo apt install maven -y
mvn -version
```

### Linux (Fedora / RHEL / CentOS)

```bash
sudo dnf install maven -y
mvn -version
```

> **Important:** Make sure the `Java version` shown in `mvn -version` is **17 or higher**. If it shows an older version, your `JAVA_HOME` environment variable may be pointing to an old JDK.

---

## 5. Understanding pom.xml

`pom.xml` stands for **Project Object Model**. It is the heart of every Maven project — it tells Maven what your project is, what libraries it needs, and how to build it.

### Full pom.xml of this project

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
         http://maven.apache.org/xsd/maven-4.0.0.xsd">

    <modelVersion>4.0.0</modelVersion>

    <!-- Project identity -->
    <groupId>com.fad</groupId>
    <artifactId>LibraryBorrowingSystem</artifactId>
    <version>1.0.0</version>
    <packaging>jar</packaging>

    <!-- Compiler settings -->
    <properties>
        <maven.compiler.release>21</maven.compiler.release>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    </properties>

    <!-- Libraries this project needs -->
    <dependencies>
        <dependency>
            <groupId>org.xerial</groupId>
            <artifactId>sqlite-jdbc</artifactId>
            <version>3.46.1.3</version>
        </dependency>
    </dependencies>

    <!-- Build settings -->
    <build>
        <sourceDirectory>src</sourceDirectory>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-jar-plugin</artifactId>
                <version>3.3.0</version>
                <configuration>
                    <archive>
                        <manifest>
                            <mainClass>com.fad.LibrarySystem.Main</mainClass>
                        </manifest>
                    </archive>
                </configuration>
            </plugin>
        </plugins>
    </build>

</project>
```

### Section-by-section explanation

#### Project Identity

```xml
<groupId>com.fad</groupId>
<artifactId>LibraryBorrowingSystem</artifactId>
<version>1.0.0</version>
<packaging>jar</packaging>
```

| Tag | Purpose | Example |
|---|---|---|
| `groupId` | Your organization or package root | `com.fad` |
| `artifactId` | The project/app name | `LibraryBorrowingSystem` |
| `version` | Current version of your project | `1.0.0` |
| `packaging` | Output format (`jar` = runnable Java archive) | `jar` |

#### Properties (Compiler Settings)

```xml
<properties>
    <maven.compiler.release>21</maven.compiler.release>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
</properties>
```

- `maven.compiler.release` — tells Java to compile for JDK 21 syntax and features
- `project.build.sourceEncoding` — ensures source files are read as UTF-8 (handles special characters)

#### Dependencies (Libraries)

```xml
<dependencies>
    <dependency>
        <groupId>org.xerial</groupId>
        <artifactId>sqlite-jdbc</artifactId>
        <version>3.46.1.3</version>
    </dependency>
</dependencies>
```

This is the most important section for adding external libraries. Each `<dependency>` block tells Maven to download a specific library from the internet (Maven Central repository).

**How to find a dependency:**

1. Go to [https://mvnrepository.com](https://mvnrepository.com)
2. Search for the library (e.g., `sqlite-jdbc`)
3. Click the version you want
4. Copy the `<dependency>` block shown and paste it inside `<dependencies>`

Example — if you wanted to add a logging library:

```xml
<dependencies>
    <!-- existing SQLite dependency -->
    <dependency>
        <groupId>org.xerial</groupId>
        <artifactId>sqlite-jdbc</artifactId>
        <version>3.46.1.3</version>
    </dependency>

    <!-- new library added -->
    <dependency>
        <groupId>org.slf4j</groupId>
        <artifactId>slf4j-simple</artifactId>
        <version>2.0.9</version>
    </dependency>
</dependencies>
```

After adding, run `mvn compile` and Maven will download the new library automatically.

#### Build Settings

```xml
<build>
    <sourceDirectory>src</sourceDirectory>
    ...
</build>
```

- `<sourceDirectory>src</sourceDirectory>` — tells Maven that Java source files are in the `src/` folder.
  By default Maven expects them in `src/main/java/`, but this project uses `src/` directly.

#### Maven commands you will use

| Command | What it does |
|---|---|
| `mvn compile` | Compiles all `.java` files, downloads missing dependencies |
| `mvn exec:java -Dexec.mainClass="..."` | Compiles and runs the app |
| `mvn clean` | Deletes the `target/` folder (compiled output) |
| `mvn clean compile` | Deletes old output and recompiles from scratch |
| `mvn dependency:resolve` | Downloads all dependencies without compiling |
| `mvn package` | Compiles and packages into a `.jar` file |

---

## 6. Installing VS Code & Java Extensions

**Step 1** — Download VS Code from [https://code.visualstudio.com](https://code.visualstudio.com) and install it.

**Step 2** — Open VS Code, press `Ctrl+Shift+X` (Windows/Linux) or `Cmd+Shift+X` (macOS) to open Extensions.

**Step 3** — Search for and install **"Extension Pack for Java"** by Microsoft.
This single pack includes everything you need:

- Language Support for Java
- Maven for Java
- Java Debugger
- Java Test Runner

**Step 4** — Also install **"SQLite Viewer"** (by Florian Klampfer) — lets you browse `library.db` inside VS Code.

**Step 5** — Restart VS Code after installation.

---

## 7. Getting the Project

### Clone via Git

```bash
git clone https://github.com/masjohncook/LibraryBorrowingSystem.git
cd LibraryBorrowingSystem
```

### Switch to the database branch

```bash
git checkout m_data
```

### Open in VS Code

```bash
code .
```

VS Code will detect the `pom.xml` automatically and recognize it as a Maven project. It may prompt you to **"Import Java Projects"** — click **Yes**.

---

## 8. SQLite from Scratch

Before looking at how this project uses SQLite, it helps to understand what SQLite is and how SQL works manually. This section walks you through using SQLite directly from the command line.

### What is SQLite?

SQLite is a **file-based relational database**. Unlike MySQL or PostgreSQL, it has no server — the entire database is stored in a single `.db` file. This makes it ideal for learning, desktop apps, and small projects.

### Installing the SQLite CLI

The SQLite CLI (command-line tool) lets you run SQL commands directly against a database file.

**Windows**

1. Go to [https://www.sqlite.org/download.html](https://www.sqlite.org/download.html)
2. Under **Precompiled Binaries for Windows**, download `sqlite-tools-win-x64-*.zip`
3. Extract it to a folder like `C:\sqlite`
4. Add `C:\sqlite` to your system `PATH` (same steps as Maven above)
5. Open Command Prompt and verify:

```cmd
sqlite3 --version
```

**macOS**

SQLite is pre-installed on macOS. Verify with:

```bash
sqlite3 --version
```

If missing, install via Homebrew:

```bash
brew install sqlite
```

**Linux (Ubuntu / Debian)**

```bash
sudo apt install sqlite3 -y
sqlite3 --version
```

**Linux (Fedora / RHEL)**

```bash
sudo dnf install sqlite -y
sqlite3 --version
```

---

### Creating a Database

Open a terminal and run:

```bash
sqlite3 practice.db
```

This creates a new file called `practice.db` and opens the SQLite prompt:

```
SQLite version 3.x.x
Enter ".help" for usage hints.
sqlite>
```

You are now inside the SQLite shell. All commands ending with `;` are SQL. Commands starting with `.` are SQLite shell commands.

---

### Creating Tables

```sql
CREATE TABLE books (
    book_id   TEXT PRIMARY KEY,
    title     TEXT NOT NULL,
    author    TEXT NOT NULL,
    genre     TEXT NOT NULL,
    available INTEGER NOT NULL DEFAULT 1
);
```

This creates a `books` table with 5 columns:

| Column | Type | Meaning |
|---|---|---|
| `book_id` | `TEXT PRIMARY KEY` | Unique identifier, cannot be duplicated |
| `title` | `TEXT NOT NULL` | Must always have a value |
| `author` | `TEXT NOT NULL` | Must always have a value |
| `genre` | `TEXT NOT NULL` | Must always have a value |
| `available` | `INTEGER DEFAULT 1` | 1 = available, 0 = borrowed |

Create a second table for members:

```sql
CREATE TABLE members (
    member_id TEXT PRIMARY KEY,
    name      TEXT NOT NULL
);
```

---

### Inserting Data

```sql
INSERT INTO books (book_id, title, author, genre, available)
VALUES ('B001', 'The Great Gatsby', 'F. Scott Fitzgerald', 'Classic', 1);

INSERT INTO books (book_id, title, author, genre, available)
VALUES ('B002', '1984', 'George Orwell', 'Dystopian', 1);

INSERT INTO members (member_id, name) VALUES ('M001', 'Alice');
INSERT INTO members (member_id, name) VALUES ('M002', 'Bob');
```

---

### Querying Data

```sql
-- Select all rows from books
SELECT * FROM books;

-- Select only title and author
SELECT title, author FROM books;

-- Filter by genre
SELECT * FROM books WHERE genre = 'Classic';

-- Filter by availability
SELECT * FROM books WHERE available = 1;

-- Count how many books exist
SELECT COUNT(*) FROM books;
```

---

### Updating Data

```sql
-- Mark a book as borrowed
UPDATE books SET available = 0 WHERE book_id = 'B001';

-- Change a book's title
UPDATE books SET title = 'Nineteen Eighty-Four' WHERE book_id = 'B002';
```

---

### Deleting Data

```sql
-- Delete a specific book
DELETE FROM books WHERE book_id = 'B001';

-- Delete all books (careful!)
DELETE FROM books;
```

---

### Useful SQLite Shell Commands

While inside `sqlite3`, these commands help you navigate:

```
.tables              -- list all tables
.schema books        -- show CREATE TABLE statement for 'books'
.mode column         -- format output as columns
.headers on          -- show column names in output
.quit                -- exit sqlite3
```

Full example session:

```
sqlite> .headers on
sqlite> .mode column
sqlite> SELECT * FROM books;
book_id  title             author               genre      available
-------  ----------------  -------------------  ---------  ---------
B001     The Great Gatsby  F. Scott Fitzgerald  Classic    1
B002     1984              George Orwell        Dystopian  1
sqlite> .quit
```

---

### How This Connects to the Project

Doing the above manually is exactly what `DatabaseManager.java` does automatically in code. For example:

**Manual SQL:**
```sql
INSERT INTO books (book_id, title, author, genre, available)
VALUES ('B001', 'The Great Gatsby', 'F. Scott Fitzgerald', 'Classic', 1);
```

**The same thing in Java (`DatabaseManager.insertBook`):**
```java
String sql = "INSERT OR IGNORE INTO books (book_id, title, author, genre, available) VALUES (?,?,?,?,?)";
PreparedStatement ps = getConnection().prepareStatement(sql);
ps.setString(1, book.getBookId());    // 'B001'
ps.setString(2, book.getTitle());     // 'The Great Gatsby'
ps.setString(3, book.getAuthor());    // 'F. Scott Fitzgerald'
ps.setString(4, book.getGenre());     // 'Classic'
ps.setInt(5, 1);                      // available = 1
ps.executeUpdate();
```

The `?` placeholders replace the hardcoded values and are filled in safely at runtime — this prevents **SQL Injection** attacks.

---

## 9. Project Structure

```
LibraryBorrowingSystem/
│
├── pom.xml                          ← Maven config (declares SQLite dependency)
├── library.db                       ← SQLite database (auto-created on first run)
│
└── src/
    └── com/fad/LibrarySystem/
        │
        ├── Main.java                ← Entry point
        │
        ├── model/                   ← M: Data classes & business logic
        │   ├── Person.java
        │   ├── LibraryItem.java
        │   ├── Books.java
        │   ├── Multimedia.java
        │   ├── Member.java
        │   ├── Librarian.java       ← Central data hub (uses DatabaseManager)
        │   ├── BorrowRecord.java
        │   ├── Reservation.java
        │   └── Fine.java
        │
        ├── view/                    ← V: Display only (System.out.println)
        │   ├── MenuView.java
        │   ├── BookView.java
        │   ├── MemberView.java
        │   ├── BorrowView.java
        │   └── FineView.java
        │
        ├── controller/              ← C: User input & coordinates Model + View
        │   ├── LibraryController.java
        │   ├── BookController.java
        │   ├── MemberController.java
        │   └── BorrowController.java
        │
        └── database/                ← DB: All SQL operations
            └── DatabaseManager.java
```

---

## 10. Understanding MVC Pattern

MVC stands for **Model — View — Controller**. It is a way of organizing code so that each layer has exactly one job.

```
User Input
    │
    ▼
┌─────────────┐     calls      ┌─────────────┐     reads/writes    ┌─────────────┐
│  Controller │ ─────────────► │    Model    │ ──────────────────► │  Database   │
│             │                │             │                      │ (SQLite)    │
│ Reads input │ ◄───────────── │ Returns data│ ◄────────────────── │             │
└─────────────┘     data       └─────────────┘     loads data       └─────────────┘
       │
       │ passes data to
       ▼
┌─────────────┐
│    View     │
│             │
│  Displays   │
│  to screen  │
└─────────────┘
```

### Rule of MVC

| Layer | Allowed to | NOT allowed to |
|---|---|---|
| **Model** | Hold data, business rules, talk to DB | Print to screen |
| **View** | Print to screen | Make business decisions |
| **Controller** | Read input, call Model, pass result to View | Hold data directly |

### Example in this project

When a member borrows a book:

```
BorrowController        Member (Model)          DatabaseManager
      │                      │                        │
      │── reads input ───────►                        │
      │    (memberId, itemId) │                        │
      │                      │                        │
      │── member.borrowItem() ►                        │
      │                      │── item.setAvailable(false)
      │                      │                        │
      │── librarian           │                        │
      │   .recordBorrow() ───►── INSERT borrow_record ►│
      │                      │── INSERT member_borrowed►│
      │                      │── UPDATE item available ►│
      │                      │                        │
      │── borrowView          │                        │
      │   .showSuccess() ────►(prints to screen)
```

---

## 11. Understanding the Database Layer

### Why SQLite?

SQLite is a **file-based** database — the entire database is stored in a single file (`library.db`). It requires no separate server installation, making it perfect for learning and small applications.

### Database Schema (Tables)

The database has **6 tables** (you saw how to create these manually in Section 8):

```sql
-- Stores all books
books (
    book_id   TEXT PRIMARY KEY,
    title     TEXT NOT NULL,
    author    TEXT NOT NULL,
    genre     TEXT NOT NULL,
    available INTEGER NOT NULL DEFAULT 1   -- 1 = available, 0 = borrowed
)

-- Stores all multimedia items (DVD, CD, Audiobook)
multimedia (
    item_id   TEXT PRIMARY KEY,
    title     TEXT NOT NULL,
    type      TEXT NOT NULL,
    duration  TEXT NOT NULL,
    available INTEGER NOT NULL DEFAULT 1
)

-- Stores all registered members
members (
    member_id TEXT PRIMARY KEY,
    name      TEXT NOT NULL
)

-- Tracks which items each member currently has borrowed
member_borrowed_items (
    member_id TEXT NOT NULL,
    item_id   TEXT NOT NULL,
    PRIMARY KEY (member_id, item_id)
)

-- Full history of all borrow and return transactions
borrow_records (
    record_id   TEXT PRIMARY KEY,
    member_id   TEXT NOT NULL,
    item_id     TEXT NOT NULL,
    borrow_date TEXT NOT NULL,
    return_date TEXT NOT NULL DEFAULT '-',  -- '-' if not yet returned
    returned    INTEGER NOT NULL DEFAULT 0  -- 0 = not returned, 1 = returned
)

-- Reservations placed by members for unavailable items
reservations (
    reservation_id   TEXT PRIMARY KEY,
    member_id        TEXT NOT NULL,
    item_id          TEXT NOT NULL,
    reservation_date TEXT NOT NULL,
    active           INTEGER NOT NULL DEFAULT 1  -- 1 = active, 0 = cancelled
)
```

### How the DB file is created automatically

You do **not** need to create `library.db` or run any SQL manually. When the app starts for the first time, `DatabaseManager.initSchema()` runs the `CREATE TABLE IF NOT EXISTS` statements automatically — the same SQL you practiced in Section 8, but executed from Java.

If the `books` table is empty (first run), the app seeds the database with 5 books, 3 multimedia items, and 3 members.

---

## 12. Code Walkthrough

### `Main.java` — Entry Point

```java
public class Main {
    public static void main(String[] args) {
        // Close the DB connection cleanly when the app exits
        Runtime.getRuntime().addShutdownHook(new Thread(DatabaseManager::close));

        LibraryController controller = new LibraryController();
        controller.start();
    }
}
```

`addShutdownHook` ensures the database connection is properly closed whenever the program exits — whether normally (choosing option 0) or forcefully (Ctrl+C).

---

### `DatabaseManager.java` — All SQL in One Place

This class handles every interaction with the database. No other class writes SQL directly.

**Getting a connection:**

```java
public static Connection getConnection() {
    if (conn == null || conn.isClosed()) {
        conn = DriverManager.getConnection("jdbc:sqlite:library.db");
        // WAL mode: better performance for concurrent reads
        // foreign_keys ON: enforce referential integrity
    }
    return conn;
}
```

**Creating tables on startup:**

```java
public static void initSchema() {
    Statement s = getConnection().createStatement();
    s.execute("""
        CREATE TABLE IF NOT EXISTS books (
            book_id   TEXT PRIMARY KEY,
            title     TEXT NOT NULL,
            author    TEXT NOT NULL,
            genre     TEXT NOT NULL,
            available INTEGER NOT NULL DEFAULT 1
        )""");
    // ... repeat for all 6 tables
}
```

**Inserting a book:**

```java
public static void insertBook(Books book) {
    String sql = "INSERT OR IGNORE INTO books (book_id, title, author, genre, available) VALUES (?,?,?,?,?)";
    PreparedStatement ps = getConnection().prepareStatement(sql);
    ps.setString(1, book.getBookId());
    ps.setString(2, book.getTitle());
    ps.setString(3, book.getAuthor());
    ps.setString(4, book.getGenre());
    ps.setInt(5, book.isAvailable() ? 1 : 0);
    ps.executeUpdate();
}
```

> **Why `?` placeholders?**
> Using `?` instead of putting values directly in the string prevents **SQL Injection**.
> Never concatenate user input directly into SQL strings like `"... WHERE name = '" + input + "'"`.

**Loading all books from DB:**

```java
public static Books[] loadBooks() {
    ResultSet rs = getConnection().createStatement()
                       .executeQuery("SELECT * FROM books ORDER BY book_id");
    List<Books> list = new ArrayList<>();
    while (rs.next()) {
        Books b = new Books(rs.getString("book_id"), rs.getString("title"),
                            rs.getString("author"), rs.getString("genre"));
        b.setAvailable(rs.getInt("available") == 1);
        list.add(b);
    }
    return list.toArray(new Books[0]);
}
```

---

### `Librarian.java` — Model & Central Data Hub

`Librarian` is the most important class. It holds all data in memory (arrays) AND keeps the database in sync on every change.

**Startup — load from DB or seed:**

```java
public Librarian(String librarianId, String name) {
    super(librarianId, name);
    DatabaseManager.initSchema();   // Create tables if they don't exist
    loadFromDatabase();             // Load existing data (or seed if empty)
}

private void loadFromDatabase() {
    if (DatabaseManager.isTableEmpty("books")) seedInitialData();

    for (Books b : DatabaseManager.loadBooks())
        catalog[catalogCount++] = b;

    for (Member m : DatabaseManager.loadMembers()) {
        members[memberCount++] = m;
        for (String itemId : DatabaseManager.loadBorrowedItemIds(m.getMemberId())) {
            LibraryItem item = findItemById(itemId);
            if (item != null) m.addBorrowedItem(item);
        }
    }
}
```

**Adding a book — memory AND database:**

```java
public Books addBook(String bookId, String title, String author, String genre) {
    if (catalogCount >= MAX_BOOKS)    return null;
    if (findBookById(bookId) != null) return null;
    Books book = new Books(bookId, title, author, genre);
    catalog[catalogCount++] = book;       // 1. add to in-memory array
    DatabaseManager.insertBook(book);     // 2. save to SQLite
    return book;
}
```

**Recording a borrow — syncs 3 tables:**

```java
public void recordBorrow(Member member, LibraryItem item) {
    String today    = LocalDate.now().toString();
    String recordId = "REC" + String.format("%03d", recordCount + 1);
    BorrowRecord record = new BorrowRecord(recordId, member, item, today);
    borrowRecords[recordCount++] = record;

    DatabaseManager.insertBorrowRecord(record);                        // → borrow_records
    DatabaseManager.insertMemberBorrowedItem(member.getMemberId(),     // → member_borrowed_items
                                              item.getItemId());
    DatabaseManager.updateItemAvailability(item.getItemId(), false);   // → books / multimedia
}
```

---

### Model Classes

**`Person.java`** — base class for Librarian and Member

```
Person
  ├── id   (String)
  └── name (String)
```

**`LibraryItem.java`** — base class for Books and Multimedia

```
LibraryItem
  ├── itemId    (String)
  ├── title     (String)
  └── available (boolean)
```

**`Books.java`** — extends LibraryItem

```
Books extends LibraryItem
  ├── author (String)
  └── genre  (String)
```

**`Member.java`** — extends Person

```
Member extends Person
  ├── borrowedItems[] (LibraryItem array, max 5)
  └── borrowCount     (int)
```

Key methods:

- `borrowItem(item)` — checks availability and borrow limit, marks item as borrowed
- `returnItem(item)` — removes item from member's list, marks it available
- `searchItem(catalog, size, keyword)` — returns a list of matching items (no printing)
- `addBorrowedItem(item)` — used **only during DB loading** to restore state without checks

---

### View Classes

Views only print to the screen. They receive data from the controller and display it. They never touch the database or make decisions.

```java
// BorrowView.java
public void showBorrowSuccess(String memberName, String itemTitle) {
    System.out.println(memberName + " successfully borrowed: " + itemTitle);
}

public void showAllRecords(List<BorrowRecord> records) {
    for (BorrowRecord r : records)
        System.out.println(r.getInfo());
}

// BookView.java
public void showGenreResults(List<Books> results, String genre) {
    if (results.isEmpty()) {
        System.out.println("No books found for genre: " + genre);
        return;
    }
    for (Books b : results)
        System.out.println("  - " + b.getTitle() + " | Author: " + b.getAuthor());
}
```

---

### Controller Classes

**`LibraryController.java`** — creates all objects and runs the main menu loop:

```java
public LibraryController() {
    this.librarian        = new Librarian("L001", "Admin Librarian"); // triggers DB load
    this.bookController   = new BookController(librarian, scanner);
    this.memberController = new MemberController(librarian, scanner);
    this.borrowController = new BorrowController(librarian, scanner);
}

public void start() {
    while (choice != 0) {
        menuView.showMainMenu();                        // View: display menu
        choice = Integer.parseInt(scanner.nextLine());
        switch (choice) {
            case 1 -> bookController.handleMenu();
            case 2 -> memberController.handleMenu();
            case 3 -> borrowController.handleMenu();
        }
    }
}
```

**`BorrowController.java`** — handles borrow and return:

```java
private void borrowItem() {
    System.out.print("Member ID : "); String memberId = scanner.nextLine();
    System.out.print("Item ID   : "); String itemId   = scanner.nextLine();

    Member      member = librarian.findMemberById(memberId); // Model lookup
    LibraryItem item   = librarian.findItemById(itemId);     // Model lookup

    boolean success = member.borrowItem(item);               // Model logic
    if (success) {
        librarian.recordBorrow(member, item);                // Model + DB sync
        borrowView.showBorrowSuccess(member.getName(),       // View: print result
                                     item.getTitle());
    } else {
        borrowView.showBorrowLimitReached();                 // View: print error
    }
}
```

**`BookController.java`** — search by genre follows the MVC rule correctly:

```java
private void searchByGenre() {
    System.out.print("Enter genre: ");
    String genre = scanner.nextLine();

    List<Books> results = librarian.searchByGenre(           // Model: returns data
            librarian.getCatalog(), librarian.getCatalogSize(), genre);

    bookView.showGenreResults(results, genre);               // View: prints result
}
```

Notice: the Model returns a `List<Books>` — it does **not** print anything. The View does all the printing.

---

## 13. How Data Flows

### First Run (empty database)

```
App starts
    │
    ▼
DatabaseManager.initSchema()
    └── Runs CREATE TABLE IF NOT EXISTS for all 6 tables
    │
    ▼
isTableEmpty("books") == true
    │
    ▼
seedInitialData()
    ├── INSERT 5 books      → books table
    ├── INSERT 3 multimedia → multimedia table
    └── INSERT 3 members   → members table
    │
    ▼
loadFromDatabase()
    └── SELECTs all rows and loads into in-memory arrays
```

### Subsequent Runs

```
App starts
    │
    ▼
loadFromDatabase()
    └── isTableEmpty("books") == false → skip seeding
    └── Loads all existing data from DB into arrays
        (books, multimedia, members, borrowed items, borrow records)
```

### Borrowing a Book

```
User enters Member ID and Item ID
    │
    ▼
BorrowController.borrowItem()
    │
    ├── librarian.findMemberById() → searches in-memory members[] array
    ├── librarian.findItemById()   → searches in-memory catalog[] array
    │
    ├── member.borrowItem(item)
    │       ├── item.setAvailable(false)  ← in-memory change
    │       └── adds item to borrowedItems[]
    │
    └── librarian.recordBorrow(member, item)
            ├── creates BorrowRecord in borrowRecords[]      ← in-memory
            ├── DatabaseManager.insertBorrowRecord()         ← saved to DB
            ├── DatabaseManager.insertMemberBorrowedItem()   ← saved to DB
            └── DatabaseManager.updateItemAvailability()     ← saved to DB
```

---

## 14. Running the Project

### Compile

```bash
mvn compile
```

This downloads the SQLite JDBC driver (first time only) and compiles all `.java` files to `target/classes/`.

### Run

```bash
mvn exec:java -Dexec.mainClass="com.fad.LibrarySystem.Main"
```

You should see:

```
===== Library System =====
1. Manage Books
2. Manage Members
3. Borrow / Return Item
4. Reservations
5. Fines
0. Exit
Choose an option:
```

### Run from VS Code

1. Open `src/com/fad/LibrarySystem/Main.java`
2. Click the **Run** button (▷) that appears above `public static void main`
3. The terminal at the bottom opens and runs the app

### Inspect the database in VS Code

Install the **SQLite Viewer** extension, then click on `library.db` in the file explorer — it opens a visual table browser directly in VS Code.

Or use the command line:

```bash
sqlite3 library.db ".tables"
sqlite3 library.db "SELECT * FROM books;"
sqlite3 library.db "SELECT * FROM members;"
sqlite3 library.db "SELECT * FROM borrow_records;"
```

### Test that data persists

1. Run the app → **Manage Books → Add Book** → add a new book
2. Exit with option `0`
3. Run the app again → **Manage Books → View All Books**
4. Your new book should still be there ✓

---

## 15. Troubleshooting

### `mvn: command not found`

Maven is not in your PATH. Re-check [Section 4](#4-installing-maven) and open a **new** terminal after updating PATH.

---

### `java: command not found`

Java is not installed or not in PATH. Re-check [Section 3](#3-installing-java-jdk).

---

### `BUILD FAILURE` — `Source option X is no longer supported`

Your Java version is too old. Run `java -version` — it must be **17 or higher**.

---

### `No suitable driver found for jdbc:sqlite`

The SQLite JDBC driver was not downloaded. Run:

```bash
mvn dependency:resolve
```

If Maven cannot connect to the internet, check your network or firewall settings.

---

### `library.db` not found after running

The database file is created in whichever directory you run the `mvn` command from. Always run Maven commands from the **project root** (where `pom.xml` is located).

---

### VS Code doesn't recognize the Maven project

1. Make sure the **Extension Pack for Java** is installed
2. Press `Ctrl+Shift+P` → type **"Java: Clean Java Language Server Workspace"** → Restart
3. VS Code should re-detect the Maven project automatically

---

### Data is not saving between runs

Exit the app by choosing option **`0`** rather than closing the terminal. Both ways will close the DB safely (the shutdown hook handles `Ctrl+C` too), but exiting cleanly via option `0` is best practice.

---

*Tutorial written for students learning Java MVC architecture and SQLite persistence.*
