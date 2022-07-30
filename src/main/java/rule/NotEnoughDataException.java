package rule;

public class NotEnoughDataException extends Exception{
    NotEnoughDataException(){
        super("Data is not sufficient for rule evaluating.");
    }
}
