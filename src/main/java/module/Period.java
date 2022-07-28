package module;

// Period represents from when.
public class Period {
    private final int amount;
    private final Interval interval;
    private final OHLC ohlc;

    public Period(int amount, Interval interval, OHLC ohlc) {
        this.amount = amount;
        this.interval = interval;
        this.ohlc = ohlc;
    }
    public int getAmount() {
        return amount;
    }
    public Interval getInterval() {
        return interval;
    }
    public OHLC getOhlc(){
        return ohlc;
    }

    public static Period parsePeriod(String s) {
        String[] orders = s.split(" ");
        int amount = Integer.parseInt(orders[0]);
        Interval interval = Interval.valueOf(orders[1]);
        OHLC ohlc = OHLC.valueOf(orders[2]);
        return new Period(amount,interval, ohlc);
    }

    @Override
    public String toString(){
       return amount +" "+ interval+ " "+ ohlc;
    }

    @Override
    public boolean equals(Object other){
        if( !(other instanceof  Period))
            return false;
        Period o = (Period) other;
        return o.amount == amount 
                && interval.equals(o.interval)
                && ohlc.equals(o.ohlc);
    }
}
