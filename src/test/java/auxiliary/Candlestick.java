package auxiliary;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Candlestick {

    private static final Random random = new Random();
    public static module.Candlestick createRandomCandlestick() {
        return new module.Candlestick(random.nextDouble(), random.nextDouble(),
                random.nextDouble(), random.nextDouble(),
                random.nextLong(), random.nextLong(), randomSymbolGenerator());
    }

    public static List<module.Candlestick> randomCandlestickList(int num) {
        ArrayList<module.Candlestick> list = new ArrayList<>();
        while(num > 0 ) {
            list.add(createRandomCandlestick());
            num--;
        }
        return list;
    }

    public static String randomSymbolGenerator() {
        return Double.toString(random.nextDouble());
    }
}
