/**
 * @author      masjohncook X RWAE1
 * @version     0.0.3
 * @copyright   (C) Copyright 2026
 * @license     None
 * @maintainer  masjohncook
 * @email       mas.john.cook@gmail.com
 * @status      Development
 */
package LibraryBorrowingSystem.Model;

import LibraryBorrowingSystem.Database.DatabaseManager;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Central service layer that owns all in-memory data and coordinates
 * every operation that reads or writes to the database.
 *
 * LibraryService is the single source of truth for the application's
 * runtime state. It is created once in App.java and injected into
 * every tab controller, so all tabs share the same live data.
 *
 * Responsibilities:
 *   - Initialises the database schema on first run
 *   - Seeds initial books, multimedia, and members if the DB is empty
 *   - Loads all persisted data into memory on startup
 *   - Provides CRUD operations for Books, Multimedia, Members,
 *     BorrowRecords, Reservations, and Fines
 *   - Delegates all SQL to {@link DatabaseManager}
 *
 * In-memory collections:
 *   - catalog       : all Book objects
 *   - multimedia    : all Multimedia objects
 *   - members       : all Member objects
 *   - borrowRecords : all BorrowRecord objects (active and returned)
 *   - reservations  : all Reservation objects (active and cancelled)
 *   - fines         : all Fine objects
 */
public class LibraryService {

    private final List<Books>         catalog       = new ArrayList<>();
    private final List<Multimedia>   multimedia    = new ArrayList<>();
    private final List<Member>       members       = new ArrayList<>();
    private final List<BorrowRecord> borrowRecords = new ArrayList<>();
    private final List<Reservation>  reservations  = new ArrayList<>();
    private final List<Fine>         fines         = new ArrayList<>();

    public LibraryService() {
        DatabaseManager.initSchema();
        loadFromDatabase();
    }

    // ── Startup ───────────────────────────────────────────────────────────────

    private void loadFromDatabase() {
        seedInitialData(); // INSERT OR IGNORE — safe to run every startup; adds new entries without touching existing ones

        for (Books b : DatabaseManager.loadBooks())             catalog.add(b);
        for (Multimedia m : DatabaseManager.loadMultimedia())  multimedia.add(m);

        for (Member m : DatabaseManager.loadMembers()) {
            members.add(m);
            for (String itemId : DatabaseManager.loadBorrowedItemIds(m.getMemberId())) {
                LibraryItem item = findItemById(itemId);
                if (item != null) m.addBorrowedItem(item);
            }
        }

        for (String[] row : DatabaseManager.loadBorrowRecordRows()) {
            Member      m    = findMemberById(row[1]);
            LibraryItem item = findItemById(row[2]);
            if (m == null || item == null) continue;
            BorrowRecord r = new BorrowRecord(row[0], m, item, row[3]);
            r.setReturnDate(row[4]);
            r.setReturned(row[5].equals("1"));
            borrowRecords.add(r);
        }

        for (String[] row : DatabaseManager.loadReservationRows()) {
            Member      m    = findMemberById(row[1]);
            LibraryItem item = findItemById(row[2]);
            if (m == null || item == null) continue;
            Reservation res = new Reservation(row[0], m, item, row[3]);
            if (row[4].equals("0")) res.cancelReservation();
            reservations.add(res);
        }

        for (String[] row : DatabaseManager.loadFineRows()) {
            Member      m    = findMemberById(row[1]);
            LibraryItem item = findItemById(row[2]);
            if (m == null || item == null) continue;
            fines.add(new Fine(row[0], m, item, Integer.parseInt(row[3])));
        }
    }

    private void seedInitialData() {
        for (Books b : Books.getInitialBooks())                  DatabaseManager.insertBook(b);
        for (Multimedia m : Multimedia.getInitialMultimedia()) DatabaseManager.insertMultimedia(m);
        for (Member m : Member.getInitialMembers())            DatabaseManager.insertMember(m);
    }

    // ── Books ─────────────────────────────────────────────────────────────────

    /**
     * Adds a new book to the catalog and persists it to the database.
     *
     * @return the new Book, or null if the ID already exists
     */
    public Books addBook(String bookId, String title, String author, String genre) {
        if (findBookById(bookId) != null) return null;
        Books book = new Books(bookId, title, author, genre);
        catalog.add(book);
        DatabaseManager.insertBook(book);
        return book;
    }

    /** Overload that defaults genre to "General". */
    public Books addBook(String bookId, String title, String author) {
        return addBook(bookId, title, author, "General");
    }

    /**
     * Removes a book from the catalog and deletes it from the database.
     *
     * @return false if the book is currently borrowed (cannot delete)
     */
    public boolean removeBook(String bookId) {
        for (int i = 0; i < catalog.size(); i++) {
            if (catalog.get(i).getBookId().equals(bookId)) {
                if (!catalog.get(i).isAvailable()) return false;
                catalog.remove(i);
                DatabaseManager.deleteBook(bookId);
                return true;
            }
        }
        return false;
    }

    /**
     * Updates a book's fields. Empty strings are ignored (field unchanged).
     *
     * @return false if no book with the given ID exists
     */
    public boolean updateBook(String bookId, String newTitle, String newAuthor, String newGenre) {
        Books book = findBookById(bookId);
        if (book == null) return false;
        if (!newTitle.trim().isEmpty())  book.setTitle(newTitle.trim());
        if (!newAuthor.trim().isEmpty()) book.setAuthor(newAuthor.trim());
        if (!newGenre.trim().isEmpty())  book.setGenre(newGenre.trim());
        DatabaseManager.updateBook(book);
        return true;
    }

    /** @return the Books with the given ID, or null if not found */
    public Books findBookById(String bookId) {
        for (Books b : catalog)
            if (b.getBookId().equals(bookId)) return b;
        return null;
    }

