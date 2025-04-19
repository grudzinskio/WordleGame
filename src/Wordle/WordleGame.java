package Wordle;

import Wordle.Controllers.StatDisplayController;
import Wordle.Statistics.UserStatisticsDAO;
import Wordle.Statistics.UserStats;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;

import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * The WordleGame class handles the game logic and user interactions for the Wordle game.
 * It manages the game state, validates user guesses, and provides feedback on the guessed words.
 *
 * @version 1.0
 * @created 14-Feb-2025 1:31:10 PM
 */
public class WordleGame {
    public CheckBox evilModeToggle;
    @FXML
    private ImageView adminSettingIcon;
    @FXML
    private ImageView adminStatsIcon;
    @FXML
    private Button adminSettingsButton;
    @FXML
    private Button adminStatsButton;
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

    @FXML
    private CheckBox hardModeToggle;

    @FXML
    private Label hardModeIndicator;

    public static boolean isHardModeEnabled = false;

    public boolean evilWordleEnabled = false;

    private boolean firstGuessMade = false;


    public String referenceWord;
    private UserStats userStats;
    public Vocabulary vocabulary = new Vocabulary();
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
        vocabulary.loadWords("repository/wordle-official-1.txt"); // Load dictionary words

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
        if (userStats.getUsername().equals("Admin")) {
            adminSettingsButton.setVisible(true);
            adminSettingIcon.setVisible(true);
            adminStatsButton.setVisible(true);
            adminStatsIcon.setVisible(true);
        }
        if (hardModeToggle != null) {
            isHardModeEnabled = hardModeToggle.isSelected();

            // Listen for user changes before first guess
            hardModeToggle.setOnAction(e -> {
                if (!firstGuessMade) {
                    isHardModeEnabled = hardModeToggle.isSelected();
                    hardModeIndicator.setVisible(isHardModeEnabled);
                } else {
                    // Lock the checkbox after the first guess
                    hardModeToggle.setDisable(true);
                }
            });

            hardModeIndicator.setVisible(isHardModeEnabled);
        }
        referenceWord = vocabulary.getRandomWord("").toUpperCase();
        if (firstGuessMade) {
            evilModeToggle.setDisable(true);
        } else {
            evilModeToggle.setOnAction(this::enableEvilMode);
        }

