package LibraryBorrowingSystem.Model;

/** Thrown when a member tries to borrow an item that is currently unavailable. */
public class ItemNotAvailableException extends RuntimeException {
    public ItemNotAvailableException(String message) {
        super(message);
    }
}
