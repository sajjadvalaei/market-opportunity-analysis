package common.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Configuration {
    public static Properties PROPS = new Properties();
    static {
        try {
            InputStream input = new FileInputStream("src/main/resources/config.properties");
            PROPS.load(input);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static void setSystemProxy(){
        System.setProperty("java.net.useSystemProxies", "true");
    }
    public static final String BROKER_ADDRESS = PROPS.getProperty("kafka.brokerAddress");
    public static final String TOPIC_NAME = PROPS.getProperty("kafka.topicName");
    public static final String DATABASE_NAME = PROPS.getProperty("db.name");
    public static final String DATABASE_URL = PROPS.getProperty("db.url");
    public static final String USER = PROPS.getProperty("db.user");
    public static final String PASSWORD = PROPS.getProperty("db.password");
}
