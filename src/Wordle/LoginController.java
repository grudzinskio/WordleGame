package Wordle;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * The LoginController class handles the login and signup logic for the Wordle game.
 * It manages user authentication and account creation, and stores account information in a text file.
 * 
 * @version 1.0
 * @created 14-Feb-2025 1:31:10 PM
 */
public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField accountTypeField;

    private static final String DATABASE_FILE = "src/Wordle/accounts.txt";

    /**
     * Handles the login button press.
     */
    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();
        String accountType = accountTypeField.getText().trim();

        // Validate that username and password meet requirements
        if (username.length() < 3) {
            showAlert(Alert.AlertType.ERROR, "Login Failed", "Username must be at least 3 characters long.");
            return;
        }
        if (password.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Login Failed", "Password cannot be empty.");
            return;
        }

        if (authenticateUser(username, password, accountType)) {
            showAlert(Alert.AlertType.INFORMATION, "Login Successful", "Welcome, " + username + "!");
            switchToWordleGame();
        } else {
            showAlert(Alert.AlertType.ERROR, "Login Failed", "Invalid username, password, or account type.");
        }
    }

    /**
     * Handles the signup button press.
     */
    @FXML
    private void handleSignUp() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();
        String accountType = accountTypeField.getText().trim();

        // Validate that username and password meet requirements
        if (username.length() < 3) {
            showAlert(Alert.AlertType.ERROR, "Sign Up Failed", "Username must be at least 3 characters long.");
            return;
        }
        if (password.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Sign Up Failed", "Password cannot be empty.");
            return;
        }

        if (accountType.equalsIgnoreCase("Admin") || accountType.equalsIgnoreCase("RegularUser")) {
            if (createAccount(username, password, accountType)) {
                showAlert(Alert.AlertType.INFORMATION, "Sign Up Successful", "Account created for " + username + "!");
            } else {
                showAlert(Alert.AlertType.ERROR, "Sign Up Failed", "Username already exists.");
            }
        } else {
            showAlert(Alert.AlertType.ERROR, "Sign Up Failed", "Invalid account type. Use 'Admin' or 'RegularUser'.");
        }
    }

    /**
     * Authenticates the user by checking the username, password, and account type against the database.
     * 
     * @param username The username entered by the user.
     * @param password The password entered by the user.
     * @param accountType The account type entered by the user.
     * @return True if the user is authenticated, false otherwise.
     */
    private boolean authenticateUser(String username, String password, String accountType) {
        try (BufferedReader reader = new BufferedReader(new FileReader(DATABASE_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(" ");
                if (parts.length == 3 && parts[0].equals(username) && parts[1].equals(password) && parts[2].equalsIgnoreCase(accountType)) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Creates a new account with the specified username, password, and account type.
     * 
     * @param username The username entered by the user.
     * @param password The password entered by the user.
     * @param accountType The account type entered by the user.
     * @return True if the account is created successfully, false otherwise.
     */
    private boolean createAccount(String username, String password, String accountType) {
        try (BufferedReader reader = new BufferedReader(new FileReader(DATABASE_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(" ");
                if (parts.length == 3 && parts[0].equals(username)) {
                    return false; // Username already exists
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(DATABASE_FILE, true))) {
            writer.write(username + " " + password + " " + accountType);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * Displays an alert with the specified type, title, and message.
     * 
     * @param alertType The type of the alert.
     * @param title The title of the alert.
     * @param message The message to be displayed in the alert.
     */
    private void showAlert(AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Switches the current scene to the Wordle game scene.
     */
    private void switchToWordleGame() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Wordle.fxml"));
            Pane root = loader.load();
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Wordle Game");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}