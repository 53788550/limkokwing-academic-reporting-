package com.example.limkokwingacademicreporting;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;

public class PrincipalLecturerDashboardController {

    @FXML private ComboBox<String> moduleComboBox;
    @FXML private TextField classField;
    @FXML private TextField moduleField;
    @FXML private TextField challengesField;
    @FXML private TextField recommendationsField;
    @FXML private DatePicker reportDate;
    @FXML private Button submitReportBtn;
    @FXML private Button viewReportsBtn;

    @FXML private Button backButton; // Back button reference

    private Connection connection;

    public void initialize() {
        try {
            // Establish database connection (MySQL)
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/LimkokwingAcademicReporting", "root", "tumi");

            // Populate ComboBox with modules
            ObservableList<String> modules = FXCollections.observableArrayList(
                    "Discrete Maths (BIDM3110)",
                    "C++ Programming I",
                    "Professional Communication",
                    "Public Relations",
                    "Java Programming II"
            );
            moduleComboBox.setItems(modules);

            // Restrict form population with pre-saved data
            clearForm();

            // Add event listener for the "Submit Report" button
            submitReportBtn.setOnAction(e -> submitReport());

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Connection Error", "Could not connect to the database.");
        }
    }

    // Clear form fields to avoid pre-populating with saved data
    private void clearForm() {
        classField.clear();
        moduleField.clear();
        challengesField.clear();
        recommendationsField.clear();
        reportDate.setValue(null);
    }

    // Submit report functionality
    private void submitReport() {
        String selectedModule = moduleComboBox.getValue();
        String className = classField.getText();
        String moduleName = moduleField.getText();
        String challenges = challengesField.getText();
        String recommendations = recommendationsField.getText();
        String reportDateStr = reportDate.getValue() != null ? reportDate.getValue().toString() : null;

        if (selectedModule != null && !className.isEmpty() && !moduleName.isEmpty() && !challenges.isEmpty() && !recommendations.isEmpty() && reportDate.getValue() != null) {
            try {
                // SQL query to insert the report into the database
                String query = "INSERT INTO weekly_reports (module, class, challenges, recommendations, report_date) VALUES (?, ?, ?, ?, ?)";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setString(1, selectedModule);
                statement.setString(2, className);
                statement.setString(3, challenges);
                statement.setString(4, recommendations);
                statement.setDate(5, Date.valueOf(reportDateStr));

                statement.executeUpdate();
                clearForm();  // Clear the form after submission

                // Show confirmation message
                showAlert(Alert.AlertType.INFORMATION, "Report Submitted", "Your report has been submitted successfully.");
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Submission Error", "Could not submit the report. Please try again.");
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "Incomplete Form", "Please fill all the fields.");
        }
    }

    // Go back to the PrincipalLecturerLogin.fxml when the "Back" button is clicked
    public void goBackToLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("PrincipalLecturerLogin.fxml"));
            Scene scene = new Scene(loader.load());

            Stage stage = (Stage) backButton.getScene().getWindow(); // Get current stage
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Could not navigate to the login screen.");
        }
    }

    // Optionally, add functionality to view past reports
    public void viewPastReports() {
        try {
            String query = "SELECT * FROM weekly_reports";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            // Code to display past reports (for now, print them to console)
            while (resultSet.next()) {
                System.out.println("Module: " + resultSet.getString("module"));
                System.out.println("Class: " + resultSet.getString("class"));
                System.out.println("Challenges: " + resultSet.getString("challenges"));
                System.out.println("Recommendations: " + resultSet.getString("recommendations"));
                System.out.println("Report Date: " + resultSet.getDate("report_date"));
                System.out.println("-----------");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Retrieval Error", "Could not retrieve past reports.");
        }
    }

    // Helper method to show alert messages
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
