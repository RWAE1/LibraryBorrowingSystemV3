/**
 * @author      masjohncook X RWAE1
 * @version     0.0.3
 * @copyright   (C) Copyright 2026
 * @license     None
 * @maintainer  masjohncook
 * @email       mas.john.cook@gmail.com
 * @status      None
 */
package LibraryBorrowingSystem.Model;

import LibraryBorrowingSystem.Database.DatabaseManager;
import java.time.LocalDate;
import java.util.List;

/**
 * Represents the librarian who manages the library system.
 * Inherits from Person — gains id and name.
 *
 * The Librarian is responsible for managing the book catalog,
 * the multimedia catalog, and the list of registered members.
 * It also keeps a full record of all borrow and return transactions.
 * On creation, initial data is automatically loaded from each class.
 *
 * Inheritance:
 *   Librarian extends Person
 *   - Inherits : id, name (and their getters/setters)
 *   - Adds     : catalog[], multimedia[], members[], borrowRecords[]
 *   - Overrides: getInfo(), toString()
 *
 * Overloading:
 *   addBook(id, title, author, genre) — full version
 *   addBook(id, title, author)        — genre defaults to "General"
 *   addMultimedia(id, title, type, duration) — full version
 *   addMultimedia(id, title, type)           — duration defaults to "Unknown"
 *
 * Associations:
 *   - catalog[]      : array of all Books in the library
 *   - multimedia[]   : array of all Multimedia items in the library
 *   - members[]      : array of all registered Members
 *   - borrowRecords[]: array of all BorrowRecord transactions
 */
public class Librarian extends Person {

    // catalog stores all Books objects in the library
    // Association: Librarian -> Books[]
    private Books[] catalog;

    // catalogCount tracks how many books are in the catalog array
    private int catalogCount;

    // multimedia stores all Multimedia objects in the library
    // Association: Librarian -> Multimedia[]
    private Multimedia[] multimedia;

    // multimediaCount tracks how many multimedia items are in the array
    private int multimediaCount;

    // members stores all registered Member objects
    // Association: Librarian -> Member[]
    private Member[] members;

    // memberCount tracks how many members are registered
    private int memberCount;

    // borrowRecords stores every borrow/return transaction
    private BorrowRecord[] borrowRecords;

    // recordCount tracks how many borrow records have been created
    private int recordCount;

    // reservations stores all active and cancelled reservations (Feature 3)
    private Reservation[] reservations;

    // reservationCount tracks how many reservations have been created
    private int reservationCount;

    // fineCount is used to generate unique fine IDs (Feature 1)
    private int fineCount;

    // Maximum sizes for each array
    private static final int MAX_BOOKS         = 100;
    private static final int MAX_MULTIMEDIA    = 50;
    private static final int MAX_MEMBERS       = 50;
    private static final int MAX_RECORDS       = 200;
    private static final int MAX_RESERVATIONS  = 100;

    /**
     * Creates a new Librarian and automatically loads initial data
     * from the Books, Multimedia, and Member classes.
     * Calls the Person constructor (super) to set id and name.
     *
     * @param librarianId unique identifier for the librarian (e.g. "L001")
     * @param name        full name of the librarian
     */
    public Librarian(String librarianId, String name) {
        // Call the parent Person constructor to set this.id and this.name
        super(librarianId, name);

        // Initialize the books catalog array with MAX_BOOKS empty slots
        this.catalog = new Books[MAX_BOOKS];
        this.catalogCount = 0;

        // Initialize the multimedia array with MAX_MULTIMEDIA empty slots
        this.multimedia = new Multimedia[MAX_MULTIMEDIA];
        this.multimediaCount = 0;

        // Initialize the members array with MAX_MEMBERS empty slots
        this.members = new Member[MAX_MEMBERS];
        this.memberCount = 0;

        // Initialize the borrow records array with MAX_RECORDS empty slots
        this.borrowRecords = new BorrowRecord[MAX_RECORDS];
        this.recordCount = 0;

        // Initialize the reservations array (Feature 3)
        this.reservations = new Reservation[MAX_RESERVATIONS];
        this.reservationCount = 0;

        // Fine ID counter starts at 0 (Feature 1)
        this.fineCount = 0;

        DatabaseManager.initSchema();

        // Load all initial data from each class
        loadFromDatabase();
    }

   // ── Startup: load from DB, seed if empty ─────────────────────────────────

