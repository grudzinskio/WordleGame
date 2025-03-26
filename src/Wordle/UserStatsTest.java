package Wordle;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class UserStatsTest {
    @Test
    public void testAverageCalculation() {
        UserStats stats = UserStats.getInstance();

        stats.updateStats("hello"); // +1 guess
        stats.updateGamesCount();

        stats.updateStats("world"); // +1 guess
        stats.updateGamesCount();

        double expectedAvg = 1.0;
        assertEquals(expectedAvg, stats.getAverageGuesses(), 0.001);
    }

    @Test
    public void testGuessTracking() {
        UserStats stats = UserStats.getInstance();
        stats.updateStats("apple");
        int guessCount = stats.getGuessCount();
        assertTrue(guessCount > 0);
    }
}
