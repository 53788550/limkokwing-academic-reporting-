package com.example.limkokwingacademicreporting;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader; // Add this import
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AddLecturerController {

    @FXML
    private TextField nameField;
    @FXML
    private ComboBox<String> facultyComboBox;  // ComboBox for faculty
    @FXML
    private ComboBox<String> courseComboBox;  // ComboBox for course
    @FXML
    private TextArea descriptionField;
    @FXML
    private Button submitButton;
    @FXML
    private Button cancelButton;  // Cancel Button

    // Map to store the faculties and their corresponding courses
    private Map<String, ObservableList<String>> facultyCoursesMap = new HashMap<>();

    @FXML
    public void initialize() {
        // Populating the faculty ComboBox with predefined faculty names
        facultyComboBox.setItems(FXCollections.observableArrayList(
                "Faculty of Communication Media & Broadcasting",
                "Faculty of Communication and Information Technology",
                "Faculty of Business Management and Globalization",
                "Faculty of Creativity in Tourism and Hospitality",
                "Faculty of Architecture and the Built Environment",
                "Faculty of Design and Innovation",
                "Faculty of Fashion and Lifestyle Design"
        ));

        // Initialize the facultyCoursesMap with courses for each faculty
        initializeFacultyCourses();

        // Add listener to update the courses when a faculty is selected
        facultyComboBox.setOnAction(event -> updateCourses());
    }

    private void initializeFacultyCourses() {
        // Mapping courses to each faculty
        facultyCoursesMap.put("Faculty of Communication Media & Broadcasting", FXCollections.observableArrayList(
                "BA (Hons) in Broadcasting and Journalism",
                "BA (Hons) in Professional Communication",
                "BA (Hons) in Digital Film and Television",
                "BA(Hons) in Events Management",
                "Associate Degree in Broadcasting (Radio and Television)",
                "Associate Degree in Journalism and Media",
                "Associate Degree in Public Relations",
                "Associate Degree in Film Production",
                "BA in Digital Film",
                "BA in Human Resource Management",
                "BBuss in Entrepreneurship"
        ));

        facultyCoursesMap.put("Faculty of Communication and Information Technology", FXCollections.observableArrayList(
                "BSc (Hons) in Information Technology",
                "BSc(Hons) in Business Information Technology",
                "BSc (Hons) in Software Engineering with Multimedia",
                "BSc(Hons) in Electronic Commerce",
                "Associate Degree in Multimedia and Software Engineering",
                "Associate Degree in Business Information Technology",
                "Associate Degree in Information Technology",
                "BSc in Business Information Technology"
        ));

        facultyCoursesMap.put("Faculty of Business Management and Globalization", FXCollections.observableArrayList(
                "BBus (Hons) in Entrepreneurship",
                "BA(Hons) in Human Resource Management",
                "BBus (Hons) International Business",
                "Associate Degree in Retail Management",
                "Associate Degree in Business Management",
                "Associate Degree in Marketing"
        ));

        facultyCoursesMap.put("Faculty of Creativity in Tourism and Hospitality", FXCollections.observableArrayList(
                "BA (Hons) in Tourism Management",
                "Associate Degree in Hotel Management",
                "Associate Degree in Events Management",
                "Associate Degree in International Tourism",
                "Associate in Tourism Management",
                "Diploma in Hotel Management",
                "BA in Tourism Management"
        ));

        facultyCoursesMap.put("Faculty of Architecture and the Built Environment", FXCollections.observableArrayList(
                "Bachelor of Interior Architecture",
                "Associate Degree in Architecture Technology",
                "Bachelor of Architectural Studies"
        ));

        facultyCoursesMap.put("Faculty of Design and Innovation", FXCollections.observableArrayList(
                "BA(Hons) in Fashion and Retailing",
                "BDesign in Professional Design",
                "Associate Degree in Fashion and Apparel Design",
                "Associate Degree in Graphic Design",
                "Associate Degree in Advertising",
                "Associate Degree in Creative Advertising",
                "BA in Fashion and Retailing"
        ));

        facultyCoursesMap.put("Faculty of Fashion and Lifestyle Design", FXCollections.observableArrayList(
                "BA(Hons) in Fashion and Retailing",
                "BDesign in Professional Design",
                "Associate Degree in Fashion and Apparel Design",
                "Associate Degree in Graphic Design",
                "Associate Degree in Advertising",
                "Associate Degree in Creative Advertising",
                "BA in Fashion and Retailing"
        ));
    }

    private void updateCourses() {
        String selectedFaculty = facultyComboBox.getValue();
        if (selectedFaculty != null && facultyCoursesMap.containsKey(selectedFaculty)) {
            // Update the courseComboBox with courses related to the selected faculty
            courseComboBox.setItems(facultyCoursesMap.get(selectedFaculty));
        } else {
            // If no faculty is selected or invalid, clear the courses dropdown
            courseComboBox.getItems().clear();
        }
    }

    @FXML
    private void handleSubmitButtonAction() {
        String name = nameField.getText();
        String faculty = facultyComboBox.getValue();
        String course = courseComboBox.getValue();
        String description = descriptionField.getText();

        if (name.isEmpty() || faculty == null || course == null || description.isEmpty()) {
            showAlert("Error", "Please fill in all fields.");
            return;
        }

        try {
            saveLecturerToDatabase(name, faculty, course, description);
            showAlert("Success", "Lecturer added successfully.");
            clearFields();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "Failed to add lecturer. Please try again.");
        }
    }

    private void saveLecturerToDatabase(String name, String faculty, String course, String description) throws SQLException {
        String url = "jdbc:mysql://localhost:3306/LimkokwingAcademicReporting";
        String user = "root";
        String password = "tumi";  // Replace with your database password if needed

        String sql = "INSERT INTO Lecturer (name, faculty, course, description) VALUES (?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, name);
            pstmt.setString(2, faculty);
            pstmt.setString(3, course);
            pstmt.setString(4, description);

            pstmt.executeUpdate();
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void clearFields() {
        nameField.clear();
        facultyComboBox.getSelectionModel().clearSelection();
        courseComboBox.getSelectionModel().clearSelection();
        descriptionField.clear();
    }

    @FXML
    private void handleCancelButtonAction() {
        try {
            // Load the AdminDashboard FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("FacultyAdminDashboard.fxml"));
            Parent root = loader.load();

            // Get the current stage and set the new scene
            Stage stage = (Stage) cancelButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to navigate back to AdminDashboard.");
        }
    }

    public Button getSubmitButton() {
        return submitButton;
    }

    public void setSubmitButton(Button submitButton) {
        this.submitButton = submitButton;
    }
}
