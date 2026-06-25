/**
 * @author      masjohncook X RWAE1
 * @version     0.0.3
 * @copyright   (C) Copyright 2026
 * @license     None
 * @maintainer  masjohncook
 * @email       mas.john.cook@gmail.com
 * @status      Development
 */
package LibraryBorrowingSystem;

import LibraryBorrowingSystem.Controller.BookFXController;
import LibraryBorrowingSystem.Controller.MemberFXController;
import LibraryBorrowingSystem.Database.DatabaseManager;
import LibraryBorrowingSystem.Model.LibraryService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;

/**
 * JavaFX entry point for the Library Borrowing System GUI.
 *
 * App extends javafx.application.Application, which is the standard
 * JavaFX lifecycle class. JavaFX calls start() on the JavaFX Application
 * Thread after the runtime is initialised.
 *
 * Architecture:
 *   - One LibraryService is created here and shared with every tab controller
 *     via each controller's setService() method. This means all tabs read and
 *     write the same in-memory data and the same database connection.
 *   - Each tab is a separate FXML file loaded by FXMLLoader. The loader
 *     automatically instantiates the controller class declared in the FXML's
 *     fx:controller attribute, then initialize() is called, then setService().
 *   - Tabs are added one by one as each FXController is built. Add new tabs
 *     by creating a loadXxxTab() method and calling it inside start().
 *
 * Run with: mvn javafx:run
 *
 * Tabs implemented so far:
 *   - Books   (BookFXController   + BookTab.fxml)
 *   - Members (MemberFXController + MemberTab.fxml)
 */
public class App extends Application {

    /** Shared service — created once, injected into every tab controller. */
    private final LibraryService service = new LibraryService();

    /**
     * Builds the main window: a TabPane containing one tab per feature area.
     * Called automatically by JavaFX after the runtime is ready.
     *
     * @param stage the primary window provided by JavaFX
     */
    @Override
    public void start(Stage stage) throws Exception {
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        tabPane.getTabs().add(loadBookTab());
        tabPane.getTabs().add(loadMemberTab());
        // Add BorrowTab, FineTab here as each controller is built

        stage.setScene(new Scene(tabPane, 1000, 650));
        stage.setTitle("Library Borrowing System");
        stage.show();
    }

    /**
     * Loads BookTab.fxml, retrieves the BookFXController, injects the service,
     * and wraps the result in a Tab labelled "Books".
     *
     * @return the fully wired Books tab
     */
    private Tab loadBookTab() throws Exception {
        FXMLLoader loader = new FXMLLoader(
            getClass().getResource("/LibraryBorrowingSystem/view/BookTab.fxml"));
        Tab tab = new Tab("Books", loader.load());
        loader.<BookFXController>getController().setService(service);
        return tab;
    }

    /**
     * Loads MemberTab.fxml, retrieves the MemberFXController, injects the service,
     * and wraps the result in a Tab labelled "Members".
     *
     * @return the fully wired Members tab
     */
    private Tab loadMemberTab() throws Exception {
        FXMLLoader loader = new FXMLLoader(
            getClass().getResource("/LibraryBorrowingSystem/view/MemberTab.fxml"));
        Tab tab = new Tab("Members", loader.load());
        loader.<MemberFXController>getController().setService(service);
        return tab;
    }

    /**
     * Called by JavaFX when the window is closed.
     * Closes the SQLite database connection cleanly.
     */
    @Override
    public void stop() {
        DatabaseManager.close();
    }

    public static void main(String[] args) {
        launch(args);
    }
}