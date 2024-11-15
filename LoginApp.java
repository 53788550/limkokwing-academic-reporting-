package com.example.limkokwingacademicreporting;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;

public class LoginApp extends Application {

    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        showMainLoginPage();
    }

    private void showMainLoginPage() {
        navigateTo("login.fxml"); // Ensure this is the correct FXML for the initial screen
    }

    public void navigateTo(String fxmlFile) {
        try {
            // Load the specified FXML file for navigation
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Scene scene = new Scene(loader.load());

            // Apply the CSS file for styling, if available
            if (getClass().getResource("login-style.css") != null) {
                scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("login-style.css")).toExternalForm());
            }

            // Set the new scene on the primary stage
            primaryStage.setScene(scene);
            primaryStage.setTitle("Limkokwing University Login System");
            primaryStage.show();

            // Get the controller of the loaded FXML
            Object controller = loader.getController();

            // Pass the LoginApp instance to each controller, based on its type
            if (controller instanceof LoginController) {
                ((LoginController) controller).setLoginApp(this);
            } else if (controller instanceof FacultyAdminController) {
                ((FacultyAdminController) controller).setLoginApp(this);
            } else if (controller instanceof LecturerController) {
                ((LecturerController) controller).setLoginApp(this);
            } else if (controller instanceof PrincipalLecturerController) {
                ((PrincipalLecturerController) controller).setLoginApp(this);
            }

        } catch (Exception e) {
            e.printStackTrace();
            showErrorAlert("Error loading FXML", "An error occurred while loading the FXML file: " + fxmlFile);
        }
    }

    private void showErrorAlert(String title, String message) {
        // Helper method to show error alerts
        System.out.println(title + ": " + message);
        // Optionally, use a JavaFX Alert to display the error message
    }

    public static void main(String[] args) {
        launch(args);
    }
}
