package LibraryBorrowingSystem.Model;

/**
 * Represents a premium-tier library member with an extended borrow limit.
 *
 * Feature 2 — Premium Member
 * PremiumMember extends Member and raises the borrow limit from 3 to 5.
 * It overrides borrowItem() to enforce the premium limit and to use
 * the [Premium] prefix in output messages.
 *
 * Inheritance:
 *   PremiumMember extends Member
 *   - Inherits : id, name, borrowedItems[], borrowCount, returnItem(), searchItem()
 *   - Adds     : membershipCode
 *   - Overrides: getBorrowLimit(), borrowItem(), getInfo(), toString()
 */
public class PremiumMember extends Member {

    private String membershipCode;

    private static final int PREMIUM_LIMIT = 5;

    /**
     * Creates a new PremiumMember.
     *
     * @param memberId       unique member ID (e.g. "PM001")
     * @param name           full name of the member
     * @param membershipCode unique premium membership code
     */
    public PremiumMember(String memberId, String name, String membershipCode) {
        super(memberId, name);
        this.membershipCode = membershipCode;
    }

    /**
     * Returns the premium borrow limit (5).
     * Overrides Member.getBorrowLimit() which returns 3.
     */
    @Override
    protected int getBorrowLimit() {
        return PREMIUM_LIMIT;
    }

    /**
     * Borrows a library item, enforcing the premium limit of 5.
     * Throws ItemNotAvailableException if the item is not on the shelf.
     * Throws BorrowLimitExceededException if this member already holds 5 items.
     *
     * @param item the LibraryItem to borrow
     * @return true if the borrow was successful
     */
    @Override
    public boolean borrowItem(LibraryItem item) {
        if (!item.isAvailable()) {
            throw new ItemNotAvailableException(
                    "Item '" + item.getTitle() + "' is currently not available for borrowing.");
        }
        if (borrowCount >= PREMIUM_LIMIT) {
            throw new BorrowLimitExceededException(
                    name + " has reached the maximum borrow limit of " + PREMIUM_LIMIT + " items.");
        }
        borrowedItems[borrowCount++] = item;
        item.setAvailable(false);
        System.out.println("[Premium] " + name + " borrowed: " + item.getTitle());
        return true;
    }

    /**
     * Returns a detailed description of this premium member.
     * Overrides Member.getInfo() to include the membership code and [Premium] label.
     */
    @Override
    public String getInfo() {
        return "PremiumMember[" + id + "] " + name
                + " | Code: " + membershipCode
                + " (borrowing: " + borrowCount + " item(s))";
    }

    @Override
    public String toString() { return getInfo(); }

    /** Returns the premium membership code. */
    public String getMembershipCode() { return membershipCode; }
}
