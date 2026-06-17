package LibraryBorrowingSystem.View;

import LibraryBorrowingSystem.Model.BorrowRecord;
import java.util.List;

public class BorrowView {

    public void showRecord(BorrowRecord record) {
        System.out.println(record.getInfo());
    }

    public void showAllRecords(List<BorrowRecord> records) {
        if (records.isEmpty()) {
            System.out.println("No borrow records.");
            return;
        }
        System.out.println("----- Borrow Records -----");
        for (BorrowRecord r : records) {
            System.out.println(r.getInfo());
        }
    }

    public void showBorrowSuccess(String memberName, String itemTitle) {
        System.out.println(memberName + " successfully borrowed: " + itemTitle);
    }

    public void showReturnSuccess(String memberName, String itemTitle) {
        System.out.println(memberName + " successfully returned: " + itemTitle);
    }

    public void showBorrowLimitReached() {
        System.out.println("Borrow limit reached. Cannot borrow more items.");
    }

    public void showItemNotAvailable(String title) {
        System.out.println("Item not available: " + title);
    }

    public void showNotFound(String message) {
        System.out.println("Not found: " + message);
    }

    public void showError(String message) {
        System.out.println("[ERROR] " + message);
    }

    public void showBorrowMenu() {
        System.out.println("\n--- Borrow / Return Menu ---");
        System.out.println("1. Borrow Item");
        System.out.println("2. Return Item");
        System.out.println("3. View All Records");
        System.out.println("0. Back");
        System.out.print("Choose: ");
    }
}