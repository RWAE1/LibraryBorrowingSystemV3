package LibraryBorrowingSystem;

import LibraryBorrowingSystem.Controller.*;
import LibraryBorrowingSystem.Database.DatabaseManager;
import LibraryBorrowingSystem.Model.LibraryService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;

public class App extends Application {

    private final LibraryService service = new LibraryService();

    @Override
    public void start(Stage stage) throws Exception {
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        tabPane.getTabs().add(loadTab("Books",         "/LibraryBorrowingSystem/view/BookTab.fxml",   BookFXController.class));
        tabPane.getTabs().add(loadTab("Members",       "/LibraryBorrowingSystem/view/MemberTab.fxml", MemberFXController.class));
        tabPane.getTabs().add(loadTab("Borrow/Return", "/LibraryBorrowingSystem/view/BorrowTab.fxml", BorrowFXController.class));
        tabPane.getTabs().add(loadTab("Fines",         "/LibraryBorrowingSystem/view/FineTab.fxml",   FineFXController.class));

        stage.setScene(new Scene(tabPane, 1000, 650));
        stage.setTitle("Library Borrowing System");
        stage.show();
    }

    private <T> Tab loadTab(String label, String fxmlPath, Class<T> controllerType) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        Tab tab = new Tab(label, loader.load());

        // Inject the shared LibraryService into the controller
        Object ctrl = loader.getController();
        if (ctrl instanceof BookFXController   c) c.setService(service);
        if (ctrl instanceof MemberFXController c) c.setService(service);
        if (ctrl instanceof BorrowFXController c) c.setService(service);
        if (ctrl instanceof FineFXController   c) c.setService(service);

        return tab;
    }

    @Override
    public void stop() {
        DatabaseManager.close();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
