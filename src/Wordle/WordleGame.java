package Wordle;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import javafx.stage.Stage;

import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * The WordleGame class handles the game logic and user interactions for the Wordle game.
 * It manages the game state, validates user guesses, and provides feedback on the guessed words.
 * 
 * @version 1.0
 * @created 14-Feb-2025 1:31:10 PM
 */
public class WordleGame {

    @FXML
    private AnchorPane rootPane;

    @FXML
    public GridPane gridPane;

    @FXML
    public VBox keyboardBox;

    @FXML
    public Button enterButton;

    public Label guess_display;

    private List<Guess> guesses;
    private int maxGuesses;
    private String referenceWord = "ALLOW"; // Example reference word
    private UserStats userStats;
    private Vocabulary vocabulary;
    private Label[][] labels;
    private int lRow = 0;
    private int lCol = 0;

    // My implementation of handling input is keep a buffer of characters,
    // so we could manipulate this array for different keyboard input.
    private List<Character> characters = new ArrayList<>();

    public WordleGame() {
        vocabulary = new Vocabulary();
        vocabulary.loadWords("src/Wordle/wordle-official-1.txt"); // Load dictionary words
    }

    /**
     * Checks if the guessed word matches the reference word.
     * 
     * @param word The guessed word.
     * @return True if the guessed word matches the reference word, false otherwise.
     */
    public boolean checkWin(String word) {
        return word.equals(referenceWord);
    }

    /**
     * Initializes the game by populating the labels array and setting up the event
     * handlers for key presses and mouse clicks.
     */
    @FXML
    public void initialize() {
        userStats = UserStats.getInstance();
        userStats.updateGamesCount();

        populateLabels();

        // Ensure rootPane has focus to capture key events
        Platform.runLater(() -> rootPane.requestFocus());

        // Handle key presses from physical keyboard
        rootPane.setOnKeyPressed(this::handlePhysicalKeyboardInput);

        // Regain focus when clicking anywhere on the pane
        rootPane.setOnMouseClicked(event -> rootPane.requestFocus());
    }

    /**
     * Populates the labels array with Label instances from the gridPane.
     */
    private void populateLabels() {
        labels = new Label[6][5];
        for (Node node : gridPane.getChildren()) {
            if (node instanceof Label) {
                Integer row = GridPane.getRowIndex(node);
                Integer col = GridPane.getColumnIndex(node);
                if (row == null) row = 0;
                if (col == null) col = 0;
                labels[row][col] = (Label) node;
            }
        }
    }

    /**
     * Handles key presses from the physical keyboard.
     * 
     * @param event The KeyEvent representing the key press.
     */
    private void handlePhysicalKeyboardInput(KeyEvent event) {
        if (event.getCode() == javafx.scene.input.KeyCode.ENTER) {
            enterButton.fire(); // Simulate Enter button press
        } else if (event.getCode() == javafx.scene.input.KeyCode.BACK_SPACE) {
            handleBackButton(); // Delete letter
        } else {
            String keyText = event.getText().toUpperCase();
            if (keyText.matches("[A-Z]") && keyText.length() == 1) {
                handleLetterKey(keyText); // Add letter
            }
        }
    }

    /**
     * Handles button presses on the virtual keyboard.
     * 
     * @param event The ActionEvent representing the button press.
     */
    @FXML
    private void handleKeyboardButton(ActionEvent event) {
        Button b = (Button) event.getSource();
        String buttonText = b.getText();
        if (buttonText.equals("ENTER")) {
            handleEnterButton();
        } else if (buttonText.equals("BACK")) {
            handleBackButton();
        } else {
            handleLetterKey(buttonText);
        }
    }

    /**
     * Handles letter key presses.
     * 
     * @param text The letter key pressed.
     */
    private void handleLetterKey(String text) {
        if (lCol < 5) {
            labels[lRow][lCol].setText(text);
            characters.add(text.charAt(0)); // Store character
            lCol++;
        }
    }

    /**
     * Handles backspace key presses.
     */
    private void handleBackButton() {
        if (lCol > 0) {
            lCol--;
            labels[lRow][lCol].setText("");
            characters.remove(characters.size() - 1); // Remove last character
        }
    }

    /**
     * Handles enter key presses.
     */
    private void handleEnterButton() {
        if (lCol == 5) {
            String word = getWordFromLabel().toUpperCase(); // Ensure uppercase comparison
            if (isValidWord(word.toLowerCase())) {
                giveFeedbackOnWord(word);
                lRow++;
                lCol = 0;
                characters.clear(); // Clear for next word
            } else {
                System.out.println("Word not in list.");
            }
        }
    }

