# Library Borrowing System — Midterm Implementation

**Subject:** Fundamentals of Application Development  
**Branch:** `midterm`  
**Student:** masjohncook  
**Date:** 2026-06-09  

---

## Overview

This project extends the existing Library Book Borrowing System by adding **4 new features** on top of the original codebase. All existing classes (`Person`, `Member`, `Librarian`, `LibraryItem`, `Books`, `Multimedia`, `BorrowRecord`) remain intact.

---

## Feature 1 — Fine System *(30 points)*

### Requirement
The library charges a fine of **Rp2,000 per day** for every day past the due date. The due date is **7 days** from the borrow date. The librarian issues fines based on a `BorrowRecord`.

### New Class: `Fine`

| Section | Detail |
|---|---|
| **Attributes** | `recordId : String`, `member : Member`, `item : LibraryItem`, `daysLate : int`, `fineAmount : int` |
| **Methods** | `calculateFine(daysLate)`, `getFineAmount()`, `getMember()`, `getInfo()` |

### Implementation

```java
// Fine.java
public class Fine {
    private String recordId;
    private Member member;
    private LibraryItem item;
    private int daysLate;
    private int fineAmount;

    public Fine(String recordId, Member member, LibraryItem item, int daysLate) {
        this.recordId   = recordId;
        this.member     = member;
        this.item       = item;
        this.daysLate   = daysLate;
        this.fineAmount = calculateFine(daysLate);
    }

    public int calculateFine(int daysLate) {
        this.fineAmount = daysLate * 2000;
        return this.fineAmount;
    }

    public String getInfo() {
        return "Fine for: " + member.getName()
             + " | Item: " + item.getTitle()
             + " | Days Late: " + daysLate
             + "\nFine: Rp" + fineAmount;
    }
}
```

### Addition to `Librarian`

```java
// Added to Librarian.java
public Fine issueFine(BorrowRecord record, int returnDay) {
    int borrowDay = Integer.parseInt(record.getBorrowDate());
    int dueDay    = borrowDay + 7;
    int daysLate  = returnDay - dueDay;

    if (daysLate <= 0) {
        throw new InvalidFineException("No fine: item was returned on time.");
    }

    String fineId = "FINE" + String.format("%03d", ++fineCount);
    return new Fine(fineId, record.getMember(), record.getItem(), daysLate);
}
```

### Demo Output
```
[Regular] Budi borrowed: Laskar Pelangi
Fine for: Budi | Item: Laskar Pelangi | Days Late: 4
Fine: Rp8000
```
> Budi borrowed on Day 1, returned on Day 12 → daysLate = 12 − (1 + 7) = **4** → Rp4 × 2000 = **Rp8000**

---

## Feature 2 — Premium Member *(25 points)*

### Requirement
Introduce a **Premium membership** tier. Regular members can borrow up to **3 items**; Premium members can borrow up to **5 items**. Both print a message if their limit is reached.

### New Class: `PremiumMember`

| Section | Detail |
|---|---|
| **Extends** | `Member` |
| **Attributes** | `membershipCode : String` |
| **Methods** | `borrowItem(item)` *(overrides)*, `getInfo()` *(overrides)*, `toString()` *(overrides)* |

### Implementation

**`Member.java` — updated `borrowItem()` with limit of 3:**

```java
// Added to Member.java
protected int getBorrowLimit() { return 3; }

public boolean borrowItem(LibraryItem item) {
    if (!item.isAvailable()) {
        throw new ItemNotAvailableException(...);
    }
    if (borrowCount >= getBorrowLimit()) {
        throw new BorrowLimitExceededException(...);
    }
    borrowedItems[borrowCount++] = item;
    item.setAvailable(false);
    System.out.println("[Regular] " + name + " borrowed: " + item.getTitle());
    return true;
}
```

**`PremiumMember.java` — overrides limit to 5:**

