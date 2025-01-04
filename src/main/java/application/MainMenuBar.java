package application;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;

public class MainMenuBar {

    private MenuBar menuBar;

    // Constructor
    public MainMenuBar(Stage primaryStage) {
        // Create a Menu for navigation
        Menu menu = new Menu("Navigation");

        // Create menu items for each page
        MenuItem dashboardMenuItem = new MenuItem("Dashboard");
        MenuItem productManagementMenuItem = new MenuItem("Product Management");
        MenuItem posMenuItem = new MenuItem("POS");
        MenuItem reportsMenuItem = new MenuItem("Reports");
        MenuItem inventoryMenuItem = new MenuItem("Inventory");
        MenuItem exitMenuItem = new MenuItem("Exit");

        // Set actions for each menu item
        dashboardMenuItem.setOnAction(e -> new Dashboard().start(new Stage()));
        productManagementMenuItem.setOnAction(e -> new ProductManagementPage().start(new Stage()));
        posMenuItem.setOnAction(e -> new POSPage().start(new Stage()));
        reportsMenuItem.setOnAction(e -> new ReportsPage().start(new Stage()));
        inventoryMenuItem.setOnAction(e -> new InventoryPage().start(new Stage()));

        // Set action for the exit menu item
        exitMenuItem.setOnAction(event -> {
            System.out.println("Exiting the application");
            primaryStage.close();
        });

        // Add all menu items to the menu
        menu.getItems().addAll(
                dashboardMenuItem,
                productManagementMenuItem,
                posMenuItem,
                reportsMenuItem,
                inventoryMenuItem,
                exitMenuItem
        );

        // Create MenuBar and apply CSS styles
        menuBar = new MenuBar(menu);
        menuBar.getStyleClass().add("menu-bar");
    }

    // Method to get the MenuBar
    public MenuBar getMenuBar() {
        return menuBar;
    }

    public static MenuBar createMenuBar(Stage primaryStage) {
        // Create a Menu for navigation
        Menu menu = new Menu("Navigation");

        // Create menu items for each page
        MenuItem dashboardMenuItem = new MenuItem("Dashboard");
        MenuItem productManagementMenuItem = new MenuItem("Product Management");
        MenuItem posMenuItem = new MenuItem("POS");
        MenuItem reportsMenuItem = new MenuItem("Reports");
        MenuItem inventoryMenuItem = new MenuItem("Inventory");
        MenuItem exitMenuItem = new MenuItem("Exit");

        // Set actions for each menu item
        dashboardMenuItem.setOnAction(e -> new Dashboard().start(new Stage()));
        productManagementMenuItem.setOnAction(e -> new ProductManagementPage().start(new Stage()));
        posMenuItem.setOnAction(e -> new POSPage().start(new Stage()));
        reportsMenuItem.setOnAction(e -> new ReportsPage().start(new Stage()));
        inventoryMenuItem.setOnAction(e -> new InventoryPage().start(new Stage()));

        // Set action for the exit menu item
        exitMenuItem.setOnAction(event -> {
            System.out.println("Exiting the application");
            primaryStage.close();
        });

        // Add all menu items to the menu
        menu.getItems().addAll(
                dashboardMenuItem,
                productManagementMenuItem,
                posMenuItem,
                reportsMenuItem,
                inventoryMenuItem,
                exitMenuItem
        );

        // Create MenuBar and apply CSS styles
        MenuBar menuBar = new MenuBar(menu);
        menuBar.getStyleClass().add("menu-bar");

        return menuBar;
    }

}