package Wordle;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {
    private static final String URL = "jdbc:sqlite:C:\\Projects\\s25-flynn-group15\\identifier.sqlite"; // Update with your actual path

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }
}