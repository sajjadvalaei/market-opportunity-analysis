package data;

import config.Configuration;
import kafka.Producer;
import module.Candlestick;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static config.Configuration.BROKER_ADDRESS;
import static config.Configuration.TOPIC_NAME;

public class Sender {
    private final static String APIS_FILE_ADDRESS = "src/main/resources/binance/symbols.smbl";
    public static void main(String[] args) throws FileNotFoundException {
        Configuration.setSystemProxy();
        final List<String> symbols = loadSymbols();
        final DataCollector dataCollector = BinanceDataCollector.getDataCollector();
        final Producer producer = new Producer(BROKER_ADDRESS);
        try {
            while (true) {
                sendMarketData(dataCollector, producer,symbols);
                sleep1Minute();
            }
        } catch (ConnectException e){
            System.err.println("couldn't connect to the Binance servers." ); ;
            e.printStackTrace();
        }
    }

    private static void sendMarketData(DataCollector dataCollector, Producer producer, List<String> symbols) throws ConnectException {
        for (String symbol : symbols) {
            try {
                Candlestick candlestick = dataCollector.getLastCandlestick(symbol);
                System.out.println(candlestick);
                producer.send(TOPIC_NAME, candlestick);
            } catch (SymbolNotFoundException e) {
                System.err.println(e.toString());
            }
        }
    }

    private static void sleep1Minute() {
        try {
            Thread.sleep(60000);
        } catch (InterruptedException ignored){
            System.out.println("Thread has been interrupted while sleep.");
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
