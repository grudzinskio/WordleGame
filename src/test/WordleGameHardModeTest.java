package test;

import Wordle.WordleGame;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.*;

public class WordleGameHardModeTest {

    private WordleGame game;

    @BeforeAll
    public static void initJavaFX() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.startup(latch::countDown); // Safely starts JavaFX
        latch.await(); // Wait for JavaFX to be ready
    }

    @BeforeEach
    public void setUp() {
        game = new WordleGame();
        game.labels = new Label[6][5];
        game.keyboardBox = new VBox(); // prevent NullPointerException
        game.lRow = 0;

        // Mock label grid
        for (int r = 0; r < 6; r++) {
            for (int c = 0; c < 5; c++) {
                game.labels[r][c] = new Label();
            }
        }

        game.referenceWord = "APPLE";
        WordleGame.isHardModeEnabled = true;
    }

    @Test
    public void testLettersAreHiddenInPreviousGuessInHardMode() {
        // Simulate first guess (row 0)
        game.giveFeedbackOnWord("ALIEN");

        // Simulate second row input before feedback
        game.lRow = 1;
        String guess = "ANGLE";
        for (int i = 0; i < 5; i++) {
            game.labels[1][i].setText(String.valueOf(guess.charAt(i)));
        }

        // Check second row is filled before feedback
        for (int c = 0; c < 5; c++) {
            assertFalse(game.labels[1][c].getText().isEmpty(), "Letter in row 1, col " + c + " should be visible before feedback");
        }

        // Submit second guess
        game.giveFeedbackOnWord(guess);

        // Check row 0 letters are hidden
        for (int c = 0; c < 5; c++) {
            assertEquals("", game.labels[0][c].getText(), "Letter in row 0, col " + c + " should be hidden");
        }

        // Check row 1 still has color styles
        for (int c = 0; c < 5; c++) {
            String style = game.labels[1][c].getStyle();
            assertTrue(style.contains("-fx-background-color"), "Row 1 label should have color styling");
        }
    }

    @Test
    public void testLettersShownWhenHardModeDisabled() {
        WordleGame.isHardModeEnabled = false;

        // Fill row 0
        game.lRow = 0;
        for (int i = 0; i < 5; i++) {
            game.labels[0][i].setText("ALIEN".substring(i, i + 1));
        }

        // Submit guess
        game.giveFeedbackOnWord("ALIEN");

        // Letters should be visible
        for (int c = 0; c < 5; c++) {
            assertFalse(game.labels[0][c].getText().isEmpty(), "Letter should be visible in normal mode");
        }
    }
}

