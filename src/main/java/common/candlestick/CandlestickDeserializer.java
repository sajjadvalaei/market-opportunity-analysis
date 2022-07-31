package common.candlestick;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Deserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class CandlestickDeserializer implements Deserializer<Candlestick> {
    private static final Logger LOGGER = LoggerFactory.getLogger(CandlestickDeserializer.class);
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Override
    public Candlestick deserialize(String topic, byte[] data) {
        try {
            return objectMapper.readValue(data, Candlestick.class);
        } catch (UnsupportedEncodingException e) {
            LOGGER.error("Serialize encoding is not supported.", e);
        } catch (JsonMappingException | JsonParseException e) {
            LOGGER.error("Json couldn't parse data in consumer Deserialization.", e);
        } catch (IOException e) {
            LOGGER.error("Error during consumer Deserialization",e);
        }
        return new Candlestick();
    }
}
