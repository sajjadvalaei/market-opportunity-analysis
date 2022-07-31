package common.database;

import common.notification.Notification;
import common.notification.NotificationSQLService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/***
 * simple mysql database just has two method 1.insert notification instance 2.select all notifications as a list.
 * TODO: I should handle probable exceptions especially in connecting to database.
 */
public class MySQLDatabase implements Database {
    private final static Logger LOGGER = LoggerFactory.getLogger(MySQLDatabase.class.getName());
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
    @Override
    public void insert(Notification notification) {
        try {
            PreparedStatement pstmt = connection.prepareStatement(
                    NotificationSQLService.getInsertAllQueryStatement(),
                    Statement.RETURN_GENERATED_KEYS);
            NotificationSQLService.setParameters(notification,pstmt);
            int rowAffected = pstmt.executeUpdate();
        } catch (SQLException e) {
            LOGGER.error("Error while inserting. " + e);
        }
    }
    @Override
    public List<Notification> getNotificationList() {
        try {
            ResultSet resultSet = statement.executeQuery(NotificationSQLService.getSelectAllQueryStringStatement());
            return makeNotificationList(resultSet);
        } catch (SQLException e) {
            LOGGER.error("Error while get all notification List. " + e);
            return Collections.emptyList();
        }
    }

    private List<Notification> makeNotificationList(ResultSet resultSet) throws SQLException {
        List<Notification> notifications = new ArrayList<>();
        while (resultSet.next()){
            notifications.add(NotificationSQLService.extractNotification(resultSet));
        }
        return notifications;
    }


    private void createTables() {
        try {
            statement.executeUpdate(NotificationSQLService.createTableStatement());
            tableName = Notification.class.getSimpleName();
        } catch (SQLException e) {
            LOGGER.error("Could not create table."+ e);
            throw new RuntimeException(e);
        }
    }

    private void createStatement() {
        try {
            statement = connection.createStatement();
        } catch (SQLException e) {
            LOGGER.error("Could not get the statement."+ e);
            throw new RuntimeException(e);
        }
    }

    private void initDatabase() {
        try {
            statement.executeUpdate("USE " + databaseName);
        } catch (SQLException e) {
            LOGGER.error("Could not use database."+ e);
            throw new RuntimeException(e);
        }
    }

    private void createDatabase() {
        try {
            statement.executeUpdate("CREATE DATABASE IF NOT EXISTS " + databaseName);
        } catch (SQLException e) {
            LOGGER.error("Could not make database."+ e);
            throw new RuntimeException(e);
        }
    }

    private void connectToMySQL() {
        try {
            connection = DriverManager.getConnection(databaseURL, user, password);
        } catch (SQLException e) {
            LOGGER.error("Could not connect to mysql"+ e);
            throw new RuntimeException(e);
        }
    }

}
