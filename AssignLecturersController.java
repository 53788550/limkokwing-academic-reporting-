package com.example.limkokwingacademicreporting;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AssignLecturersController {

    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField emailField;
    @FXML private CheckBox principalLecturerCheck;
    @FXML private CheckBox yearLeaderCheck;
    @FXML private CheckBox tutorCheck;
    @FXML private CheckBox lecturerCheck;

    @FXML private RadioButton discreteMaths;
    @FXML private RadioButton cppProgramming;
    @FXML private RadioButton profComm;
    @FXML private RadioButton publicRelations;
    @FXML private RadioButton javaProgramming;

    @FXML private RadioButton bscsMy3S1;
    @FXML private RadioButton bscBitY3S1;
    @FXML private RadioButton bmy3S1;
    @FXML private RadioButton dfpy1S1;
    @FXML private RadioButton dfpy2S1;

    @FXML private Button saveButton;
    private ToggleGroup moduleGroup;
    private ToggleGroup classGroup;

    @FXML
    private void initialize() {
        moduleGroup = new ToggleGroup();
        discreteMaths.setToggleGroup(moduleGroup);
        cppProgramming.setToggleGroup(moduleGroup);
        profComm.setToggleGroup(moduleGroup);
        publicRelations.setToggleGroup(moduleGroup);
        javaProgramming.setToggleGroup(moduleGroup);

        classGroup = new ToggleGroup();
        bscsMy3S1.setToggleGroup(classGroup);
        bscBitY3S1.setToggleGroup(classGroup);
        bmy3S1.setToggleGroup(classGroup);
        dfpy1S1.setToggleGroup(classGroup);
        dfpy2S1.setToggleGroup(classGroup);
    }

    @FXML
    private void handleSave() {
        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();
        String email = emailField.getText();

        String roles = getSelectedRoles();
        String selectedModule = getSelectedModule();
        String selectedClass = getSelectedClass();

        saveToDatabase(firstName, lastName, email, roles, selectedModule, selectedClass);
    }

    private String getSelectedRoles() {
        StringBuilder roles = new StringBuilder();
        if (principalLecturerCheck.isSelected()) roles.append("Principal Lecturer, ");
        if (yearLeaderCheck.isSelected()) roles.append("Year Leader, ");
        if (tutorCheck.isSelected()) roles.append("Tutor, ");
        if (lecturerCheck.isSelected()) roles.append("Lecturer, ");
        if (roles.length() > 0) roles.setLength(roles.length() - 2);
        return roles.toString();
    }

    private String getSelectedModule() {
        RadioButton selectedModule = (RadioButton) moduleGroup.getSelectedToggle();
        return selectedModule != null ? selectedModule.getText() : "None";
    }

    private String getSelectedClass() {
        RadioButton selectedClass = (RadioButton) classGroup.getSelectedToggle();
        return selectedClass != null ? selectedClass.getText() : "None";
    }

    private void saveToDatabase(String firstName, String lastName, String email, String roles, String module, String classAssigned) {
        String url = "jdbc:mysql://localhost:3306/LimkokwingAcademicReporting";
        String user = "root";
        String password = "tumi";

        String insertQuery = "INSERT INTO assigned_lecturers (first_name, last_name, email, roles, modules_assigned, classes_assigned) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement stmt = conn.prepareStatement(insertQuery)) {

            stmt.setString(1, firstName);
            stmt.setString(2, lastName);
            stmt.setString(3, email);
            stmt.setString(4, roles);
            stmt.setString(5, module);
            stmt.setString(6, classAssigned);

            int rowsInserted = stmt.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("A new lecturer was assigned successfully!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCancel() {
        try {
            // Load the FacultyAdminDashboard.fxml file
            Parent dashboardView = FXMLLoader.load(getClass().getResource("FacultyAdminDashboard.fxml"));

            // Get the current stage (window)
            Stage currentStage = (Stage) saveButton.getScene().getWindow();

            // Set the scene with the FacultyAdminDashboard
            currentStage.setScene(new Scene(dashboardView));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
