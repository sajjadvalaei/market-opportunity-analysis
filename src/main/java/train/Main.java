package train;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

public class Main {
    public static void main(String[] args) {
        LinkedList<String> names = new LinkedList<>();

        names.addFirst("Welcome");
        names.addFirst("To");
        names.addFirst("Gfg");

        // Getting ListIterator
        ListIterator<String> namesIterator
                = names.listIterator();

        // Traversing elements using next() method
        while (namesIterator.hasNext()) {
            System.out.println(namesIterator.next());
        }
    }
}
