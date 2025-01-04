package application;

import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ReportsPage extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Create a navigation bar (MenuBar)
        MenuBar menuBar = createNavigationBar(primaryStage);

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
            String reports = loadReportsFromFile("profit_report.txt");
            if (reports.isEmpty()) {
                reportDisplay.setText("No reports available.");
            } else {
                reportDisplay.setText(formatReports(reports));
                fadeTransition.play(); // Play the animation when content is loaded
            }
        });

        // Button action to clear reports
        clearReportButton.setOnAction(e -> {
            if (clearReportsFile("profit_report.txt")) {
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

        mainLayout.getChildren().addAll(menuBar, contentLayout);

        Scene scene = new Scene(mainLayout, 500, 400);
        primaryStage.setTitle("Reports");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Helper method to create the navigation bar
    private MenuBar createNavigationBar(Stage primaryStage) {
        MenuBar menuBar = new MenuBar();

        Menu homeMenu = new Menu("Home");
        MenuItem homeItem = new MenuItem("Go to Home");
        homeItem.setOnAction(e -> {
            MainPage mainPage = new MainPage();
            try {
                mainPage.start(primaryStage); // Navigate to MainPage
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        homeMenu.getItems().add(homeItem);

        Menu exitMenu = new Menu("Exit");
        MenuItem exitItem = new MenuItem("Exit Application");
        exitItem.setOnAction(e -> primaryStage.close());
        exitMenu.getItems().add(exitItem);

        menuBar.getMenus().addAll(homeMenu, exitMenu);
        return menuBar;
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

    // Helper method to format reports for better readability
    private String formatReports(String reports) {
        String[] lines = reports.split("\n");
        StringBuilder formattedReport = new StringBuilder();
        formattedReport.append("===== Profit Report =====\n\n");
        for (String line : lines) {
            formattedReport.append("- ").append(line).append("\n");
        }
        formattedReport.append("\n=========================");
        return formattedReport.toString();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
