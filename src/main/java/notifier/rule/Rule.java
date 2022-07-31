package notifier.rule;

import notifier.exception.MemoryNotAcceptableException;
import notifier.exception.NotEnoughDataException;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/***
 * Definition is related to indicators in markets, and it tries to find them.
 * Each rule can be satisfied or not. Each rule has a memory.
 * TODO: Make @satisfy parameter Chandlestick to make sense more.
 */
public interface Rule {
    /***
     * maybe here is the most confusing part of my code, and it should be fixed.
     *  @Satisfy(symbol) is checking whether the last candlestick from the symbol market
     *  added to the memory satisfies the rule or not.
      */
    public boolean satisfy(String symbol) throws NotEnoughDataException;

    RuleMemory getMemory();

    void setMemory(RuleMemory memory) throws MemoryNotAcceptableException;

    /***
     * Factory and parse form String was necessary because I assume that the user is writing rules in the source file.
     */
    public class Factory{
        public static Rule create(String input){
            if(input.startsWith("SMA"))
                return SMARule.parseSMA(input);
            throw new RuntimeException("Input format not acceptable.");
        }

        public static List<Rule> load(String address) throws FileNotFoundException {
            File file  = new File(address);
            Scanner sc = new Scanner(file);
            List<Rule> rules = new ArrayList<>();
            while( sc.hasNext() ){
                rules.add( create(sc.nextLine()) );
            }
            sc.close();
            return rules;
        }
    }
}

