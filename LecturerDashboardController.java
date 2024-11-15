package com.example.limkokwingacademicreporting;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LecturerDashboardController {

    @FXML
    private ComboBox<String> moduleComboBox;

    @FXML
    private ComboBox<Integer> weekComboBox;

    @FXML
    private TextArea reportDetailsField;

    @FXML
    private TableView<StudentAttendance> attendanceTable;

    @FXML
    private TableColumn<StudentAttendance, String> studentNameColumn;

    @FXML
    private TableColumn<StudentAttendance, String> attendanceColumn;

    private ObservableList<StudentAttendance> studentAttendanceList;

    @FXML
    public void initialize() {
        // Populate modules in ComboBox (replace with actual database query)
        moduleComboBox.setItems(FXCollections.observableArrayList("BIDM3110 DISCRETE MATHS", " DICP2112 C++ PROGRAMMING I", "BCPR1112 PUBLIC RELATIONS"));

        // Populate weeks in ComboBox (1 to 12)
        weekComboBox.setItems(FXCollections.observableArrayList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12));

        // Initialize the attendance list
        studentAttendanceList = FXCollections.observableArrayList();

        // Fetch student list from the database and populate the TableView
        fetchStudentListFromDatabase();

        // Set up TableView columns
        studentNameColumn.setCellValueFactory(new PropertyValueFactory<>("studentName"));
        attendanceColumn.setCellValueFactory(new PropertyValueFactory<>("attendance"));

        // Add ComboBox to Attendance column for marking attendance
        attendanceColumn.setCellFactory(column -> new TableCell<>() {
            private final ComboBox<String> comboBox = new ComboBox<>(FXCollections.observableArrayList("Present", "Absent"));

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    comboBox.setValue(item);
                    setGraphic(comboBox);
                    comboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
                        StudentAttendance student = getTableView().getItems().get(getIndex());
                        student.setAttendance(newVal);
                    });
                }
            }
        });

        // Set the data for the TableView
        attendanceTable.setItems(studentAttendanceList);
    }

    private void fetchStudentListFromDatabase() {
        String url = "jdbc:mysql://localhost:3306/LimkokwingAcademicReporting"; // Update with your database details
        String username = "root"; // Database username
        String password = "tumi"; // Database password
        String query = "SELECT first_name, last_name FROM students";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                String fullName = resultSet.getString("first_name") + " " + resultSet.getString("last_name");
                studentAttendanceList.add(new StudentAttendance(fullName, "Absent")); // Default attendance to "Absent"
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Could not fetch student names.");
        }
    }

    @FXML
    private void handleSaveReport() {
        String module = moduleComboBox.getValue();
        Integer week = weekComboBox.getValue();
        String reportDetails = reportDetailsField.getText();

        if (module == null || week == null || reportDetails == null || reportDetails.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please fill in all fields.");
            return;
        }

        saveReportToDatabase(module, week, reportDetails);
    }

    private void saveReportToDatabase(String module, Integer week, String reportDetails) {
        String url = "jdbc:mysql://localhost:3306/LimkokwingAcademicReporting"; // Update to your database details
        String username = "root"; // Your database username
        String password = "tumi"; // Your database password

        String insertQuery = "INSERT INTO attendance_records (module_name, week_number, report_details) VALUES (?, ?, ?)";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(insertQuery)) {

            statement.setString(1, module);
            statement.setInt(2, week);
            statement.setString(3, reportDetails);

            statement.executeUpdate();
            showAlert(Alert.AlertType.INFORMATION, "Success", "Report saved successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Could not save the report.");
        }
    }

    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/limkokwingacademicreporting/LecturerLogin.fxml"));
            Region root = loader.load();

            Stage stage = (Stage) moduleComboBox.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load the previous screen.");
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Inner class for TableView data
    public static class StudentAttendance {
        private final String studentName;
        private String attendance;

        public StudentAttendance(String studentName, String attendance) {
            this.studentName = studentName;
            this.attendance = attendance;
        }

        public String getStudentName() {
            return studentName;
        }

        public String getAttendance() {
            return attendance;
        }

        public void setAttendance(String attendance) {
            this.attendance = attendance;
        }
    }
}