    // ── Multimedia ────────────────────────────────────────────────────────────

    /**
     * Adds a new multimedia item and persists it to the database.
     *
     * @return the new Multimedia, or null if the ID already exists
     */
    public Multimedia addMultimedia(String itemId, String title, String type, String duration) {
        if (findMultimediaById(itemId) != null) return null;
        Multimedia item = new Multimedia(itemId, title, type, duration);
        multimedia.add(item);
        DatabaseManager.insertMultimedia(item);
        return item;
    }

    /** Overload that defaults duration to "Unknown". */
    public Multimedia addMultimedia(String itemId, String title, String type) {
        return addMultimedia(itemId, title, type, "Unknown");
    }

    /** @return the Multimedia item with the given ID, or null if not found */
    public Multimedia findMultimediaById(String itemId) {
        for (Multimedia m : multimedia)
            if (m.getItemId().equals(itemId)) return m;
        return null;
    }

    // ── Members ───────────────────────────────────────────────────────────────

    /**
     * Registers a new member and persists them to the database.
     *
     * @return the new Member, or null if the ID already exists
     */
    public Member registerMember(String memberId, String name) {
        if (findMemberById(memberId) != null) return null;
        Member member = new Member(memberId, name);
        members.add(member);
        DatabaseManager.insertMember(member);
        return member;
    }

    /**
     * Removes a member from the system and deletes them from the database.
     *
     * @return false if the member still has borrowed items
     */
    public boolean removeMember(String memberId) {
        for (int i = 0; i < members.size(); i++) {
            if (members.get(i).getMemberId().equals(memberId)) {
                if (members.get(i).getBorrowCount() > 0) return false;
                members.remove(i);
                DatabaseManager.deleteMember(memberId);
                return true;
            }
        }
        return false;
    }

    /** @return the Member with the given ID, or null if not found */
    public Member findMemberById(String memberId) {
        for (Member m : members)
            if (m.getMemberId().equals(memberId)) return m;
        return null;
    }

    // ── Combined catalog lookup ───────────────────────────────────────────────

    /**
     * Looks up a LibraryItem by ID, searching both books and multimedia.
     *
     * @return the matching Book or Multimedia, or null if not found
     */
    public LibraryItem findItemById(String itemId) {
        Books book = findBookById(itemId);
        if (book != null) return book;
        return findMultimediaById(itemId);
    }

    // ── Borrow records ────────────────────────────────────────────────────────

    /**
     * Records a borrow transaction and persists it to the database.
     * Updates the item's availability and the member's borrowed-items list.
     * Generates a UUID-based record ID to avoid collisions across restarts.
     */
    public void recordBorrow(Member member, LibraryItem item) {
        String recordId = "REC-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String today    = LocalDate.now().toString();
        BorrowRecord record = new BorrowRecord(recordId, member, item, today);
        borrowRecords.add(record);
        DatabaseManager.insertBorrowRecord(record);
        DatabaseManager.insertMemberBorrowedItem(member.getMemberId(), item.getItemId());
        DatabaseManager.updateItemAvailability(item.getItemId(), false);
    }

    /**
     * Records a return transaction and updates all related state in the database.
     * Sets the return date, marks the record as returned, and restores the item's availability.
     */
    public void recordReturn(Member member, LibraryItem item) {
        String today = LocalDate.now().toString();
        for (BorrowRecord r : borrowRecords) {
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

    /**
     * Creates a reservation for a member on a given item and persists it to the database.
     *
     * @return the new Reservation object
     */
    public Reservation addReservation(Member member, LibraryItem item) {
        String resId   = "RES-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String resDate = LocalDate.now().toString();
        Reservation res = new Reservation(resId, member, item, resDate);
        reservations.add(res);
        DatabaseManager.insertReservation(resId, member.getMemberId(), item.getItemId(), resDate);
        return res;
    }

    // ── Fines ─────────────────────────────────────────────────────────────────

    /**
     * Issues a fine to a member for a late return and persists it to the database.
     *
     * @param daysLate number of days past the due date
     * @return the new Fine object
     */
    public Fine recordFine(Member member, LibraryItem item, int daysLate) {
        String fineId = "FINE-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        Fine fine = new Fine(fineId, member, item, daysLate);
        fines.add(fine);
        DatabaseManager.insertFine(fine);
        return fine;
    }

    // ── Search ────────────────────────────────────────────────────────────────

    /**
     * Returns all books whose genre matches the given string (case-insensitive).
     *
     * @param genre the genre to filter by
     * @return list of matching Book objects (empty if none found)
     */
    public List<Books> searchByGenre(String genre) {
        List<Books> results = new ArrayList<>();
        for (Books b : catalog)
            if (b.getGenre().equalsIgnoreCase(genre)) results.add(b);
        return results;
    }

    /**
     * Returns all multimedia items whose type matches the given string (case-insensitive).
     *
     * @param type the media type to filter by (e.g. "DVD", "CD")
     * @return list of matching Multimedia objects (empty if none found)
     */
    public List<Multimedia> searchByType(String type) {
        List<Multimedia> results = new ArrayList<>();
        for (Multimedia m : multimedia)
            if (m.getType().equalsIgnoreCase(type)) results.add(m);
        return results;
    }

    // ── Getters ───────────────────────────────────────────────────────────────

    public List<Books>        getCatalog()       { return catalog; }
    public List<Multimedia>   getMultimedia()    { return multimedia; }
    public List<Member>       getMembers()       { return members; }
    public List<BorrowRecord> getBorrowRecords() { return borrowRecords; }
    public List<Reservation>  getReservations()  { return reservations; }
    public List<Fine>         getFines()         { return fines; }
}