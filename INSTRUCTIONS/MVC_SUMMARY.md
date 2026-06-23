# MVC Restructure Summary
## Library Borrowing System — Meeting 1

**Branch:** `MVC`  
**Date:** 2026-06-09  
**Based on:** original Library Borrowing System (main branch)

---

## What We Did and Why

This session had two goals:
1. Restructure the project into the **MVC pattern**
2. Make the project follow **professional Java conventions**

---

## Part 1 — MVC Restructure

### The Problem with the Original Code

In the original project, everything was in one flat package (`LibraryBorrowingSystem`). The `main.java` file was doing three jobs at once:

| Job | Example |
|---|---|
| **Data/Logic** | Calling `borrowItem()`, managing arrays |
| **Display** | `System.out.println("--- Book Menu ---")` |
| **Input** | `Scanner sc = new Scanner(System.in)` |

This makes code hard to maintain. If you want to change how a menu looks, you have to dig through hundreds of lines of logic to find the right `println`.

---

### The Solution — MVC (Model / View / Controller)

MVC separates the code into three clear responsibilities:

| Layer | Responsibility | Rule |
|---|---|---|
| **Model** | Data and business logic | Never prints to screen |
| **View** | Display only | Never reads input, never creates objects |
| **Controller** | Input and coordination | Calls Model to do work, calls View to show results |

---

### New Folder Structure

```
src/
  com/
    fad/
      library/
        model/          ← all data classes (unchanged logic)
        view/           ← all display code (new)
        controller/     ← all menu and input code (new)
        Main.java       ← entry point (5 lines only)
```

---

### Step 1 — Model Layer (`com.fad.LibrarySystem.model`)

**What we did:** Moved all existing classes into the model package. No logic was changed — only the package declaration was updated.

**Files in model:**

| File | What it represents |
|---|---|
| `Person.java` | Base class for Member and Librarian |
| `LibraryItem.java` | Base class for Books and Multimedia |
| `Member.java` | A library member who can borrow items |
| `Librarian.java` | Manages catalog, members, and records |
| `Books.java` | A book in the library |
| `Multimedia.java` | A DVD, CD, or Audiobook |
| `BorrowRecord.java` | One borrow/return transaction |
| `Fine.java` | A late-return fine |
| `Reservation.java` | A reservation for an unavailable item |

**Key change in `Member.java`:**

Before (model was printing):
```java
public boolean borrowItem(LibraryItem item) {
    ...
    System.out.println("  [SUCCESS] " + name + " borrowed \"" + item.getTitle() + "\".");
    return true;
}
```

After (model is silent — View handles the message):
```java
public boolean borrowItem(LibraryItem item) {
    if (!item.isAvailable()) return false;
    if (borrowCount >= getBorrowLimit()) return false;
    borrowedItems[borrowCount++] = item;
    item.setAvailable(false);
    return true;  // caller decides what to display
}
```

> **Why?** The Model should not know or care how results are displayed. Tomorrow you might display in a GUI, a web page, or a log file — the Model stays the same.

---

### Step 2 — View Layer (`com.fad.LibrarySystem.view`)

**What we did:** Created 5 new View classes. Each one only contains `System.out.println()` calls. No Scanner, no logic.

**Files in view:**

| File | Displays |
|---|---|
| `MenuView.java` | Main menu, error messages, info messages |
| `BookView.java` | Book catalog, book menu, add/not-found messages |
| `MemberView.java` | Member list, member menu, registered/not-found messages |
| `BorrowView.java` | Borrow records, borrow/return success and error messages |
| `FineView.java` | Fine details and fine list |

**Example — `BookView.java`:**
```java
public void showAllBooks(List<Books> books) {
    if (books.isEmpty()) {
        System.out.println("No books in catalog.");
        return;
    }
    System.out.println("----- Book Catalog -----");
    for (int i = 0; i < books.size(); i++) {
        System.out.println((i + 1) + ". " + books.get(i).getInfo());
    }
}
```

> **Rule:** If you see `new Books(...)` or `scanner.nextLine()` inside a View class, something is wrong.

---

### Step 3 — Controller Layer (`com.fad.LibrarySystem.controller`)

**What we did:** Created 4 Controller classes. Each Controller owns the menu loop for one feature area.

**Files in controller:**

| File | Manages |
|---|---|
| `LibraryController.java` | Top-level menu, delegates to sub-controllers |
| `BookController.java` | Book menu: add, view all, search by genre |
| `MemberController.java` | Member menu: register, view all |
| `BorrowController.java` | Borrow menu: borrow, return, view records |

