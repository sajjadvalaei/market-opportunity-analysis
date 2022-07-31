
import auxiliary.AbstractContainerDatabaseTest;
import auxiliary.NotificationAux;
import fetcher.Sender;
import common.database.MySQLDatabase;
import common.notification.Notification;
import org.junit.*;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.utility.DockerImageName;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;


public class MySQLDatabaseTest extends AbstractContainerDatabaseTest {

    private static final Logger LOGGER = Logger.getLogger(Sender.class.getName());
    private static final DockerImageName MYSQL_80_IMAGE = DockerImageName.parse("mysql:5.5");
    private static final String DATABASE_NAME = "test";

    private static final String USER = "sajjad";
    private static final String PASSWORD = "0022701303";
    private static final int NOTIFICATION_NUM = 100;

    @ClassRule
    public static MySQLContainer<?> mysql = new MySQLContainer<>(MYSQL_80_IMAGE)
            .withDatabaseName(DATABASE_NAME)
            .withUsername(USER)
            .withPassword(PASSWORD);
    static MySQLDatabase database;
    @BeforeClass
    public static void beforeClass(){
        mysql.start();
        database = new MySQLDatabase(mysql.getJdbcUrl(), USER, PASSWORD, DATABASE_NAME);
    }
    @Before
    public void beforeTest(){
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