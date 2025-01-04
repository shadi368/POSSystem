package application;

import javafx.application.Application;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database extends Application {

    // Database credentials
    private static final String URL = "jdbc:mysql://localhost:3306/pos_system"; // Update database name
    private static final String USERNAME = "root"; // Replace with your DB username
    private static final String PASSWORD = "root"; // Replace with your DB password

    // Singleton instance of the database connection
    private static Connection connection;

    // Private constructor to prevent instantiation

    public Database() {
        // Initialization or setup for the database connection
        System.out.println("Database instance created.");
    }

    @Override
    public void start(Stage stage) throws Exception {

    }

    /**
     * Establishes and returns the database connection.
     *
     * @return Connection instance
     * @throws SQLException if a database access error occurs
     */
    public static synchronized Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                // Establish the connection
                connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                System.out.println("Database connected successfully.");
            } catch (SQLException e) {
                System.err.println("Failed to connect to the database: " + e.getMessage());
                throw e;
            }
        }
        return connection;
    }

    /**
     * Closes the database connection.
     */
    public static synchronized void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed.");
            }
        } catch (SQLException e) {
            System.err.println("Error closing database connection: " + e.getMessage());
        }
    }
}
