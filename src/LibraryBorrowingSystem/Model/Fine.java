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

    private String recordId;
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
    public Fine(String recordId, Member member, LibraryItem item, int daysLate) {
        this.recordId  = recordId;
        this.member    = member;
        this.item      = item;
        this.daysLate  = daysLate;
        this.fineAmount = calculateFine(daysLate);
    }

    /**
     * Computes the fine amount: daysLate * 2000.
     * Stores the result in fineAmount and returns it.
     *
     * @param daysLate number of days past the due date
     * @return total fine in Rupiah
     */
    public int calculateFine(int daysLate) {
        this.fineAmount = daysLate * 2000;
        return this.fineAmount;
    }

    /** Returns a formatted summary of this fine. */
    public String getInfo() {
        return "Fine for: " + member.getName()
                + " | Item: " + item.getTitle()
                + " | Days Late: " + daysLate
                + "\nFine: Rp" + fineAmount;
    }

    // ── Getters ───────────────────────────────────────────────────────────────

    public int    getFineAmount() { return fineAmount; }
    public Member getMember()     { return member; }
    public String getRecordId()   { return recordId; }
    public int    getDaysLate()   { return daysLate; }

    public String toString() { return getInfo(); }
}
