
import auxiliary.AbstractContainerDatabaseTest;
import auxiliary.MySQLDatabaseAux;
import auxiliary.NotificationAux;
import database.MySQLDatabase;
import module.Notification;
import org.junit.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.utility.DockerImageName;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;


public class DatabaseTest extends AbstractContainerDatabaseTest {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseTest.class);
    private static final DockerImageName MYSQL_80_IMAGE = DockerImageName.parse("mysql:5.5");
    private static final String DATABASE_NAME = "test";

    private static final String USER = "sajjad";
    private static final String PASSWORD = "0022701303";
    private static final int NOTIFICATION_NUM = 100;

    /*
     * Ordinarily you wouldn't try and run multiple containers simultaneously - this is just used for testing.
     * To avoid memory issues with the default, low memory, docker machine setup, we instantiate only one container
     * at a time, inside the test methods themselves.
     */
    /*
    @ClassRule
    public static MySQLContainer<?> mysql = new MySQLContainer<>(MYSQL_IMAGE);
    @ClassRule
    public static MySQLContainer<?> mysqlOldVersion = new MySQLContainer<>(DockerImageName.parse("mysql:5.5");)
    @ClassRule
    public static MySQLContainer<?> mysqlCustomConfig = new MySQLContainer<>(DockerImageName.parse("mysql:5.6"))
                                                              .withConfigurationOverride("somepath/mysql_conf_override");

    */
    @ClassRule
    public static MySQLContainer<?> mysql = new MySQLContainer<>(MYSQL_80_IMAGE)
            .withDatabaseName(DATABASE_NAME)
            .withUsername(USER)
            .withPassword(PASSWORD)
            .withLogConsumer(new Slf4jLogConsumer(logger));
    static MySQLDatabase database;
    @BeforeClass
    public static void beforeClass(){
        mysql.start();
        MySQLDatabaseAux.changeJDBCURLForTest(mysql.getJdbcUrl());
        MySQLDatabase.start(DATABASE_NAME);
    }

    @Before
    public void beforeTest(){
        database = MySQLDatabase.getDatabase();
    }
    @Test
    public void checkTestContainerWork() throws SQLException {

        ResultSet resultSet = performQuery(mysql, "SELECT 1");
        resultSet.next();
        int resultSetInt = resultSet.getInt(1);

        Assert.assertEquals("A basic SELECT query succeeds", 1, resultSetInt);
    }

    @Test
    public void storeRandomNotificationInDatabase() throws SQLException {
        Notification notification = NotificationAux.createRandomNotification();
        database.insert(notification);

        ResultSet resultSet = performQuery(mysql, NotificationAux.selectAllQueryStringStatement());
        Assert.assertTrue(NotificationAux.resultSetContains(resultSet,notification));
    }

    @Test
    public void storeSeveralRandomNotificationInDatabase() throws SQLException{
        List<Notification> notifications = NotificationAux.createRandomNotificationList(NOTIFICATION_NUM);
        notifications.forEach(notification -> database.insert(notification) );

        ResultSet resultSet = performQuery(mysql, NotificationAux.selectAllQueryStringStatement());
        for (Notification notif : notifications) {
            Assert.assertTrue(NotificationAux.resultSetContains(resultSet, notif));
        }
    }

    @Test
    public void getListOfNotifications(){
        List<Notification> notifications = NotificationAux.createRandomNotificationList(NOTIFICATION_NUM);
        notifications.forEach(notification -> database.insert(notification) );

        List<Notification> resultList = database.getNotificationList();
        Assert.assertTrue(resultList.containsAll(notifications));
    }

}