package rule;

import module.Candlestick;
import module.Interval;
import module.Period;

import java.util.*;

public class SMARuleMemoryService implements RuleMemoryService {
    SMARuleMemoryService(){

    }
    Map<String,MyLinkedList> listMap = new HashMap<>();
    @Override
    public void append(Candlestick candle) {
        if(!listMap.containsKey(candle.getSymbol()))
            createSymbol(candle.getSymbol());
        MyLinkedList list = listMap.get(candle.getSymbol());
        list.storeLastMinute(candle);
    }

    private void createSymbol(String symbol) {
        MyLinkedList list = new MyLinkedList();
        listMap.put(symbol, list);
        for(int i = 0;i < 60*48; i++)
            list.storeLastMinute(new Candlestick());
    }

    @Override
    public Double getAverage(Period period, String symbol) {
        MyLinkedList list = listMap.get(symbol);
        Candlestick candle = list.getAverageCandlestick(period.getAmount(), period.getInterval());
        Double[] data = new Double[]{ candle.getOpen(), candle.getClose(),
                                        candle.getHigh(), candle.getLow()};
        return data[period.getOhlc().ordinal()];
    }

    @Override
    public Candlestick getLast(String symbol){
        if( !listMap.containsKey(symbol) )
            return new Candlestick();
        MyLinkedList list = listMap.get(symbol);
        return list.getLastCandlestick();
    }



    private static class MyLinkedList{
        private final LinkedList<Candlestick>[] list;

        private MyLinkedList() {
            list = new LinkedList[3];
            for (int i = 0; i < 3; i++) {
                list[i] = new LinkedList<>();
            }
        }
        private Candlestick getAverageCandlestick(int amount, Interval interval) {
            ListIterator<Candlestick> iterator
                    = list[interval.ordinal()].listIterator();
            List<Candlestick>  candles = new ArrayList<>();
            for(int ind = 0; ind < amount; ind++){
                candles.add(iterator.next());
            }
            System.out.println(amount + " " + interval + " " + candles);
            return getAverageCandlestickOfList(candles);
        }
        private void storeLastMinute(Candlestick candle) {
            System.out.println(candle);
            list[0].addFirst(candle);
            if (list[0].size() % 60 == 0)
                storeLastHour();
        }
        private void storeLastHour(){
            Candlestick hourAverage = getAverageCandlestick(60,Interval.M);
            list[1].addFirst(hourAverage);
            if (list[1].size() % 24 == 0)
                storeLastDay();
        }
        private void storeLastDay(){
            Candlestick dayAverage = getAverageCandlestick(24,Interval.H);
            list[2].addFirst(dayAverage);
        }
        private Candlestick getAverageCandlestickOfList(List<Candlestick> candles) {
            double[] data = new double[4];
            int size = candles.size();
            candles.forEach(candle ->{
                data[0] += candle.getOpen();
                data[1] += candle.getClose();
                data[2] += candle.getHigh();
                data[3] += candle.getLow();
            });
            System.out.print("data:");
            for (int i = 0; i < 4; i++) {
                data[i] = data[i] / size;
                System.out.print(data[i]);
            }
            System.out.println();
            Candlestick candle =
                    new Candlestick (data[0], data[1], data[2], data[3],
                            candles.get(0).getOpenTime(), candles.get(size-1).getCloseTime(),
                            candles.get(0).getSymbol());
            return candle;
        }

        public Candlestick getLastCandlestick() {
            return list[0].getFirst();
        }
    }

}
