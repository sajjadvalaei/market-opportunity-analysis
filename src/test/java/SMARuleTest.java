import auxiliary.RuleAux;
import module.Interval;
import module.Period;
import org.junit.Assert;
import org.junit.Test;
import rule.Rule;

public class SMARuleTest {
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

}


