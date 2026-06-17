package LibraryBorrowingSystem.Controller;

import LibraryBorrowingSystem.Model.Member;
import LibraryBorrowingSystem.Model.Librarian;
import LibraryBorrowingSystem.View.MemberView;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MemberController {

    private Librarian librarian;
    private MemberView memberView;
    private Scanner scanner;

    public MemberController(Librarian librarian, Scanner scanner) {
        this.librarian  = librarian;
        this.memberView = new MemberView();
        this.scanner    = scanner;
    }

    public void handleMenu() {
        int choice = -1;
        while (choice != 0) {
            memberView.showMemberMenu();
            try {
                choice = Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                memberView.showError("Please enter a valid number.");
                continue;
            }
            switch (choice) {
                case 1  -> registerMember();
                case 2  -> viewAllMembers();
                case 0  -> { return; }
                default -> memberView.showError("Invalid option. Please try again.");
            }
        }
    }

    private void registerMember() {
        System.out.print("Member ID : "); String id   = scanner.nextLine().trim();
        System.out.print("Name      : "); String name = scanner.nextLine().trim();

        if (id.isEmpty() || name.isEmpty()) {
            memberView.showError("Member ID and Name cannot be empty.");
            return;
        }
        try {
            Member member = librarian.registerMember(id, name);
            if (member != null) memberView.showMemberRegistered(name);
            else memberView.showError("Could not register member (duplicate ID or member list full).");
        } catch (RuntimeException e) {
            memberView.showError("Could not save member: " + e.getMessage());
        }
    }

    private void viewAllMembers() {
        List<Member> members = new ArrayList<>();
        for (int i = 0; i < librarian.getMemberCount(); i++) {
            members.add(librarian.getMembers()[i]);
        }
        memberView.showAllMembers(members);                      // View
    }
}