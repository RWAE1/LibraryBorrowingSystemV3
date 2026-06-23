package LibraryBorrowingSystem.Controller;

import LibraryBorrowingSystem.Model.Librarian;
import LibraryBorrowingSystem.View.MenuView;
import java.util.Scanner;

public class LibraryController {

    private Librarian librarian;
    private MenuView menuView;
    private BookController bookController;
    private MemberController memberController;
    private BorrowController borrowController;
    private ReservationController reservationController;
    private Scanner scanner;

    public LibraryController() {
        this.librarian       = new Librarian("L001", "Admin Librarian");
        this.menuView         = new MenuView();
        this.scanner          = new Scanner(System.in);
        this.bookController   = new BookController(librarian, scanner);
        this.memberController = new MemberController(librarian, scanner);
        this.borrowController = new BorrowController(librarian, scanner);
        this.reservationController = new ReservationController(librarian, scanner);
    }

    public void start() {
        int choice = -1;
        while (choice != 0) {
            menuView.showMainMenu();
            try {
                choice = Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                menuView.showError("Please enter a number.");
                continue;
            }
            switch (choice) {
                case 1  -> bookController.handleMenu();
                case 2  -> memberController.handleMenu();
                case 3  -> borrowController.handleMenu();
                case 4  -> reservationController.handleMenu();
                case 5  -> menuView.showMessage("Fines — coming in Meeting 2.");
                case 0  -> menuView.showMessage("Goodbye!");
                default -> menuView.showError("Invalid option. Try again.");
            }
        }
    }
}