package notifier.exception;

public class NotEnoughDataException extends Exception{
    public NotEnoughDataException(){
        super("Data is not sufficient for rule evaluating.");
    }
}
