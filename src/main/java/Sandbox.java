import io.vavr.control.Try;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.stream.Stream;

public class Sandbox {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");

    private static Try<LocalDate> parseDate(String dateString) {
        return Try.of(() -> LocalDate.from(formatter.parse(dateString)));
    }

    public static void main(String args[]) {
        Stream.of("12/31/2014",
                "01-01-2015",
                "12/31/2015",
                "not a date",
                "01/01/2016")
                .map(Sandbox::parseDate)//Parse String to LocalDate
                .peek(v -> v.onFailure(t -> System.out.println("Failed due to " + t.getMessage())))//Print error on failure
                .filter(Try::isSuccess)//Filter valids
                .map(Try::get)//Get wrapped value
                .map(DayOfWeek::from)//Map to day of week
                .forEach(System.out::println);//Print
    }

    Try<Integer> divide(Integer dividend, Integer divisor) {
        return Try.of(() -> dividend / divisor);
    }

}
