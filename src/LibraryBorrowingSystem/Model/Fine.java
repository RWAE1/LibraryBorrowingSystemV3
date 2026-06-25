package LibraryBorrowingSystem.Model;

/**
 * Represents a late-return fine issued against a member.
 *
 * Feature 1 — Fine System
 * Fine is created by Librarian.issueFine() when a member returns
 * an item past its due date (7 days from the borrow date).
 * The fine amount is calculated as daysLate * 2000 (Rp2,000 per day).
 *
 * Associations:
 *   - member : the Member who owes the fine
 *   - item   : the LibraryItem that was returned late
 */
public class Fine {

    private String fineId;
    private Member member;
    private LibraryItem item;
    private int daysLate;
    private int fineAmount;

    /**
     * Creates a Fine for a late return.
     * calculateFine() is called automatically to set fineAmount.
     *
     * @param recordId  unique fine record ID (e.g. "FINE001")
     * @param member    the member who returned late
     * @param item      the item that was returned late
     * @param daysLate  number of days past the due date
     */
    public Fine(String fineId, Member member, LibraryItem item, int daysLate) {
        this.fineId     = fineId;
        this.member     = member;
        this.item       = item;
        this.daysLate   = daysLate;
        this.fineAmount = daysLate * 2000;
    }

    /**
     * Calculates the fine amount for a given number of days late.
     * Pure function — does not modify any state.
     *
     * @param daysLate number of days past the due date
     * @return fine amount in Rupiah
     */
    public static int calculateFine(int daysLate) {
        return daysLate * 2000;
    }

    public String getInfo() {
        return "Fine for: " + member.getName()
                + " | Item: "       + item.getTitle()
                + " | Days Late: "  + daysLate
                + "\nFine: Rp"      + fineAmount;
    }

    public String      getFineId()     { return fineId; }
    public int         getFineAmount() { return fineAmount; }
    public Member      getMember()     { return member; }
    public LibraryItem getItem()       { return item; }
    public int         getDaysLate()   { return daysLate; }

    /** @deprecated Use {@link #getFineId()} instead. */
    @Deprecated
    public String getRecordId() { return fineId; }

    @Override
    public String toString() { return getInfo(); }
}