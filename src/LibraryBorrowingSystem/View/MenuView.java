package LibraryBorrowingSystem.View;

public class MenuView {

    public void showMainMenu() {
        System.out.println("\n===== Library System =====");
        System.out.println("1. Manage Books");
        System.out.println("2. Manage Members");
        System.out.println("3. Borrow / Return Item");
        System.out.println("4. Reservations");
        System.out.println("5. Fines");
        System.out.println("0. Exit");
        System.out.print("Choose an option: ");
    }

    public void showMessage(String message) {
        System.out.println("[INFO] " + message);
    }

    public void showError(String error) {
        System.out.println("[ERROR] " + error);
    }

    public void showSeparator() {
        System.out.println("---------------------------");
    }
}