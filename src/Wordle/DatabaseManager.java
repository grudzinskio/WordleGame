package Wordle;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {


    private static final String HOME_DIR = System.getProperty("user.home");
    private static final String DB_PATH = HOME_DIR + "\\IdeaProjects\\s25-flynn-group15\\identifier.sqlite";
    private static final String URL = "jdbc:sqlite:" + DB_PATH;

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }
}