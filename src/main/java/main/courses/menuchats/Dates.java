package main.courses.menuchats;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Utility class for handling date parsing and formatting.
 * Provides methods to convert dates between different string formats.
 * 
 * Author: bott_ma
 */
public class Dates
{
    /**
     * Date format with day, month, and year: dd/MM/yyyy
     */
    public static final DateTimeFormatter dateWithYear = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    /**
     * ISO standard date format: yyyy-MM-dd
     */
    public static final DateTimeFormatter isoDate = DateTimeFormatter.ISO_LOCAL_DATE;

    /**
     * Parses a string in the format dd/MM/yyyy into a {@link LocalDate} object.
     *
     * @param dateString the date string in dd/MM/yyyy format
     * @return the parsed {@link LocalDate}
     * @throws java.time.format.DateTimeParseException if the input string is not in the expected format
     */
    public static LocalDate parseDateWithYear(String dateString) {
        return LocalDate.parse(dateString, dateWithYear);
    }

    /**
     * Converts a date string from the format dd/MM/yyyy to the ISO format yyyy-MM-dd.
     *
     * @param dateString the date string in dd/MM/yyyy format
     * @return the formatted date string in yyyy-MM-dd format
     * @throws java.time.format.DateTimeParseException if the input string is not in the expected format
     */
    public static String convertToIsoFormat(String dateString) {
        LocalDate date = parseDateWithYear(dateString);
        return date.format(isoDate);
    }
}
