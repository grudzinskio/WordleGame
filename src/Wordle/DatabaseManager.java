package Wordle;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {


    private static final String DB_PATH = "repository/identifier.sqlite";
    private static final String URL = "jdbc:sqlite:" + DB_PATH;

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }
}