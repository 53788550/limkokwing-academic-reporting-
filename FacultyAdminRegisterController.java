package com.example.limkokwingacademicreporting;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class FacultyAdminRegisterController {

    // Database connection parameters
    private static final String DB_URL = "jdbc:mysql://localhost:3306/LimkokwingAcademicReporting";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "tumi";  // Update with your database password

    private LoginApp loginApp;  // Reference to the LoginApp

    @FXML
    private TextField nameField;

    @FXML
    private TextField usernameField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField phoneField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private DatePicker dobField;

    @FXML
    private StackPane stackPane;

    @FXML
    private ImageView backgroundImageView;  // ImageView for the background

    @FXML
    private void initialize() {
        // Set the background image
        Image backgroundImage = new Image(Objects.requireNonNull(getClass().getResource("/com/example/limkokwingacademicreporting/img.png")).toExternalForm());
        backgroundImageView.setImage(backgroundImage);

        // Optionally, adjust the opacity of the background image to match your design
        backgroundImageView.setOpacity(0.5); // Adjust opacity for the background
    }

    @FXML
    private void handleRegister() {
        try {
            String name = nameField.getText();
            String username = usernameField.getText();
            String email = emailField.getText();
            String phone = phoneField.getText();
            String password = passwordField.getText();
            LocalDateTime dob = dobField.getValue() != null ? dobField.getValue().atStartOfDay() : null;

            if (name.isEmpty() || username.isEmpty() || email.isEmpty() || phone.isEmpty() || password.isEmpty() || dob == null) {
                showAlert(Alert.AlertType.WARNING, "Registration Failed", "Please fill in all fields.");
                return;
            }

            if (registerFacultyAdmin(name, username, email, phone, password, dob)) {
                showAlert(Alert.AlertType.INFORMATION, "Registration Successful", "You have been registered successfully!");
                logRegistrationAttempt(email, true);  // Log the successful registration
                handleBackToLogin();  // Navigate back to login
            } else {
                showAlert(Alert.AlertType.ERROR, "Registration Failed", "Registration failed. Email might already be in use.");
            }
        } catch (Exception e) {
            e.printStackTrace(); // Print stack trace to understand the underlying cause
            showAlert(Alert.AlertType.ERROR, "Registration Error", "An unexpected error occurred.");
        }
    }

    // Method to register a faculty admin
    private boolean registerFacultyAdmin(String name, String username, String email, String phone, String password, LocalDateTime dob) {
        String hashedPassword = hashPassword(password);

        if (hashedPassword == null) {
            return false;
        }

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            // Check if the email already exists
            String checkQuery = "SELECT * FROM faculty_admins WHERE email = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
            checkStmt.setString(1, email);
            ResultSet resultSet = checkStmt.executeQuery();
            if (resultSet.next()) {
                showAlert(Alert.AlertType.ERROR, "Registration Failed", "Email already exists.");
                return false;
            }

            // Insert the new faculty admin into the database
            String insertQuery = "INSERT INTO faculty_admins (name, username, email, phone, password, dob) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement insertStmt = conn.prepareStatement(insertQuery);
            insertStmt.setString(1, name);
            insertStmt.setString(2, username);
            insertStmt.setString(3, email);
            insertStmt.setString(4, phone);
            insertStmt.setString(5, hashedPassword);
            insertStmt.setTimestamp(6, Timestamp.valueOf(dob)); // Save the date of birth as timestamp
            insertStmt.executeUpdate();
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

    // Method to log the registration attempt
    private void logRegistrationAttempt(String email, boolean success) {
        String logFilePath = "FacultyAdminRegistration.txt";  // Changed log file to FacultyAdminRegistration.txt
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd "));
        String logMessage = String.format("Timestamp: %s | Email: %s | Success: %b\n", timestamp, email, success);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(logFilePath, true))) {
            writer.write(logMessage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void handleBackToLogin() {
        try {
            // Navigate to FacultyAdminLogin.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("FacultyAdminLogin.fxml"));
            Parent root = loader.load();

            FacultyAdminController loginController = loader.getController();
            loginController.autoFillEmail(emailField.getText());  // Pass the email to the login page

            // Get the current stage from the event (this method works if you have access to the event)
            Stage stage = (Stage) nameField.getScene().getWindow();  // You can use any control's scene, here we use nameField
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to handle navigation back to the login page and auto-fill the email

    // Setter method to set the LoginApp reference
    public void setLoginApp(LoginApp loginApp) {
        this.loginApp = loginApp;
    }
}
