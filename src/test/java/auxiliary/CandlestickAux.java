package auxiliary;

import common.candlestick.Candlestick;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CandlestickAux {

    private static final Random random = new Random();
    public static Candlestick createRandomCandlestick() {
        return new Candlestick(random.nextDouble(), random.nextDouble(),
                random.nextDouble(), random.nextDouble(),
                random.nextLong(), random.nextLong(), randomSymbolGenerator());
    }

    public static List<Candlestick> randomCandlestickList(int num) {
        ArrayList<Candlestick> list = new ArrayList<>();
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
