package LibraryBorrowingSystem.Controller;

import LibraryBorrowingSystem.Model.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class BorrowFXController {

    @FXML private TextField                        memberIdField;
    @FXML private TextField                        itemIdField;
    @FXML private Label                            statusLabel;
    @FXML private TableView<BorrowRecord>          recordTable;
    @FXML private TableColumn<BorrowRecord, String> colRecordId;
    @FXML private TableColumn<BorrowRecord, String> colMember;
    @FXML private TableColumn<BorrowRecord, String> colItem;
    @FXML private TableColumn<BorrowRecord, String> colBorrowDate;
    @FXML private TableColumn<BorrowRecord, String> colReturnDate;
    @FXML private TableColumn<BorrowRecord, String> colReturnStatus;

    private LibraryService service;

    public void setService(LibraryService service) {
        this.service = service;
        loadRecords();
    }

    @FXML
    public void initialize() {
        colRecordId   .setCellValueFactory(new PropertyValueFactory<>("recordId"));
        colMember     .setCellValueFactory(d ->
            new SimpleStringProperty(d.getValue().getMember().getName()));
        colItem       .setCellValueFactory(d ->
            new SimpleStringProperty(d.getValue().getItem().getTitle()));
        colBorrowDate .setCellValueFactory(new PropertyValueFactory<>("borrowDate"));
        colReturnDate .setCellValueFactory(new PropertyValueFactory<>("returnDate"));
        colReturnStatus.setCellValueFactory(d ->
            new SimpleStringProperty(d.getValue().isReturned() ? "Returned" : "Active"));
    }

    @FXML
    private void handleBorrow() {
        String memberId = memberIdField.getText().trim();
        String itemId   = itemIdField.getText().trim();

        if (memberId.isEmpty() || itemId.isEmpty()) {
            setStatus("Member ID and Item ID are required.", true);
            return;
        }

        Member      member = service.findMemberById(memberId);
        LibraryItem item   = service.findItemById(itemId);

        if (member == null) { setStatus("Member not found: " + memberId, true); return; }
        if (item == null)   { setStatus("Item not found: " + itemId, true);     return; }
        if (!item.isAvailable()) { setStatus("\"" + item.getTitle() + "\" is not available.", true); return; }

        boolean ok = member.borrowItem(item);
        if (ok) {
            service.recordBorrow(member, item);
            setStatus("Borrowed: \"" + item.getTitle() + "\" → " + member.getName(), false);
            loadRecords();
            clearFields();
        } else {
            setStatus("Borrow limit reached for " + member.getName() + ".", true);
        }
    }

    @FXML
    private void handleReturn() {
        String memberId = memberIdField.getText().trim();
        String itemId   = itemIdField.getText().trim();

        if (memberId.isEmpty() || itemId.isEmpty()) {
            setStatus("Member ID and Item ID are required.", true);
            return;
        }

        Member      member = service.findMemberById(memberId);
        LibraryItem item   = service.findItemById(itemId);

        if (member == null) { setStatus("Member not found: " + memberId, true); return; }
        if (item == null)   { setStatus("Item not found: " + itemId, true);     return; }

        boolean ok = member.returnItem(item);
        if (ok) {
            service.recordReturn(member, item);
            setStatus("Returned: \"" + item.getTitle() + "\" from " + member.getName(), false);
            loadRecords();
            clearFields();
        } else {
            setStatus("Item not found in " + member.getName() + "'s borrowed list.", true);
        }
    }

    private void loadRecords() {
        recordTable.setItems(FXCollections.observableArrayList(service.getBorrowRecords()));
    }

    private void clearFields() {
        memberIdField.clear();
        itemIdField.clear();
    }

    private void setStatus(String message, boolean isError) {
        statusLabel.setText(message);
        statusLabel.setStyle(isError
            ? "-fx-text-fill: #cc0000; -fx-font-weight: bold;"
            : "-fx-text-fill: #007700; -fx-font-weight: bold;");
    }
}
