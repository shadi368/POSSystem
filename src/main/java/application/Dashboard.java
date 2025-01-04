package application;

import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Dashboard extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Create MenuBar using the shared method from MainMenuBar (no instance needed)
        MenuBar menuBar = MainMenuBar.createMenuBar(primaryStage);  // Directly use static method

        // Create buttons for navigation
        Button viewProductsButton = createAnimatedButton("View Products");
        Button reportsButton = createAnimatedButton("Generate Reports");

        // Tooltips for buttons
        viewProductsButton.setTooltip(new Tooltip("View and manage existing products"));
        reportsButton.setTooltip(new Tooltip("Generate various reports"));

        // Set actions for buttons
        viewProductsButton.setOnAction(e -> new ProductManagementPage().start(primaryStage));
        reportsButton.setOnAction(e -> new ReportsPage().start(primaryStage));

        // Create the layout and set padding/spacing
        VBox layout = new VBox(15, menuBar, viewProductsButton, reportsButton);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center; -fx-spacing: 15;");

        // Apply fade-in animation to buttons
        applyFadeInAnimation(viewProductsButton);
        applyFadeInAnimation(reportsButton);

        // Add sliding transition effect for the MenuBar (Slide in from top)
        applySlideInAnimation(menuBar);

        // Create Scene and apply styles
        Scene scene = new Scene(layout, 400, 300);

        // Load and apply CSS from resources folder
        try {
            String cssPath = getClass().getClassLoader().getResource("application/dashboard.css").toExternalForm();
            scene.getStylesheets().add(cssPath);
        } catch (NullPointerException e) {
            System.err.println("CSS file not found. Please ensure 'dashboard.css' is in the 'application' package in the resources folder.");
        }

        primaryStage.setTitle("Dashboard");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Method to create animated buttons
    private Button createAnimatedButton(String text) {
        Button button = new Button(text);
        button.getStyleClass().add("button");
        button.setOnMouseEntered(e -> {
            button.setStyle("-fx-background-color: #3700b3; -fx-text-fill: white;");
            button.setEffect(new DropShadow(20, Color.BLACK));
        });
        button.setOnMouseExited(e -> {
            button.setStyle("-fx-background-color: #6200ea; -fx-text-fill: white;");
            button.setEffect(null);
        });
        return button;
    }

    // Method to apply fade-in animation to buttons
    private void applyFadeInAnimation(Button button) {
        FadeTransition fade = new FadeTransition(Duration.seconds(1), button);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.play();
    }

    // Method to apply slide-in animation for the MenuBar
    private void applySlideInAnimation(MenuBar menuBar) {
        menuBar.setTranslateY(-100);  // Initially position it above the screen
        FadeTransition slideInTransition = new FadeTransition(Duration.seconds(1), menuBar);
        slideInTransition.setFromValue(0);
        slideInTransition.setToValue(1);
        slideInTransition.setCycleCount(1);
        slideInTransition.setOnFinished(e -> {
            menuBar.setTranslateY(0);  // Final position of the MenuBar
        });
        slideInTransition.play();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
