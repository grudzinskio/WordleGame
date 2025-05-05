package Wordle;

import Wordle.Controllers.StatDisplayController;
import Wordle.Statistics.HighScoreDAO;
import Wordle.Statistics.UserStatisticsDAO;
import Wordle.Statistics.UserStats;
import Wordle.HintManager;
import Wordle.FeedbackManager;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Side;
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
import java.util.*;

/**
 * The WordleGame class handles the game logic and user interactions for the Wordle game.
 * It manages the game state, validates user guesses, and provides feedback on the guessed words.
 *
 * @version 1.0
 * @created 14-Feb-2025 1:31:10 PM
 */
public class WordleGame {
    public CheckBox evilModeToggle;
    public static WordleGame currentGame;
    @FXML
    public Button leaderboardButton;
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

    @FXML
    private Button listHintButton;
    @FXML
    private ContextMenu suggestionPopup;   //THis is the popup where list of words are shown


    public static boolean isHardModeEnabled = false;

    public boolean evilWordleEnabled = false;

    private boolean firstGuessMade = false;

    private long startTime; // Timer start in milliseconds

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

    // Stores the feedback of each word.
    private final List<LetterStatus[]> feedbackHistory = new ArrayList<>();

    private HintManager hintManager;
    private FeedbackManager feedbackManager;

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
        // Set static instance reference.
        currentGame = this;
        userStats = UserStats.getInstance();
        if (userStats.getUsername().equals("Admin")) {
            adminSettingsButton.setVisible(true);
            adminSettingIcon.setVisible(true);
            adminStatsButton.setVisible(true);
            adminStatsIcon.setVisible(true);
        }
        if (hardModeToggle != null) {
            isHardModeEnabled = hardModeToggle.isSelected();
            hardModeToggle.setOnAction(e -> {
                if (!firstGuessMade) {
                    isHardModeEnabled = hardModeToggle.isSelected();
                    hardModeIndicator.setVisible(isHardModeEnabled);
                } else {
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
        suggestionPopup = new ContextMenu();
        suggestionPopup.setAutoHide(true);     // closes when user clicks elsewhere
        restartButton.setVisible(false);
        Platform.runLater(() -> rootPane.requestFocus());
        rootPane.setOnKeyPressed(this::handlePhysicalKeyboardInput);
        rootPane.setOnMouseClicked(event -> rootPane.requestFocus());

        // Start the timer when the game challenge is accepted/started.
        startTime = System.currentTimeMillis();
        hintManager = new HintManager(referenceWord, labels, hintCells, hintButton, maxHints);
        feedbackManager = new FeedbackManager(labels, keyboardBox);
    }
    public static WordleGame getCurrentGame() {
        return currentGame;
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
        if (suggestionPopup != null && suggestionPopup.isShowing()) {
            suggestionPopup.hide();
        }
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
        if (suggestionPopup != null && suggestionPopup.isShowing()) {
            suggestionPopup.hide();
        }

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
        boolean win = checkWin(word);
        feedbackManager.giveFeedbackOnWord(word, referenceWord, lRow, isHardModeEnabled, win, feedbackHistory);

        if (win) {
            long endTime = System.currentTimeMillis();
            long elapsedTimeSeconds = (endTime - startTime) / 1000;
            HighScoreDAO.saveHighScore(userStats.getUsername(), lRow + 1, (int) elapsedTimeSeconds);
            if (userStats != null) {
                UserStatisticsDAO.saveUserStatistics(userStats);
            }
            restartButton.setVisible(true);
            disableInput();
            System.out.println("You guessed correctly in " + elapsedTimeSeconds + " seconds and " + lRow + " guesses!");

            if (StatDisplayController.instance != null) {
                StatDisplayController.instance.refreshStats();
            }
        } else {
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
    public void handleRestart() {
        firstGuessMade = false;
        if (hardModeToggle != null) {
            hardModeToggle.setDisable(false);
            isHardModeEnabled = hardModeToggle.isSelected();
            hardModeIndicator.setVisible(isHardModeEnabled);
        }
        lRow = 0;
        lCol = 0;
        characters.clear();
        feedbackHistory.clear();
        hintsUsed = 0;
        if (suggestionPopup != null)  // just in case if pop up is open, hide it.
            suggestionPopup.hide();
        hintButton.setDisable(false);
        guess_display.setText("6");

        //restart evil wordle
        evilModeToggle.setSelected(false);
        evilModeToggle.setDisable(false);
        evilWordleEnabled = false;

        // Re-read hard mode toggle (if available) in case it was changed
        if (hardModeToggle != null) {
            isHardModeEnabled = hardModeToggle.isSelected();
        }
        if (hardModeIndicator != null) {
            hardModeIndicator.setVisible(isHardModeEnabled);
        }
        for (int r = 0; r < 6; r++) {
            for (int c = 0; c < 5; c++) {
                if (labels[r][c] != null) {
                    labels[r][c].setText("");
                    labels[r][c].setStyle("");
                    hintCells[r][c] = false;
                }
            }
        }
        keyboardBox.lookupAll(".key").forEach(node -> {
            if (node instanceof Button) {
                node.setStyle("");
                node.setDisable(false);
            }
        });

        //reset vocabulary file in case evil wordle was played previously
        vocabulary.vocabRestart();

        // Choose a new word and reset stats
        referenceWord = vocabulary.getRandomWord("").toUpperCase();
        System.out.println("New word: " + referenceWord);
        restartButton.setVisible(false);
        Platform.runLater(() -> rootPane.requestFocus());
        rootPane.setOnKeyPressed(this::handlePhysicalKeyboardInput);
        userStats.resetStats();

        // Restart the leaderboard time
        startTime = System.currentTimeMillis();
        hintManager.resetHints();
        hintManager.updateReferenceWord(referenceWord);
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
        hintManager.requestHint(lRow);
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

    /**
     * Created by Mathias G
     * This launches the secondary window pop-up to display a user's stats
     * @param actionEvent actionEvent is when the View Stats button is clicked
     * @throws IOException Exception thrown if fxml issues occur and file can't be loaded
     */
    public void viewLeaderboard(ActionEvent actionEvent) throws IOException {
        Parent stats = FXMLLoader.load(getClass().getResource("Views/HighScoreBoard.fxml"));
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

    /**
     * Returns up to <code>maxToReturn</code> words from the vocabulary that satisfy
     * all information revealed so far.
     */
    public List<String> getWordSuggestions(int maxToReturn) {

        Set<Character> greensAndYellows = new HashSet<>();
        Map<Integer,Character> green = new HashMap<>();
        Map<Character,Set<Integer>> yellow = new HashMap<>();


        for (LetterStatus[] row : feedbackHistory) {
            for (int pos = 0; pos < 5; pos++) {
                char ch = row[pos].getLetter();
                switch (row[pos].getStatus()) {
                    case CORRECT -> {
                        green.put(pos, ch);
                        greensAndYellows.add(ch);
                    }
                    case MISPLACED -> {
                        yellow.computeIfAbsent(ch,k -> new HashSet<>()).add(pos);
                        greensAndYellows.add(ch);
                    }
                }
            }
        }


        Set<Character> eliminated = new HashSet<>();
        for (LetterStatus[] row : feedbackHistory) {
            for (LetterStatus ls : row) {
                if (ls.getStatus() == LetterStatus.Status.INCORRECT &&
                  !greensAndYellows.contains(ls.getLetter())) {
                    eliminated.add(ls.getLetter());
                }
            }
        }

       //Filter the Vocabulary
        List<String> pool = Vocabulary.getVocabulary().getReferenceWords(); // or merged list
        List<String> out  = new ArrayList<>();

        for (String raw : pool) {
            String word = raw.toUpperCase();
            boolean bad = false;


            for (char e : eliminated)
                if (word.indexOf(e) != -1) { bad = true; break; }
            if (bad) continue;


            for (var g : green.entrySet())
                if (word.charAt(g.getKey()) != g.getValue()) { bad = true; break; }
            if (bad) continue;


            for (var y : yellow.entrySet()) {
                char ch = y.getKey();
                if (word.indexOf(ch) == -1) { bad = true; break; }
                for (int banned : y.getValue())
                    if (word.charAt(banned) == ch) { bad = true; break; }
                if (bad) break;
            }
            if (bad) continue;

            out.add(raw);
            if (out.size() == maxToReturn) break;
        }
        return out;
    }

    @FXML
    private void handleListHint(ActionEvent e) {
        if (lRow == 0) {                   // no guesses made mean so list is hidden
            suggestionPopup.hide();
            return;                        // simply do nothing
        }
        List<String> sugg = getWordSuggestions(5);

        if (sugg.isEmpty()) {
            suggestionPopup.hide();
            return;
        }
        // rebuild the popup menu
        suggestionPopup.getItems().clear();
        for (String w : sugg) {
            MenuItem mi = new MenuItem(w);
            suggestionPopup.getItems().add(mi);
        }
        suggestionPopup.show(listHintButton, Side.BOTTOM, 0, 0);
    }



}