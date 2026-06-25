package LibraryBorrowingSystem.Controller;

import LibraryBorrowingSystem.Model.Books;
import LibraryBorrowingSystem.Model.LibraryService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

public class BookFXController {

    @FXML private TextField              genreSearchField;
    @FXML private TableView<Books>        bookTable;
    @FXML private TableColumn<Books, String> colBookId;
    @FXML private TableColumn<Books, String> colTitle;
    @FXML private TableColumn<Books, String> colAuthor;
    @FXML private TableColumn<Books, String> colGenre;
    @FXML private TableColumn<Books, String> colStatus;

    private LibraryService service;

    // Called by App.java after FXML is loaded to inject the shared service
    public void setService(LibraryService service) {
        this.service = service;
        loadBooks();
    }

    @FXML
    public void initialize() {
        colBookId.setCellValueFactory(new PropertyValueFactory<>("bookId"));
        colTitle .setCellValueFactory(new PropertyValueFactory<>("title"));
        colAuthor.setCellValueFactory(new PropertyValueFactory<>("author"));
        colGenre .setCellValueFactory(new PropertyValueFactory<>("genre"));
        colStatus.setCellValueFactory(data ->
            new SimpleStringProperty(data.getValue().isAvailable() ? "Available" : "Borrowed"));
    }

    private void loadBooks() {
        bookTable.setItems(FXCollections.observableArrayList(service.getCatalog()));
    }

    @FXML
    private void handleSearch() {
        String genre = genreSearchField.getText().trim();
        if (genre.isEmpty()) {
            loadBooks();
        } else {
            bookTable.setItems(FXCollections.observableArrayList(service.searchByGenre(genre)));
        }
    }

    @FXML
    private void handleAddBook() {
        Dialog<Books> dialog = new Dialog<>();
        dialog.setTitle("Add Book");

        ButtonType addBtn = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addBtn, ButtonType.CANCEL);

        TextField tfId     = new TextField(); tfId.setPromptText("Book ID (e.g. B010)");
        TextField tfTitle  = new TextField(); tfTitle.setPromptText("Title");
        TextField tfAuthor = new TextField(); tfAuthor.setPromptText("Author");
        TextField tfGenre  = new TextField(); tfGenre.setPromptText("Genre");

        VBox box = new VBox(6,
            new Label("Book ID:"), tfId,
            new Label("Title:"),   tfTitle,
            new Label("Author:"),  tfAuthor,
            new Label("Genre:"),   tfGenre);
        box.setPadding(new Insets(12));
        dialog.getDialogPane().setContent(box);

        dialog.setResultConverter(btn -> {
            if (btn == addBtn) {
                String genre = tfGenre.getText().trim();
                return service.addBook(
                    tfId.getText().trim(),
                    tfTitle.getText().trim(),
                    tfAuthor.getText().trim(),
                    genre.isEmpty() ? "General" : genre);
            }
            return null;
        });

        dialog.showAndWait().ifPresent(book -> {
            if (book == null) showAlert("Could not add book. Check for duplicate ID.");
            else loadBooks();
        });
    }

    @FXML
    private void handleDeleteBook() {
        Books selected = bookTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Select a book from the table first.");
            return;
        }
        boolean ok = service.removeBook(selected.getBookId());
        if (ok) loadBooks();
        else showAlert("Cannot delete \"" + selected.getTitle() + "\" — it is currently borrowed.");
    }

    private void showAlert(String message) {
        new Alert(Alert.AlertType.WARNING, message, ButtonType.OK).showAndWait();
    }
}
