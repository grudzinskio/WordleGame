package Wordle.Statistics;

import Wordle.DatabaseManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class HighScoreDAO {

    public static void saveHighScore(String username, int guesses, int timeSeconds) {
        String createTableQuery =
                "CREATE TABLE IF NOT EXISTS high_scores (" +
                "username TEXT, " +
                "guesses INTEGER, " +
                "time_seconds INTEGER)";
        String insertQuery = "INSERT INTO high_scores (username, guesses, time_seconds) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createTableQuery);
            try (PreparedStatement pstmt = conn.prepareStatement(insertQuery)) {
                pstmt.setString(1, username);
                pstmt.setInt(2, guesses);
                pstmt.setInt(3, timeSeconds);
                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<HighScore> getHighScores() {
        List<HighScore> scores = new ArrayList<>();
        String query = "SELECT username, guesses, time_seconds FROM high_scores ORDER BY time_seconds ASC, guesses ASC";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                String user = rs.getString("username");
                int guesses = rs.getInt("guesses");
                int timeSeconds = rs.getInt("time_seconds");
                scores.add(new HighScore(user, guesses, timeSeconds));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return scores;
    }
}