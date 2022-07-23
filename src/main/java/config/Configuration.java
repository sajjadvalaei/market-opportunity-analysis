package config;

public class Configuration {
    public static void setSystemProxy(){
        System.setProperty("java.net.useSystemProxies", "true");
    }
}
