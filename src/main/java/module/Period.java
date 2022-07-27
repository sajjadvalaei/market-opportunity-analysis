package module;

// Period represents from when.
public class Period {
    private final int amount;
    private final Interval interval;
    public Period(int amount, Interval interval) {
        this.amount = amount;
        this.interval = interval;
    }


    public static Period parsePeriod(String s) {
        String[] orders = s.split(" ");
        int amount = Integer.parseInt(orders[0]);
        Interval interval = Interval.valueOf(orders[1]);
        return new Period(amount,interval);
    }

    @Override
    public String toString(){
       return amount +" "+ interval;
    }

    @Override
    public boolean equals(Object other){
        if( !(other instanceof  Period))
            return false;
        Period o = (Period) other;
        return o.amount == amount && interval.equals(o.interval) ;
    }
}
