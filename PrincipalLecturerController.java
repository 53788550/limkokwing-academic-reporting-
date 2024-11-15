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

public class PrincipalLecturerController {

    private LoginApp loginApp;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private ImageView backgroundImageView;  // Background image view

    // Database connection parameters
    private static final String DB_URL = "jdbc:mysql://localhost:3306/LimkokwingAcademicReporting";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "tumi";

    @FXML
    private void initialize() {
        // Set background image for the login page
        Image backgroundImage = new Image(getClass().getResourceAsStream("/com/example/limkokwingacademicreporting/img.png"));  // Specify the correct path to image.png
        backgroundImageView.setImage(backgroundImage);
    }

    @FXML
    private void handleLogin() {
        String email = emailField.getText();
        String password = passwordField.getText();

        if (validateCredentials(email, password)) {
            showAlert(Alert.AlertType.INFORMATION, "Login Successful", "Welcome, Principal Lecturer!");

            // Store the email after a successful login
            storeEmail(email);

            // Load the PrincipalLecturerDashboard.fxml
            loadDashboard();

            // Log the successful login attempt to the Principal.txt file
            logLoginAttempt(email, true);
        } else {
            showAlert(Alert.AlertType.ERROR, "Login Failed", "Invalid email or password. Please try again.");
            logLoginAttempt(email, false);  // Log the failed attempt to the Principal.txt file
        }
    }

    @FXML
    private void handleRegister() {
        String email = emailField.getText();
        String password = passwordField.getText();

        if (email.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Registration Failed", "Email and password cannot be empty.");
            return;
        }

        if (registerPrincipalLecturer(email, password)) {
            showAlert(Alert.AlertType.INFORMATION, "Registration Successful", "You have been registered successfully!");
        } else {
            showAlert(Alert.AlertType.ERROR, "Registration Failed", "Registration failed. Email might already be in use.");
        }
    }

    @FXML
    private void handleBackToLogin() {
        if (loginApp != null) {
            loginApp.navigateTo("login.fxml");
        }
    }

    public void setLoginApp(LoginApp loginApp) {
        this.loginApp = loginApp;
    }

    // Method to validate credentials
    private boolean validateCredentials(String email, String password) {
        String hashedPassword = hashPassword(password);

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "SELECT * FROM principal_lecturers WHERE email = ? AND password = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, email);
            stmt.setString(2, hashedPassword);

            ResultSet resultSet = stmt.executeQuery();
            return resultSet.next();  // true if user exists with matching email and password

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Login Error", "Unable to verify credentials.");
        }
        return false;
    }

    // Method to register a principal lecturer
    private boolean registerPrincipalLecturer(String email, String password) {
        String hashedPassword = hashPassword(password);

        if (hashedPassword == null) {
            return false;
        }

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "INSERT INTO principal_lecturers (email, password) VALUES (?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, email);
            stmt.setString(2, hashedPassword);
            stmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Registration Error", "An error occurred during registration.");
            return false;
        }
    }

    // Method to show alert messages
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Method to load the PrincipalLecturerDashboard.fxml
    private void loadDashboard() {
        try {
            // Load the PrincipalLecturerDashboard FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("PrincipalLecturerDashboard.fxml"));
            Parent dashboardRoot = loader.load();

            // Set the scene for the dashboard
            Scene dashboardScene = new Scene(dashboardRoot);
            Stage currentStage = (Stage) emailField.getScene().getWindow();
            currentStage.setScene(dashboardScene);
            currentStage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load dashboard. Please try again.");
        }
    }

    // Method to store the email after a successful login
    private void storeEmail(String email) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("lastLoginEmail.txt"))) {
            writer.write(email);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to log the login attempt to Principal.txt
    private void logLoginAttempt(String email, boolean success) {
        String logFilePath = "Principal.txt";  // Changed log file name
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String logMessage = String.format("Timestamp: %s | Email: %s | Success: %b\n", timestamp, email, success);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(logFilePath, true))) {
            writer.write(logMessage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Helper method for password hashing
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
}