    private void loadFromDatabase() {
        if (DatabaseManager.isTableEmpty("books")) seedInitialData();

        for (Books b : DatabaseManager.loadBooks())           catalog[catalogCount++]     = b;
        for (Multimedia m : DatabaseManager.loadMultimedia()) multimedia[multimediaCount++] = m;
        for (Member m : DatabaseManager.loadMembers()) {
            members[memberCount++] = m;
            for (String itemId : DatabaseManager.loadBorrowedItemIds(m.getMemberId())) {
                LibraryItem item = findItemById(itemId);
                if (item != null) m.addBorrowedItem(item);
            }
        }

        recordCount      = DatabaseManager.getBorrowRecordCount();
        reservationCount = DatabaseManager.getReservationCount();
        fineCount = DatabaseManager.loadFineRows().size();

        List<String[]> rows = DatabaseManager.loadBorrowRecordRows();
        int idx = 0;
        for (String[] row : rows) {
            Member      m    = findMemberById(row[1]);
            LibraryItem item = findItemById(row[2]);
            if (m == null || item == null) { recordCount--; continue; }
            BorrowRecord r = new BorrowRecord(row[0], m, item, row[3]);
            r.setReturnDate(row[4]);
            r.setReturned(row[5].equals("1"));
            borrowRecords[idx++] = r;
        }
    }

    private void seedInitialData() {
        for (Books b : Books.getInitialBooks())                DatabaseManager.insertBook(b);
        for (Multimedia m : Multimedia.getInitialMultimedia()) DatabaseManager.insertMultimedia(m);
        for (Member m : Member.getInitialMembers())            DatabaseManager.insertMember(m);
    }

// ── Books ─────────────────────────────────────────────────────────────────

    public Books addBook(String bookId, String title, String author, String genre) {
        if (catalogCount >= MAX_BOOKS)    return null;
        if (findBookById(bookId) != null) return null;
        Books book = new Books(bookId, title, author, genre);
        catalog[catalogCount++] = book;
        DatabaseManager.insertBook(book);
        return book;
    }

    public Books addBook(String bookId, String title, String author) {
        return addBook(bookId, title, author, "General");
    }

    public boolean removeBook(String bookId) {
        for (int i = 0; i < catalogCount; i++) {
            if (catalog[i].getBookId().equals(bookId)) {
                if (!catalog[i].isAvailable()) return false;
                for (int j = i; j < catalogCount - 1; j++) catalog[j] = catalog[j + 1];
                catalog[--catalogCount] = null;
                DatabaseManager.deleteBook(bookId);
                return true;
            }
        }
        return false;
    }

    public boolean updateBook(String bookId, String newTitle, String newAuthor, String newGenre) {
        Books book = findBookById(bookId);
        if (book == null) return false;
        if (!newTitle.trim().isEmpty())  book.setTitle(newTitle.trim());
        if (!newAuthor.trim().isEmpty()) book.setAuthor(newAuthor.trim());
        if (!newGenre.trim().isEmpty())  book.setGenre(newGenre.trim());
        DatabaseManager.updateBook(book);
        return true;
    }

    public Books findBookById(String bookId) {
        for (int i = 0; i < catalogCount; i++)
            if (catalog[i].getBookId().equals(bookId)) return catalog[i];
        return null;
    }

    // ── Multimedia ────────────────────────────────────────────────────────────

    public Multimedia addMultimedia(String itemId, String title, String type, String duration) {
        if (multimediaCount >= MAX_MULTIMEDIA)  return null;
        if (findMultimediaById(itemId) != null) return null;
        Multimedia item = new Multimedia(itemId, title, type, duration);
        multimedia[multimediaCount++] = item;
        DatabaseManager.insertMultimedia(item);
        return item;
    }

    public Multimedia addMultimedia(String itemId, String title, String type) {
        return addMultimedia(itemId, title, type, "Unknown");
    }

    public Multimedia findMultimediaById(String itemId) {
        for (int i = 0; i < multimediaCount; i++)
            if (multimedia[i].getItemId().equals(itemId)) return multimedia[i];
        return null;
    }

    // ── Members ───────────────────────────────────────────────────────────────

    public Member registerMember(String memberId, String name) {
        if (memberCount >= MAX_MEMBERS)       return null;
        if (findMemberById(memberId) != null) return null;
        Member member = new Member(memberId, name);
        members[memberCount++] = member;
        DatabaseManager.insertMember(member);
        return member;
    }

    public boolean removeMember(String memberId) {
        for (int i = 0; i < memberCount; i++) {
            if (members[i].getMemberId().equals(memberId)) {
                if (members[i].getBorrowCount() > 0) return false;
                for (int j = i; j < memberCount - 1; j++) members[j] = members[j + 1];
                members[--memberCount] = null;
                DatabaseManager.deleteMember(memberId);
                return true;
            }
        }
        return false;
    }

    public Member findMemberById(String memberId) {
        for (int i = 0; i < memberCount; i++)
            if (members[i].getMemberId().equals(memberId)) return members[i];
        return null;
    }

