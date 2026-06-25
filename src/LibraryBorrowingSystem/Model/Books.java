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

/**
 * Represents a book in the library collection.
 * Inherits from LibraryItem — gains itemId, title, and available.
 *
 * A Books object is a specific type of LibraryItem that adds
 * two extra attributes: the author and the genre of the book.
 * It also provides the initial book data used at system startup.
 *
 * Inheritance:
 *   Books extends LibraryItem
 *   - Inherits : itemId, title, available (and their getters/setters)
 *   - Adds     : author, genre
 *   - Overrides: getInfo(), toString()
 *
 * Attributes:
 *   - author : the name of the book's author
 *   - genre  : the category of the book (e.g. "Novel", "Sci-Fi", "Classic")
 */
public class Books extends LibraryItem {

    // author stores the name of who wrote this book
    private String author;

    // genre stores the category of this book, e.g. "Novel", "Sci-Fi", "Classic"
    private String genre;

    /**
     * Creates a new book with all details provided.
     * Calls the LibraryItem constructor (super) to set itemId, title, available.
     *
     * @param bookId unique book identifier (e.g. "B001")
     * @param title  title of the book
     * @param author author of the book
     * @param genre  genre/category of the book
     */
    public Books(String bookId, String title, String author, String genre) {
        // Call the parent LibraryItem constructor to set itemId, title, and available=true
        super(bookId, title);

        // Store the author — this is specific to Books, not in LibraryItem
        this.author = author;

        // Store the genre — this is specific to Books, not in LibraryItem
        this.genre = genre;
    }

    /**
     * Returns the pre-defined initial book data for the library.
     * Keeping this data here ensures the Books class owns its own defaults.
     *
     * @return array of Books objects pre-filled with default catalog entries
     */
    public static Books[] getInitialBooks() {
        // Create an array that can hold 5 Books objects
        Books[] initial = new Books[5];

        // Fill each slot with a pre-defined book: ID, title, author, genre
        initial[0] = new Books("B001", "The Great Gatsby",       "F. Scott Fitzgerald", "Classic");
        initial[1] = new Books("B002", "To Kill a Mockingbird",  "Harper Lee",          "Fiction");
        initial[2] = new Books("B003", "1984",                   "George Orwell",       "Dystopian");
        initial[3] = new Books("B004", "Brave New World",        "Aldous Huxley",       "Sci-Fi");
        initial[4] = new Books("B005", "The Catcher in the Rye", "J.D. Salinger",       "Fiction");

        // Return the completed array
        return initial;
    }

    /**
     * Returns a detailed description of this book.
     * OVERRIDES the base getInfo() in LibraryItem with book-specific details.
     * The parent version only shows itemId, title, and status.
     * This version also adds author and genre.
     *
     * @return a formatted string with full book details
     */
    @Override
    public String getInfo() {
        // Determine the availability status using if-else
        String status;
        if (available == true) {
            status = "Available";
        } else {
            status = "Borrowed";
        }

        // Build a string that includes the book-specific fields
        // available, itemId, title are accessed directly because they are protected in LibraryItem
        return "[" + itemId + "] \"" + title + "\" by " + author
                + " | Genre: " + genre
                + " (" + status + ")";
    }

    /**
     * Returns a readable summary of this book.
     * Calls the overridden getInfo() method from this class.
     */
    @Override
    public String toString() {
        // Delegates to getInfo() — which is the overridden version in Books
        return getInfo();
    }

    // ── Getters ───────────────────────────────────────────────────────────────

    /**
     * Returns the book ID.
     * This is an alias for getItemId() inherited from LibraryItem,
     * kept for readability when working specifically with books.
     */
    public String getBookId() { return itemId; }

    /** Returns the author of this book. */
    public String getAuthor() { return author; }

    /** Returns the genre/category of this book. */
    public String getGenre() { return genre; }

    // ── Setters ───────────────────────────────────────────────────────────────

    /** Updates the book ID. */
    public void setBookId(String bookId) { this.itemId = bookId; }

    /** Updates the author of this book. */
    public void setAuthor(String author) { this.author = author; }

    /** Updates the genre/category of this book. */
    public void setGenre(String genre) { this.genre = genre; }
}
