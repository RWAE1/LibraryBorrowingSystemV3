package LibraryBorrowingSystem.View;

import LibraryBorrowingSystem.Model.Fine;
import java.util.List;

public class FineView {

    public void showFine(Fine fine) {
        System.out.println(fine.getInfo());
    }

    public void showAllFines(List<Fine> fines) {
        if (fines.isEmpty()) {
            System.out.println("No fines recorded.");
            return;
        }
        System.out.println("----- Fines -----");
        for (Fine f : fines) {
            System.out.println(f.getInfo());
        }
    }

    public void showFineIssued(String memberName, int amount) {
        System.out.println("Fine issued for " + memberName + ": Rp" + amount);
    }
}