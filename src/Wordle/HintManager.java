package Wordle;

import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class HintManager {
    private String referenceWord;
    private final Label[][] labels;
    private final boolean[][] hintCells;
    private final Button hintButton;
    private int hintsUsed;
    private final int maxHints;

    public HintManager(String referenceWord, Label[][] labels, boolean[][] hintCells, Button hintButton, int maxHints) {
        this.referenceWord = referenceWord;
        this.labels = labels;
        this.hintCells = hintCells;
        this.hintButton = hintButton;
        this.hintsUsed = 0;
        this.maxHints = maxHints;
    }

    /**
     * Provides a hint for the current row by revealing one letter from the reference word.
     * The hint is displayed in the appropriate cell and styled to differentiate it from user input.
     *
     * @param lRow The current row index where the hint should be applied.
     */
    public void requestHint(int lRow) {
        if (hintsUsed >= maxHints) {
            System.out.println("No more hints available.");
            return;
        }

        int hintIndex = -1;
        for (int i = 0; i < 5; i++) {
            if (isLetterAlreadyRevealed(i, lRow)) {
                continue;
            }
            hintIndex = i;
            break;
        }

        if (hintIndex == -1) {
            System.out.println("No available cell for a hint in the current row.");
            return;
        }

        char hintLetter = referenceWord.charAt(hintIndex);
        labels[lRow][hintIndex].setText(String.valueOf(hintLetter));
        labels[lRow][hintIndex].setStyle("-fx-background-color: blue; -fx-text-fill: white;");
        hintCells[lRow][hintIndex] = true;
        hintsUsed++;

        if (hintsUsed >= maxHints) {
            hintButton.setDisable(true);
        }
    }

    /**
     * Checks if a letter in the specified column has already been revealed in the current or previous rows.
     *
     * @param col  The column index to check.
     * @param lRow The current row index.
     * @return True if the letter is already revealed, false otherwise.
     */
    private boolean isLetterAlreadyRevealed(int col, int lRow) {
        String correctLetter = String.valueOf(referenceWord.charAt(col));
        for (int r = 0; r < lRow; r++) {
            if (labels[r][col].getText().equalsIgnoreCase(correctLetter)) {
                return true;
            }
        }
        return labels[lRow][col].getText().equalsIgnoreCase(correctLetter);
    }

    /**
     * Gets the number of hints used so far in the current game.
     *
     * @return The number of hints used.
     */
    public int getHintsUsed() {
        return hintsUsed;
    }

    /**
     * Resets the hint-related state, including the number of hints used and the hint cells.
     * Re-enables the hint button for a new game.
     */
    public void resetHints() {
        hintsUsed = 0;
        hintButton.setDisable(false);
        for (int r = 0; r < hintCells.length; r++) {
            for (int c = 0; c < hintCells[r].length; c++) {
                hintCells[r][c] = false;
            }
        }
    }

    /**
     * Updates the reference word to a new word. This is used when the game restarts.
     *
     * @param newReferenceWord The new reference word to be used for hints.
     */
    public void updateReferenceWord(String newReferenceWord) {
        this.referenceWord = newReferenceWord;
    }
}