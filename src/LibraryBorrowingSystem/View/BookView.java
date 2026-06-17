package LibraryBorrowingSystem.View;

import LibraryBorrowingSystem.Model.Books;
import LibraryBorrowingSystem.Model.Multimedia;
import java.util.List;

public class BookView {

    public void showBook(Books book) {
        System.out.println(book.getInfo());
    }

    public void showAllBooks(List<Books> books) {
        if (books.isEmpty()) {
            System.out.println("No books in catalog.");
            return;
        }
        System.out.println("----- Book Catalog -----");
        for (int i = 0; i < books.size(); i++) {
            System.out.println((i + 1) + ". " + books.get(i).getInfo());
        }
    }

    public void showBookAdded(String title) {
        System.out.println("Book added: " + title);
    }

    public void showBookNotFound() {
        System.out.println("Book not found.");
    }

    public void showGenreResults(List<Books> results, String genre) {
        if (results.isEmpty()) {
            System.out.println("No books found for genre: " + genre);
            return;
        }
        System.out.println("Search results for genre: " + genre);
        for (Books b : results)
            System.out.println("  - " + b.getTitle() + " | Author: " + b.getAuthor()
                    + " | Available: " + b.isAvailable());
    }

    public void showTypeResults(List<Multimedia> results, String type) {
        if (results.isEmpty()) {
            System.out.println("No multimedia found for type: " + type);
            return;
        }
        System.out.println("Search results for type: " + type);
        for (Multimedia m : results)
            System.out.println("  - " + m.getTitle() + " | Type: " + m.getType()
                    + " | Available: " + m.isAvailable());
    }

    public void showError(String message) {
        System.out.println("[ERROR] " + message);
    }

    public void showBookMenu() {
        System.out.println("\n--- Book Menu ---");
        System.out.println("1. Add Book");
        System.out.println("2. View All Books");
        System.out.println("3. Search by Genre");
        System.out.println("0. Back");
        System.out.print("Choose: ");
    }
}