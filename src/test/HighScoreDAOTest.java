package test;

import Wordle.DatabaseManager;
import Wordle.Statistics.HighScore;
import Wordle.Statistics.HighScoreDAO;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test file generated with Google Gemini 2.5 and manually corrected
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class HighScoreDAOTest {

    // No longer trying to use an in-memory database connection directly
    // due to static method limitations in DatabaseManager

    @BeforeAll
    static void setUpDatabase() throws SQLException {
        // Instead of setting up an in-memory DB, we ensure the actual DB table is clean
        System.out.println("Setting up test environment by clearing high_scores table in default DB.");
        try (Connection conn = DatabaseManager.getConnection()) {
            clearHighScoresTable(conn); // Clear the actual DB table before tests
        } catch (SQLException e) {
            System.err.println("Failed to clear database table before tests: " + e.getMessage());
            throw e;
        }
    }

    static void clearHighScoresTable(Connection conn) throws SQLException {
        // Ensure table exists before trying to delete/clear
        String createTableQuery =
                "CREATE TABLE IF NOT EXISTS high_scores (" +
                        "username TEXT, " +
                        "guesses INTEGER, " +
                        "time_seconds INTEGER)";
        String deleteQuery = "DELETE FROM high_scores";
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(createTableQuery); // Ensure table exists
            stmt.executeUpdate(deleteQuery);
            System.out.println("Cleared high_scores table.");
        }
    }

    @AfterAll
    static void tearDownDatabase() throws SQLException {
        // Optionally clear the actual table again after tests
        System.out.println("Tearing down test environment by clearing high_scores table.");
        try (Connection conn = DatabaseManager.getConnection()) {
            clearHighScoresTable(conn);
        } catch (SQLException e) {
            System.err.println("Failed to clear database table after tests: " + e.getMessage());
            throw e;
        }
    }

    @Test
    @Order(1)
    @DisplayName("Test saving a single high score")
    void testSaveSingleScore() {
        assertDoesNotThrow(() -> {
            HighScoreDAO.saveHighScore("Player1", 5, 120);
        }, "Saving score should not throw an exception");

        List<HighScore> scores = HighScoreDAO.getHighScores();
        assertNotNull(scores, "Score list should not be null");
        assertEquals(1, scores.size(), "Should retrieve 1 score after saving one");
        assertEquals("Player1", scores.get(0).getUsername());
        assertEquals(5, scores.get(0).getGuesses());
        assertEquals(120, scores.get(0).getTimeSeconds());
    }

    @Test
    @Order(2)
    @DisplayName("Test saving multiple scores and retrieving them in correct order")
    void testSaveAndRetrieveMultipleScoresOrdered() {
        // Save scores out of order (total 1 + 5 = 6 scores now)
        HighScoreDAO.saveHighScore("PlayerFastest", 2, 30);    // Fastest time, fewest guesses
        HighScoreDAO.saveHighScore("PlayerSlow", 6, 180);      // Slowest time
        HighScoreDAO.saveHighScore("PlayerMedGuess", 4, 90);   // Medium time/guess
        HighScoreDAO.saveHighScore("PlayerFastFewGuess", 3, 60); // Fast time, few guesses
        HighScoreDAO.saveHighScore("PlayerFastMoreGuess", 5, 60);// Fast time, more guesses

        // Retrieve scores
        List<HighScore> scores = HighScoreDAO.getHighScores();

        // Assertions
        assertNotNull(scores, "Score list should not be null");
        assertEquals(6, scores.size(), "Should retrieve 6 scores in total (1 from previous test + 5 from this)");

        // Check sorting order (Time ASC, then Guesses ASC)
        // Expected order:
        // 1. PlayerFastest (2 guesses, 30s)
        // 2. PlayerFastFewGuess (3 guesses, 60s)
        // 3. PlayerFastMoreGuess (5 guesses, 60s)
        // 4. PlayerMedGuess (4 guesses, 90s)
        // 5. Player1 (5 guesses, 120s) - from previous test
        // 6. PlayerSlow (6 guesses, 180s)

        assertEquals("PlayerFastest", scores.get(0).getUsername(), "Score 1 Username");
        assertEquals(30, scores.get(0).getTimeSeconds(), "Score 1 Time");
        assertEquals(2, scores.get(0).getGuesses(), "Score 1 Guesses");

        assertEquals("PlayerFastFewGuess", scores.get(1).getUsername(), "Score 2 Username");
        assertEquals(60, scores.get(1).getTimeSeconds(), "Score 2 Time");
        assertEquals(3, scores.get(1).getGuesses(), "Score 2 Guesses");

        assertEquals("PlayerFastMoreGuess", scores.get(2).getUsername(), "Score 3 Username");
        assertEquals(60, scores.get(2).getTimeSeconds(), "Score 3 Time");
        assertEquals(5, scores.get(2).getGuesses(), "Score 3 Guesses");

        assertEquals("PlayerMedGuess", scores.get(3).getUsername(), "Score 4 Username");
        assertEquals(90, scores.get(3).getTimeSeconds(), "Score 4 Time");
        assertEquals(4, scores.get(3).getGuesses(), "Score 4 Guesses");

        assertEquals("Player1", scores.get(4).getUsername(), "Score 5 Username (from test 1)"); // From testSaveSingleScore
        assertEquals(120, scores.get(4).getTimeSeconds(), "Score 5 Time");
        assertEquals(5, scores.get(4).getGuesses(), "Score 5 Guesses");

        assertEquals("PlayerSlow", scores.get(5).getUsername(), "Score 6 Username");
        assertEquals(180, scores.get(5).getTimeSeconds(), "Score 6 Time");
        assertEquals(6, scores.get(5).getGuesses(), "Score 6 Guesses");
    }

    @Test
    @Order(3)
    @DisplayName("Test retrieving scores when none exist")
    void testGetHighScores_Empty() throws SQLException {
        // Clear the table first using the actual connection
        try (Connection conn = DatabaseManager.getConnection()) {
            clearHighScoresTable(conn);
        }

        List<HighScore> scores = HighScoreDAO.getHighScores();
        assertNotNull(scores, "Score list should not be null even when empty");
        assertTrue(scores.isEmpty(), "Score list should be empty after clearing");
    }
}