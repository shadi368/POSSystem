package application;

import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;

public class MainPage extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Create navigation menu bar from MainMenuBar class
        MenuBar menuBar = MainMenuBar.createMenuBar(primaryStage); // Reuse the menu bar

        // Create navigation buttons for the system
        Button inventoryButton = createAnimatedButton("Inventory");
        Button reportsButton = createAnimatedButton("Reports");
        Button productManagementButton = createAnimatedButton("Product Management");
        Button posButton = createAnimatedButton("POS");
        Button exitButton = createAnimatedButton("Exit");

        // Set actions for each button
        inventoryButton.setOnAction(e -> {
            new Dashboard().start(new Stage()); // Open Inventory Dashboard
            primaryStage.close();
        });

        reportsButton.setOnAction(e -> {
            new ReportsPage().start(new Stage()); // Open Reports page
            primaryStage.close();
        });

        productManagementButton.setOnAction(e -> {
            new ProductManagementPage().start(new Stage()); // Open Product Management Page
            primaryStage.close();
        });

        posButton.setOnAction(e -> {
            new POSPage().start(new Stage()); // Open POS page
            primaryStage.close();
        });

        exitButton.setOnAction(e -> {
            System.out.println("Exiting the application");
            primaryStage.close();
        });

        // Organize buttons in a VBox with alignment and spacing
        VBox buttonLayout = new VBox(15, inventoryButton, reportsButton, productManagementButton, posButton, exitButton);
        buttonLayout.setAlignment(Pos.CENTER); // Center the buttons
        buttonLayout.setStyle("-fx-padding: 20px; -fx-background-color: #f0f4f7;");

        // Wrap the buttons and menu bar in a BorderPane for better structure
        BorderPane rootLayout = new BorderPane();
        rootLayout.setTop(menuBar);
        rootLayout.setCenter(buttonLayout);

        // Align menu bar to the top center
        BorderPane.setAlignment(menuBar, Pos.TOP_CENTER);

        // Add a fade-in animation for the entire layout
        FadeTransition fadeInTransition = new FadeTransition(Duration.seconds(1), rootLayout);
        fadeInTransition.setFromValue(0);
        fadeInTransition.setToValue(1);
        fadeInTransition.play();

        // Set the scene for the MainPage
        Scene scene = new Scene(rootLayout, 400, 300);
        primaryStage.setTitle("System Navigation");
        primaryStage.setScene(scene);
        primaryStage.show();

    }

    // Helper method to create animated buttons
    private Button createAnimatedButton(String text) {
        Button button = new Button(text);
        button.setStyle("-fx-font-size: 14px; -fx-padding: 10; -fx-background-color: #4CAF50; -fx-text-fill: white; -fx-cursor: hand;");

        // Add hover effect and scale transition
        ScaleTransition scaleTransition = new ScaleTransition(Duration.seconds(0.3), button);
        scaleTransition.setFromX(1);
        scaleTransition.setFromY(1);
        scaleTransition.setToX(1.1);
        scaleTransition.setToY(1.1);

        // Add hover effect for scale transition
        button.setOnMouseEntered(e -> scaleTransition.play());
        button.setOnMouseExited(e -> {
            scaleTransition.setToX(1);  // Set to original scale on mouse exit
            scaleTransition.setToY(1);  // Set to original scale on mouse exit
        });

        // Add fade-in animation for each button
        FadeTransition fadeInTransition = new FadeTransition(Duration.seconds(1), button);
        fadeInTransition.setFromValue(0);
        fadeInTransition.setToValue(1);
        fadeInTransition.play();

        return button;
    }

    public static void main(String[] args) {
        // Launch the LoginPage first
        Platform.runLater(() -> {
            LoginPage loginPage = new LoginPage();
            loginPage.setLoginSuccessCallback(() -> {
                // Once login is successful, launch the MainPage
                new MainPage().start(new Stage());
            });
            loginPage.start(new Stage());
        });
    }
}