```java
public class PremiumMember extends Member {
    private String membershipCode;
    private static final int PREMIUM_LIMIT = 5;

    public PremiumMember(String memberId, String name, String membershipCode) {
        super(memberId, name);
        this.membershipCode = membershipCode;
    }

    @Override
    protected int getBorrowLimit() { return PREMIUM_LIMIT; }

    @Override
    public boolean borrowItem(LibraryItem item) {
        if (!item.isAvailable()) {
            throw new ItemNotAvailableException(...);
        }
        if (borrowCount >= PREMIUM_LIMIT) {
            throw new BorrowLimitExceededException(...);
        }
        borrowedItems[borrowCount++] = item;
        item.setAvailable(false);
        System.out.println("[Premium] " + name + " borrowed: " + item.getTitle());
        return true;
    }
}
```

### Demo Output
```
[Regular] Budi borrowed: Laskar Pelangi
[Regular] Budi borrowed: Dilan
[Regular] Budi borrowed: Bumi Manusia
[Regular] Borrow limit reached. Maximum 3 items for regular members.

[Premium] Sari borrowed: Filosofi Kopi
[Premium] Sari borrowed: Pulang
[Premium] Sari borrowed: Hujan
[Premium] Sari borrowed: Perahu Kertas
[Premium] Sari borrowed: Supernova
[Premium] Borrow limit reached. Maximum 5 items for premium members.
```

---

## Feature 3 — Reservation System *(25 points)*

### Requirement
When an item is unavailable, a member can **place a reservation**. The librarian manages all active reservations and can display them.

### New Class: `Reservation`

| Section | Detail |
|---|---|
| **Attributes** | `reservationId : String`, `member : Member`, `item : LibraryItem`, `reservationDate : String`, `active : boolean` |
| **Methods** | `getReservationId()`, `getMember()`, `getItem()`, `isActive()`, `cancelReservation()`, `getInfo()` |

### Implementation

```java
// Reservation.java
public class Reservation {
    private String reservationId;
    private Member member;
    private LibraryItem item;
    private String reservationDate;
    private boolean active;

    public Reservation(String reservationId, Member member,
                       LibraryItem item, String reservationDate) {
        this.reservationId   = reservationId;
        this.member          = member;
        this.item            = item;
        this.reservationDate = reservationDate;
        this.active          = true;
    }

    public void cancelReservation() { this.active = false; }

    public String getInfo() {
        return "[Reservation] ID: " + reservationId
             + " | Member: " + member.getName()
             + " | Item: "   + item.getTitle()
             + " | Date: "   + reservationDate
             + " | Status: " + (active ? "Active" : "Cancelled");
    }
}
```

### Additions to `Librarian`

```java
// Added to Librarian.java
private Reservation[] reservations = new Reservation[100];
private int reservationCount = 0;

public Reservation addReservation(Member member, LibraryItem item) {
    String resId   = "RES" + String.format("%03d", reservationCount + 1);
    String resDate = "Day-" + (reservationCount + 1);
    Reservation res = new Reservation(resId, member, item, resDate);
    reservations[reservationCount++] = res;
    return res;
}

public void displayAllReservations() {
    for (int i = 0; i < reservationCount; i++) {
        if (reservations[i].isActive()) {
            System.out.println("  " + reservations[i].getInfo());
        }
    }
}
```

### Demo Output
```
  ---- Active Reservations (1 total) ----
  [Reservation] ID: RES001 | Member: Sari | Item: Dilan | Date: Day-1 | Status: Active
```

---

## Feature 4 — Search by Genre / Type *(20 points)*

### Requirement
The librarian can filter the catalog by **genre** (for Books) or by **type** (for Multimedia). Both use the **same method name** to demonstrate **method overloading**.

### Implementation — Added to `Librarian`

```java
// Overloaded method 1: search Books by genre
public void searchByGenre(Books[] catalog, int size, String genre) {
    System.out.println("Search results for genre: " + genre);
    boolean found = false;
    for (int i = 0; i < size; i++) {
        if (catalog[i] != null && catalog[i].getGenre().equalsIgnoreCase(genre)) {
            System.out.println("- " + catalog[i].getTitle()
                + " | Author: " + catalog[i].getAuthor()
                + " | Available: " + catalog[i].isAvailable());
            found = true;
        }
    }
    if (!found) System.out.println("No items found for: " + genre);
}

// Overloaded method 2: search Multimedia by type (same name, different parameter type)
public void searchByGenre(Multimedia[] catalog, int size, String type) {
    System.out.println("Search results for type: " + type);
    boolean found = false;
    for (int i = 0; i < size; i++) {
        if (catalog[i] != null && catalog[i].getType().equalsIgnoreCase(type)) {
            System.out.println("- " + catalog[i].getTitle()
                + " | Type: " + catalog[i].getType()
                + " | Available: " + catalog[i].isAvailable());
            found = true;
        }
    }
    if (!found) System.out.println("No items found for: " + type);
}
```

