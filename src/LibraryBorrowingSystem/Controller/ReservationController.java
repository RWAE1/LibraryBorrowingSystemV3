package LibraryBorrowingSystem.Controller;

import java.util.Scanner;
import LibraryBorrowingSystem.Model.Member;
import LibraryBorrowingSystem.Model.LibraryItem;
import LibraryBorrowingSystem.Model.Reservation;
import LibraryBorrowingSystem.Model.Librarian; 

public class ReservationController {
    
    private Librarian librarian;
    private Scanner scanner;

    public ReservationController(Librarian librarian, Scanner scanner) {
        this.librarian = librarian;
        this.scanner = scanner;
    }

    public void handleMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("\n--------------------------------------------");
            System.out.println("          RESERVATION MANAGEMENT");
            System.out.println("--------------------------------------------");
            System.out.println("  1. Make a Reservation");
            System.out.println("  2. Check Reservation Queue for an Item");
            System.out.println("  0. Back to Main Menu");
            System.out.println("--------------------------------------------");
            System.out.print("Choose an option: ");

            String input = scanner.nextLine().trim();
            int choice = -1;
            try {
                choice = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Error: Please enter a valid number.");
                continue;
            }

            switch (choice) {
                case 1 -> processMakeReservation();
                case 2 -> processCheckQueue();
                case 0 -> back = true;
                default -> System.out.println("Error: Invalid option. Try again.");
            }
        }
    }

    private void processMakeReservation() {
        System.out.print("Enter Member ID: ");
        String memberId = scanner.nextLine().trim();
        System.out.print("Enter Item ID to reserve: ");
        String itemId = scanner.nextLine().trim();

        Member foundMember = librarian.findMemberById(memberId); 
        LibraryItem foundItem = librarian.findItemById(itemId);  

        if (foundMember == null) {
            System.out.println("Error: Member not found.");
            return;
        }
        if (foundItem == null) {
            System.out.println("Error: Item not found.");
            return;
        }

        Reservation newRes = librarian.addReservation(foundMember, foundItem);
        
        if (newRes != null) {
            System.out.println("Success! " + foundMember.getName() + " reserved " + foundItem.getTitle() + " on " + newRes.getReservationDate());
        } else {
            System.out.println("Error: Max reservations limit reached or invalid data!");
        }
    }

    private void processCheckQueue() {
        System.out.print("Enter Item ID to check queue: ");
        String itemId = scanner.nextLine().trim();

        LibraryItem foundItem = librarian.findItemById(itemId); 

        if (foundItem != null) {
            System.out.println("\n--- Reservation Queue for " + foundItem.getTitle() + " ---");
            boolean found = false;
            
            // Fetch the array and count from your Librarian model
            Reservation[] allRes = librarian.getReservations();
            int resCount = librarian.getReservationCount();
            
            for (int i = 0; i < resCount; i++) {
                Reservation res = allRes[i];
                if (res != null && res.getItem().getItemId().equals(foundItem.getItemId()) && res.isActive()) {
                    System.out.println(res.getInfo());
                    found = true;
                }
            }
            
            if (!found) {
                System.out.println("Nobody is actively reserving this item right now.");
            }
        } else {
            System.out.println("Error: Item not found.");
        }
    }
}