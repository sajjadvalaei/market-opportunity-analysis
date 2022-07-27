package database;

import module.Notification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MySQLDatabase {
    private final static Logger LOGGER = LoggerFactory.getLogger(MySQLDatabase.class);
    public static String DB_URL = "jdbc:mysql://localhost/";
    static final String USER = "sajjad";
    static final String PASSWORD = "0022701303";
    private static MySQLDatabase database;
    private Connection connection;
    private Statement statement;
    private String databaseName;
    private String tableName;

    private MySQLDatabase(String databaseName) {
        this.databaseName = databaseName;
        connectToMySQL();
        createStatement();
        createDatabase();
        initDatabase();
        createTables();
    }

    public static void start(String databaseName) {
        database = new MySQLDatabase(databaseName);
    }

    public static MySQLDatabase getDatabase() {
        if (database == null)
            throw new NullPointerException("Database has not yet been set, mysql database is not started");
        return database;
    }

    public void insert(Notification notification) {
        try {
            PreparedStatement pstmt = connection.prepareStatement(
                    Notification.getInsertAllQueryStatement(),
                    Statement.RETURN_GENERATED_KEYS);
            notification.setParameters(pstmt);
            int rowAffected = pstmt.executeUpdate();
        } catch (SQLException e) {
            LOGGER.error("Error while inserting.", e);
            throw new RuntimeException(e);
        }
    }


    public List<Notification> getNotificationList() {
        try {
            ResultSet resultSet = statement.executeQuery(Notification.getSelectAllQueryStringStatement());
            return makeNotificationList(resultSet);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private List<Notification> makeNotificationList(ResultSet resultSet) throws SQLException {
        List<Notification> notifications = new ArrayList<>();
        while (resultSet.next()){
            notifications.add(new Notification(resultSet));
        }
        return notifications;
    }

    private void createTables() {
        try {
            statement.executeUpdate(Notification.createTableStatement());
            tableName = Notification.class.getSimpleName();
        } catch (SQLException e) {
            LOGGER.error("Could not create table.", e);
            throw new RuntimeException(e);
        }
    }

    private void createStatement() {
        try {
            statement = connection.createStatement();
        } catch (SQLException e) {
            LOGGER.error("Could not get the statement.", e);
            throw new RuntimeException(e);
        }
    }

    private void initDatabase() {
        try {
            statement.executeUpdate("USE " + databaseName);
        } catch (SQLException e) {
            LOGGER.error("Could not use database.", e);
            throw new RuntimeException(e);
        }
    }

    private void createDatabase() {
        try {
            statement.executeUpdate("CREATE DATABASE IF NOT EXISTS " + databaseName);
        } catch (SQLException e) {
            LOGGER.error("Could not make database.", e);
            throw new RuntimeException(e);
        }
    }

    private void connectToMySQL() {
        try {
            connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);
        } catch (SQLException e) {
            LOGGER.error("Could not connect to mysql", e);
            throw new RuntimeException(e);
        }
    }

}
