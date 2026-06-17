package LibraryBorrowingSystem.Controller;

import LibraryBorrowingSystem.Model.Books;
import LibraryBorrowingSystem.Model.Librarian;
import LibraryBorrowingSystem.View.BookView;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class BookController {

    private Librarian librarian;
    private BookView bookView;
    private Scanner scanner;

    public BookController(Librarian librarian, Scanner scanner) {
        this.librarian = librarian;
        this.bookView  = new BookView();
        this.scanner   = scanner;
    }
    
    public void handleMenu() {
        int choice = -1;
        while (choice != 0) {
            bookView.showBookMenu();
            try {
                choice = Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                bookView.showError("Please enter a valid number.");
                continue;
            }
            switch (choice) {
                case 1  -> addBook();
                case 2  -> viewAllBooks();
                case 3  -> searchByGenre();
                case 0  -> { return; }
                default -> bookView.showError("Invalid option. Please try again.");
            }
        }
    }

    private void addBook() {
        System.out.print("Book ID : "); String id     = scanner.nextLine().trim();
        System.out.print("Title   : "); String title  = scanner.nextLine().trim();
        System.out.print("Author  : "); String author = scanner.nextLine().trim();
        System.out.print("Genre   : "); String genre  = scanner.nextLine().trim();

        if (id.isEmpty() || title.isEmpty() || author.isEmpty()) {
            bookView.showError("Book ID, Title, and Author cannot be empty.");
            return;
        }
        try {
            Books book = librarian.addBook(id, title, author, genre.isEmpty() ? "General" : genre);
            if (book != null) bookView.showBookAdded(title);
            else bookView.showError("Failed to add book (duplicate ID or catalog full).");
        } catch (RuntimeException e) {
            bookView.showError("Could not save book: " + e.getMessage());
        }
    }
    private void viewAllBooks() {
        // convert Librarian's catalog array to a List for the View
        List<Books> books = new ArrayList<>();
        for (int i = 0; i < librarian.getCatalogSize(); i++) {
            books.add(librarian.getCatalog()[i]);
        }
        bookView.showAllBooks(books);
    }

    private void searchByGenre() {
        System.out.print("Enter genre: ");
        String genre = scanner.nextLine();
        List<Books> results = librarian.searchByGenre(                        // Model
                librarian.getCatalog(), librarian.getCatalogSize(), genre);
        bookView.showGenreResults(results, genre);                            // View
    }
}