        System.out.println("Word is: " + referenceWord);
        userStats = UserStats.getInstance();

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
     * Enable Evil Mode Wordle
     * @param actionEvent when checkbox clicked
     */
    private void enableEvilMode(ActionEvent actionEvent) {
        if(firstGuessMade) {
            evilModeToggle.setDisable(true);
        } else {
            //Ensure that the user actually wants to play Evil Wordle and explain what it does
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setHeight(250);
            alert.setTitle("Evil Wordle Confirmation");
            alert.setHeaderText("Please confirm your choice");
            alert.setContentText("Evil Wordle will have the game change the target word based on your recent guesses" +
                    " to make it as difficult as possible alongside hints being disabled. " +
                    "\nWould you like to proceed?");
            Optional<ButtonType> confirm = alert.showAndWait();

            //enable evil wordle
            if (confirm.isPresent() && confirm.get() == ButtonType.OK) {
                evilModeToggle.setDisable(true);
                evilWordleEnabled = true;
                hardModeToggle.setDisable(true);
            } else {
                evilModeToggle.setSelected(false);
            }
            Platform.runLater(() -> rootPane.requestFocus());

            //eliminate hints
            hintsUsed = 2;
            referenceWord = "00000";
        }
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
                if (!firstGuessMade) {
                    firstGuessMade = true;
                    hardModeToggle.setDisable(true); // Lock the checkbox after first guess
                    evilModeToggle.setDisable(true);
                }
                userStats.updateStats(word);
                giveFeedbackOnWord(word);
                lRow++;
                lCol = 0;
                characters.clear(); // Clear for next word

                // Update guess display
                guess_display.setText(String.valueOf(6 - lRow));

                // If this was the last row and game not won, count as loss
                if (lRow == 6 && !checkWin(word)) {
                    UserStatisticsDAO.saveUserStatistics(userStats);
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
     * In Hard Mode, previous guesses display only color-coded feedback without showing letters.
     *
     * @param word The guessed word to be checked against the reference word.
     */
    public void giveFeedbackOnWord(String word) {
        //checks game status
        if(evilWordleEnabled) {
            //should get a new word based on most recent guess
            String tempWord = vocabulary.getRandomWord(word).toUpperCase();

            //returns empty string if there are no more possible answers
            if(!tempWord.isEmpty()) {

                // changes the reference word if it isn't empty
                referenceWord = tempWord;
            }
        }
        LetterStatus[] feedback = LetterStatus.getFeedback(word, referenceWord);
        int currentRow = lRow; // Save the row index being processed

        // Apply color feedback for current guess
        for (int i = 0; i < 5; i++) {
            Label label = labels[currentRow][i];
            LetterStatus.Status status = feedback[i].getStatus();
            char letter = feedback[i].getLetter();

            switch (status) {
                case CORRECT:
                    label.setStyle("-fx-background-color: green; -fx-text-fill: white;");
                    updateKeyboardButtonStyle(letter, "-fx-background-color: green; -fx-text-fill: white;");
                    break;
                case MISPLACED:
                    label.setStyle("-fx-background-color: yellow; -fx-text-fill: black;");
                    updateKeyboardButtonStyle(letter, "-fx-background-color: yellow; -fx-text-fill: black;");
                    break;
                case INCORRECT:
                    label.setStyle("-fx-background-color: gray; -fx-text-fill: white;");
                    updateKeyboardButtonStyle(letter, "-fx-background-color: #4F4F4F; -fx-text-fill: white;");
                    break;
            }

            // Set text only if not in hard mode, or if this is a win row
            if (!isHardModeEnabled || checkWin(word)) {
                label.setText(String.valueOf(letter));
            } else {
                label.setText(""); // Hide letters in hard mode (unless it's the win row)
            }
        }

        boolean win = checkWin(word);

        if (win) {
            if (userStats != null) {
                UserStatisticsDAO.saveUserStatistics(userStats);
            }
            restartButton.setVisible(true);
            disableInput();
            System.out.println("You guessed the word correctly!");

            if (StatDisplayController.instance != null) {
                StatDisplayController.instance.refreshStats();
            }
        } else {
            // In Hard Mode: hide the **previous row** letters (not current) after feedback is shown
            if (isHardModeEnabled && lRow > 0) {
                int prevRow = lRow - 1;
                Platform.runLater(() -> {
                    for (int i = 0; i < 5; i++) {
                        labels[prevRow][i].setText("");
                    }
                });
            }
        }
    }


    @FXML
    private void handleRestart() {
        firstGuessMade = false;
        if (hardModeToggle != null) {
            hardModeToggle.setDisable(false); // Re-enable toggle for new game
            isHardModeEnabled = hardModeToggle.isSelected(); // Refresh value
            hardModeIndicator.setVisible(isHardModeEnabled);
        }
        lRow = 0;
        lCol = 0;
        characters.clear();
        hintsUsed = 0;
        hintButton.setDisable(false); // Re-enable the hint button
        guess_display.setText("6");

        //restart evil wordle
        evilModeToggle.setSelected(false);
        evilModeToggle.setDisable(false);
        evilWordleEnabled = false;

        // Re-read hard mode toggle (if available) in case it was changed
        if (hardModeToggle != null) {
            isHardModeEnabled = hardModeToggle.isSelected();
        }

        // Update the hard mode indicator label visibility
        if (hardModeIndicator != null) {
            hardModeIndicator.setVisible(isHardModeEnabled);
        }

        // Clear all grid labels and reset hint cell tracking
        for (int r = 0; r < 6; r++) {
            for (int c = 0; c < 5; c++) {
                if (labels[r][c] != null) {
                    labels[r][c].setText("");      // Clear letter
                    labels[r][c].setStyle("");     // Clear background color
                    hintCells[r][c] = false;       // Reset hint markers
                }
            }
        }

        // Reset keyboard button styles and re-enable them
        keyboardBox.lookupAll(".key").forEach(node -> {
            if (node instanceof Button) {
                node.setStyle("");          // Clear any coloring
                node.setDisable(false);     // Enable key again
            }
        });

        //reset vocabulary file in case evil wordle was played previously
        vocabulary.vocabRestart();

        // Choose a new word and reset stats
        referenceWord = vocabulary.getRandomWord("").toUpperCase();
        System.out.println("New word: " + referenceWord);

        // Hide restart button again
        restartButton.setVisible(false);

        // Re-enable keyboard and focus handling
        Platform.runLater(() -> rootPane.requestFocus());
        rootPane.setOnKeyPressed(this::handlePhysicalKeyboardInput);

        // Reset user stats for the new game
        userStats.resetStats();
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
        if (keyboardBox == null) return; // Safe guard for test environments

        keyboardBox.lookupAll(".key").stream()
                .filter(node -> node instanceof Button)
                .map(node -> (Button) node)
                .filter(button -> button.getText().equalsIgnoreCase(String.valueOf(letter)))
                .findFirst()
                .ifPresent(button -> {
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
		Parent stats = FXMLLoader.load(getClass().getResource("Views/Stats_Display.fxml"));
		Scene scene = new Scene(stats);
		Stage stage = new Stage();

        stage.setScene(scene);
        stage.setTitle("User Stats");
        stage.setOnHidden(e -> requestFocusOnRootPane()); // Request focus on rootPane when window is closed
        stage.show();
    }

    public void adminSettings(ActionEvent actionEvent) throws IOException {
        Parent adminSetting = FXMLLoader.load(getClass().getResource("Views/AdminSettings.fxml"));
        Scene scene = new Scene(adminSetting);
        Stage stage = new Stage();

        stage.setScene(scene);
        stage.setTitle("Admin Settings");
        stage.setOnHidden(e -> requestFocusOnRootPane()); // Request focus on rootPane when window is closed
        stage.show();
    }

    public void adminStats(ActionEvent actionEvent) throws IOException {
        Parent adminSetting = FXMLLoader.load(getClass().getResource("Views/AdminStats.fxml"));
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