**How a Controller works — `BorrowController.borrowItem()`:**
```java
private void borrowItem() {
    // 1. Read input
    System.out.print("Member ID : "); String memberId = scanner.nextLine();
    System.out.print("Item ID   : "); String itemId   = scanner.nextLine();

    // 2. Call Model
    Member member     = librarian.findMemberById(memberId);
    LibraryItem item  = librarian.findItemById(itemId);

    // 3. Handle errors via View
    if (member == null) { borrowView.showNotFound("Member " + memberId); return; }
    if (item == null || !item.isAvailable()) { borrowView.showItemNotAvailable(itemId); return; }

    // 4. Call Model to do work
    boolean success = member.borrowItem(item);

    // 5. Call View to show result
    if (success) {
        librarian.recordBorrow(member, item);
        borrowView.showBorrowSuccess(member.getName(), item.getTitle());
    } else {
        borrowView.showBorrowLimitReached();
    }
}
```

> **The pattern:** Read input → Call Model → Call View. The Controller never prints directly (except the input prompts like `System.out.print("Member ID : ")`).

---

### Step 4 — Simplified Main.java

Before (500+ lines of menus, logic, and input all mixed together):
```java
// old main.java had Scanner, switch statements, System.out.println,
// borrowItem(), displayCatalog() all in one file
```

After (5 lines):
```java
package com.fad.LibrarySystem;

import com.fad.LibrarySystem.controller.LibraryController;

public class Main {
    public static void main(String[] args) {
        LibraryController controller = new LibraryController();
        controller.start();
    }
}
```

> **Why?** `Main` should only start the application. All the logic belongs in Controllers and Models.

---

### One Shared Scanner

**Important fix:** The guide showed each Controller creating its own `new Scanner(System.in)`. This causes a conflict — multiple Scanners on the same input stream skip each other's lines.

**Solution:** One Scanner is created in `LibraryController` and passed to every sub-controller:

```java
// LibraryController constructor
this.scanner          = new Scanner(System.in);          // created ONCE
this.bookController   = new BookController(librarian, scanner);   // shared
this.memberController = new MemberController(librarian, scanner); // shared
this.borrowController = new BorrowController(librarian, scanner); // shared
```

---

## Part 2 — Professional Java Conventions

### Package Naming

Java convention: package names use **reverse domain name** in lowercase.

| Before | After |
|---|---|
| `library.model` | `com.fad.LibrarySystem.model` |
| `library.view` | `com.fad.LibrarySystem.view` |
| `library.controller` | `com.fad.LibrarySystem.controller` |

**Why reverse domain?** It guarantees uniqueness. If two developers both name their package `library.model`, they conflict. `com.fad.LibrarySystem.model` is unique to this developer.

### .gitignore

Added a proper `.gitignore` so compiled files and IDE files are never pushed to GitHub:

```
out/          ← compiled .class files (javac output)
*.class       ← individual compiled classes
*.jar         ← packaged applications
.idea/        ← IntelliJ/PyCharm project files
.vscode/      ← VS Code settings
._*           ← macOS hidden metadata files
.DS_Store     ← macOS folder metadata
```

> **Why?** Compiled files are generated — they don't belong in version control. Each developer compiles from source. IDE files are personal — they shouldn't be forced on teammates.

---

## What Stays the Same

- All existing classes: `Person`, `Member`, `Librarian`, `Books`, `Multimedia`, `BorrowRecord`, `Fine`, `Reservation`
- All business logic (borrow limits, fine calculation, search by genre)
- All menu options (Books, Members, Borrow/Return)
- The application behavior from the user's perspective

---

## What Comes Next

| Meeting | What Will Be Added |
|---|---|
| **Meeting 2** | `library.dao` — replace in-memory arrays with SQLite database |
| **Meeting 3** | JavaFX GUI — replace console View with a window built in SceneBuilder |

> The Model package will not change in either meeting. This is the benefit of MVC — you can swap the View without touching the logic.

---

## Self-Check Questions

1. If you want to change how the book catalog is displayed, which file do you open?
2. If you want to change the borrow limit from 3 to 5, which file do you open?
3. Can `BookView.java` create a `new Books(...)` object? Why or why not?
4. Why do we use one shared Scanner instead of one per Controller?
5. Why does the package start with `com.fad` instead of just `library`?
