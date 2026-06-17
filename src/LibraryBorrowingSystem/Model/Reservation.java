package LibraryBorrowingSystem.Model;

/**
 * Represents a member's reservation for an item that is currently unavailable.
 *
 * Feature 3 — Reservation System
 * A Reservation is created by Librarian.addReservation() when a member
 * wants an item that is already borrowed. The reservation starts as active
 * and can be cancelled via cancelReservation().
 *
 * Associations:
 *   - member : the Member who placed the reservation
 *   - item   : the LibraryItem being reserved
 */
public class Reservation {

    private String reservationId;
    private Member member;
    private LibraryItem item;
    private String reservationDate;
    private boolean active;

    /**
     * Creates a new active reservation.
     *
     * @param reservationId  unique reservation ID (e.g. "RES001")
     * @param member         the member reserving the item
     * @param item           the item being reserved
     * @param reservationDate simplified date string (e.g. "Day-1")
     */
    public Reservation(String reservationId, Member member, LibraryItem item, String reservationDate) {
        this.reservationId   = reservationId;
        this.member          = member;
        this.item            = item;
        this.reservationDate = reservationDate;
        this.active          = true;
    }

    /** Cancels this reservation — sets active to false. */
    public void cancelReservation() {
        this.active = false;
    }

    /** Returns a formatted summary of this reservation. */
    public String getInfo() {
        return "[Reservation] ID: " + reservationId
                + " | Member: " + member.getName()
                + " | Item: "   + item.getTitle()
                + " | Date: "   + reservationDate
                + " | Status: " + (active ? "Active" : "Cancelled");
    }

    // ── Getters ───────────────────────────────────────────────────────────────

    public String      getReservationId()   { return reservationId; }
    public Member      getMember()          { return member; }
    public LibraryItem getItem()            { return item; }
    public String      getReservationDate() { return reservationDate; }
    public boolean     isActive()           { return active; }

    public String toString() { return getInfo(); }
}
