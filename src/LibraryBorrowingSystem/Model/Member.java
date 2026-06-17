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
 * Represents a library member who can borrow, return, and search for items.
 * Inherits from Person — gains id and name.
 *
 * A Member is a specific type of Person who interacts with the library
 * by borrowing and returning LibraryItems (Books or Multimedia).
 * The member can hold up to MAX_BORROW items at the same time.
 *
 * Inheritance:
 *   Member extends Person
 *   - Inherits : id, name (and their getters/setters)
 *   - Adds     : borrowedItems array, borrowCount
 *   - Overrides: getInfo(), toString()
 *
 * Overloading:
 *   searchItem(catalog, size, keyword) — search by keyword
 *   searchItem(catalog, size)          — no keyword, lists all available items
 *
 * Attributes:
 *   - borrowedItems : array of items the member is currently borrowing
 *   - borrowCount   : number of items currently borrowed
 */
public class Member extends Person {

    // borrowedItems holds all LibraryItems (Books or Multimedia) this member currently has
    // protected so PremiumMember can directly add/remove items without re-implementing the array
    protected LibraryItem[] borrowedItems;

    // borrowCount tracks how many items this member is currently borrowing
    // protected so PremiumMember can read and increment it in its overridden borrowItem()
    protected int borrowCount;

    // MAX_BORROW is the physical array capacity — enough for any member tier (premium max = 5)
    private static final int MAX_BORROW = 5;

    /**
     * Returns the maximum number of items this member tier can borrow at once.
     * Regular members are limited to 3. PremiumMember overrides this to return 5.
     * Called inside borrowItem() so the limit is always tier-appropriate.
     */
    protected int getBorrowLimit() { return 3; }

    /**
     * Creates a new Member with the given ID and name.
     * Calls the Person constructor (super) to set id and name.
     * The borrowed items array is initialized empty.
     *
     * @param memberId unique member identifier (e.g. "M001")
     * @param name     full name of the member
     */
    public Member(String memberId, String name) {
        // Call the parent Person constructor to set this.id and this.name
        super(memberId, name);

        // Create an empty array with MAX_BORROW slots to hold borrowed items
        this.borrowedItems = new LibraryItem[MAX_BORROW];

        // No items are borrowed yet, so the count starts at 0
        this.borrowCount = 0;
    }

    /**
     * Returns the pre-defined initial member data for the library.
     * Keeping this data here ensures the Member class owns its own defaults.
     *
     * @return array of Member objects pre-filled with default members
     */
    public static Member[] getInitialMembers() {
        // Create an array that can hold 3 Member objects
        Member[] initial = new Member[3];

        // Fill each slot with a pre-defined member using their ID and name
        initial[0] = new Member("M001", "Alice");
        initial[1] = new Member("M002", "Bob");
        initial[2] = new Member("M003", "Charlie");

        // Return the completed array
        return initial;
    }

    /**
     * Borrows a library item (Book or Multimedia) for this member.
     * Regular members are limited to 3 items (getBorrowLimit() returns 3).
     * PremiumMember overrides this method for a limit of 5.
     *
     * Throws ItemNotAvailableException if the item is currently borrowed by someone else.
     * Throws BorrowLimitExceededException if this member has reached their tier limit.
     *
     * @param item the LibraryItem to borrow (can be a Books or Multimedia object)
     * @return true if the borrow was successful
     */
     // Returns true/false — View layer handles the output message
    public boolean borrowItem(LibraryItem item) {
        if (!item.isAvailable()) return false;
        if (borrowCount >= getBorrowLimit()) return false;
        borrowedItems[borrowCount++] = item;
        item.setAvailable(false);
        return true;
    }

    public boolean returnItem(LibraryItem item) {
        for (int i = 0; i < borrowCount; i++) {
            if (borrowedItems[i] != null
                    && borrowedItems[i].getItemId().equals(item.getItemId())) {
                item.setAvailable(true);
                borrowedItems[i] = borrowedItems[--borrowCount];
                borrowedItems[borrowCount] = null;
                return true;
            }
        }
        return false;
    }

    public java.util.List<LibraryItem> searchItem(LibraryItem[] catalog, int catalogSize, String keyword) {
        java.util.List<LibraryItem> results = new java.util.ArrayList<>();
        for (int i = 0; i < catalogSize; i++)
            if (catalog[i] != null
                    && catalog[i].getTitle().toLowerCase().contains(keyword.toLowerCase()))
                results.add(catalog[i]);
        return results;
    }

    public java.util.List<LibraryItem> searchItem(LibraryItem[] catalog, int catalogSize) {
        java.util.List<LibraryItem> results = new java.util.ArrayList<>();
        for (int i = 0; i < catalogSize; i++)
            if (catalog[i] != null && catalog[i].isAvailable())
                results.add(catalog[i]);
        return results;
    }

    @Override
    public String getInfo() {
        return "Member[" + id + "] " + name + " (borrowing: " + borrowCount + " item(s))";
    }

    @Override
    public String toString() { return getInfo(); }

    // Used only during DB loading to restore borrowed state without availability check
    void addBorrowedItem(LibraryItem item) {
        if (borrowCount < borrowedItems.length) borrowedItems[borrowCount++] = item;
    }

    public String getMemberId()             { return id; }
    public LibraryItem[] getBorrowedItems() { return borrowedItems; }
    public int getBorrowCount()             { return borrowCount; }

    public void setMemberId(String memberId) { this.id = memberId; }
}