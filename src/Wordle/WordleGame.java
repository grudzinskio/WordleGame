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

    @FXML
    public Button hintButton;

    public Label guess_display;

    @FXML
    private Button restartButton;

    public String referenceWord;
    private UserStats userStats;
    private final Vocabulary vocabulary = new Vocabulary();
    private StatDisplayController instance = new StatDisplayController();
    public Label[][] labels;
    public int lRow = 0;
    public int lCol = 0;

    // My implementation of handling input is keep a buffer of characters,
    // so we could manipulate this array for different keyboard input.
    private List<Character> characters = new ArrayList<>();
    public int maxHints = 2;
    public int hintsUsed = 0;
    public boolean[][] hintCells = new boolean[6][5]; // Tracks cells filled by hints.

    public WordleGame() {

        vocabulary = Vocabulary.getVocabulary();
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
        referenceWord = vocabulary.getRandomWord().toUpperCase();
        System.out.println("Word is: " + referenceWord);
        userStats = UserStats.getInstance();
        userStats.updateGamesCount();

        populateLabels();

        restartButton.setVisible(false);

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
        hintCells = new boolean[6][5]; // All false by default.
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
        // Skip over any cells that are already marked as hints.
        while (lCol < 5 && hintCells[lRow][lCol]) {
            lCol++;
        }
        if (lCol < 5) {
            labels[lRow][lCol].setText(text);
            characters.add(text.charAt(0)); // Store character
            lCol++;
            // Skip over any additional hint cells that follow.
            while (lCol < 5 && hintCells[lRow][lCol]) {
                lCol++;
            }
        }
    }

    /**
     * Handles backspace key presses.
     */
    private void handleBackButton() {
        if (lCol > 0) {
            lCol--;
            // Skip over any hint cells when moving back.
            while (lCol > 0 && hintCells[lRow][lCol]) {
                lCol--;
            }
            // Check if the current cell is a hint cell; if so, move back one more.
            if (hintCells[lRow][lCol]) {
                return;
            }
            labels[lRow][lCol].setText("");
            characters.remove(characters.size() - 1);
        }
    }

    /**
     * Handles enter key presses.
     */
    private void handleEnterButton() {
        if (lCol == 5) {
            String word = getWordFromLabel().toUpperCase(); // Ensure uppercase comparison
            if (isValidWord(word.toLowerCase())) {
                userStats.updateStats(word);
                giveFeedbackOnWord(word);
                lRow++;
                lCol = 0;
                characters.clear(); // Clear for next word

                // Update guess display
                guess_display.setText(String.valueOf(6 - lRow));

                // If this was the last row and game not won, count as loss
                if (lRow == 6 && !checkWin(word)) {
                    userStats.updateGamesCount();
                    restartButton.setVisible(true);
                    disableInput();
                    if (StatDisplayController.instance != null) {
                        StatDisplayController.instance.refreshStats();
                    }
                }
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

        if (checkWin(word)) {
            userStats.updateGamesCount();
            UserStatisticsDAO.saveUserStatistics(userStats);
            restartButton.setVisible(true);

            disableInput();
            System.out.println("You guessed the word correctly!");

            if (StatDisplayController.instance != null) {
                StatDisplayController.instance.refreshStats();
            }
        }
    }

    @FXML
    private void handleRestart() {
        lRow = 0;
        lCol = 0;
        characters.clear();

        // Reset guess display
        guess_display.setText("6");

        // Clear grid
        for (int r = 0; r < 6; r++) {
            for (int c = 0; c < 5; c++) {
                if (labels[r][c] != null) {
                    labels[r][c].setText("");
                    labels[r][c].setStyle(""); // reset styling
                }
            }
        }

        // Reset virtual keyboard
        keyboardBox.lookupAll(".key").forEach(node -> {
            if (node instanceof Button) {
                node.setStyle(""); // remove custom style
                node.setDisable(false); // re-enable key
            }
        });

        // Choose new word
        referenceWord = vocabulary.getRandomWord().toUpperCase();
        System.out.println("New word: " + referenceWord);

        // Hide restart button
        restartButton.setVisible(false);

        // Re-enable input
        Platform.runLater(() -> rootPane.requestFocus());
        rootPane.setOnKeyPressed(this::handlePhysicalKeyboardInput);
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
     * Checks if the correct letter for the given column is already revealed
     * in any previous row or in the current row.
     *
     * @param col The column index (0-4).
     * @return True if the correct letter at position col is already shown.
     */
    private boolean isLetterAlreadyRevealed(int col) {
        String correctLetter = String.valueOf(referenceWord.charAt(col));
        // Check all previous rows.
        for (int r = 0; r < lRow; r++) {
            if (labels[r][col].getText().equalsIgnoreCase(correctLetter)) {
                return true;
            }
        }
        // Also check the current row.
        if (labels[lRow][col].getText().equalsIgnoreCase(correctLetter)) {
            return true;
        }
        return false;
    }

    /**
     * Provides a hint for the current guess by revealing one letter from the secret word
     * in its correct position.
     * 
     * The method reveals one correct letter from {@code referenceWord} in its proper cell.
     * If the user has already used the maximum number of hints allowed, the system will
     * notify them that no more hints are available.
     * The hint is selected by scanning from left-to-right and skipping any cell
     * whose correct letter has already been revealed.
     * The revealed letter is styled (blue background) so the user can clearly
     * differentiate it from their normal guesses.
     * 
     */
    public void requestHint() {
        if (hintsUsed >= maxHints) {
            System.out.println("No more hints available.");
            return;
        }
        int hintIndex = -1;
        for (int i = 0; i < 5; i++) {
            // Skip cells if the correct letter for that column is already revealed.
            if (isLetterAlreadyRevealed(i)) {
                continue;
            }
            hintIndex = i;
            break;
        }
        if (hintIndex == -1) {
            System.out.println("No available cell for a hint in the current row.");
            return;
        }
        // Get the correct letter from the secret word for the chosen cell.
        char hintLetter = referenceWord.charAt(hintIndex);
        labels[lRow][hintIndex].setText(String.valueOf(hintLetter));
        labels[lRow][hintIndex].setStyle("-fx-background-color: blue; -fx-text-fill: white;");
        hintCells[lRow][hintIndex] = true; // Mark this cell as uneditable by hint.
        hintsUsed++;
        if (hintsUsed >= maxHints) {
            hintButton.setDisable(true);
        }
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

    public void adminStats(ActionEvent actionEvent) throws IOException {
        Parent adminSetting = FXMLLoader.load(getClass().getResource("AdminStats.fxml"));
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