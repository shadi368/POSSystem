package application;

import javafx.scene.paint.Color;
import javafx.animation.ScaleTransition;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class POSPage extends Application {

    // UI Elements
    private Label profitLabel, cashInDeskLabel;
    private TilePane productPane;
    private TextField searchField, sellQuantityField;
    private Button searchButton, sellButton, newDayButton, finishDayButton;
    private ToggleButton darkModeToggle; // Declare darkModeToggle as a class-level field
    private ObservableList<Map<String, Object>> data = FXCollections.observableArrayList();

    private double dailyProfit = 0;
    private double cashInDesk = 0;
    private boolean dayStarted = false;
    private Product selectedProduct;
    private StackPane mainContent;

    // JDBC variables
    private Connection connection;
    private final String dbURL = "jdbc:mysql://localhost:3306/pos_system"; // Adjust as needed
    private final String dbUsername = "root"; // Adjust as needed
    private final String dbPassword = "root"; // Adjust as needed

    // Logger for debugging
    private static final Logger logger = Logger.getLogger(POSPage.class.getName());

    @Override
    public void start(Stage primaryStage) {
        // Root layout
        VBox rootLayout = new VBox();

        // Create the header navigation bar
        HBox header = createHeaderNavigation(primaryStage);
        rootLayout.getChildren().add(header);

        // Main content area
        mainContent = new StackPane();
        rootLayout.getChildren().add(mainContent);

        // Create a scene with the root layout and set dimensions
        Scene scene = new Scene(rootLayout, 1200, 800);

        // Apply CSS
        URL cssResource = getClass().getResource("/views/styles.css");
        if (cssResource != null) {
            scene.getStylesheets().add(cssResource.toExternalForm());
        } else {
            logger.warning("CSS file not found.");
        }

        // Set the scene to the primary stage
        primaryStage.setScene(scene);

        // Set the title of the stage
        primaryStage.setTitle("POS Page");

        // Show the stage
        primaryStage.show();

        // Establish database connection
        try {
            connection = DriverManager.getConnection(dbURL, dbUsername, dbPassword);
            logger.info("Connected to the database successfully.");
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Database connection failed", e);
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to connect to the database.");
        }

        // Show the POS page UI
        showPOSPage();
    }

    // Method to create the header navigation bar
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

    // Method to create styled header buttons
    private Button createHeaderButton(String text) {
        Button button = new Button(text);
        button.getStyleClass().add("header-button"); // Apply CSS class for styling
        return button;
    }

    // Method to show the POS page
    private void showPOSPage() {
        VBox posPage = new VBox(10);
        posPage.setAlignment(Pos.TOP_CENTER);

        // Profit and cash labels
        profitLabel = new Label("Profit: $0.00");
        profitLabel.getStyleClass().add("profit-label");
        cashInDeskLabel = new Label("Cash in Desk: $0.00");
        cashInDeskLabel.getStyleClass().add("cash-in-desk-label");
        posPage.getChildren().addAll(profitLabel, cashInDeskLabel);

        // Search field and button
        searchField = new TextField();
        searchField.setPromptText("Search by barcode or name...");
        searchButton = new Button("Search");
        searchButton.getStyleClass().add("button");
        HBox searchBox = new HBox(10, searchField, searchButton);
        searchBox.setAlignment(Pos.CENTER);
        posPage.getChildren().add(searchBox);

        // Product tile pane
        productPane = new TilePane();
        productPane.setHgap(20);
        productPane.setVgap(20);
        productPane.setPrefColumns(4);
        productPane.getStyleClass().add("product-tile-pane");
        posPage.getChildren().add(productPane);

        // Sell quantity and button
        sellQuantityField = new TextField();
        sellQuantityField.setPromptText("Quantity to Sell");
        sellButton = new Button("Sell");
        sellButton.getStyleClass().add("button");
        HBox sellBox = new HBox(10, sellQuantityField, sellButton);
        sellBox.setAlignment(Pos.CENTER);
        posPage.getChildren().add(sellBox);

        // Buttons for new day and finish day
        newDayButton = new Button("Start New Day");
        newDayButton.getStyleClass().add("button");
        finishDayButton = new Button("Finish Day");
        finishDayButton.getStyleClass().add("button");
        HBox dayButtons = new HBox(10, newDayButton, finishDayButton);
        dayButtons.setAlignment(Pos.CENTER);
        posPage.getChildren().add(dayButtons);

        // Initially hide components
        hidePOSUIComponents();

        // Set up actions
        newDayButton.setOnAction(e -> handleNewDayAction());
        finishDayButton.setOnAction(e -> handleFinishDayAction());
        sellButton.setOnAction(e -> handleSellAction());
        searchButton.setOnAction(e -> handleSearchAction());

        mainContent.getChildren().setAll(posPage);
    }

    private void handleSearchAction() {
        String searchQuery = searchField.getText().trim();
        loadProductsFromDatabase(searchQuery);
        updateProductPane();
    }

    private void handleSellAction() {
        try {
            if (selectedProduct == null) {
                showAlert(Alert.AlertType.WARNING, "No Product Selected", "Please select a product to sell.");
                return;
            }

            int quantityToSell = Integer.parseInt(sellQuantityField.getText().trim());
            if (quantityToSell <= 0 || quantityToSell > selectedProduct.getQuantity()) {
                showAlert(Alert.AlertType.WARNING, "Invalid Quantity", "Please enter a valid quantity.");
                return;
            }

            double profit = (selectedProduct.getSellingPrice() - selectedProduct.getCostPrice()) * quantityToSell;
            dailyProfit += profit;
            cashInDesk += selectedProduct.getSellingPrice() * quantityToSell;
            selectedProduct.setQuantity(selectedProduct.getQuantity() - quantityToSell);

            profitLabel.setText(String.format("Profit: $%.2f", dailyProfit));
            cashInDeskLabel.setText(String.format("Cash in Desk: $%.2f", cashInDesk));

            updateProductInDatabase(selectedProduct);
            saveInvoiceToFile(selectedProduct, quantityToSell); // Save invoice to file
            showAlert(Alert.AlertType.INFORMATION, "Sale Completed", "Product sold successfully.");
            sellQuantityField.clear();
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.WARNING, "Invalid Input", "Enter a valid number for quantity.");
        }
    }

    private void updateProductInDatabase(Product selectedProduct) {
        String updateQuery = "UPDATE products SET quantity = ?, selling_price = ? WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(updateQuery)) {
            stmt.setInt(1, selectedProduct.getQuantity());
            stmt.setDouble(2, selectedProduct.getSellingPrice());
            stmt.setInt(3, selectedProduct.getId());

            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated > 0) {
                logger.info("Product with ID " + selectedProduct.getId() + " was updated successfully.");
            } else {
                logger.warning("Product with ID " + selectedProduct.getId() + " could not be updated.");
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error updating product in database", e);
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to update product.");
        }
    }

    private void handleNewDayAction() {
        logger.info("New day started.");
        dayStarted = true;
        finishDayButton.setVisible(true);
        newDayButton.setDisable(true);
        resetDayCounters();
        showPOSUIComponents();
        loadProductsFromDatabase("");
        updateProductPane();
    }

    private void handleFinishDayAction() {
        logger.info("Day finished.");
        showDaySummary();
        resetDayCounters();
        hidePOSUIComponents();
        finishDayButton.setVisible(false);
        newDayButton.setDisable(false);
        dayStarted = false;
    }

    private void resetDayCounters() {
        dailyProfit = 0;
        cashInDesk = 0;
        profitLabel.setText("Profit: $0.00");
        cashInDeskLabel.setText("Cash in Desk: $0.00");
    }

    private void loadProductsFromDatabase(String searchQuery) {
        data.clear();
        try {
            String sql = "SELECT * FROM products";
            if (!searchQuery.isEmpty()) {
                sql += " WHERE barcode LIKE ? OR name LIKE ?";
            }

            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                if (!searchQuery.isEmpty()) {
                    stmt.setString(1, "%" + searchQuery + "%");
                    stmt.setString(2, "%" + searchQuery + "%");
                }

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
                    Map<String, Object> productData = new HashMap<>();
                    productData.put("product", product);
                    data.add(productData);
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error loading products from database", e);
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to load products.");
        }
    }

    private void updateProductPane() {
        productPane.getChildren().clear();
        for (Map<String, Object> productData : data) {
            Product product = (Product) productData.get("product");

            VBox productBox = new VBox(5);
            productBox.setAlignment(Pos.CENTER);
            productBox.getStyleClass().add("product-box");

            Label nameLabel = new Label(product.getName());
            Label barcodeLabel = new Label("Barcode: " + product.getBarcode());
            Label priceLabel = new Label(String.format("$%.2f", product.getSellingPrice()));

            ImageView productImage = new ImageView();
            try {
                Image image = new Image(getClass().getResourceAsStream(product.getImage()));
                productImage.setImage(image);
                productImage.setFitHeight(100);
                productImage.setFitWidth(100);
                productImage.setPreserveRatio(true);
            } catch (Exception e) {
                logger.warning("Failed to load image for product: " + product.getName());
            }

            productBox.getChildren().addAll(productImage, nameLabel, barcodeLabel, priceLabel);

            // Style toggle for selection
            productBox.setOnMouseClicked(e -> {
                if (selectedProduct == product) {
                    selectedProduct = null;
                    productBox.getStyleClass().remove("selected-product");
                } else {
                    productPane.getChildren().forEach(node -> node.getStyleClass().remove("selected-product"));
                    selectedProduct = product;
                    productBox.getStyleClass().add("selected-product");
                }
            });

            // Add hover effects using ScaleTransition
            productBox.setOnMouseEntered(event -> {
                ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(200), productBox);
                scaleTransition.setToX(1.05);
                scaleTransition.setToY(1.05);
                scaleTransition.play();
            });

            productBox.setOnMouseExited(event -> {
                ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(200), productBox);
                scaleTransition.setToX(1);
                scaleTransition.setToY(1);
                scaleTransition.play();
            });

            productPane.getChildren().add(productBox);
        }
    }

    private void hidePOSUIComponents() {
        productPane.setVisible(false);
        sellQuantityField.setDisable(true);
        sellButton.setDisable(true);
        finishDayButton.setVisible(false);
        cashInDeskLabel.setVisible(false);
        profitLabel.setVisible(false);
    }

    private void showPOSUIComponents() {
        productPane.setVisible(true);
        sellQuantityField.setDisable(false);
        sellButton.setDisable(false);
        cashInDeskLabel.setVisible(true);
        profitLabel.setVisible(true);
    }

    private void showDaySummary() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Day Summary");
        alert.setHeaderText("Summary of the Day");
        alert.setContentText(String.format("Total Profit: $%.2f\nCash in Desk: $%.2f", dailyProfit, cashInDesk));
        alert.showAndWait();
    }

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

    // Method to save invoice to sales_report file
    private void saveInvoiceToFile(Product product, int quantitySold) {
        try (FileWriter writer = new FileWriter("sales_report.txt", true)) {
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String formattedDateTime = now.format(formatter);

            String invoice = String.format(
                    "Date: %s | Product: %s | Quantity Sold: %d | Total: $%.2f\n",
                    formattedDateTime, product.getName(), quantitySold, product.getSellingPrice() * quantitySold
            );
            writer.write(invoice);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error saving invoice to file", e);
            showAlert(Alert.AlertType.ERROR, "File Error", "Failed to save invoice.");
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}