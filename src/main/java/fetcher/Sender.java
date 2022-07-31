package fetcher;

import common.config.Configuration;
import common.candlestick.CandlestickSerializer;
import common.candlestick.Candlestick;
import fetcher.collector.BinanceDataCollector;
import fetcher.collector.DataCollector;
import fetcher.exception.ProducerShotDownException;
import fetcher.exception.SymbolNotFoundException;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.KafkaException;
import org.apache.kafka.common.errors.AuthorizationException;
import org.apache.kafka.common.errors.OutOfOrderSequenceException;
import org.apache.kafka.common.errors.ProducerFencedException;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

import static common.config.Configuration.*;

/***
 * This thread fetches data from api every 60 seconds and sends it by kafka producer as soon as it could.
 * in case of failure, it tries again to send data to brokers.
 */
public class Sender extends  Thread{

    //Warning: don't remove this.
    static{
        Configuration.setSystemProxy();
        System.out.println("Proxy set.");
    }
    private static final int TRY_NUM = Integer.parseInt(PROPS.getProperty("producer.maximumTryNumber"));
    private final static String APIS_FILE_ADDRESS = PROPS.getProperty("api.fileAddress");
    private final static Logger LOGGER = LoggerFactory.getLogger(Sender.class);
    private static final String TRANSACTIONAL_ID = "producer_unique_transactional_id";

    private final DataCollector dataCollector = BinanceDataCollector.getDataCollector();
    private final KafkaProducer<String,Candlestick> producer = createProducer(BROKER_ADDRESS, TRANSACTIONAL_ID);

    public static void main(String[] args) {
        Configuration.setSystemProxy();
        Thread sender = new Sender();
        sender.start();
    }

    private Sender(){
        super();
    }

    public void run(){
        try {
            final List<String> symbols = loadSymbols();
            producer.initTransactions();
            while (true) {
                List<Candlestick> candles = getMarketData(symbols);
                sendMarketData(candles);
                sleep1Minute();
            }
        } catch (ConnectException e){
            LOGGER.error("couldn't connect to the Binance servers."+e ); ;
        } catch (ProducerShotDownException e) {
            LOGGER.error(e.toString());
        } catch (FileNotFoundException e) {
            //your file path is wrong.
            throw new RuntimeException(e);
        }
        producer.close();
    }

    private List<Candlestick> getMarketData(List<String> symbols) throws ConnectException {
        List<Candlestick> candles = new ArrayList<>();
        for (String symbol : symbols) {
            try {
                Candlestick candlestick = dataCollector.getLastCandlestick(symbol);
                candles.add(candlestick);
            } catch (SymbolNotFoundException e) {
                LOGGER.error("symbol " + symbol + " is not valid." + e);
            }
        }
        return candles;
    }
    private void sendMarketData(List<Candlestick> candles) throws ProducerShotDownException {
        try{
            sendMarketDataExceptionNotHandled(candles);
        } catch (ProducerFencedException | OutOfOrderSequenceException | AuthorizationException e) {
            // We can't recover from these exceptions, so our only option is to close the producer and exit.
            producer.close();
            throw new ProducerShotDownException("Producer couldn't make it. shot down... ." + e);
        } catch (KafkaException e) {
            producer.abortTransaction();
            trySendMarkData(candles, 1);
        }
    }

    private void trySendMarkData(List<Candlestick> candles, int i) throws ProducerShotDownException {
        try{
            sendMarketDataExceptionNotHandled(candles);
        }catch (KafkaException e){
            if(i >= TRY_NUM){
                producer.close();
                throw  new ProducerShotDownException("Couldn't send data after " + TRY_NUM + " tries. " + e);
            }
            LOGGER.error("try sending data try number: " + i + " : " + e);
            trySendMarkData(candles,i+1);
        }
    }

    private void sendMarketDataExceptionNotHandled(List<Candlestick> candles) {
        producer.beginTransaction();
        for (Candlestick candle : candles) {
            producer.send(new ProducerRecord<>(TOPIC_NAME,candle.getSymbol(),candle));
            LOGGER.info("candlestick: " + candle + " Added.");
        }
        producer.commitTransaction();
    }

    private static KafkaProducer<String,Candlestick> createProducer(String brokerAddress, String uniqueTransactionalId) {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, brokerAddress);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, CandlestickSerializer.class.getName());
        props.put(ProducerConfig.TRANSACTIONAL_ID_CONFIG, uniqueTransactionalId);
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        return new KafkaProducer<>(props);
    }
    private static List<String> loadSymbols() throws FileNotFoundException {
        List<String> ret = new ArrayList<>();
        File file = new File(Sender.APIS_FILE_ADDRESS);
        Scanner sc = new Scanner(file);
        sc.forEachRemaining(ret::add);
        System.out.println(ret);
        return ret;
    }
    private static void sleep1Minute() {
        try {
            Thread.sleep(60000);
        } catch (InterruptedException ignored){
            System.out.println("Thread has been interrupted while sleep.");
        }
    }
}
