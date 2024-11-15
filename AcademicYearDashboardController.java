package com.example.limkokwingacademicreporting;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;

public class AcademicYearDashboardController {

    @FXML
    private DatePicker startDateField;

    @FXML
    private DatePicker endDateField;

    @FXML
    private ComboBox<Integer> numTermsComboBox;

    @FXML
    private TableView<Term> termsTable;

    @FXML
    private TableColumn<Term, String> termColumn;

    @FXML
    private TableColumn<Term, String> nameColumn;

    @FXML
    private TableColumn<Term, String> termStartDateColumn;

    @FXML
    private TableColumn<Term, String> termEndDateColumn;

    @FXML
    private ComboBox<String> periodStructureComboBox;

    @FXML
    private TextField ttDayField;

    // Data model for the TableView
    private ObservableList<Term> terms = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Initialize combo boxes and default values
        numTermsComboBox.getItems().addAll(1, 2, 3);
        periodStructureComboBox.getItems().addAll("10 Day 5 Period", "5 Day 5 Period");

        // Bind columns to Term properties
        termColumn.setCellValueFactory(cellData -> cellData.getValue().termProperty());
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        termStartDateColumn.setCellValueFactory(cellData -> cellData.getValue().startDateProperty());
        termEndDateColumn.setCellValueFactory(cellData -> cellData.getValue().endDateProperty());

        // Assign the observable list to the TableView
        termsTable.setItems(terms);

        // Add example data to the terms list, updated to use "Semester"
        terms.add(new Term("1", "Semester 1", "01/01/2023", "31/03/2023"));
        terms.add(new Term("2", "Semester 2", "01/04/2023", "30/06/2023"));
    }

    @FXML
    private void handleCreate() {
        // Validate input fields
        if (startDateField.getValue() == null || endDateField.getValue() == null || numTermsComboBox.getValue() == null || periodStructureComboBox.getValue() == null) {
            showAlert("Error", "Please fill in all required fields.");
            return;
        }

        // Get data from the fields
        LocalDate startDate = startDateField.getValue();
        LocalDate endDate = endDateField.getValue();
        Integer numTerms = numTermsComboBox.getValue();
        String periodStructure = periodStructureComboBox.getValue();

        // Logic to create new semesters based on the number of terms selected
        for (int i = 1; i <= numTerms; i++) {
            String semesterName = "Semester " + i;
            String semesterStartDate = startDate.toString();
            String semesterEndDate = endDate.toString();
            terms.add(new Term(String.valueOf(i), semesterName, semesterStartDate, semesterEndDate));

            // Optionally, you can adjust the start and end date for each semester
            startDate = startDate.plusMonths(3);
            endDate = endDate.plusMonths(3);

            // Save the data to the database
            saveTermToDatabase(i, semesterName, semesterStartDate, semesterEndDate);
        }

        // Clear the input fields after creation
        startDateField.setValue(null);
        endDateField.setValue(null);
        numTermsComboBox.setValue(null);
        periodStructureComboBox.setValue(null);
        ttDayField.clear();

        System.out.println("Academic Year Created with " + numTerms + " semesters.");
    }

    private void saveTermToDatabase(int termId, String semesterName, String startDate, String endDate) {
        String url = "jdbc:mysql://localhost:3306/LimkokwingAcademicReporting"; // Replace with your database details
        String user = "root"; // Replace with your username
        String password = "tumi"; // Replace with your password

        String query = "INSERT INTO terms (term_id, semester_name, start_date, end_date) VALUES (?, ?, ?, ?)";

        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, termId);
            statement.setString(2, semesterName);
            statement.setString(3, startDate);
            statement.setString(4, endDate);

            statement.executeUpdate();
            System.out.println("Term saved to the database.");

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "Failed to save term data to the database.");
        }
    }

    @FXML
    private void handleCancel() {
        System.out.println("Cancel button pressed");

        // Load the FacultyAdminDashboard view
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("FacultyAdminDashboard.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);

            // Get the current stage
            Stage stage = (Stage) startDateField.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load Faculty Admin Dashboard.");
        }

        // Clear the fields if cancel is pressed
        startDateField.setValue(null);
        endDateField.setValue(null);
        numTermsComboBox.setValue(null);
        periodStructureComboBox.setValue(null);
        ttDayField.clear();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Data model class for Term
    public static class Term {
        private final String term;
        private final String name;
        private final String startDate;
        private final String endDate;

        public Term(String term, String name, String startDate, String endDate) {
            this.term = term;
            this.name = name;
            this.startDate = startDate;
            this.endDate = endDate;
        }

        public javafx.beans.property.StringProperty termProperty() {
            return new javafx.beans.property.SimpleStringProperty(term);
        }

        public javafx.beans.property.StringProperty nameProperty() {
            return new javafx.beans.property.SimpleStringProperty(name);
        }

        public javafx.beans.property.StringProperty startDateProperty() {
            return new javafx.beans.property.SimpleStringProperty(startDate);
        }

        public javafx.beans.property.StringProperty endDateProperty() {
            return new javafx.beans.property.SimpleStringProperty(endDate);
        }
    }
}
