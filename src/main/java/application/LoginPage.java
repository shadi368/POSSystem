package application;

import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.*;

public class LoginPage extends Application {

    private static final String USER_FILE = "C:/Users/PC/Desktop/users.txt"; // Path to the user file
    private Runnable loginSuccessCallback; // Declare the loginSuccessCallback

    @Override
    public void start(Stage primaryStage) {
        // Initialize the UI components for the login page
        Label usernameLabel = new Label("Username:");
        TextField usernameField = new TextField();
        Label passwordLabel = new Label("Password:");
        PasswordField passwordField = new PasswordField();
        Button loginButton = createAnimatedButton("Login");
        Button registerButton = createAnimatedButton("Register");

        // Add an action for the Login button
        loginButton.setOnAction(e -> {
            String username = usernameField.getText();
            String password = passwordField.getText();
            if (verifyUser(username, password)) {
                // Trigger the success callback after successful login
                if (loginSuccessCallback != null) {
                    loginSuccessCallback.run(); // Call the callback to open the MainPage
                }
                primaryStage.close(); // Close the login page after successful login
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Invalid credentials");
                alert.show();
            }
        });

        // Add an action for the Register button
        registerButton.setOnAction(e -> openRegistrationForm());

        // Layout setup
        VBox layout = new VBox(10, usernameLabel, usernameField, passwordLabel, passwordField, loginButton, registerButton);
        layout.setStyle("-fx-padding: 20px; -fx-background-color: #f9f9f9; -fx-alignment: center;");

        // Apply fade-in animation to the layout
        FadeTransition fadeInTransition = new FadeTransition(Duration.seconds(1), layout);
        fadeInTransition.setFromValue(0);
        fadeInTransition.setToValue(1);
        fadeInTransition.play();

        Scene scene = new Scene(layout, 300, 250);
        primaryStage.setTitle("Login");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Helper method to create animated buttons
    private Button createAnimatedButton(String text) {
        Button button = new Button(text);
        button.setStyle("-fx-font-size: 14px; -fx-padding: 10; -fx-background-color: #0078D7; -fx-text-fill: white; -fx-cursor: hand;");

        // Add hover effect
        button.setOnMouseEntered(e -> button.setStyle("-fx-font-size: 14px; -fx-padding: 10; -fx-background-color: #005fa3; -fx-text-fill: white; -fx-cursor: hand;"));
        button.setOnMouseExited(e -> button.setStyle("-fx-font-size: 14px; -fx-padding: 10; -fx-background-color: #0078D7; -fx-text-fill: white; -fx-cursor: hand;"));

        // Add fade-in animation for the button
        FadeTransition fadeInTransition = new FadeTransition(Duration.seconds(1), button);
        fadeInTransition.setFromValue(0);
        fadeInTransition.setToValue(1);
        fadeInTransition.play();

        return button;
    }

    // Register form handling
    private void openRegistrationForm() {
        Stage registrationStage = new Stage();
        Label usernameLabel = new Label("Username:");
        TextField usernameField = new TextField();
        Label passwordLabel = new Label("Password:");
        PasswordField passwordField = new PasswordField();
        Button registerButton = createAnimatedButton("Register");

        registerButton.setOnAction(e -> {
            String username = usernameField.getText();
            String password = passwordField.getText();
            if (!username.isEmpty() && !password.isEmpty()) {
                if (isUserExists(username)) {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setContentText("Username already exists!");
                    alert.show();
                } else {
                    saveUserToFile(username, password);
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setContentText("Registration successful!");
                    alert.show();
                    registrationStage.close();
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Please fill in all fields");
                alert.show();
            }
        });

        VBox layout = new VBox(10, usernameLabel, usernameField, passwordLabel, passwordField, registerButton);
        layout.setStyle("-fx-padding: 20px; -fx-background-color: #f9f9f9; -fx-alignment: center;");

        // Apply fade-in animation to the layout
        FadeTransition fadeInTransition = new FadeTransition(Duration.seconds(1), layout);
        fadeInTransition.setFromValue(0);
        fadeInTransition.setToValue(1);
        fadeInTransition.play();

        Scene scene = new Scene(layout, 300, 200);
        registrationStage.setTitle("Register");
        registrationStage.setScene(scene);
        registrationStage.show();
    }

    private boolean verifyUser(String username, String password) {
        File file = new File(USER_FILE);
        if (!file.exists()) return false;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] user = line.split(":");
                if (user[0].equals(username) && user[1].equals(password)) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean isUserExists(String username) {
        File file = new File(USER_FILE);
        if (!file.exists()) return false;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] user = line.split(":");
                if (user[0].equals(username)) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void saveUserToFile(String username, String password) {
        File file = new File(USER_FILE);
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, true))) {
            bw.write(username + ":" + password);
            bw.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to set the login success callback
    public void setLoginSuccessCallback(Runnable callback) {
        this.loginSuccessCallback = callback;
    }

    public static void main(String[] args) {
        Platform.runLater(() -> {
            LoginPage loginPage = new LoginPage();
            loginPage.setLoginSuccessCallback(() -> {
                // Once login is successful, launch the MainPage
                new MainPage().start(new Stage());
            });
            loginPage.start(new Stage());
        });
    }
}
