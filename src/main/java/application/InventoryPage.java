package application;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.effect.DropShadow;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class InventoryPage extends Application {

    private Stage primaryStage;
    private TableView<Product> productTable;
    private TextField quantityField;
    private Button updateButton;

    // JDBC variables
    private Connection connection;
    private final String dbURL = "jdbc:mysql://localhost:3306/pos_system"; // Adjust as needed
    private final String dbUsername = "root"; // Adjust as needed
    private final String dbPassword = "root"; // Adjust as needed

    // Logger for debugging
    private static final Logger logger = Logger.getLogger(InventoryPage.class.getName());

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("Inventory Management");

        // Create the header navigation bar
        HBox header = createHeaderNavigation(primaryStage);
        header.setStyle("-fx-background-color: #3b3b3b; -fx-padding: 10; -fx-alignment: center;");

        // Create a simple inventory UI
        BorderPane inventoryLayout = new BorderPane();

        // Create a table to display products
        productTable = new TableView<>();
        setupProductTable();

        // Create fields and buttons for updating product quantity
        quantityField = new TextField();
        quantityField.setPromptText("Enter new quantity");
        updateButton = new Button("Update Quantity");
        updateButton.setOnAction(e -> handleUpdateQuantity());

        HBox updateBox = new HBox(10, quantityField, updateButton);
        updateBox.setAlignment(Pos.CENTER);

        VBox mainContent = new VBox(10, productTable, updateBox);
        mainContent.setAlignment(Pos.CENTER);

        inventoryLayout.setCenter(mainContent);

        // Use BorderPane to position the header at the top and main content in the center
        BorderPane root = new BorderPane();
        root.setTop(header); // Header at the top
        root.setCenter(inventoryLayout); // Main content in the center

        // Create Scene and apply styles
        Scene scene = new Scene(root, 800, 600);

        // Load and apply CSS from resources folder
        try {
            String cssPath = getClass().getResource("/views/styles.css").toExternalForm();
            scene.getStylesheets().add(cssPath);
        } catch (NullPointerException e) {
            System.err.println("CSS file not found. Please ensure 'styles.css' is in the 'views' folder.");
        }

        // Establish database connection
        try {
            connection = DriverManager.getConnection(dbURL, dbUsername, dbPassword);
            logger.info("Connected to the database successfully.");
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Database connection failed", e);
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to connect to the database.");
        }

        // Load products into the table
        loadProducts();

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Method to create the header navigation bar (consistent with POSPage and Dashboard)
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

    // Method to create header buttons (consistent with POSPage and Dashboard)
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

    // Method to set up the product table
    private void setupProductTable() {
        TableColumn<Product, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Product, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Product, Integer> quantityColumn = new TableColumn<>("Quantity");
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        TableColumn<Product, Double> priceColumn = new TableColumn<>("Price");
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("sellingPrice"));

        productTable.getColumns().addAll(idColumn, nameColumn, quantityColumn, priceColumn);
    }

    // Method to load products from the database
    private void loadProducts() {
        ObservableList<Product> products = FXCollections.observableArrayList();
        try {
            String sql = "SELECT * FROM products";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    Product product = new Product(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("description"),
                            rs.getString("category"),
                            rs.getDouble("selling_price"),
                            rs.getDouble("cost_price"),
                            rs.getInt("quantity"),
                            rs.getString("image"),
                            rs.getString("barcode")
                    );
                    products.add(product);
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error loading products from database", e);
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to load products.");
        }
        productTable.setItems(products);
    }

    // Method to handle updating product quantity
    private void handleUpdateQuantity() {
        Product selectedProduct = productTable.getSelectionModel().getSelectedItem();
        if (selectedProduct == null) {
            showAlert(Alert.AlertType.WARNING, "No Product Selected", "Please select a product to update.");
            return;
        }

        try {
            int newQuantity = Integer.parseInt(quantityField.getText().trim());
            if (newQuantity < 0) {
                showAlert(Alert.AlertType.WARNING, "Invalid Quantity", "Quantity cannot be negative.");
                return;
            }

            // Update the product quantity in the database
            String updateQuery = "UPDATE products SET quantity = ? WHERE id = ?";
            try (PreparedStatement stmt = connection.prepareStatement(updateQuery)) {
                stmt.setInt(1, newQuantity);
                stmt.setInt(2, selectedProduct.getId());
                stmt.executeUpdate();
            }

            // Update the product quantity in the table
            selectedProduct.setQuantity(newQuantity);
            productTable.refresh();

            showAlert(Alert.AlertType.INFORMATION, "Success", "Product quantity updated successfully.");
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.WARNING, "Invalid Input", "Please enter a valid number for quantity.");
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error updating product quantity in database", e);
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to update product quantity.");
        }
    }

    // Method to show an alert dialog
    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @Override
    public void stop() throws Exception {
        if (connection != null && !connection.isClosed()) {
            connection.close();
            logger.info("Database connection closed.");
        }
        super.stop();
    }
}