    // ── Combined catalog lookup ───────────────────────────────────────────────

    public LibraryItem findItemById(String itemId) {
        Books book = findBookById(itemId);
        if (book != null) return book;
        return findMultimediaById(itemId);
    }

    public LibraryItem[] getAllItems() {
        LibraryItem[] all = new LibraryItem[catalogCount + multimediaCount];
        for (int i = 0; i < catalogCount; i++)    all[i]                = catalog[i];
        for (int i = 0; i < multimediaCount; i++) all[catalogCount + i] = multimedia[i];
        return all;
    }

    public int getAllItemsCount() { return catalogCount + multimediaCount; }

    // ── Borrow records ────────────────────────────────────────────────────────

    public void recordBorrow(Member member, LibraryItem item) {
        if (recordCount >= MAX_RECORDS) return;
        String today    = LocalDate.now().toString();
        String recordId = "REC" + String.format("%03d", recordCount + 1);
        BorrowRecord record = new BorrowRecord(recordId, member, item, today);
        borrowRecords[recordCount++] = record;
        DatabaseManager.insertBorrowRecord(record);
        DatabaseManager.insertMemberBorrowedItem(member.getMemberId(), item.getItemId());
        DatabaseManager.updateItemAvailability(item.getItemId(), false);
    }

    public void recordReturn(Member member, LibraryItem item) {
        String today = LocalDate.now().toString();
        for (int i = 0; i < recordCount; i++) {
            BorrowRecord r = borrowRecords[i];
            if (!r.isReturned()
                    && r.getMember().getMemberId().equals(member.getMemberId())
                    && r.getItem().getItemId().equals(item.getItemId())) {
                r.setReturnDate(today);
                r.setReturned(true);
                DatabaseManager.markBorrowRecordReturned(member.getMemberId(), item.getItemId(), today);
                DatabaseManager.deleteMemberBorrowedItem(member.getMemberId(), item.getItemId());
                DatabaseManager.updateItemAvailability(item.getItemId(), true);
                return;
            }
        }
    }

    // ── Reservations ──────────────────────────────────────────────────────────

    public Reservation addReservation(Member member, LibraryItem item) {
        if (reservationCount >= MAX_RESERVATIONS) return null;
        String resId   = "RES" + String.format("%03d", reservationCount + 1);
        String resDate = LocalDate.now().toString();
        Reservation res = new Reservation(resId, member, item, resDate);
        reservations[reservationCount++] = res;
        DatabaseManager.insertReservation(resId, member.getMemberId(), item.getItemId(), resDate);
        return res;
    }

    // ── Fines ─────────────────────────────────────────────────────────────────

    public Fine issueFine(Member member, LibraryItem item, int daysLate) {
        // Generate a unique ID using fineCount
        String fineId = "FINE" + String.format("%03d", fineCount + 1);
        
        // Create the fine
        Fine newFine = new Fine(fineId, member, item, daysLate);
        
        // Increment the counter so the next one gets a new number
        fineCount++;
        
        // Save it to the database
        DatabaseManager.insertFine(newFine);
        
        return newFine;
    }

    // ── Search by Genre / Type (method overloading) ───────────────────────────

    public List<Books> searchByGenre(Books[] catalog, int size, String genre) {
        List<Books> results = new java.util.ArrayList<>();
        for (int i = 0; i < size; i++)
            if (catalog[i] != null && catalog[i].getGenre().equalsIgnoreCase(genre))
                results.add(catalog[i]);
        return results;
    }

    public List<Multimedia> searchByGenre(Multimedia[] catalog, int size, String type) {
        List<Multimedia> results = new java.util.ArrayList<>();
        for (int i = 0; i < size; i++)
            if (catalog[i] != null && catalog[i].getType().equalsIgnoreCase(type))
                results.add(catalog[i]);
        return results;
    }

    // ── Info ──────────────────────────────────────────────────────────────────

    @Override
    public String getInfo()  { return "Librarian[" + id + "] " + name; }
    public String toString() { return getInfo(); }

    // ── Getters ───────────────────────────────────────────────────────────────

    public String          getLibrarianId()      { return id; }
    public Books[]         getCatalog()          { return catalog; }
    public int             getCatalogCount()     { return catalogCount; }
    public int             getCatalogSize()      { return catalogCount; }
    public Multimedia[]    getMultimedia()       { return multimedia; }
    public int             getMultimediaCount()  { return multimediaCount; }
    public Member[]        getMembers()          { return members; }
    public int             getMemberCount()      { return memberCount; }
    public BorrowRecord[]  getBorrowRecords()    { return borrowRecords; }
    public int             getRecordCount()      { return recordCount; }
    public Reservation[]   getReservations()     { return reservations; }
    public int             getReservationCount() { return reservationCount; }
}