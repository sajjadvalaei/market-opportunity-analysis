package module;

public class Candlestick{
    private Double open;
    private Double close;
    private Double high;
    private Double low;
    private Long openTime;
    private Long closeTime;

    private String symbol;

    // this constructor made for its Deserializer
    public Candlestick(){
        setAllCandlestickParameters(0.0,0.0,0.0,0.0,0L,0L,"");
    }

    public Candlestick(Double open, Double close, Double high, Double low, Long openTime, Long closeTime, String key) {
        setAllCandlestickParameters(open,close,high,low,openTime,closeTime,key);
    }
    private void setAllCandlestickParameters(Double open, Double close, Double high, Double low, Long openTime, Long closeTime, String key) {
        this.open = open;
        this.close = close;
        this.high = high;
        this.low = low;
        this.openTime = openTime;
        this.closeTime = closeTime;
        this.symbol = key;
    }
    public Double getOpen() {
        return open;
    }
    public Double getClose() {
        return close;
    }
    public Double getHigh() {
        return high;
    }
    public Double getLow() {
        return low;
    }
    public Long getOpenTime() {
        return openTime;
    }
    public Long getCloseTime() {
        return closeTime;
    }


    public String getSymbol() {
        return symbol;
    }

    @Override
    public String toString(){
        return symbol + " " + openTime + " " + open + " " + close + " " + high + " " + low + " ";
    }

    @Override
    public boolean equals(Object o){
        if( !(o instanceof Candlestick ))
            return false;
        Candlestick co = (Candlestick) o;
        return this.getOpenTime().equals(co.getOpenTime()) &&
                this.getSymbol().equals(co.getSymbol());

    }

}
