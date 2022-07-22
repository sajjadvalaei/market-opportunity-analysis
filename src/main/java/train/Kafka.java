package train;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Printed;

public class Kafka{
    public static void main(String[] args) {
        StreamsBuilder builder = new StreamsBuilder();
        KStream<String, String> stream =
                builder.stream("input-topic", Consumed.with(Serdes.String(), Serdes.String()));
        //Printed<String,String> printed = new Printed<String, String>();
        //stream.print(printed);
    }
}
