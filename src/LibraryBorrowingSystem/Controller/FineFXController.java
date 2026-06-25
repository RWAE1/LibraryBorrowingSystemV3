package LibraryBorrowingSystem.Controller;

import LibraryBorrowingSystem.Model.Fine;
import LibraryBorrowingSystem.Model.LibraryService;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class FineFXController {

    @FXML private TableView<Fine>           fineTable;
    @FXML private TableColumn<Fine, String> colFineId;
    @FXML private TableColumn<Fine, String> colFineMember;
    @FXML private TableColumn<Fine, String> colFineItem;
    @FXML private TableColumn<Fine, Number> colDaysLate;
    @FXML private TableColumn<Fine, Number> colAmount;

    private LibraryService service;

    public void setService(LibraryService service) {
        this.service = service;
        loadFines();
    }

    @FXML
    public void initialize() {
        colFineId    .setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getFineId()));
        colFineMember.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getMember().getName()));
        colFineItem  .setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getItem().getTitle()));
        colDaysLate  .setCellValueFactory(d -> new SimpleIntegerProperty(d.getValue().getDaysLate()));
        colAmount    .setCellValueFactory(d -> new SimpleIntegerProperty(d.getValue().getFineAmount()));
    }

    private void loadFines() {
        fineTable.setItems(FXCollections.observableArrayList(service.getFines()));
    }
}