    /**
     * Retrieves the word from the labels in the current row.
     * 
     * @return The word formed by the letters in the current row.
     */
    private String getWordFromLabel() {
        StringBuilder word = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            word.append(labels[lRow][i].getText());
        }
        return word.toString();
    }

    /**
     * Checks if the specified word is valid by looking it up in the dictionary.
     * 
     * @param word The word to check.
     * @return True if the word is valid, false otherwise.
     */
    public boolean isValidWord(String word) {
        return vocabulary.contains(word);
    }

    /**
     * Provides feedback on the guessed word by comparing it to the reference word.
     * Each letter in the guessed word is checked against the corresponding letter
     * in the reference word and is styled accordingly:
     * - Green background if the letter is in the correct position.
     * - Yellow background if the letter is in the word but in the wrong position.
     * - Gray background if the letter is not in the word.
     *
     * @param word The guessed word to be checked against the reference word.
     */
    public void giveFeedbackOnWord(String word) {
        LetterStatus[] feedback = LetterStatus.getFeedback(word, referenceWord);
        // Apply styles based on feedback
        for (int i = 0; i < 5; i++) {
            LetterStatus.Status status = feedback[i].getStatus();
            switch (status) {
                case CORRECT:
                    labels[lRow][i].setStyle("-fx-background-color: green; -fx-text-fill: white;");
                    updateKeyboardButtonStyle(feedback[i].getLetter(), "-fx-background-color: green; -fx-text-fill: white;");
                    break;
                case MISPLACED:
                    labels[lRow][i].setStyle("-fx-background-color: yellow; -fx-text-fill: black;");
                    updateKeyboardButtonStyle(feedback[i].getLetter(), "-fx-background-color: yellow; -fx-text-fill: black;");
                    break;
                case INCORRECT:
                    labels[lRow][i].setStyle("-fx-background-color: gray; -fx-text-fill: white;");
                    updateKeyboardButtonStyle(feedback[i].getLetter(), "-fx-background-color: #4F4F4F; -fx-text-fill: white;");
                    break;
            }
        }
        // Update stats for the guessed word
        UserStats.getInstance().updateStats(word);

        if (checkWin(word)) {
            UserStatisticsDAO.saveUserStatistics(userStats);
            disableInput();
            System.out.println("You guessed the word correctly!");
        }
    }

    /**
     * Disables input from both the physical and virtual keyboards.
     */
    private void disableInput() {
        rootPane.setOnKeyPressed(null);
        keyboardBox.getChildren().forEach(node -> {
            if (node instanceof Button) {
                node.setDisable(true);
            }
        });
    }

    /**
     * Updates the style of the keyboard button corresponding to the given letter.
     * The style is updated to the given style if the button is not already green.
     *
     * @param letter The letter corresponding to the keyboard button.
     * @param style  The new style to be applied to the button.
     */
    private void updateKeyboardButtonStyle(char letter, String style) {
        keyboardBox.lookupAll(".key").stream()
                .map(node -> (Button) node) // Assuming all ".key" nodes are Buttons
                .filter(button -> button.getText().equalsIgnoreCase(String.valueOf(letter)))
                .findFirst()
                .ifPresent(button -> {
                    // Check if the button is already green
                    if (!button.getStyle().contains("green")) {
                        button.setStyle(style);
                    }
                });
    }

    /**
     * Created by Mathias G
     * This launches the secondary window pop-up to display a user's stats
     * @param actionEvent actionEvent is when the View Stats button is clicked
     * @throws IOException Exception thrown if fxml issues occur and file can't be loaded
     */
    public void viewStats(ActionEvent actionEvent) throws IOException {
        Parent stats = FXMLLoader.load(getClass().getResource("Stats_Display.fxml"));
        Scene scene = new Scene(stats);
        Stage stage = new Stage();

        stage.setScene(scene);
        stage.setTitle("User Stats");
        stage.setOnHidden(e -> requestFocusOnRootPane()); // Request focus on rootPane when window is closed
        stage.show();
    }

    public void adminSettings(ActionEvent actionEvent) throws IOException {
        Parent adminSetting = FXMLLoader.load(getClass().getResource("AdminSettings.fxml"));
        Scene scene = new Scene(adminSetting);
        Stage stage = new Stage();

        stage.setScene(scene);
        stage.setTitle("Admin Settings");
        stage.setOnHidden(e -> requestFocusOnRootPane()); // Request focus on rootPane when window is closed
        stage.show();
    }
    //this method to request focus on the rootPane
    private void requestFocusOnRootPane() {
        Platform.runLater(() -> rootPane.requestFocus());
    }
}