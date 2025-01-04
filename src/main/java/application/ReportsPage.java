package application;

import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color; // Use JavaFX Color
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;


public class ReportsPage extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Create the header navigation bar
        HBox header = createHeaderNavigation(primaryStage);
        header.setStyle("-fx-background-color: #3b3b3b; -fx-padding: 10; -fx-alignment: center;");

        // Button to load and display reports
        Button generateReportButton = new Button("Generate Report");
        generateReportButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10;");

        // Button to clear the reports
        Button clearReportButton = new Button("Clear Reports");
        clearReportButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10;");

        // Label for title
        Label titleLabel = new Label("Reports");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #333;");

        // TextArea to display reports
        TextArea reportDisplay = new TextArea();
        reportDisplay.setEditable(false); // Make it read-only
        reportDisplay.setWrapText(true);  // Enable text wrapping
        reportDisplay.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 14px; -fx-control-inner-background: #f9f9f9; -fx-text-fill: #000;");

        // Add a fade transition animation for the report display
        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(1), reportDisplay);
        fadeTransition.setFromValue(0);
        fadeTransition.setToValue(1);

        // Button action to load reports
        generateReportButton.setOnAction(e -> {
            String reports = loadReportsFromFile("sales_report.txt");
            if (reports.isEmpty()) {
                reportDisplay.setText("No reports available.");
            } else {
                reportDisplay.setText(formatReports(reports));
                fadeTransition.play(); // Play the animation when content is loaded
            }
        });

        // Button action to clear reports
        clearReportButton.setOnAction(e -> {
            if (clearReportsFile("sales_report.txt")) {
                reportDisplay.setText("Reports have been cleared.");
            } else {
                reportDisplay.setText("Failed to clear reports.");
            }
        });

        // Layout for the page
        VBox mainLayout = new VBox();
        mainLayout.setAlignment(Pos.TOP_CENTER);
        mainLayout.setPadding(new javafx.geometry.Insets(15));
        mainLayout.setStyle("-fx-background-color: #eef2f3;");

        VBox contentLayout = new VBox(10, titleLabel, generateReportButton, clearReportButton, new ScrollPane(reportDisplay));
        contentLayout.setAlignment(Pos.CENTER);

        mainLayout.getChildren().addAll(header, contentLayout);

        Scene scene = new Scene(mainLayout, 500, 400);
        primaryStage.setTitle("Reports");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Method to create the header navigation bar (consistent with POSPage, Dashboard, and ProductManagementPage)
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

    // Method to create header buttons (consistent with POSPage, Dashboard, and ProductManagementPage)
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

    // Helper method to load reports from a file
    private String loadReportsFromFile(String fileName) {
        StringBuilder reportContent = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                reportContent.append(line).append("\n");
            }
        } catch (IOException e) {
            // Display an alert in case of an error
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Unable to load reports");
            alert.setContentText("An error occurred while reading the report file.");
            alert.showAndWait();
        }
        return reportContent.toString();
    }

    // Helper method to clear the contents of the reports file
    private boolean clearReportsFile(String fileName) {
        try {
            Files.write(Paths.get(fileName), new byte[0]); // Clear the file by writing an empty byte array
            return true;
        } catch (IOException e) {
            // Display an alert in case of an error
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Unable to clear reports");
            alert.setContentText("An error occurred while clearing the report file.");
            alert.showAndWait();
            return false;
        }
    }

    // Helper method to format reports for better readability and details
    private String formatReports(String reports) {
        String[] lines = reports.split("\n");
        StringBuilder formattedReport = new StringBuilder();

        formattedReport.append("===== Detailed Profit Report =====\n\n");

        for (String line : lines) {
            // Example of how we might split each report entry for detailed sections
            String[] parts = line.split(",");
            if (parts.length == 3) {
                formattedReport.append("Date: ").append(parts[0].trim()).append("\n");
                formattedReport.append("Profit: ").append(parts[1].trim()).append("\n");
                formattedReport.append("Items Sold: ").append(parts[2].trim()).append("\n");
                formattedReport.append("--------------------------------\n");
            } else {
                formattedReport.append(line).append("\n");
            }
        }

        formattedReport.append("\n===============================");
        return formattedReport.toString();
    }

    public static void main(String[] args) {
        launch(args);
    }
}