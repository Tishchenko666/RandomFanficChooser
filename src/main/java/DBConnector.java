import java.sql.*;
import java.util.ArrayList;

public class DBConnector {

    private Connection connection;
    private Statement statement;

    public DBConnector() throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/sofiia?serverTimezone=Europe/Helsinki&useSSL=false", "root", "");
        statement = connection.createStatement();
    }

    public void createFanTable() throws SQLException {
        statement.execute("CREATE TABLE IF NOT EXISTS fanfics(" +
                "id INTEGER PRIMARY KEY AUTO_INCREMENT," +
                "fan_name VARCHAR(50)," +
                "genre VARCHAR(20)," +
                "original_history VARCHAR(50)) CHARACTER SET utf8");
        statement.close();
    }

    public int addFanfics(ArrayList<StringBuilder> insertParts) throws SQLException {
        statement = connection.createStatement();
        StringBuilder insertQuery = new StringBuilder("INSERT INTO fanfics (`fan_name`, `genre`, `original_history`) VALUES ");
        for (int i = 0; i < insertParts.size() - 1; i++) {
            insertQuery.append(insertParts.get(i)).append(", ");
        }
        insertQuery.append(insertParts.get(insertParts.size() - 1));
        return statement.executeUpdate(String.valueOf(insertQuery));
    }

    public ResultSet getOriginalHistoryList() throws SQLException {
        String originalHistoryQuery = " SELECT DISTINCT original_history FROM fanfics";
        statement = connection.createStatement();
        return statement.executeQuery(originalHistoryQuery);
    }

    public ResultSet getRandomFanfic() throws SQLException {
        String randomFanficQuery = "SELECT * FROM fanfics ORDER BY RAND() LIMIT 1";
        statement = connection.createStatement();
        return statement.executeQuery(randomFanficQuery);
    }

    public ResultSet getRandomFanficByGenre(StringBuilder chosenGenres) throws SQLException {
        String randomFanficGenreQuery = "SELECT * FROM fanfics WHERE genre IN " + chosenGenres + " ORDER BY RAND() LIMIT 1";
        statement = connection.createStatement();
        return statement.executeQuery(randomFanficGenreQuery);
    }

    public ResultSet getRandomFanficByHistory(StringBuilder chosenHistories) throws SQLException {
        String randomFanficHistoryQuery = "SELECT * FROM fanfics WHERE original_history IN " + chosenHistories + " ORDER BY RAND() LIMIT 1";
        statement = connection.createStatement();
        return statement.executeQuery(randomFanficHistoryQuery);
    }

    public ResultSet getRandomFanficByGenreAndHistory(String inCondition) throws SQLException {
        String randomFanficGenreHistoryQuery = "SELECT * FROM fanfics WHERE genre IN " + inCondition + " ORDER BY RAND() LIMIT 1";
        statement = connection.createStatement();
        return statement.executeQuery(randomFanficGenreHistoryQuery);
    }

    public void closeStatement() throws SQLException {
        statement.close();
    }

    public void closeConnection() throws SQLException {
        statement.close();
    }

}
