// Java
package Wordle;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class StatDisplayController {

    public static StatDisplayController instance;
    public TextField averageGuesses;
    public TextField gamesPlayed;

    @FXML
    private void initialize() {
        instance = this;
        refreshStats();
    }

    public void refreshStats() {
        String username = UserStats.getInstance().getUsername();
        String query = "SELECT average_guesses, games_played FROM user_statistics WHERE username = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                double avgGuesses = rs.getDouble("average_guesses");
                int played = rs.getInt("games_played");

                averageGuesses.setText(String.format("%.2f", avgGuesses));
                gamesPlayed.setText(String.valueOf(played));
            } else {
                averageGuesses.setText("0.00");
                gamesPlayed.setText("0");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            averageGuesses.setText("0.00");
            gamesPlayed.setText("0");
        }
    }
}