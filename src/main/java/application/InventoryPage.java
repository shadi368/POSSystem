package application;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class InventoryPage extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Create navigation menu bar from MainMenuBar class
        MenuBar menuBar = MainMenuBar.createMenuBar(primaryStage);  // Reuse the createMenuBar method

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

        // Form for updating product quantities and image
        TextField quantityField = new TextField();
        TextField imageField = new TextField();
        Button updateQuantityButton = new Button("Update Quantity");
        Button chooseImageButton = new Button("Choose Image");
        Button updateImageButton = new Button("Update Image");

        updateQuantityButton.setDisable(true);  // Initially disabled
        updateImageButton.setDisable(true);  // Initially disabled
        imageField.setEditable(false);  // Make the image field non-editable

        // File chooser for selecting product image
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif"));
        chooseImageButton.setOnAction(event -> {
            File selectedFile = fileChooser.showOpenDialog(primaryStage);
            if (selectedFile != null) {
                // Update the image path in the image field
                imageField.setText(selectedFile.getAbsolutePath());
            }
        });

        // Enable form when a row is selected
        table.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                quantityField.setText(String.valueOf(newSelection.get("quantity")));
                imageField.setText((String) newSelection.get("image"));
                updateQuantityButton.setDisable(false);
                updateImageButton.setDisable(false);
            }
        });

        // Update the product quantity
        updateQuantityButton.setOnAction(event -> {
            Map<String, Object> selectedProduct = table.getSelectionModel().getSelectedItem();
            if (selectedProduct != null) {
                try {
                    // Get new quantity from the form
                    int newQuantity = Integer.parseInt(quantityField.getText().trim());

                    // Update product quantity
                    selectedProduct.put("quantity", newQuantity);

                    // Refresh the table to show updated data
                    table.refresh();

                    // Save updated data back to the file
                    saveAllProductsToFile("products.txt", data);

                    // Clear form
                    quantityField.clear();
                    updateQuantityButton.setDisable(true);  // Disable button until a new row is selected
                } catch (NumberFormatException e) {
                    showAlert("Invalid Input", "Quantity must be numeric.");
                }
            }
        });

        // Update the product image
        updateImageButton.setOnAction(event -> {
            Map<String, Object> selectedProduct = table.getSelectionModel().getSelectedItem();
            if (selectedProduct != null) {
                String newImagePath = imageField.getText().trim();

                // Update product image path
                selectedProduct.put("image", newImagePath);

                // Refresh the table to show updated data
                table.refresh();

                // Save updated data back to the file
                saveAllProductsToFile("products.txt", data);

                // Clear form
                imageField.clear();
                updateImageButton.setDisable(true);
            }
        });

        // Layout for updating quantity and image
        VBox formLayout = new VBox(5,
                new Label("Quantity:"), quantityField,
                updateQuantityButton,
                new Label("Product Image:"), imageField,
                chooseImageButton, updateImageButton
        );

        // Layout for main page (table and form)
        HBox mainLayout = new HBox(10, table, formLayout);

        // Layout with menu bar at the top
        VBox mainLayoutWithMenu = new VBox(menuBar, mainLayout);
        Scene scene = new Scene(mainLayoutWithMenu, 800, 400);

        primaryStage.setTitle("Inventory Management");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void loadProductsFromFile(String fileName, ObservableList<Map<String, Object>> data) {
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 6) {  // Account for the image field
                    String name = parts[0].trim();
                    String category = parts[1].trim();
                    double sellingPrice = Double.parseDouble(parts[2].trim());
                    double costPrice = Double.parseDouble(parts[3].trim());
                    int quantity = Integer.parseInt(parts[4].trim());
                    String imagePath = parts[5].trim();  // Image path is the last element
                    data.add(createProduct(name, category, sellingPrice, costPrice, quantity, imagePath));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveAllProductsToFile(String fileName, ObservableList<Map<String, Object>> data) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            for (Map<String, Object> product : data) {
                String line = String.format("%s,%s,%.2f,%.2f,%d,%s",
                        product.get("name"), product.get("category"),
                        product.get("sellingPrice"), product.get("costPrice"),
                        product.get("quantity"), product.get("image"));
                writer.write(line);
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
        product.put("image", imagePath);  // Store the image path

        return product;
    }

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
