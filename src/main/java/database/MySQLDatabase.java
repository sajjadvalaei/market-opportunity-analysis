package database;

import module.Notification;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class MySQLDatabase {
    private final static Logger LOGGER = Logger.getLogger(MySQLDatabase.class.getName());
    private final String databaseURL;
    private final String user;
    private final String password;
    private Connection connection;
    private Statement statement;
    private String databaseName;
    private String tableName;

    public MySQLDatabase(String databaseURL, String user, String password, String databaseName) {
        this.databaseURL = databaseURL;
        this.user = user;
        this.password = password;
        this.databaseName = databaseName;
        connectToMySQL();
        createStatement();
        createDatabase();
        initDatabase();
        createTables();
    }

    public void insert(Notification notification) {
        try {
            PreparedStatement pstmt = connection.prepareStatement(
                    Notification.getInsertAllQueryStatement(),
                    Statement.RETURN_GENERATED_KEYS);
            notification.setParameters(pstmt);
            int rowAffected = pstmt.executeUpdate();
        } catch (SQLException e) {
            LOGGER.severe("Error while inserting. " + e);
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
            LOGGER.severe("Could not create table."+ e);
            throw new RuntimeException(e);
        }
    }

    private void createStatement() {
        try {
            statement = connection.createStatement();
        } catch (SQLException e) {
            LOGGER.severe("Could not get the statement."+ e);
            throw new RuntimeException(e);
        }
    }

    private void initDatabase() {
        try {
            statement.executeUpdate("USE " + databaseName);
        } catch (SQLException e) {
            LOGGER.severe("Could not use database."+ e);
            throw new RuntimeException(e);
        }
    }

    private void createDatabase() {
        try {
            statement.executeUpdate("CREATE DATABASE IF NOT EXISTS " + databaseName);
        } catch (SQLException e) {
            LOGGER.severe("Could not make database."+ e);
            throw new RuntimeException(e);
        }
    }

    private void connectToMySQL() {
        try {
            connection = DriverManager.getConnection(databaseURL, user, password);
        } catch (SQLException e) {
            LOGGER.severe("Could not connect to mysql"+ e);
            throw new RuntimeException(e);
        }
    }

}
