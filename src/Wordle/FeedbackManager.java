package Wordle;

import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import java.util.List;

public class FeedbackManager {
    private final Label[][] labels;
    private final VBox keyboardBox;

    public FeedbackManager(Label[][] labels, VBox keyboardBox) {
        this.labels = labels;
        this.keyboardBox = keyboardBox;
    }

    /**
     * Provides feedback on the guessed word by comparing it to the reference word.
     * Updates the labels and keyboard styles based on the feedback.
     *
     * @param word The guessed word.
     * @param referenceWord The word to be guessed.
     * @param lRow The current row index where feedback is displayed.
     * @param isHardModeEnabled Whether hard mode is enabled.
     * @param win Whether the guessed word is correct.
     * @param feedbackHistory The history of feedback for previous guesses.
     */
    public void giveFeedbackOnWord(String word, String referenceWord, int lRow, boolean isHardModeEnabled, boolean win, List<LetterStatus[]> feedbackHistory) {
        LetterStatus[] feedback = LetterStatus.getFeedback(word, referenceWord);
        feedbackHistory.add(feedback);

        for (int i = 0; i < 5; i++) {
            Label label = labels[lRow][i];
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

            if (!isHardModeEnabled || win) {
                label.setText(String.valueOf(letter));
            } else {
                label.setText("");
            }
        }
    }

    private void updateKeyboardButtonStyle(char letter, String style) {
        if (keyboardBox == null) return;

        keyboardBox.lookupAll(".key").stream()
                .filter(node -> node instanceof javafx.scene.control.Button)
                .map(node -> (javafx.scene.control.Button) node)
                .filter(button -> button.getText().equalsIgnoreCase(String.valueOf(letter)))
                .findFirst()
                .ifPresent(button -> {
                    if (!button.getStyle().contains("green")) {
                        button.setStyle(style);
                    }
                });
    }
}