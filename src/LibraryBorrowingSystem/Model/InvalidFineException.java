package LibraryBorrowingSystem.Model;

/** Thrown when a fine is issued for a record that was returned on time (daysLate <= 0). */
public class InvalidFineException extends RuntimeException {
    public InvalidFineException(String message) {
        super(message);
    }
}
