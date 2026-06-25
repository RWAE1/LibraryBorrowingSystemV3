package LibraryBorrowingSystem.Controller;

import LibraryBorrowingSystem.Model.LibraryService;
import LibraryBorrowingSystem.Model.Member;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

public class MemberFXController {

    @FXML private TableView<Member>           memberTable;
    @FXML private TableColumn<Member, String> colMemberId;
    @FXML private TableColumn<Member, String> colName;
    @FXML private TableColumn<Member, Number> colBorrowCount;

    private LibraryService service;

    public void setService(LibraryService service) {
        this.service = service;
        loadMembers();
    }

    @FXML
    public void initialize() {
        colMemberId  .setCellValueFactory(new PropertyValueFactory<>("memberId"));
        colName      .setCellValueFactory(new PropertyValueFactory<>("name"));
        colBorrowCount.setCellValueFactory(data ->
            new SimpleIntegerProperty(data.getValue().getBorrowCount()));
    }

    private void loadMembers() {
        memberTable.setItems(FXCollections.observableArrayList(service.getMembers()));
    }

    @FXML
    private void handleRegister() {
        Dialog<Member> dialog = new Dialog<>();
        dialog.setTitle("Register Member");

        ButtonType registerBtn = new ButtonType("Register", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(registerBtn, ButtonType.CANCEL);

        TextField tfId   = new TextField(); tfId.setPromptText("Member ID (e.g. M010)");
        TextField tfName = new TextField(); tfName.setPromptText("Full Name");

        VBox box = new VBox(6, new Label("Member ID:"), tfId, new Label("Name:"), tfName);
        box.setPadding(new Insets(12));
        dialog.getDialogPane().setContent(box);

        dialog.setResultConverter(btn -> {
            if (btn == registerBtn)
                return service.registerMember(tfId.getText().trim(), tfName.getText().trim());
            return null;
        });

        dialog.showAndWait().ifPresent(member -> {
            if (member == null) showAlert("Could not register member. Check for duplicate ID.");
            else loadMembers();
        });
    }

    @FXML
    private void handleRemove() {
        Member selected = memberTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Select a member from the table first.");
            return;
        }
        boolean ok = service.removeMember(selected.getMemberId());
        if (ok) loadMembers();
        else showAlert("Cannot remove \"" + selected.getName() + "\" — they still have borrowed items.");
    }

    private void showAlert(String message) {
        new Alert(Alert.AlertType.WARNING, message, ButtonType.OK).showAndWait();
    }
}
