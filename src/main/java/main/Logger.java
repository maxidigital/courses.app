package main;

import blue.underwater.commons.tools.FileTools;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.logging.Level;

/**
 *
 * @author bott_ma
 */
public class Logger
{

    private static final String DIR_NAME = "./logs/";
    private static final String FILE_NAME = getDateTimeForFileName() + "_logger.txt";
    private static final DateFormat dfmt = new SimpleDateFormat("yyyyMMdd HH:mm:ss.SSS");

    static {
        File dir = new File(DIR_NAME);
        dir.mkdirs();
    }

    public static void log(String text, Object... args) {
        text = dfmt.format(new Date(System.currentTimeMillis())) + ": " + String.format(text, args);
        try {
            FileTools.appendToTextFile(DIR_NAME + FILE_NAME, text);
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(Logger.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void print(String text, Object... args) {
        text = dfmt.format(new Date(System.currentTimeMillis())) + ": " + String.format(text, args);
        System.out.println(text);
    }

    public static String getDateTimeForFileName() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
        return now.format(formatter);
    }
}
