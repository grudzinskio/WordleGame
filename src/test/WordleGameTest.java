package test;

import static org.junit.jupiter.api.Assertions.*;

import Wordle.WordleGame;
import javafx.embed.swing.JFXPanel;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

// These tests were generated using ChatGPT and revised manually
@Nested
class WordleGameTest {

    private WordleGame game;
    static {
        // This will initialize the JavaFX toolkit.
        new JFXPanel();
    }
    @BeforeEach
    public void setUp() {
        game = new WordleGame();
        // Simulate a 6x5 grid of labels.
        game.labels = new Label[6][5];
        game.hintCells = new boolean[6][5];
        for (int row = 0; row < 6; row++) {
            for (int col = 0; col < 5; col++) {
                game.labels[row][col] = new Label();
                // Initialize all cells with empty text.
                game.labels[row][col].setText("");
                game.hintCells[row][col] = false;
            }
        }
        // Reset row and column indices.
        game.lRow = 0;
        game.lCol = 0;
        // Reset hint usage.
        game.hintsUsed = 0;
        game.maxHints = 2;
        // Create a dummy hint button.
        game.hintButton = new Button();
        // Set the secret/reference word to "ALLOW".
        game.referenceWord = "ALLOW";
    }

    @Test
    public void testRequestHintRevealsCorrectLetter() {
        // In an empty current row, we expect the hint to reveal the first letter 'A'.
        game.lRow = 0;
        game.lCol = 0;
        // Ensure the current row cells are empty.
        for (int i = 0; i < 5; i++) {
            game.labels[0][i].setText("");
        }
        game.hintsUsed = 0;

        game.requestHint();

        // Expect column 0 to have the letter "A" (from "ALLOW").
        String revealed = game.labels[0][0].getText();
        assertEquals("A", revealed.toUpperCase());
        // The cell should be marked as a hint cell.
        assertTrue(game.hintCells[0][0]);
        // Hint count should be incremented.
        assertEquals(1, game.hintsUsed);
    }

    @Test
    public void testRequestHintSkipsAlreadyRevealedInPreviousRow() {
        // Simulate a previous row (row 0) containing the correct letter at column 0.
        game.lRow = 1;
        game.labels[0][0].setText("A"); // 'A' revealed in row 0, col 0.
        // Ensure current row (row 1) is empty.
        for (int col = 0; col < 5; col++) {
            game.labels[1][col].setText("");
            game.hintCells[1][col] = false;
        }
        game.lCol = 0;
        game.hintsUsed = 0;

        game.requestHint();

        // Since column 0 is already revealed (in a previous row), the hint should be placed
        // in the next available column—in this case, column 1, which should reveal 'L'.
        String hinted = game.labels[1][1].getText();
        assertEquals("L", hinted.toUpperCase());
        // Ensure the cell is now marked as a hint cell.
        assertTrue(game.hintCells[1][1]);
    }

    @Test
    public void testRequestHintDisablesHintButtonWhenMaxReached() {
        // Set hintsUsed to one less than the maximum.
        game.hintsUsed = 1;
        game.lRow = 0;
        game.lCol = 0;
        // Clear current row.
        for (int i = 0; i < 5; i++) {
            game.labels[0][i].setText("");
            game.hintCells[0][i] = false;
        }
        game.requestHint();
        // Now hintsUsed should equal maxHints (2) and hintButton should be disabled.
        assertEquals(2, game.hintsUsed);
        assertTrue(game.hintButton.isDisabled());
    }

    @Test
    public void testRequestHintNoAvailableCell() {
        // Simulate a current row where all cells already display the correct letters.
        game.lRow = 0;
        game.lCol = 0;
        for (int i = 0; i < 5; i++) {
            String correctLetter = String.valueOf(game.referenceWord.charAt(i));
            game.labels[0][i].setText(correctLetter);
            game.hintCells[0][i] = true;
        }
        int usedHintsBefore = game.hintsUsed;
        game.requestHint();
        // Since every cell is already correctly revealed as hint cells,
        // hintsUsed should remain unchanged.
        assertEquals(usedHintsBefore, game.hintsUsed);
    }
}