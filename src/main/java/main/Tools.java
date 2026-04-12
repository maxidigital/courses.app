package main;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 *
 * @author bott_ma
 */
public class Tools
{

    private final static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy");

    public static String getTextDate(int ordinal) {
        LocalDate targetDate = LocalDate.now().plusDays(ordinal);
        String format = targetDate.format(dateTimeFormatter);
        return format;
    }

}
