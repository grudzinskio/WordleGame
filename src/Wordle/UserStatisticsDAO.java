package Wordle;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

public class UserStatisticsDAO {
    public static void saveUserStatistics(UserStats stats) {
        UserStats existingStats = getUserStatistics(stats.getUsername());
        if (existingStats != null) {
            stats.incrementGameCount(existingStats.getGamesCount());
            stats.updateLetterFrequency(new JSONObject(existingStats.getLetterFrequencies()).toString());
            stats.updateWordFrequency(new JSONObject(existingStats.getWordFrequencies()).toString());
            stats.guessCount += existingStats.getGuessCount();
        }

        double averageGuesses = stats.getGamesCount() > 0 ? (double) stats.getGuessCount() / stats.getGamesCount() : 0;

        String query = "INSERT INTO user_statistics (username, games_played, average_guesses, letter_frequencies, word_frequencies) " +
                "VALUES (?, ?, ?, ?, ?) " +
                "ON CONFLICT(username) DO UPDATE SET games_played = excluded.games_played, " +
                "average_guesses = excluded.average_guesses, " +
                "letter_frequencies = excluded.letter_frequencies, " +
                "word_frequencies = excluded.word_frequencies;";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, stats.getUsername());
            pstmt.setInt(2, stats.getGamesCount());
            pstmt.setDouble(3, averageGuesses);
            pstmt.setString(4, new JSONObject(stats.getLetterFrequencies()).toString());
            pstmt.setString(5, new JSONObject(stats.getWordFrequencies()).toString());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static UserStats getUserStatistics(String username) {
        String query = "SELECT * FROM user_statistics WHERE username = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                UserStats stats = new UserStats(username);
                stats.incrementGameCount(rs.getInt("games_played"));
                stats.updateLetterFrequency(new JSONObject(rs.getString("letter_frequencies")).toString());
                stats.updateWordFrequency(new JSONObject(rs.getString("word_frequencies")).toString());

                return stats;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static Map<String, Integer> getLetterFrequencies() {
        Map<String, Integer> letterFrequencies = new HashMap<>();
        String query = "SELECT letter_frequencies FROM user_statistics";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                String json = rs.getString("letter_frequencies");
                JSONObject jsonObject = new JSONObject(json);
                for (String key : jsonObject.keySet()) {
                    letterFrequencies.put(key, jsonObject.getInt(key));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return letterFrequencies;
    }

    public static Map<String, Integer> getWordFrequencies() {
        Map<String, Integer> wordFrequencies = new HashMap<>();
        String query = "SELECT word_frequencies FROM user_statistics";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                String json = rs.getString("word_frequencies");
                JSONObject jsonObject = new JSONObject(json);
                for (String key : jsonObject.keySet()) {
                    wordFrequencies.put(key, jsonObject.getInt(key));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return wordFrequencies;
    }

    public static List<String> getAllUsers() {
        List<String> users = new ArrayList<>();
        String query = "SELECT username FROM user_statistics";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                users.add(rs.getString("username"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }
}