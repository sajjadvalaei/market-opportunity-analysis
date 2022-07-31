package common.candlestick;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Serializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CandlestickSerializer implements Serializer<Candlestick> {
    private static final Logger LOGGER = LoggerFactory.getLogger(CandlestickDeserializer.class);
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Override
    public byte[] serialize(String s, Candlestick candlestick) {
        try {
            return objectMapper.writeValueAsBytes(candlestick);
        } catch (JsonProcessingException e) {
            LOGGER.error("Json couldn't parse sending data", e);
        }
        return new byte[0];
    }
}
