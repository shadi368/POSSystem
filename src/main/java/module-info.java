module org.example.project {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.swing;  // Add this line
    requires com.google.zxing;
    requires com.google.zxing.javase;
    requires java.sql;
    requires jdk.javadoc;


    exports application;
    opens application to javafx.fxml;
}
