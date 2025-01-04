package application;

import javafx.application.Application;
import javafx.stage.Stage;

public class InventoryManagementApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Create an instance of ProductManagementPage
        new ProductManagementPage().start(primaryStage);  // Start ProductManagementPage
    }

    public static void main(String[] args) {
        launch(args);  // Launch the JavaFX application
    }
}
