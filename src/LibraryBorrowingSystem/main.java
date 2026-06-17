package LibraryBorrowingSystem;

import LibraryBorrowingSystem.Controller.LibraryController;
import LibraryBorrowingSystem.Database.DatabaseManager;

public class main {
    public static void main(String[] args) {
        Runtime.getRuntime().addShutdownHook(new Thread(DatabaseManager::close));
        try {
            LibraryController controller = new LibraryController();
            controller.start();
        } catch (RuntimeException e) {
            System.out.println("[FATAL] Application failed to start: " + e.getMessage());
        }
    }
}