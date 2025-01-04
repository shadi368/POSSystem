package application;

import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class ProductManagementPage extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Create navigation bar
        MenuBar menuBar = createMenuBar(primaryStage);

        // Table for displaying products
        TableView<Map<String, Object>> table = new TableView<>();
        ObservableList<Map<String, Object>> data = FXCollections.observableArrayList();

        // Create columns for product data
        TableColumn<Map<String, Object>, String> nameColumn = new TableColumn<>("Name");
        TableColumn<Map<String, Object>, String> categoryColumn = new TableColumn<>("Category");
        TableColumn<Map<String, Object>, Double> sellingPriceColumn = new TableColumn<>("Selling Price");
        TableColumn<Map<String, Object>, Double> costPriceColumn = new TableColumn<>("Cost Price");
        TableColumn<Map<String, Object>, Integer> quantityColumn = new TableColumn<>("Quantity");

        // Set the cell value factory for each column
        nameColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty((String) cellData.getValue().get("name")));
        categoryColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty((String) cellData.getValue().get("category")));
        sellingPriceColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>((Double) cellData.getValue().get("sellingPrice")));
        costPriceColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>((Double) cellData.getValue().get("costPrice")));
        quantityColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>((Integer) cellData.getValue().get("quantity")));

        // Add columns to the table
        table.getColumns().addAll(nameColumn, categoryColumn, sellingPriceColumn, costPriceColumn, quantityColumn);

        // Load data from the file
        loadProductsFromFile("products.txt", data);
        table.setItems(data);

        // Apply fade-in animation to the table
        FadeTransition tableFadeIn = new FadeTransition(Duration.seconds(1), table);
        tableFadeIn.setFromValue(0);
        tableFadeIn.setToValue(1);
        tableFadeIn.play();

        // Form for editing products
        TextField nameField = new TextField();
        TextField categoryField = new TextField();
        TextField sellingPriceField = new TextField();
        TextField costPriceField = new TextField();
        TextField quantityField = new TextField();
        TextField imageField = new TextField();
        imageField.setPromptText("Image Path");
        imageField.setEditable(false); // Make the field non-editable

        // Button to open image chooser
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif"));
        Button chooseImageButton = new Button("Choose Image");
        chooseImageButton.setOnAction(event -> {
            File selectedFile = fileChooser.showOpenDialog(primaryStage);
            if (selectedFile != null) {
                // Update the image path in the product form
                String imagePath = selectedFile.getAbsolutePath();
                imageField.setText(imagePath); // Set image path in the field
            }
        });

        Button updateButton = new Button("Update Product");
        updateButton.setDisable(true); // Initially disabled

        // Add product form fields
        Button addButton = new Button("Add Product");

        // Enable form when a row is selected
        table.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                nameField.setText((String) newSelection.get("name"));
                categoryField.setText((String) newSelection.get("category"));
                sellingPriceField.setText(String.valueOf(newSelection.get("sellingPrice")));
                costPriceField.setText(String.valueOf(newSelection.get("costPrice")));
                quantityField.setText(String.valueOf(newSelection.get("quantity")));
                imageField.setText((String) newSelection.get("image"));
                updateButton.setDisable(false);

                // Slide-in animation for the form when a product is selected
                TranslateTransition slideIn = new TranslateTransition(Duration.seconds(0.5), nameField);
                slideIn.setFromX(-300);
                slideIn.setToX(0);
                slideIn.play();
            }
        });

        // Update the product details
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
                    String imagePath = imageField.getText().trim(); // Get the image path

                    // Update product details
                    selectedProduct.put("name", newName);
                    selectedProduct.put("category", newCategory);
                    selectedProduct.put("sellingPrice", newSellingPrice);
                    selectedProduct.put("costPrice", newCostPrice);
                    selectedProduct.put("quantity", newQuantity);
                    selectedProduct.put("image", imagePath); // Update image path

                    // Refresh the table to show updated data
                    table.refresh();

                    // Save updated data back to the file
                    saveAllProductsToFile("products.txt", data);

                    // Clear form
                    nameField.clear();
                    categoryField.clear();
                    sellingPriceField.clear();
                    costPriceField.clear();
                    quantityField.clear();
                    imageField.clear();
                    updateButton.setDisable(true); // Disable button until a new row is selected
                } catch (NumberFormatException e) {
                    showAlert("Invalid Input", "Price, selling price, and quantity must be numeric.");
                }
            }
        });

        // Add a new product to the table
        addButton.setOnAction(event -> {
            try {
                // Get values from the form to add a new product
                String newName = nameField.getText().trim();
                String newCategory = categoryField.getText().trim();
                double newSellingPrice = Double.parseDouble(sellingPriceField.getText().trim());
                double newCostPrice = Double.parseDouble(costPriceField.getText().trim());
                int newQuantity = Integer.parseInt(quantityField.getText().trim());
                String imagePath = imageField.getText().trim(); // Get the image path

                // Create a new product
                Map<String, Object> newProduct = createProduct(newName, newCategory, newSellingPrice, newCostPrice, newQuantity, imagePath);

                // Add the new product to the table
                data.add(newProduct);

                // Save the new product to the file
                saveAllProductsToFile("products.txt", data);

                // Clear the form
                nameField.clear();
                categoryField.clear();
                sellingPriceField.clear();
                costPriceField.clear();
                quantityField.clear();
                imageField.clear();

            } catch (NumberFormatException e) {
                showAlert("Invalid Input", "Price, selling price, and quantity must be numeric.");
            }
        });

        // Delete selected product
        Button deleteButton = new Button("Delete Product");
        deleteButton.setOnAction(event -> {
            Map<String, Object> selectedProduct = table.getSelectionModel().getSelectedItem();
            if (selectedProduct != null) {
                data.remove(selectedProduct);
                saveAllProductsToFile("products.txt", data);
                table.refresh(); // Refresh the table after removal
            } else {
                showAlert("No Selection", "Please select a product to delete.");
            }
        });

        // Layout for product editing form
        VBox formLayout = new VBox(5,
                new Label("Name:"), nameField,
                new Label("Category:"), categoryField,
                new Label("Selling Price:"), sellingPriceField,
                new Label("Cost Price:"), costPriceField,
                new Label("Quantity:"), quantityField,
                chooseImageButton, imageField,
                addButton, updateButton, deleteButton
        );

        // Apply fade-in animation to the form
        FadeTransition formFadeIn = new FadeTransition(Duration.seconds(1), formLayout);
        formFadeIn.setFromValue(0);
        formFadeIn.setToValue(1);
        formFadeIn.play();

        HBox mainLayout = new HBox(10, table, formLayout);

        VBox mainLayoutWithMenu = new VBox(menuBar, mainLayout);
        Scene scene = new Scene(mainLayoutWithMenu, 800, 400);

        primaryStage.setTitle("Product Management");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Create navigation menu bar with links to all pages
    private MenuBar createMenuBar(Stage primaryStage) {
        Menu menu = new Menu("Navigation");

        // Create menu items for each page
        MenuItem productManagementMenuItem = new MenuItem("Product Management");
        productManagementMenuItem.setOnAction(event -> {
            new ProductManagementPage().start(new Stage());
            primaryStage.close();
        });

        MenuItem POSPageMenuItem = new MenuItem("POS Page");
        POSPageMenuItem.setOnAction(event -> {
            new POSPage().start(new Stage()); // Open POS page
            primaryStage.close();  // Close the current ProductManagementPage window
        });

        MenuItem reportsPageMenuItem = new MenuItem("Reports Page");
        reportsPageMenuItem.setOnAction(event -> {
            System.out.println("Navigating to Reports Page");
            // Replace with actual navigation logic for Reports Page
        });

        MenuItem inventoryPageMenuItem = new MenuItem("Inventory Page");
        inventoryPageMenuItem.setOnAction(event -> {
            new InventoryPage().start(new Stage());
            primaryStage.close(); // Close current page
        });

        // Add the new MainPage menu item
        MenuItem mainPageMenuItem = new MenuItem("Main Page");
        mainPageMenuItem.setOnAction(event -> {
            new MainPage().start(new Stage()); // Open MainPage
            primaryStage.close(); // Close the current window
        });

        MenuItem exitMenuItem = new MenuItem("Exit");
        exitMenuItem.setOnAction(event -> {
            System.out.println("Exiting the application");
            primaryStage.close();
        });

        // Add all menu items to the menu
        menu.getItems().addAll(
                mainPageMenuItem,  // Add MainPage navigation
                productManagementMenuItem,
                POSPageMenuItem,
                reportsPageMenuItem,
                inventoryPageMenuItem,
                exitMenuItem
        );

        return new MenuBar(menu);
    }

    private void loadProductsFromFile(String fileName, ObservableList<Map<String, Object>> data) {
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 6) { // Account for the image path
                    String name = parts[0].trim();
                    String category = parts[1].trim();
                    double sellingPrice = Double.parseDouble(parts[2].trim());
                    double costPrice = Double.parseDouble(parts[3].trim());
                    int quantity = Integer.parseInt(parts[4].trim());
                    String imagePath = parts[5].trim(); // Image path is the 6th element
                    Map<String, Object> product = createProduct(name, category, sellingPrice, costPrice, quantity, imagePath);
                    data.add(product);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveAllProductsToFile(String fileName, ObservableList<Map<String, Object>> data) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            for (Map<String, Object> product : data) {
                String imagePath = (String) product.get("image"); // Get the image path
                writer.write(product.get("name") + "," + product.get("category") + "," + product.get("sellingPrice") + "," + product.get("costPrice") + "," + product.get("quantity") + "," + imagePath);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Map<String, Object> createProduct(String name, String category, double sellingPrice, double costPrice, int quantity, String imagePath) {
        Map<String, Object> product = new HashMap<>();
        product.put("name", name);
        product.put("category", category);
        product.put("sellingPrice", sellingPrice);
        product.put("costPrice", costPrice);
        product.put("quantity", quantity);
        product.put("image", imagePath); // Store the image path
        return product;
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
