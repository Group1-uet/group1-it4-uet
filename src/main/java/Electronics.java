import java.time.LocalDate;

public class Electronics extends Items{
    public Electronics(String id) {
        super(id);
    }

    public Electronics(String id, String name) {
        super(id, name);
    }

    public Electronics(String id, String name, String descrpition, double startingPrice, double currentPrice, LocalDate timeStart, LocalDate timeEnd) {
        super(id, name, descrpition, startingPrice, currentPrice, timeStart, timeEnd);
    }

}
