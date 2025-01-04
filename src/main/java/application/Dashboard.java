package application;

import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.effect.DropShadow;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Dashboard extends Application {
    private BorderPane root;

    public Dashboard() {
        // Initialize the UI for the Dashboard page
        root = new BorderPane();

        // Create a title label
        Label titleLabel = new Label("Welcome to the Dashboard!");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        // Add the title to the center of the BorderPane
        root.setCenter(titleLabel);
    }

    // Method to get the root node of the Dashboard UI
    public Parent getRoot() {
        return root;
    }

    @Override
    public void start(Stage primaryStage) {
        // Create a horizontal container for the header (navigation options)
        HBox header = createHeaderNavigation(primaryStage); // Use the same navigation as POSPage
        header.setStyle("-fx-background-color: #3b3b3b; -fx-padding: 10; -fx-alignment: center;");

        // Create buttons for the main content
        Button viewProductsButton = createAnimatedButton("View Products");
        Button generateReportsButton = createAnimatedButton("Generate Reports");

        // Tooltips for buttons
        viewProductsButton.setTooltip(new Tooltip("View and manage existing products"));
        generateReportsButton.setTooltip(new Tooltip("Generate various reports"));

        // Set actions for buttons
        viewProductsButton.setOnAction(e -> new ProductManagementPage().start(primaryStage));
        generateReportsButton.setOnAction(e -> new ReportsPage().start(primaryStage));

        // Create a VBox for the main content
        VBox mainContent = new VBox(15, viewProductsButton, generateReportsButton);
        mainContent.getStyleClass().add("vbox"); // Add a CSS class for styling
        mainContent.setStyle("-fx-alignment: center; -fx-padding: 20;");

        // Apply fade-in animation to buttons
        applyFadeInAnimation(viewProductsButton);
        applyFadeInAnimation(generateReportsButton);

        // Use BorderPane to position the header at the top and main content in the center
        BorderPane root = new BorderPane();
        root.setTop(header); // Header at the top
        root.setCenter(mainContent); // Main content in the center

        // Create Scene and apply styles
        Scene scene = new Scene(root, 800, 600);

        // Load and apply CSS from resources folder
        try {
            String cssPath = getClass().getResource("/views/dashboard.css").toExternalForm();
            scene.getStylesheets().add(cssPath);
        } catch (NullPointerException e) {
            System.err.println("CSS file not found. Please ensure 'dashboard.css' is in the 'application' package in the resources folder.");
        }

        primaryStage.setTitle("Dashboard");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Method to create the header navigation bar (consistent with POSPage)
    private HBox createHeaderNavigation(Stage primaryStage) {
        HBox header = new HBox(10); // 10px spacing between buttons
        header.getStyleClass().add("header"); // Apply CSS class for styling

        // Create buttons for navigation
        Button dashboardButton = createHeaderButton("Dashboard");
        Button productManagementButton = createHeaderButton("Product Management");
        Button posButton = createHeaderButton("POS");
        Button reportsButton = createHeaderButton("Reports");
        Button inventoryButton = createHeaderButton("Inventory");
        Button exitButton = createHeaderButton("Exit");

        // Set actions for each button
        dashboardButton.setOnAction(e -> new Dashboard().start(new Stage()));
        productManagementButton.setOnAction(e -> new ProductManagementPage().start(new Stage()));
        posButton.setOnAction(e -> new POSPage().start(new Stage()));
        reportsButton.setOnAction(e -> new ReportsPage().start(new Stage()));
        inventoryButton.setOnAction(e -> new InventoryPage().start(new Stage()));

        // Set action for the exit button
        exitButton.setOnAction(event -> {
            System.out.println("Exiting the application");
            primaryStage.close();
        });

        // Add all buttons to the header
        header.getChildren().addAll(
                dashboardButton,
                productManagementButton,
                posButton,
                reportsButton,
                inventoryButton,
                exitButton
        );

        return header;
    }

    // Method to create header buttons (consistent with POSPage)
    private Button createHeaderButton(String text) {
        Button button = new Button(text);
        button.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 14px; -fx-cursor: hand;");
        button.setOnMouseEntered(e -> {
            button.setStyle("-fx-background-color: #6200ea; -fx-text-fill: white;");
            button.setEffect(new DropShadow(10, Color.BLACK));
        });
        button.setOnMouseExited(e -> {
            button.setStyle("-fx-background-color: transparent; -fx-text-fill: white;");
            button.setEffect(null);
        });
        return button;
    }

    // Method to create animated buttons
    private Button createAnimatedButton(String text) {
        Button button = new Button(text);
        button.getStyleClass().add("button"); // Add a CSS class for styling
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

    public static void main(String[] args) {
        launch(args);
    }
}