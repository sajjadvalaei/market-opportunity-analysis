import module.Candlestick;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class FakeCandlestick {

    public static Candlestick createRandomCandlestick() {
        Random random = new Random();
        return new Candlestick(random.nextDouble(), random.nextDouble(),
                random.nextDouble(), random.nextDouble(),
                random.nextLong(), random.nextLong(), Double.toString(random.nextDouble()));
    }

    public static List<Candlestick> randomCandlestickList(int num) {
        ArrayList<Candlestick> list = new ArrayList<>();
        while(num > 0 ) {
            list.add(createRandomCandlestick());
            num--;
        }
        return list;
    }
}
