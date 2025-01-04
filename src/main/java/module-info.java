module org.example.project {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.apache.poi.ooxml; // For handling Office documents
    requires org.apache.poi.poi;  // Core POI functionality
    requires org.apache.xmlbeans; // XMLBeans (required by POI)
    requires org.apache.logging.log4j;
    requires java.desktop; // Logging

    exports application; // Ensure this matches your package name
}
