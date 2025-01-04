package application;

import javafx.application.Application;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class MainMenuBar {

    public static MenuBar createMenuBar(Stage primaryStage) {
        Menu menu = new Menu("Navigation");

        // Create menu items for each page
        MenuItem dashboardMenuItem = createMenuItem("Dashboard", primaryStage, new Dashboard());
        MenuItem productManagementMenuItem = createMenuItem("Product Management", primaryStage, new ProductManagementPage());
        MenuItem posPageMenuItem = createMenuItem("POS Page", primaryStage, new POSPage());
        MenuItem reportsPageMenuItem = createMenuItem("Reports Page", primaryStage, new ReportsPage());
        MenuItem inventoryPageMenuItem = createMenuItem("Inventory Page", primaryStage, new InventoryPage());
        MenuItem exitMenuItem = new MenuItem("Exit");
        exitMenuItem.setOnAction(event -> {
            System.out.println("Exiting the application");
            primaryStage.close();
        });

        // Add all menu items to the menu
        menu.getItems().addAll(
                dashboardMenuItem,
                productManagementMenuItem,
                posPageMenuItem,
                reportsPageMenuItem,
                inventoryPageMenuItem,
                exitMenuItem
        );

        // Create MenuBar and apply CSS styles
        MenuBar menuBar = new MenuBar(menu);
        menuBar.getStyleClass().add("menu-bar");

        return menuBar;
    }

    private static MenuItem createMenuItem(String text, Stage primaryStage, Application targetPage) {
        MenuItem menuItem = new MenuItem(text);

        // Add action to navigate to the specified page
        menuItem.setOnAction(event -> {
            if (targetPage != null) {
                try {
                    targetPage.start(new Stage());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                primaryStage.close();
            }
        });

        // Apply custom styles to menu items
        menuItem.getStyleClass().add("menu-item");

        return menuItem;
    }
}
