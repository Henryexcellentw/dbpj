module com.example.databaseproject {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop;


    opens com.example.databaseproject to javafx.fxml;
    opens Entity to javafx.base;
    exports com.example.databaseproject;
}