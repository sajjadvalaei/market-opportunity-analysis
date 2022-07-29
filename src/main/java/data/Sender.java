package data;

import config.Configuration;
import kafka.Producer;
import module.Candlestick;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static config.Configuration.BROKER_ADDRESS;
import static config.Configuration.TOPIC_NAME;

public class Sender {
    private final static String APIS_FILE_ADDRESS = "src/main/resources/binance/symbols.smbl";
    private static Producer producer;
    public static void main(String[] args) {
        Configuration.setSystemProxy();
        try {
            while(true) {
                Producer.start(BROKER_ADDRESS);
                producer = Producer.getProducer();
                DataCollector dataCollector = BinanceDataCollector.getDataCollector();
                List<String> symbols = loadSymbols();
                symbols.forEach(symbol -> {
                    Candlestick candlestick = dataCollector.getLastCandlestick(symbol);
                    System.out.println(candlestick);
                    producer.send(TOPIC_NAME, candlestick);
                });
                Thread.sleep(10000);
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static List<String> loadSymbols() throws FileNotFoundException {
        List<String> ret = new ArrayList<>();
        File file = new File(Sender.APIS_FILE_ADDRESS);
        Scanner sc = new Scanner(file);
        sc.forEachRemaining(ret::add);
        System.out.println(ret);
        return ret;
    }
}
