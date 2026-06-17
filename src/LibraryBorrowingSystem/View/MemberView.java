package LibraryBorrowingSystem.View;

import LibraryBorrowingSystem.Model.Member;
import java.util.List;

public class MemberView {

    public void showMember(Member member) {
        System.out.println(member.getInfo());
    }

    public void showAllMembers(List<Member> members) {
        if (members.isEmpty()) {
            System.out.println("No members registered.");
            return;
        }
        System.out.println("----- Members -----");
        for (int i = 0; i < members.size(); i++) {
            System.out.println((i + 1) + ". " + members.get(i).getInfo());
        }
    }

    public void showMemberRegistered(String name) {
        System.out.println("Member registered: " + name);
    }

    public void showMemberNotFound() {
        System.out.println("Member not found.");
    }

    public void showError(String message) {
        System.out.println("[ERROR] " + message);
    }

    public void showMemberMenu() {
        System.out.println("\n--- Member Menu ---");
        System.out.println("1. Register Member");
        System.out.println("2. View All Members");
        System.out.println("0. Back");
        System.out.print("Choose: ");
    }
}