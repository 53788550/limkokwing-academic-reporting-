package com.example.limkokwingacademicreporting;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;

public class LoginController {

    public Button facultyAdminButton;
    public Button lecturerButton;
    private LoginApp loginApp;

    public void setLoginApp(LoginApp loginApp) {

        this.loginApp = loginApp;
    }

    @FXML
    private void handleFacultyAdminLogin() {
        try {
            loginApp.navigateTo("FacultyAdminLogin.fxml");
        } catch (Exception e) {
            showError("Error loading Faculty Admin Login", e.getMessage());
        }
    }

    @FXML
    private void handleLecturerLogin() {
        try {
            loginApp.navigateTo("LecturerLogin.fxml");
        } catch (Exception e) {
            showError("Error loading Lecturer Login", e.getMessage());
        }
    }

    @FXML
    private void handlePrincipalLecturerLogin() {
        try {
            loginApp.navigateTo("PrincipalLecturerLogin.fxml");
        } catch (Exception e) {
            showError("Error loading Principal Lecturer Login", e.getMessage());
        }
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}
