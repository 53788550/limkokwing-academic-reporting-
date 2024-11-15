package com.example.limkokwingacademicreporting;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AddSemesterAndModuleController {

    @FXML
    private TableView<Module> moduleTable;
    @FXML
    private TableColumn<Module, ComboBox<String>> codeColumn;
    @FXML
    private TableColumn<Module, ComboBox<String>> nameColumn;
    @FXML
    private TableColumn<Module, ComboBox<String>> semesterColumn;
    @FXML
    private TableColumn<Module, Integer> occColumn;
    @FXML
    private TableColumn<Module, ImageView> deleteColumn;
    @FXML
    private TableColumn<Module, ImageView> confirmColumn;

    private ObservableList<Module> moduleList;
    private final Map<String, String> moduleMap = new HashMap<>();

    // Database connection details
    private final String dbUrl = "jdbc:mysql://localhost:3306/LimkokwingAcademicReporting";
    private final String dbUser = "root";
    private final String dbPassword = "tumi";

    public void initialize() {
        // Populate module map
        moduleMap.put("BIDM3110", "DISCRETE MATHS");
        moduleMap.put("DICP2112", "C++ PROGRAMMING I");
        moduleMap.put("BCPC1112", "PROFESSIONAL COMMUNICATION");
        moduleMap.put("BCPR1112", "PUBLIC RELATIONS");
        moduleMap.put("BIJP3212", "JAVA PROGRAMMING II");

        ObservableList<String> moduleCodes = FXCollections.observableArrayList(moduleMap.keySet());
        ObservableList<String> semesters = FXCollections.observableArrayList("S1", "S2");

        codeColumn.setCellValueFactory(new PropertyValueFactory<>("moduleCodeDropdown"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("moduleNameDropdown"));
        semesterColumn.setCellValueFactory(new PropertyValueFactory<>("semesterDropdown"));
        occColumn.setCellValueFactory(new PropertyValueFactory<>("occurrence"));
        deleteColumn.setCellValueFactory(new PropertyValueFactory<>("deleteIcon"));
        confirmColumn.setCellValueFactory(new PropertyValueFactory<>("confirmIcon"));

        loadModuleData(moduleCodes, semesters);
        moduleTable.setItems(moduleList);
    }

    private void loadModuleData(ObservableList<String> moduleCodes, ObservableList<String> semesters) {
        moduleList = FXCollections.observableArrayList(
                new Module(moduleCodes, semesters, 1),
                new Module(moduleCodes, semesters, 1),
                new Module(moduleCodes, semesters, 1)
        );
    }

    @FXML
    private void handleCancel() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("FacultyAdminDashboard.fxml"));
            Parent dashboardRoot = loader.load();
            Stage stage = (Stage) moduleTable.getScene().getWindow();
            stage.setScene(new Scene(dashboardRoot));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public class Module {
        private final ComboBox<String> moduleCodeDropdown;
        private final ComboBox<String> moduleNameDropdown;
        private final ComboBox<String> semesterDropdown;
        private final int occurrence;
        private final ImageView deleteIcon;
        private final ImageView confirmIcon;

        public Module(ObservableList<String> moduleCodes, ObservableList<String> semesters, int occurrence) {
            this.moduleCodeDropdown = new ComboBox<>(moduleCodes);
            this.moduleNameDropdown = new ComboBox<>();
            this.semesterDropdown = new ComboBox<>(semesters);
            this.occurrence = occurrence;
            this.deleteIcon = new ImageView(new Image(Objects.requireNonNull(getClass().getResource("/com/example/limkokwingacademicreporting/icons/deleteIcon.png")).toExternalForm()));
            this.confirmIcon = new ImageView(new Image(Objects.requireNonNull(getClass().getResource("/com/example/limkokwingacademicreporting/icons/confirmIcon.png")).toExternalForm()));

            deleteIcon.setFitHeight(20);
            deleteIcon.setFitWidth(20);
            confirmIcon.setFitHeight(20);
            confirmIcon.setFitWidth(20);

            // Set module name when module code is selected
            moduleCodeDropdown.setOnAction(_ -> {
                String selectedCode = moduleCodeDropdown.getSelectionModel().getSelectedItem();
                moduleNameDropdown.setValue(moduleMap.getOrDefault(selectedCode, ""));
            });

            // Handle click on delete icon only, preventing event propagation to other elements
            deleteIcon.setOnMouseClicked(event -> {
                event.consume();  // Prevent the click from propagating to the parent TableView or row
                moduleTable.getItems().remove(this);  // Remove this specific module from the TableView
                deleteModuleFromDatabase();  // Remove the module from the database
            });

            // Handle click on confirm icon
            confirmIcon.setOnMouseClicked(event -> saveModuleToDatabase());
        }

        private void saveModuleToDatabase() {
            String moduleCode = moduleCodeDropdown.getSelectionModel().getSelectedItem();
            String moduleName = moduleNameDropdown.getSelectionModel().getSelectedItem();
            String semester = semesterDropdown.getSelectionModel().getSelectedItem();

            try (Connection connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword)) {
                String sql = "INSERT INTO AddSemesterAndModule (module_code, module_name, semester, occurrence) VALUES (?, ?, ?, ?)";
                try (PreparedStatement statement = connection.prepareStatement(sql)) {
                    statement.setString(1, moduleCode);
                    statement.setString(2, moduleName);
                    statement.setString(3, semester);
                    statement.setInt(4, occurrence);
                    statement.executeUpdate();
                    System.out.println("Module saved successfully.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                System.err.println("Failed to save module to database.");
            }
        }

        private void deleteModuleFromDatabase() {
            String moduleCode = moduleCodeDropdown.getSelectionModel().getSelectedItem();

            try (Connection connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword)) {
                String sql = "DELETE FROM AddSemesterAndModule WHERE module_code = ?";
                try (PreparedStatement statement = connection.prepareStatement(sql)) {
                    statement.setString(1, moduleCode);
                    statement.executeUpdate();
                    System.out.println("Module deleted successfully.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                System.err.println("Failed to delete module from database.");
            }
        }

        public ComboBox<String> getModuleCodeDropdown() { return moduleCodeDropdown; }
        public ComboBox<String> getModuleNameDropdown() { return moduleNameDropdown; }
        public ComboBox<String> getSemesterDropdown() { return semesterDropdown; }
        public int getOccurrence() { return occurrence; }
        public ImageView getDeleteIcon() { return deleteIcon; }
        public ImageView getConfirmIcon() { return confirmIcon; }
    }
}
