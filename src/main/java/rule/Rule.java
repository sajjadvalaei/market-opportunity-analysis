package rule;

public interface Rule {
    public boolean satisfy();

    public class Factory{
        public static Rule create(String input){
            if(input.startsWith("SMA"))
                return SMARule.parseSMA(input);
            throw new RuntimeException("Input format not acceptable.");
        }
    }
}

