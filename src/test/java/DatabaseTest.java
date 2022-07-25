
import org.apache.commons.lang3.SystemUtils;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.utility.DockerImageName;

import java.sql.ResultSet;
import java.sql.SQLException;


public class DatabaseTest extends AbstractContainerDatabaseTest {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseTest.class);
    private static final DockerImageName MYSQL_80_IMAGE = DockerImageName.parse("mysql:8.0.29");

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
    @Test
    public void testSimple() throws SQLException {
        try (
                MySQLContainer<?> mysql = new MySQLContainer<>(MYSQL_80_IMAGE)
                        .withLogConsumer(new Slf4jLogConsumer(logger))
        ) {
            mysql.start();

            ResultSet resultSet = performQuery(mysql, "SELECT 1");
            int resultSetInt = resultSet.getInt(1);

            Assert.assertEquals("A basic SELECT query succeeds", 1, resultSetInt);
        }
    }

    @Test
    public void testSpecificVersion() throws SQLException {
        try (
                MySQLContainer<?> mysqlOldVersion = new MySQLContainer<>(MYSQL_80_IMAGE)
                        .withConfigurationOverride("somepath/mysql_conf_override")
                        .withLogConsumer(new Slf4jLogConsumer(logger))
        ) {
            mysqlOldVersion.start();

            ResultSet resultSet = performQuery(mysqlOldVersion, "SELECT VERSION()");
            String resultSetString = resultSet.getString(1);

            Assert.assertTrue(
                    "The database version can be set using a container rule parameter",
                    resultSetString.startsWith("5.6")
            );
        }
    }

    @Test
    public void testMySQLWithCustomIniFile() throws SQLException {
        Assume.assumeFalse(SystemUtils.IS_OS_WINDOWS);

        try (
                MySQLContainer<?> mysqlCustomConfig = new MySQLContainer<>(MYSQL_80_IMAGE)
                        .withConfigurationOverride("somepath/mysql_conf_override")
        ) {
            mysqlCustomConfig.start();

            ResultSet resultSet = performQuery(mysqlCustomConfig, "SELECT @@GLOBAL.innodb_file_format");
            String result = resultSet.getString(1);

            Assert.assertEquals("The InnoDB file format has been set by the ini file content", "Barracuda", result);
        }

    }
}