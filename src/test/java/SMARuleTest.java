import auxiliary.RuleAux;
import common.candlestick.Candlestick;
import notifier.exception.MemoryNotAcceptableException;
import notifier.rule.SMARuleMemory;
import org.junit.Assert;
import org.junit.Test;
import notifier.exception.NotEnoughDataException;
import notifier.rule.Rule;
import notifier.rule.RuleMemory;

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
    public void addDataToRuleMemory_checkSMARuleSatisfy_mustBeTrue() throws NotEnoughDataException, MemoryNotAcceptableException {
        Rule rule = Rule.Factory.create("SMA 2 M OPEN > 1 M CLOSE");
        rule.setMemory( new SMARuleMemory());
        List<Candlestick> candles = new ArrayList<>();
        candles.add(new Candlestick(10.0,1.0,0.0,0.0,0L,0L,"test"));
        candles.add(new Candlestick(10.0,1.0,0.0,0.0,60L,60L,"test"));
        RuleMemory memory = rule.getMemory();
        candles.forEach(memory::append);
        Assert.assertTrue(rule.satisfy("test"));
    }

    @Test
    public void addDataToRuleMemory_checkSMARuleSatisfy_museBeFalse() throws NotEnoughDataException, MemoryNotAcceptableException {
        Rule rule = Rule.Factory.create("SMA 1 M OPEN < 2 M CLOSE");
        rule.setMemory(new SMARuleMemory());
        List<Candlestick> candles = new ArrayList<>();
        candles.add(new Candlestick(10.0,1.0,0.0,0.0,0L,0L,"test"));
        candles.add(new Candlestick(10.0,1.0,0.0,0.0,60L,60L,"test"));
        RuleMemory memory = rule.getMemory();
        candles.forEach(memory::append);
        Assert.assertFalse(rule.satisfy("test"));
    }
    @Test
    public void addMultiSymbolDataToRuleMemory_checkSMARuleSatisfy_mustBeFalse() throws NotEnoughDataException, MemoryNotAcceptableException {
        Rule rule = Rule.Factory.create("SMA 1 M OPEN < 2 M CLOSE");
        rule.setMemory(new SMARuleMemory());
        List<Candlestick> candles = new ArrayList<>();
        candles.add(new Candlestick(10.0,1.0,0.0,0.0,0L,0L,"test"));
        candles.add(new Candlestick(10.0,1000.0,0.0,0.0,60L,60L,"wrong"));
        candles.add(new Candlestick(10.0,1.0,0.0,0.0,60L,60L,"test"));
        RuleMemory memory = rule.getMemory();
        candles.forEach(memory::append);
        Assert.assertFalse(rule.satisfy("test"));
    }

    @Test
    public void addSeveralDataToRuleMemory_checkVariousSMARTRuleSatisfy() throws NotEnoughDataException, MemoryNotAcceptableException {
        List<Candlestick> candles = new ArrayList<>();
        SMARuleMemory memory = new SMARuleMemory();
        for (int i = 0; i < 60*46; i++){
            candles.add(new Candlestick(10.0,1.0,10.0,0.0,0L,0L,"test"));
        }
        for (int i = 0; i < 118; i++){
            candles.add(new Candlestick(10.0,1.0,0.0,0.0,0L,0L,"test"));
        }
        candles.add(new Candlestick(1.0,10.0,0.0,0.0,0L,0L,"test"));
        candles.add(new Candlestick(1.0,10.0,0.0,0.0,0L,0L,"test"));
        candles.forEach(memory::append);
        Rule dRule = Rule.Factory.create("SMA 1 D HIGH < 2 D HIGH");
        dRule.setMemory(memory);
        Assert.assertTrue(dRule.satisfy("test"));
        Rule hRule = Rule.Factory.create("SMA 1 H OPEN > 2 H CLOSE");
        hRule.setMemory(memory);
        Assert.assertTrue(hRule.satisfy("test"));
        Rule mRule = Rule.Factory.create("SMA 1 M OPEN > 2 M CLOSE");
        mRule.setMemory(memory);
        Assert.assertFalse(mRule.satisfy("test"));
    }

}


