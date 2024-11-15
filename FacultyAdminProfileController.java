package com.example.limkokwingacademicreporting;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.control.Button;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FacultyAdminProfileController {

    @FXML
    private Label nameLabel;
    @FXML
    private Label nameDetailLabel;
    @FXML
    private Label usernameDetailLabel;
    @FXML
    private Label dobDetailLabel;
    @FXML
    private Label emailDetailLabel;
    @FXML
    private Label phoneDetailLabel;
    @FXML
    private ImageView profileImageView;
    @FXML
    private Button backButton;

    // Database credentials
    private final String url = "jdbc:mysql://localhost:3306/LimkokwingAcademicReporting";
    private final String user = "root";
    private final String password = "tumi";

    // Method to load profile data into the fields from the database
    public void loadProfileData(String email) {
        try (Connection connection = DriverManager.getConnection(url, user, password)) {
            String query = "SELECT name, username, dob, email, phone FROM faculty_admins WHERE email = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, email);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                String name = resultSet.getString("name");
                String username = resultSet.getString("username");
                String dob = resultSet.getString("dob");
                String emailFromDb = resultSet.getString("email");
                String phone = resultSet.getString("phone");

                // Set data to the labels
                nameLabel.setText(name);
                nameDetailLabel.setText(name);
                usernameDetailLabel.setText("@" + username);
                dobDetailLabel.setText(formatDate(dob));
                emailDetailLabel.setText(emailFromDb);
                phoneDetailLabel.setText(phone);
            } else {
                System.out.println("No profile data found for the provided email.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Helper method to format the date
    private String formatDate(String dob) {
        try {
            SimpleDateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date date = originalFormat.parse(dob);
            SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd");
            return targetFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
            return dob;
        }
    }

    // Method to handle the "Back" button click
    @FXML
    public void handleBack() {
        try {
            // Load the FacultyAdminDashboard FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("FacultyAdminDashboard.fxml"));
            Parent root = loader.load();

            // Get current stage and set the new scene
            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
