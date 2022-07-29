package config;

public class Configuration {
    public static void setSystemProxy(){
        System.setProperty("java.net.useSystemProxies", "true");
    }
    public static final String BROKER_ADDRESS = "localhost:9092";
    public static final String TOPIC_NAME = "candlestick";
    public static final String DATABASE_NAME = "Market";
}