> **Method Overloading** — Java selects the correct version at compile time based on the parameter type (`Books[]` vs `Multimedia[]`).

### Demo Output
```
Search results for genre: Fiction
- To Kill a Mockingbird | Author: Harper Lee | Available: true
- The Catcher in the Rye | Author: J.D. Salinger | Available: true

Search results for type: DVD
- Inception | Type: DVD | Available: true
```

---

## Bonus — Exception Handling *(+15 points)*

### Requirement
Add meaningful exception handling for at least **2 scenarios** using custom exception classes that extend `RuntimeException`.

### New Exception Classes

| Class | Extends | Scenario |
|---|---|---|
| `ItemNotAvailableException` | `RuntimeException` | Member tries to borrow an unavailable item |
| `BorrowLimitExceededException` | `RuntimeException` | Member tries to borrow beyond their limit |
| `InvalidFineException` | `RuntimeException` | Fine issued for an on-time return (daysLate ≤ 0) |

### Implementation

```java
// ItemNotAvailableException.java
public class ItemNotAvailableException extends RuntimeException {
    public ItemNotAvailableException(String message) { super(message); }
}

// BorrowLimitExceededException.java
public class BorrowLimitExceededException extends RuntimeException {
    public BorrowLimitExceededException(String message) { super(message); }
}

// InvalidFineException.java
public class InvalidFineException extends RuntimeException {
    public InvalidFineException(String message) { super(message); }
}
```

### Usage in `main.java`

```java
// Scenario 1: borrow unavailable item
try {
    member.borrowItem(unavailableItem);
} catch (ItemNotAvailableException e) {
    System.out.println("Error: " + e.getMessage());
}

// Scenario 2: exceed borrow limit
try {
    member.borrowItem(book1);
    member.borrowItem(book2);
    member.borrowItem(book3);
    member.borrowItem(book4); // throws here
} catch (BorrowLimitExceededException e) {
    System.out.println("Error: " + e.getMessage());
}
```

### Demo Output
```
Error: Item 'Dilan' is currently not available for borrowing.
[Regular] Budi borrowed: Book A
[Regular] Budi borrowed: Book B
[Regular] Budi borrowed: Book C
Error: Budi has reached the maximum borrow limit of 3 items.
```

---

## Class Diagram Summary

```
Person
├── Member
│   └── PremiumMember          (Feature 2 — new)
└── Librarian

LibraryItem
├── Books
└── Multimedia

BorrowRecord ──────────────── links Member + LibraryItem
Fine         ──────────────── links Member + LibraryItem  (Feature 1 — new)
Reservation  ──────────────── links Member + LibraryItem  (Feature 3 — new)

Exceptions (Feature Bonus — new):
├── ItemNotAvailableException   extends RuntimeException
├── BorrowLimitExceededException extends RuntimeException
└── InvalidFineException        extends RuntimeException
```

---

## OOP Concepts Applied

| Concept | Applied In |
|---|---|
| **Inheritance** | `PremiumMember extends Member`; `Member/Librarian extend Person`; `Books/Multimedia extend LibraryItem` |
| **Overriding** | `PremiumMember.borrowItem()`, `PremiumMember.getInfo()`, `PremiumMember.toString()` override `Member`'s versions |
| **Overloading** | `Librarian.searchByGenre(Books[], int, String)` and `searchByGenre(Multimedia[], int, String)` — same name, different parameter types |
| **Encapsulation** | All attributes are `private` or `protected`; accessed only through getters and setters |
| **Polymorphism** | `LibraryItem` reference holds both `Books` and `Multimedia`; `Member` reference holds both `Member` and `PremiumMember` |
| **Exception Handling** | Custom `RuntimeException` subclasses thrown in `borrowItem()` and `issueFine()`, caught with `try-catch` in `main` |
