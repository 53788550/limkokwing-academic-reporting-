package com.example.limkokwingacademicreporting;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FacultyAdminController {

    private LoginApp loginApp;

    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private ImageView backgroundImageView;

    // Database credentials
    private final String url = "jdbc:mysql://localhost:3306/LimkokwingAcademicReporting";
    private final String user = "root";
    private final String password = "tumi";

    public void initialize() {
        // Set background image with reduced opacity
        Image backgroundImage = new Image(getClass().getResource("/com/example/limkokwingacademicreporting/img.png").toExternalForm());
        backgroundImageView.setImage(backgroundImage);
        backgroundImageView.setOpacity(0.4);
    }

    // Method to auto-fill the email field with the last email used
    public void autoFillEmail() {
        String lastEmail = retrieveEmail();
        if (lastEmail != null) {
            emailField.setText(lastEmail);
        }
    }

    @FXML
    private void handleLogin() {
        String email = emailField.getText();
        String passwordText = passwordField.getText();

        // Validate inputs
        if (email.isEmpty() || passwordText.isEmpty()) {
            showAlert("Login Failed", "Please fill in both fields.");
            logLoginAttempt(email, false);
            return;
        }

        // Authenticate user
        if (authenticateUser(email, passwordText)) {
            showAlert("Login Successful", "Welcome!");
            storeEmail(email);
            loadDashboard();
            logLoginAttempt(email, true);
        } else {
            showAlert("Login Failed", "Invalid email or password.");
            logLoginAttempt(email, false);
        }
    }

    @FXML
    private void handleRegister() {
        try {
            // Load registration screen
            FXMLLoader loader = new FXMLLoader(getClass().getResource("FacultyAdminRegister.fxml"));
            Parent registerRoot = loader.load();
            Scene registerScene = new Scene(registerRoot);

            // Get current stage and set the registration scene
            Stage currentStage = (Stage) emailField.getScene().getWindow();
            currentStage.setScene(registerScene);
            currentStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Unable to load the registration screen.");
        }
    }

    private boolean authenticateUser(String email, String password) {
        try (Connection connection = DriverManager.getConnection(url, user, this.password)) {
            String query = "SELECT * FROM faculty_admins WHERE email = ? AND password = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, email);
            preparedStatement.setString(2, hashPassword(password));
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Database connection failed. Please try again.");
            return false;
        }
    }

    private void loadDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("FacultyAdminDashboard.fxml"));
            Parent dashboardRoot = loader.load();
            Scene dashboardScene = new Scene(dashboardRoot);
            Stage currentStage = (Stage) emailField.getScene().getWindow();
            currentStage.setScene(dashboardScene);
            currentStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Unable to load the dashboard.");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void logLoginAttempt(String email, boolean success) {
        String logFilePath = "LoginLog.txt";
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String logMessage = String.format("Timestamp: %s | Email: %s | Success: %b\n", timestamp, email, success);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(logFilePath, true))) {
            writer.write(logMessage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void storeEmail(String email) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("lastLoginEmail.txt"))) {
            writer.write(email);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String retrieveEmail() {
        try (BufferedReader reader = new BufferedReader(new FileReader("lastLoginEmail.txt"))) {
            return reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();

            for (byte b : hashedBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    @FXML
    private void handleBackToLogin() {
        if (loginApp == null) {
            System.out.println("Error: LoginApp instance is null.");
            showAlert("Navigation Error", "Unable to navigate back to the login page.");
            return;
        }
        loginApp.navigateTo("login.fxml");
    }

    public void setLoginApp(LoginApp loginApp) {
        this.loginApp = loginApp;
        System.out.println("LoginApp instance set in FacultyAdminController");
    }

    public void autoFillEmail(String email) {
    }
}
