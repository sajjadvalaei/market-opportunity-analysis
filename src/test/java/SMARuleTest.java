import auxiliary.RuleAux;
import module.Candlestick;
import org.junit.Assert;
import org.junit.Test;
import rule.NotEnoughDataException;
import rule.Rule;
import rule.RuleMemoryService;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class SMARuleTest {
    private static final String ADDRESS = "src/test/resources/rule/random_rules.rl";
    private static final int RULES_NUM = 100;

    @Test
    public void createFromStringTest(){
        Rule rule = RuleAux.createRandomRule();
        Rule readerRule = Rule.Factory.create(rule.toString());
        Assert.assertEquals(rule, readerRule);
    }
    @Test
    public void notEqualityTest(){
        Rule rule = RuleAux.createRandomRule();
        Rule other = RuleAux.createRandomRule();
        Assert.assertNotEquals(rule,other);
    }
    @Test
    public void writeCustomRuleToFile_testReadFromThat_mustBeEqual() throws FileNotFoundException{
        File file = new File(ADDRESS);
        PrintWriter printWriter = new PrintWriter(file);
        String s = "SMA 10 D OPEN > 11 H OPEN";
        printWriter.println(s);
        printWriter.close();

        Rule other = Rule.Factory.create(s);
        Assert.assertTrue(Rule.Factory.load(ADDRESS).contains(other));
    }
    @Test
    public void  writeRulesToFile_testReadRulesFromThat_mustBeEqual() throws FileNotFoundException {
        List<Rule> other = RuleAux.createRandomList(RULES_NUM);
        File file = new File(ADDRESS);
        RuleAux.writeToFile(other, file);
        List<Rule> rules = Rule.Factory.load(ADDRESS);
        Assert.assertTrue(rules.containsAll(other));
    }
    @Test
    public void addDataToRuleMemory_checkSMARuleSatisfy_mustBeTrue() throws NotEnoughDataException {
        Rule rule = Rule.Factory.create("SMA 2 M OPEN > 1 M CLOSE");
        List<Candlestick> candles = new ArrayList<>();
        candles.add(new Candlestick(10.0,1.0,0.0,0.0,0L,0L,"test"));
        candles.add(new Candlestick(10.0,1.0,0.0,0.0,60L,60L,"test"));
        RuleMemoryService memory = rule.getMemory();
        candles.forEach(memory::append);
        Assert.assertTrue(rule.satisfy("test"));
    }

    @Test
    public void addDataToRuleMemory_checkSMARuleSatisfy_museBeFalse() throws NotEnoughDataException {
        Rule rule = Rule.Factory.create("SMA 1 M OPEN < 2 M CLOSE");
        List<Candlestick> candles = new ArrayList<>();
        candles.add(new Candlestick(10.0,1.0,0.0,0.0,0L,0L,"test"));
        candles.add(new Candlestick(10.0,1.0,0.0,0.0,60L,60L,"test"));
        RuleMemoryService memory = rule.getMemory();
        candles.forEach(memory::append);
        Assert.assertFalse(rule.satisfy("test"));
    }
    @Test
    public void addMultiSymbolDataToRuleMemory_checkSMARuleSatisfy_mustBeFalse() throws NotEnoughDataException {
        Rule rule = Rule.Factory.create("SMA 1 M OPEN < 2 M CLOSE");
        List<Candlestick> candles = new ArrayList<>();
        candles.add(new Candlestick(10.0,1.0,0.0,0.0,0L,0L,"test"));
        candles.add(new Candlestick(10.0,1000.0,0.0,0.0,60L,60L,"wrong"));
        candles.add(new Candlestick(10.0,1.0,0.0,0.0,60L,60L,"test"));
        RuleMemoryService memory = rule.getMemory();
        candles.forEach(memory::append);
        Assert.assertFalse(rule.satisfy("test"));
    }

    @Test
    public void addSeveralDataToRuleMemory_checkVariousSMARTRuleSatisfy() throws NotEnoughDataException {
        List<Candlestick> candles = new ArrayList<>();
        for (int i = 0; i < 60*46; i++){
            candles.add(new Candlestick(10.0,1.0,10.0,0.0,0L,0L,"test"));
        }
        for (int i = 0; i < 118; i++){
            candles.add(new Candlestick(10.0,1.0,0.0,0.0,0L,0L,"test"));
        }
        candles.add(new Candlestick(1.0,10.0,0.0,0.0,0L,0L,"test"));
        candles.add(new Candlestick(1.0,10.0,0.0,0.0,0L,0L,"test"));
        Rule dRule = Rule.Factory.create("SMA 1 D HIGH < 2 D HIGH");
        RuleMemoryService memory = dRule.getMemory();
        candles.forEach(memory::append);
        Assert.assertTrue(dRule.satisfy("test"));
        Rule hRule = Rule.Factory.create("SMA 1 H OPEN > 2 H CLOSE");
        Assert.assertTrue(hRule.satisfy("test"));

        Rule mRule = Rule.Factory.create("SMA 1 M OPEN > 2 M CLOSE");
        Assert.assertFalse(mRule.satisfy("test"));
    }

}


