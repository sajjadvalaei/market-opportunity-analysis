package rule;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public interface Rule {
    public boolean satisfy(String key) throws NotEnoughDataException;

    RuleMemoryService getMemory();

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

