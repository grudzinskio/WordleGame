package Wordle;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

/**
 * @author gillj
 * @version 1.0
 * @created 14-Feb-2025 1:31:10 PM
 */
public class WordleGame {

    public VBox keyboardBox;
    @FXML
    private AnchorPane rootPane;

    @FXML
    public GridPane gridPane;

    private List<Guess> guesses;
    private int maxGuesses;
    public String referenceWord = "ALLOW"; // Example reference word
    public Label[][] labels;
    public int lRow = 0;
    private int lCol = 0;

    // My implementation of handling input is keep a buffer of characters,
    // so we could manipulate this array for different keyboard input.
    private List<Character> characters = new ArrayList<>();

    public WordleGame() {
    }

    public boolean checkWin() {
        return false;
    }

    /**
     * @param word
     */
    public boolean isValidWord(String word) {
        return true;
    }

    /**
     * @param guess
     */
    public Guess makeGuess(String guess) {
        return null;
    }


    /**
     * Initialize the game by populating the labels array and setting up the event
     * handlers for key presses and mouse clicks.
     */
    @FXML
    public void initialize() {
        populateLabels();

        // Ensure rootPane has focus to capture key events
        Platform.runLater(() -> rootPane.requestFocus());

        // Handle key presses from physical keyboard
        rootPane.setOnKeyPressed(this::handlePhysicalKeyboardInput);

        //Regain focus when clicking anywhere on the pane
        rootPane.setOnMouseClicked(event -> rootPane.requestFocus());
    }

    /*
        populate labels array, so we can use the instances to manipulate stuff.
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


    private void handlePhysicalKeyboardInput(KeyEvent event) {
        if (event.getCode() == javafx.scene.input.KeyCode.ENTER) {
            handleEnterButton(); // Submit guess
        } else if (event.getCode() == javafx.scene.input.KeyCode.BACK_SPACE) {
            handleBackButton(); // Delete letter
        } else {
            String keyText = event.getText().toUpperCase();
            if (keyText.matches("[A-Z]") && keyText.length() == 1) {
                handleLetterKey(keyText); // Add letter
            }
        }
    }


    /*
        Handles different keys on virtual keyboard of our GUI.
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


    /*
        Handle letters specifically
     */
    private void handleLetterKey(String text) {
        if (lCol < 5) {
            labels[lRow][lCol].setText(text);
            characters.add(text.charAt(0)); // Store character
            lCol++;
        }
    }

    private void handleBackButton() {
        if (lCol > 0) {
            lCol--;
            labels[lRow][lCol].setText("");
            characters.remove(characters.size() - 1); // Remove last character
        }
    }

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

    private String getWordFromLabel() {
        StringBuilder word = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            word.append(labels[lRow][i].getText());
        }
        return word.toString();
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
                    updateKeyboardButtonStyle(letter, "-fx-background-color: green; -fx-text-fill: white;");
                    break;
                case MISPLACED:
                    labels[lRow][i].setStyle("-fx-background-color: yellow; -fx-text-fill: black;");
                    updateKeyboardButtonStyle(letter, "-fx-background-color: yellow; -fx-text-fill: black;");
                    break;
                case INCORRECT:
                    labels[lRow][i].setStyle("-fx-background-color: gray; -fx-text-fill: white;");
                    updateKeyboardButtonStyle(letter, "-fx-background-color: black; -fx-text-fill: white;");
                    break;
            }
        }
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

}