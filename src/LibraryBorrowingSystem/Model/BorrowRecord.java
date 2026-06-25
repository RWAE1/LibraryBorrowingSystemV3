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
 * Represents a single borrow transaction in the library system.
 *
 * A BorrowRecord is created every time a member borrows a LibraryItem
 * (which can be a Books or a Multimedia object), and it is updated when
 * the item is returned. It links a Member and a LibraryItem together
 * with date information.
 *
 * Associations:
 *   - member : links to the Member who borrowed the item  (Member)
 *   - item   : links to the item that was borrowed        (LibraryItem)
 *
 * Attributes:
 *   - recordId   : unique identifier for this record (e.g. "REC001")
 *   - member     : the member who borrowed the item
 *   - item       : the item that was borrowed (Books or Multimedia)
 *   - borrowDate : the date the item was borrowed
 *   - returnDate : the date the item was returned ("-" if not yet returned)
 *   - returned   : true if the item has been returned, false otherwise
 */
public class BorrowRecord {

    // recordId is the unique ID for this transaction, e.g. "REC001"
    private String recordId;

    // member holds a reference to the Member who borrowed the item
    // Association: BorrowRecord -> Member
    private Member member;

    // item holds a reference to the LibraryItem that was borrowed
    // It is typed as LibraryItem so it works for both Books and Multimedia
    // Association: BorrowRecord -> LibraryItem
    private LibraryItem item;

    // borrowDate stores the date when the item was taken out
    private String borrowDate;

    // returnDate stores the date when the item was brought back
    // Set to "-" by default until the item is actually returned
    private String returnDate;

    // returned is a flag: false means the item is still out, true means it has been returned
    private boolean returned;

    /**
     * Creates a new borrow record when a member takes a LibraryItem.
     * The record starts as not returned, with returnDate set to "-".
     *
     * @param recordId   unique identifier for this record
     * @param member     the member borrowing the item
     * @param item       the LibraryItem being borrowed (Books or Multimedia)
     * @param borrowDate the date the item is borrowed
     */
    public BorrowRecord(String recordId, Member member, LibraryItem item, String borrowDate) {
        // Save the unique record ID
        this.recordId = recordId;

        // Save the reference to the member who is borrowing
        this.member = member;

        // Save the reference to the item being borrowed (Books or Multimedia)
        this.item = item;

        // Save the date the item was borrowed
        this.borrowDate = borrowDate;

        // Set return date to "-" because the item has not been returned yet
        this.returnDate = "-";

        // Mark the record as not returned yet
        this.returned = false;
    }

    // ── Getters ───────────────────────────────────────────────────────────────

    /** Returns the unique record ID. */
    public String getRecordId() { return recordId; }

    /** Returns the member associated with this record. */
    public Member getMember() { return member; }

    /** Returns the LibraryItem (Books or Multimedia) associated with this record. */
    public LibraryItem getItem() { return item; }

    /** Returns the date the item was borrowed. */
    public String getBorrowDate() { return borrowDate; }

    /** Returns the date the item was returned, or "-" if not yet returned. */
    public String getReturnDate() { return returnDate; }

    /** Returns true if the item has been returned. */
    public boolean isReturned() { return returned; }

    // ── Setters ───────────────────────────────────────────────────────────────

    /** Updates the record ID. */
    public void setRecordId(String recordId) { this.recordId = recordId; }

    /** Updates the member linked to this record. */
    public void setMember(Member member) { this.member = member; }

    /** Updates the item linked to this record. */
    public void setItem(LibraryItem item) { this.item = item; }

    /** Updates the borrow date. */
    public void setBorrowDate(String borrowDate) { this.borrowDate = borrowDate; }

    /** Updates the return date when the item is handed back. */
    public void setReturnDate(String returnDate) { this.returnDate = returnDate; }

    /** Marks whether the item has been returned. */
    public void setReturned(boolean returned) { this.returned = returned; }

    /**
     * Returns a readable summary of this borrow record as a single String.
     * Example: Record[REC001] Member: Alice | Item: 1984 | Borrowed: 2026-04-29 | Returned: -
     */
    public String toString() {
        // Build a formatted string combining all record details into one readable line
        return "Record[" + recordId + "]"
                + " Member: "    + member.getName()   // get the member's name via getter
                + " | Item: "    + item.getTitle()     // get the item's title via getter
                + " | Borrowed: " + borrowDate
                + " | Returned: " + returnDate;
    }

    public String getInfo() {
        return toString();
    }
}