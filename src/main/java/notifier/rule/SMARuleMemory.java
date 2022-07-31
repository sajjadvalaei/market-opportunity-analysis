package notifier.rule;

import common.candlestick.Candlestick;
import common.period.Interval;
import common.period.Period;
import notifier.exception.NotEnoughDataException;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

/***
 *  handle functions that are explained in its interface and @getAverage is what SMARule needs to verify satisfaction.
 *  TODO: add data to memory by its time not just add to the end.
  */

public class SMARuleMemory implements RuleMemory {
    public SMARuleMemory(){
    }
    Map<String,MyLinkedList> listMap = new HashMap<>();
    @Override
    public void append(Candlestick candle) {
        createSymbolIfNotExisted(candle.getSymbol());
        MyLinkedList list = listMap.get(candle.getSymbol());
        list.storeLastMinute(candle);
    }

    private void createSymbolIfNotExisted(String symbol) {
        if(listMap.containsKey(symbol))
            return;
        MyLinkedList list = new MyLinkedList();
        listMap.put(symbol, list);
    }

    public Double getAverage(Period period, String symbol) throws NotEnoughDataException {
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

    @Override
    public boolean isDuplicated(Candlestick candlestick) {
        if(!listMap.containsKey(candlestick.getSymbol()))
            return false;
        return listMap.get(candlestick.getSymbol()).contains(candlestick);
    }


    private static class MyLinkedList{
        private final LinkedList<Candlestick>[] list;

        private MyLinkedList() {
            list = new LinkedList[3];
            for (int i = 0; i < 3; i++) {
                list[i] = new LinkedList<>();
            }
        }
        private Candlestick getAverageCandlestick(int amount, Interval interval) throws NotEnoughDataException {
            LinkedList<Candlestick> l = list[interval.ordinal()];
            if(l.size() < amount)
                throw new NotEnoughDataException();
            ListIterator<Candlestick> iterator
                    = l.listIterator();
            List<Candlestick>  candles = new ArrayList<>();
            for(int ind = 0; ind < amount; ind++){
                candles.add(iterator.next());
            }
            return getAverageCandlestickOfList(candles);
        }
        private void storeLastMinute(Candlestick candle) {
            list[0].addFirst(candle);
            if (list[0].size() % 60 == 0)
                storeLastHour();
        }
        private void storeLastHour(){
            try {
                Candlestick hourAverage = getAverageCandlestick(60, Interval.M);
                list[1].addFirst(hourAverage);
                if (list[1].size() % 24 == 0)
                    storeLastDay();
            } catch (NotEnoughDataException e){
                //doesn't happen because the size is already checked.
            }
        }
        private void storeLastDay(){
            try {
                Candlestick dayAverage = getAverageCandlestick(24, Interval.H);
                list[2].addFirst(dayAverage);
            } catch (NotEnoughDataException e) {
                //doesn't happen because the size is already checked.
            }
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
            for (int i = 0; i < 4; i++) {
                data[i] = data[i] / size;
            }
            Candlestick candle =
                    new Candlestick (data[0], data[1], data[2], data[3],
                            candles.get(0).getOpenTime(), candles.get(size-1).getCloseTime(),
                            candles.get(0).getSymbol());
            return candle;
        }

        public Candlestick getLastCandlestick() {
            return list[0].getFirst();
        }

        private boolean contains(Candlestick candlestick) {
            if(list[0].getLast().getOpenTime() > candlestick.getOpenTime() )
                return true;
            return list[0].contains(candlestick);
        }
    }

}
