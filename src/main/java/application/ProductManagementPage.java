package application;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.io.File;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class ProductManagementPage extends Application {
    private BorderPane root;

    private void showProductManagementPage(Stage primaryStage) {
        System.out.println("Navigating to Product Management Page");

        // Create the Product Management UI
        ProductManagementPage productManagementPage = new ProductManagementPage();

        // Set the Product Management UI as the scene for the primary stage
        Scene productManagementScene = new Scene(productManagementPage.getRoot(), 800, 600);
        productManagementScene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm()); // Apply CSS
        primaryStage.setScene(productManagementScene);
        primaryStage.setTitle("Product Management Page");
        primaryStage.show();
    }

    public Parent getRoot() {
        return root;
    }

    private static final String URL = "jdbc:mysql://localhost:3306/pos_system";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "root";
    private static Connection connection;

    // Database Connection
    private Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        }
        return connection;
    }

    private String generateRandomBarcode() {
        Random random = new Random();
        StringBuilder barcode = new StringBuilder();
        for (int i = 0; i < 12; i++) {
            barcode.append(random.nextInt(10)); // Generate random 12-digit barcode
        }
        return barcode.toString();
    }

    private Image generateBarcode(String value) {
        try {
            // Use ZXing to generate a barcode
            MultiFormatWriter writer = new MultiFormatWriter();
            BitMatrix bitMatrix = writer.encode(value, BarcodeFormat.CODE_128, 200, 100); // Adjust size as needed

            // Convert BitMatrix to Image
            javafx.scene.image.WritableImage image = new javafx.scene.image.WritableImage(bitMatrix.getWidth(), bitMatrix.getHeight());
            for (int x = 0; x < bitMatrix.getWidth(); x++) {
                for (int y = 0; y < bitMatrix.getHeight(); y++) {
                    image.getPixelWriter().setColor(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }
            return image;
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void start(Stage primaryStage) {
        // Create the header navigation bar
        HBox header = createHeaderNavigation(primaryStage);
        header.setStyle("-fx-background-color: #3b3b3b; -fx-padding: 10; -fx-alignment: center;");

        // Table for displaying products
        TableView<Map<String, Object>> table = new TableView<>();
        ObservableList<Map<String, Object>> data = FXCollections.observableArrayList();

        // Create columns for product data
        TableColumn<Map<String, Object>, String> nameColumn = new TableColumn<>("Name");
        TableColumn<Map<String, Object>, String> categoryColumn = new TableColumn<>("Category");
        TableColumn<Map<String, Object>, Double> sellingPriceColumn = new TableColumn<>("Selling Price");
        TableColumn<Map<String, Object>, Double> costPriceColumn = new TableColumn<>("Cost Price");
        TableColumn<Map<String, Object>, Integer> quantityColumn = new TableColumn<>("Quantity");
        TableColumn<Map<String, Object>, String> imageColumn = new TableColumn<>("Image Path");
        TableColumn<Map<String, Object>, String> barcodeColumn = new TableColumn<>("Barcode");

        // Set the cell value factory for each column
        nameColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty((String) cellData.getValue().get("name")));
        categoryColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty((String) cellData.getValue().get("category")));
        sellingPriceColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>((Double) cellData.getValue().get("sellingPrice")));
        costPriceColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>((Double) cellData.getValue().get("costPrice")));
        quantityColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>((Integer) cellData.getValue().get("quantity")));
        imageColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty((String) cellData.getValue().get("image")));
        barcodeColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty((String) cellData.getValue().get("barcode")));

        // Add columns to the table
        table.getColumns().addAll(nameColumn, categoryColumn, sellingPriceColumn, costPriceColumn, quantityColumn, imageColumn, barcodeColumn);

        // Load products from the database
        loadProductsFromDatabase(data);
        table.setItems(data);

        // Form for editing products
        TextField nameField = new TextField();
        TextField categoryField = new TextField();
        TextField sellingPriceField = new TextField();
        TextField costPriceField = new TextField();
        TextField quantityField = new TextField();
        TextField imageField = new TextField();
        imageField.setPromptText("Image Path");
        imageField.setEditable(false); // Make the field non-editable

        // Barcode TextField and ImageView
        TextField barcodeField = new TextField();
        barcodeField.setPromptText("Scan or Enter Barcode");
        ImageView barcodeImageView = new ImageView();

        // Button to open image chooser
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif"));
        Button chooseImageButton = new Button("Choose Image");
        chooseImageButton.setStyle("-fx-background-color: #6200ea; -fx-text-fill: white;");

        chooseImageButton.setOnAction(event -> {
            File selectedFile = fileChooser.showOpenDialog(primaryStage);
            if (selectedFile != null) {
                imageField.setText(selectedFile.getAbsolutePath());
            }
        });

        Button addButton = new Button("Add Product");
        addButton.setStyle("-fx-background-color: #6200ea; -fx-text-fill: white;");

        Button updateButton = new Button("Update Product");
        updateButton.setStyle("-fx-background-color: #6200ea; -fx-text-fill: white;");
        updateButton.setDisable(true); // Initially disabled

        // Enable form when a row is selected for update
        table.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                nameField.setText((String) newSelection.get("name"));
                categoryField.setText((String) newSelection.get("category"));
                sellingPriceField.setText(String.valueOf(newSelection.get("sellingPrice")));
                costPriceField.setText(String.valueOf(newSelection.get("costPrice")));
                quantityField.setText(String.valueOf(newSelection.get("quantity")));
                imageField.setText((String) newSelection.get("image"));
                barcodeField.clear(); // Clear manual barcode input
                updateButton.setDisable(false);

                // Generate and display barcode if not entered manually
                String barcodeValue = (String) newSelection.get("barcode");
                if (barcodeValue != null && !barcodeValue.isEmpty()) {
                    Image barcodeImage = generateBarcode(barcodeValue);
                    barcodeImageView.setImage(barcodeImage);
                }
            }
        });

        // Add product functionality
        addButton.setOnAction(event -> {
            try {
                // Get new values from the form
                String newName = nameField.getText().trim();
                String newCategory = categoryField.getText().trim();
                double newSellingPrice = Double.parseDouble(sellingPriceField.getText().trim());
                double newCostPrice = Double.parseDouble(costPriceField.getText().trim());
                int newQuantity = Integer.parseInt(quantityField.getText().trim());
                String imagePath = imageField.getText().trim();
                String barcode = barcodeField.getText().trim();

                if (barcode.isEmpty()) {
                    barcode = generateRandomBarcode(); // Generate barcode if not provided
                }

                // Create a new product
                addProductToDatabase(newName, newCategory, newSellingPrice, newCostPrice, newQuantity, imagePath, barcode);
                data.clear();
                loadProductsFromDatabase(data); // Reload updated data

                // Clear form after adding the product
                nameField.clear();
                categoryField.clear();
                sellingPriceField.clear();
                costPriceField.clear();
                quantityField.clear();
                imageField.clear();
                barcodeField.clear();
            } catch (NumberFormatException e) {
                showAlert("Invalid Input", "Price, selling price, and quantity must be numeric.");
            }
        });

        // Update product functionality
        updateButton.setOnAction(event -> {
            Map<String, Object> selectedProduct = table.getSelectionModel().getSelectedItem();
            if (selectedProduct != null) {
                try {
                    // Get new values from the form
                    String newName = nameField.getText().trim();
                    String newCategory = categoryField.getText().trim();
                    double newSellingPrice = Double.parseDouble(sellingPriceField.getText().trim());
                    double newCostPrice = Double.parseDouble(costPriceField.getText().trim());
                    int newQuantity = Integer.parseInt(quantityField.getText().trim());
                    String imagePath = imageField.getText().trim();
                    String barcode = barcodeField.getText().trim();

                    if (barcode.isEmpty()) {
                        barcode = generateRandomBarcode();
                    }

                    // Update product in the database
                    int productId = (Integer) selectedProduct.get("id");
                    updateProductInDatabase(productId, newName, newCategory, newSellingPrice, newCostPrice, newQuantity, imagePath, barcode);

                    // Reload data
                    data.clear();
                    loadProductsFromDatabase(data);

                    // Clear form
                    nameField.clear();
                    categoryField.clear();
                    sellingPriceField.clear();
                    costPriceField.clear();
                    quantityField.clear();
                    imageField.clear();
                    barcodeField.clear();
                    updateButton.setDisable(true);
                } catch (NumberFormatException e) {
                    showAlert("Invalid Input", "Price, selling price, and quantity must be numeric.");
                }
            }
        });

        // Generate barcode button functionality
        Button generateBarcodeButton = new Button("Generate Random Barcode");
        generateBarcodeButton.setStyle("-fx-background-color: #6200ea; -fx-text-fill: white;");
        generateBarcodeButton.setOnAction(event -> {
            String barcode = generateRandomBarcode();
            barcodeField.setText(barcode); // Set the generated barcode in the text input
            Image barcodeImage = generateBarcode(barcode);
            barcodeImageView.setImage(barcodeImage); // Display the generated barcode image
        });

        // Layout for product editing form
        VBox formLayout = new VBox(5,
                new Label("Name:"), nameField,
                new Label("Category:"), categoryField,
                new Label("Selling Price:"), sellingPriceField,
                new Label("Cost Price:"), costPriceField,
                new Label("Quantity:"), quantityField,
                chooseImageButton, imageField,
                new Label("Barcode:"), barcodeField,
                new Label("Generated Barcode:"), barcodeImageView,
                generateBarcodeButton, // Add the barcode generation button
                addButton, updateButton
        );
        formLayout.setPrefWidth(300);

        // Wrap the root layout in a ScrollPane
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(new HBox(10, table, formLayout));

        // Main layout using BorderPane
        BorderPane root = new BorderPane();
        root.setTop(header); // Header at the top
        root.setCenter(scrollPane); // Main content in the center

        // Create Scene and apply styles
        Scene scene = new Scene(root, 900, 600);
        scene.getStylesheets().add(getClass().getResource("/views/product_management.css").toExternalForm());

        primaryStage.setTitle("Product Management");
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

    // Load products from the database
    private void loadProductsFromDatabase(ObservableList<Map<String, Object>> data) {
        try (Connection connection = getConnection()) {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM products");
            while (rs.next()) {
                Map<String, Object> product = new HashMap<>();
                product.put("id", rs.getInt("id"));
                product.put("name", rs.getString("name"));
                product.put("category", rs.getString("category"));
                product.put("sellingPrice", rs.getDouble("selling_price"));
                product.put("costPrice", rs.getDouble("cost_price"));
                product.put("quantity", rs.getInt("quantity"));
                product.put("image", rs.getString("image"));
                product.put("barcode", rs.getString("barcode"));
                data.add(product);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Add product to the database
    private void addProductToDatabase(String name, String category, double sellingPrice, double costPrice, int quantity, String image, String barcode) {
        try (Connection connection = getConnection()) {
            String sql = "INSERT INTO products (name, category, selling_price, cost_price, quantity, image, barcode) VALUES (?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, name);
                stmt.setString(2, category);
                stmt.setDouble(3, sellingPrice);
                stmt.setDouble(4, costPrice);
                stmt.setInt(5, quantity);
                stmt.setString(6, image);
                stmt.setString(7, barcode);
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Update product in the database
    private void updateProductInDatabase(int id, String name, String category, double sellingPrice, double costPrice, int quantity, String image, String barcode) {
        try (Connection connection = getConnection()) {
            String sql = "UPDATE products SET name = ?, category = ?, selling_price = ?, cost_price = ?, quantity = ?, image = ?, barcode = ? WHERE id = ?";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, name);
                stmt.setString(2, category);
                stmt.setDouble(3, sellingPrice);
                stmt.setDouble(4, costPrice);
                stmt.setInt(5, quantity);
                stmt.setString(6, image);
                stmt.setString(7, barcode);
                stmt.setInt(8, id);
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Show alert message
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}