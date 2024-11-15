module com.example.limkokwingacademicreporting {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires mysql.connector.j;


    opens com.example.limkokwingacademicreporting to javafx.fxml;
    exports com.example.limkokwingacademicreporting;
}