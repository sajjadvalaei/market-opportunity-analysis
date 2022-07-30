package config;

public class Configuration {
    public static void setSystemProxy(){
        System.setProperty("java.net.useSystemProxies", "true");
    }
    public static final String BROKER_ADDRESS = "localhost:9092";
    public static final String TOPIC_NAME = "candlestick";
    public static final String DATABASE_NAME = "Market";
    public static final String DATABASE_URL = "jdbc:mysql://localhost/";
    public static final String USER = "sajjad";
    public static final String PASSWORD = "0022701303";
}
