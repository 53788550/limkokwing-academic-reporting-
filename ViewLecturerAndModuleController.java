package com.example.limkokwingacademicreporting;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ViewLecturerAndModuleController {

    @FXML private TableView<Lecturer> lecturerTable;
    @FXML private TableColumn<Lecturer, String> firstNameColumn;
    @FXML private TableColumn<Lecturer, String> lastNameColumn;
    @FXML private TableColumn<Lecturer, String> emailColumn;
    @FXML private TableColumn<Lecturer, String> rolesColumn;
    @FXML private TableColumn<Lecturer, String> modulesAssignedColumn;
    @FXML private TableColumn<Lecturer, String> classesAssignedColumn;
    @FXML private Button cancelButton; // Reference for the Cancel button

    private ObservableList<Lecturer> lecturerList = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        // Set up the table columns
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        rolesColumn.setCellValueFactory(new PropertyValueFactory<>("roles"));
        modulesAssignedColumn.setCellValueFactory(new PropertyValueFactory<>("modulesAssigned"));
        classesAssignedColumn.setCellValueFactory(new PropertyValueFactory<>("classesAssigned"));

        // Load data from the database
        loadLecturerData();
    }

    private void loadLecturerData() {
        String url = "jdbc:mysql://localhost:3306/LimkokwingAcademicReporting"; // Replace with your database name
        String user = "root"; // Replace with your MySQL username
        String password = "tumi"; // Replace with your MySQL password

        String query = "SELECT first_name, last_name, email, roles, modules_assigned, classes_assigned FROM assigned_lecturers";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                String firstName = rs.getString("first_name");
                String lastName = rs.getString("last_name");
                String email = rs.getString("email");
                String roles = rs.getString("roles");
                String modulesAssigned = rs.getString("modules_assigned");
                String classesAssigned = rs.getString("classes_assigned");

                lecturerList.add(new Lecturer(firstName, lastName, email, roles, modulesAssigned, classesAssigned));
            }

            lecturerTable.setItems(lecturerList);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Handle cancel button to go back to FacultyAdminDashboard
    @FXML
    private void handleCancel() throws IOException {
        // Load the FacultyAdminDashboard.fxml
        FXMLLoader loader = new FXMLLoader(getClass().getResource("FacultyAdminDashboard.fxml"));
        Stage stage = (Stage) cancelButton.getScene().getWindow(); // Get the current stage
        stage.setScene(new Scene(loader.load())); // Load the new scene and set it
        stage.show(); // Show the new stage
    }

    public static class Lecturer {
        private final String firstName;
        private final String lastName;
        private final String email;
        private final String roles;
        private final String modulesAssigned;
        private final String classesAssigned;

        public Lecturer(String firstName, String lastName, String email, String roles, String modulesAssigned, String classesAssigned) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.email = email;
            this.roles = roles;
            this.modulesAssigned = modulesAssigned;
            this.classesAssigned = classesAssigned;
        }

        public String getFirstName() { return firstName; }
        public String getLastName() { return lastName; }
        public String getEmail() { return email; }
        public String getRoles() { return roles; }
        public String getModulesAssigned() { return modulesAssigned; }
        public String getClassesAssigned() { return classesAssigned; }
    }
}
