package application;

import javafx.application.Application;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class POSPage extends Application {

    private ObservableList<Map<String, Object>> data = FXCollections.observableArrayList();
    private double dailyProfit = 0;  // Track the daily profit
    private Label profitLabel;  // Declare profitLabel here
    private boolean dayStarted = false;  // Flag to check if the day has started

    @Override
    public void start(Stage primaryStage) {
        // Create the MenuBar with system navigation options
        MenuBar menuBar = MainMenuBar.createMenuBar(primaryStage); // Reuse the menu bar

        // POS System elements (Initially hidden)
        TableView<Map<String, Object>> table = new TableView<>();
        TextField searchField = new TextField();
        Button searchButton = new Button("Search");
        TextField sellQuantityField = new TextField();
        Button sellButton = new Button("Sell Product");

        // Create columns for product data
        TableColumn<Map<String, Object>, String> nameColumn = new TableColumn<>("Name");
        TableColumn<Map<String, Object>, String> categoryColumn = new TableColumn<>("Category");
        TableColumn<Map<String, Object>, Double> priceColumn = new TableColumn<>("Selling Price");
        TableColumn<Map<String, Object>, Double> costColumn = new TableColumn<>("Cost Price");
        TableColumn<Map<String, Object>, Integer> quantityColumn = new TableColumn<>("Quantity");
        TableColumn<Map<String, Object>, ImageView> imageColumn = new TableColumn<>("Image");

        // Set the cell value factory for each column
        nameColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty((String) cellData.getValue().get("name")));
        categoryColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty((String) cellData.getValue().get("category")));
        priceColumn.setCellValueFactory(cellData ->
                new SimpleObjectProperty<>((Double) cellData.getValue().get("sellingPrice")));
        costColumn.setCellValueFactory(cellData ->
                new SimpleObjectProperty<>((Double) cellData.getValue().get("costPrice")));
        quantityColumn.setCellValueFactory(cellData ->
                new SimpleObjectProperty<>((Integer) cellData.getValue().get("quantity")));

        // Set Image Column
        imageColumn.setCellValueFactory(cellData -> {
            String imagePath = (String) cellData.getValue().get("image");
            ImageView imageView = new ImageView(new Image(new File(imagePath).toURI().toString()));
            imageView.setFitWidth(50);  // Set fixed width for images
            imageView.setFitHeight(50);  // Set fixed height for images
            return new SimpleObjectProperty<>(imageView);
        });

        // Add columns to the table
        table.getColumns().addAll(nameColumn, categoryColumn, priceColumn, costColumn, quantityColumn, imageColumn);

        // Load data from the file
        loadProductsFromFile("products.txt");
        table.setItems(data);

        // Initialize profitLabel here
        profitLabel = new Label("Profit Today: $0.00");

        // Start New Day functionality
        Button newDayButton = new Button("Start New Day");
        Button finishDayButton = new Button("Finish Day");

        // Initially hide the POS elements
        table.setVisible(false);
        searchField.setVisible(false);
        searchButton.setVisible(false);
        sellQuantityField.setVisible(false);
        sellButton.setVisible(false);
        profitLabel.setVisible(false);
        finishDayButton.setVisible(false);

        // Start New Day button action
        newDayButton.setOnAction(event -> {
            if (!dayStarted) {
                dayStarted = true;
                profitLabel.setText("Profit Today: $0.00");

                // Show POS elements after starting the day
                table.setVisible(true);
                searchField.setVisible(true);
                searchButton.setVisible(true);
                sellQuantityField.setVisible(true);
                sellButton.setVisible(true);
                profitLabel.setVisible(true);
                newDayButton.setVisible(false);
                finishDayButton.setVisible(true);
            }
        });

        // Finish Day button functionality
        finishDayButton.setOnAction(event -> {
            if (dayStarted) {
                dayStarted = false;
                saveProfitReport(dailyProfit);
                profitLabel.setText("Profit Today: $" + String.format("%.2f", dailyProfit));
                table.setVisible(false);
                searchField.setVisible(false);
                searchButton.setVisible(false);
                sellQuantityField.setVisible(false);
                sellButton.setVisible(false);
                profitLabel.setVisible(false);
                finishDayButton.setVisible(false);
                newDayButton.setVisible(true);
            }
        });

        // Layout for searching, selling, and profit
        VBox searchLayout = new VBox(5, new Label("Search Products:"), searchField, searchButton);
        VBox sellLayout = new VBox(5, new Label("Sell Quantity:"), sellQuantityField, sellButton);
        VBox profitLayout = new VBox(5, profitLabel);

        // Main layout for POS system
        HBox mainLayout = new HBox(10, table, searchLayout, sellLayout, profitLayout);
        mainLayout.setSpacing(10);

        // Layout for the "Start New Day" button
        VBox startDayLayout = new VBox(10, newDayButton);

        // Layout for Finish Day button
        VBox finishDayLayout = new VBox(10, finishDayButton);

        // Layout for the whole scene
        VBox rootLayout = new VBox(20, menuBar, startDayLayout, mainLayout, profitLayout, finishDayLayout);

        // Scene setup
        Scene scene = new Scene(rootLayout, 1000, 400);
        primaryStage.setTitle("POS - Point of Sale");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Helper methods remain unchanged (loadProductsFromFile, saveAllProductsToFile, etc.)

    // Method to load products from a file
    private void loadProductsFromFile(String fileName) {
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 6) { // 6 because we now have an image path
                    String name = parts[0].trim();
                    String category = parts[1].trim();
                    double sellingPrice = Double.parseDouble(parts[2].trim());
                    double costPrice = Double.parseDouble(parts[3].trim());
                    int quantity = Integer.parseInt(parts[4].trim());
                    String imagePath = parts[5].trim(); // Path to image

                    data.add(createProduct(name, category, sellingPrice, costPrice, quantity, imagePath));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Helper method to create a product as a Map with image path
    private Map<String, Object> createProduct(String name, String category, double sellingPrice, double costPrice, int quantity, String imagePath) {
        Map<String, Object> product = new HashMap<>();
        product.put("name", name);
        product.put("category", category);
        product.put("sellingPrice", sellingPrice);
        product.put("costPrice", costPrice);
        product.put("quantity", quantity);
        product.put("image", imagePath); // Store image path
        return product;
    }

    // Helper method to save the profit report
    private void saveProfitReport(double profit) {
        String dateTime = LocalDateTime.now().toString();
        String reportLine = String.format("Date: %s, Profit: $%.2f", dateTime, profit);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("profit_report.txt", true))) {
            writer.write(reportLine);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Helper method to show an alert
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
