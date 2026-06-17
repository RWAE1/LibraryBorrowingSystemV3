/**
 * @author      masjohncook X RWAE 1
 * @version     0.0.2
 * @copyright   (C) Copyright 2026
 * @license     None
 * @maintainer  masjohncook
 * @email       mas.john.cook@gmail.com
 * @status      None
 */
package LibraryBorrowingSystem.Model;

/**
 * Base class representing any item that can be borrowed from the library.
 *
 * Both Books and Multimedia inherit from this class.
 * It holds the three attributes that every borrowable item shares:
 * a unique item ID, a title, and an availability status.
 * Subclasses add their own specific attributes on top of these.
 *
 * Associations:
 *   - Extended by Books and Multimedia
 *   - Used by BorrowRecord to track what was borrowed
 *   - Used by Member to store borrowed items
 *
 * Attributes:
 *   - itemId    : unique identifier for the item (e.g. "B001", "M001")
 *   - title     : the title of the item
 *   - available : true if the item is on the shelf, false if it is borrowed
 */
public class LibraryItem {

    // itemId is the unique code for this item (e.g. "B001" for a book, "M001" for multimedia)
    // protected means this class AND its subclasses can access it directly
    protected String itemId;

    // title stores the title of this item
    // protected so subclasses can use it directly without calling a getter
    protected String title;

    // available tracks whether the item is on the shelf (true) or borrowed (false)
    // protected so subclasses can read it directly in their getInfo() methods
    protected boolean available;

    /**
     * Creates a new library item with the given ID and title.
     * All items start as available when first created.
     *
     * @param itemId unique identifier for the item
     * @param title  title of the item
     */
    public LibraryItem(String itemId, String title) {
        // Store the unique item ID
        this.itemId = itemId;

        // Store the title of the item
        this.title = title;

        // Every new item starts as available on the shelf
        this.available = true;
    }

    /**
     * Returns a basic description of this item.
     * This is the BASE version — subclasses Books and Multimedia
     * will override this method with their own detailed version.
     *
     * @return a formatted string with the item's ID, title, and availability
     */
    public String getInfo() {
        // Determine the availability status as a word using if-else
        String status;
        if (available == true) {
            status = "Available";
        } else {
            status = "Borrowed";
        }

        // Base implementation — shows item ID, title, and status
        return "[" + itemId + "] \"" + title + "\" (" + status + ")";
    }

    // ── Getters ───────────────────────────────────────────────────────────────

    /** Returns the unique item ID. */
    public String getItemId() { return itemId; }

    /** Returns the title of the item. */
    public String getTitle() { return title; }

    /** Returns true if the item is currently available to borrow. */
    public boolean isAvailable() { return available; }

    // ── Setters ───────────────────────────────────────────────────────────────

    /** Updates the item ID. */
    public void setItemId(String itemId) { this.itemId = itemId; }

    /** Updates the title of the item. */
    public void setTitle(String title) { this.title = title; }

    /** Sets whether the item is available (true) or borrowed (false). */
    public void setAvailable(boolean available) { this.available = available; }

    /**
     * Returns a readable summary of this item.
     * Calls getInfo() so subclasses that override getInfo()
     * will automatically get the correct output here too.
     */
    public String toString() {
        return getInfo();
    }
}
