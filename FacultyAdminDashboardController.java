package com.example.limkokwingacademicreporting;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class FacultyAdminDashboardController {

    @FXML
    private Button viewProfileButton;
    @FXML
    private Button addLecturerButton;
    @FXML
    private Button addAcademicYearButton;
    @FXML
    public Button addModuleButton;
    @FXML
    private Button assignRolesButton;
    @FXML
    private Button viewAssignmentsButton;
    @FXML
    private Button logoutButton;
    private final String loggedInEmail = "sentle@gmail.com";  // Current logged-in email

    @FXML
    public void handleViewProfile() {
        // Passing the logged-in email to the profile screen
        loadSceneWithEmail("/com/example/limkokwingacademicreporting/FacultyAdminProfile.fxml", viewProfileButton, loggedInEmail);
    }

    @FXML
    public void handleAddLecturer() {
        loadScene("/com/example/limkokwingacademicreporting/addLecturer.fxml", addLecturerButton);
    }

    @FXML
    public void handleAddAcademicYear() {
        loadScene("/com/example/limkokwingacademicreporting/AcademicYearDashboard.fxml", addAcademicYearButton);
    }

    @FXML
    public void handleAddModule() {
        loadScene("/com/example/limkokwingacademicreporting/AddSemesterAndModule.fxml", addModuleButton);
    }

    @FXML
    public void handleAssignRoles() {
        loadScene("/com/example/limkokwingacademicreporting/assignLecturers.fxml", assignRolesButton);
    }

    @FXML
    public void handleViewAssignments() {
        System.out.println("View Assignments clicked");
        loadScene("/com/example/limkokwingacademicreporting/ViewLecturerAndModule.fxml", viewAssignmentsButton);
    }

    @FXML
    public void handleLogout() {
        loadScene("/com/example/limkokwingacademicreporting/FacultyAdminLogin.fxml", logoutButton);
    }

    private void loadScene(String fxmlPath, Button button) {
        loadSceneWithEmail(fxmlPath, button, loggedInEmail);
    }

    // Modified to accept email and pass it to the next screen's controller
    private void loadSceneWithEmail(String fxmlPath, Button button, String email) {
        try {
            // Get the FXML file URL
            URL fxmlUrl = getClass().getResource(fxmlPath);
            if (fxmlUrl == null) {
                System.err.println("FXML file not found at path: " + fxmlPath);
                showAlert("Error", "FXML file not found. Please check the file path.");
                return;
            }

            // Load the FXML file
            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent root = loader.load();

            // Pass the email to the next controller
            Object controller = loader.getController();
            if (controller instanceof FacultyAdminProfileController) {
                // Pass the logged-in email to FacultyAdminProfileController
                FacultyAdminProfileController profileController = (FacultyAdminProfileController) controller;
                profileController.loadProfileData(email); // Ensure this method is correctly defined in the profile controller
            }

            // Get the current stage from the button's scene and switch to the new scene
            Stage stage = (Stage) button.getScene().getWindow();
            if (stage == null) {
                System.err.println("Stage is null. Cannot switch scenes.");
                showAlert("Error", "Unable to determine the current window.");
                return;
            }

            // Create the new scene and show it
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Unable to load the requested screen. \nDetails: " + e.getMessage());
        }
    }

    // Method to display an error alert
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Getter method for logged-in email
    public String getLoggedInEmail() {
        return loggedInEmail;
    }
}
