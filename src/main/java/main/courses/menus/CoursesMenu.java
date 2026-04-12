package main.courses.menus;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

/**
 *
 * @author bott_ma
 */
public class CoursesMenu implements InlineMenu
{

    @Override
    public InlineKeyboardMarkup getMenu() {
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        List<InlineKeyboardButton> row1 = new ArrayList<>();
        row1.add(InlineKeyboardButton.builder().text(getTextDate(-6)).callbackData(getTextDateWithYear(-6)).build());
        row1.add(InlineKeyboardButton.builder().text(getTextDate(-5)).callbackData(getTextDateWithYear(-5)).build());
        row1.add(InlineKeyboardButton.builder().text(getTextDate(-4)).callbackData(getTextDateWithYear(-4)).build());
        keyboard.add(row1);
        List<InlineKeyboardButton> row2 = new ArrayList<>();
        row2.add(InlineKeyboardButton.builder().text(getTextDate(-3)).callbackData(getTextDateWithYear(-3)).build());
        row2.add(InlineKeyboardButton.builder().text(getTextDate(-2)).callbackData(getTextDateWithYear(-2)).build());
        row2.add(InlineKeyboardButton.builder().text(getTextDate(-1)).callbackData(getTextDateWithYear(-1)).build());
        keyboard.add(row2);

        List<InlineKeyboardButton> today = new ArrayList<>();
        LocalDate todayDate = LocalDate.now(ZoneId.of("Europe/Madrid"));
        String dayOfWeek = todayDate.getDayOfWeek().toString();
        // Capitalizar primera letra y resto en minúsculas
        dayOfWeek = dayOfWeek.substring(0, 1) + dayOfWeek.substring(1).toLowerCase();
        today.add(InlineKeyboardButton.builder().text("Today " + dayOfWeek).callbackData(getTextDateWithYear(0)).build());
        keyboard.add(today);

        List<InlineKeyboardButton> past1 = new ArrayList<>();
        past1.add(InlineKeyboardButton.builder().text(getTextDate(1)).callbackData(getTextDateWithYear(1)).build());
        past1.add(InlineKeyboardButton.builder().text(getTextDate(2)).callbackData(getTextDateWithYear(2)).build());
        past1.add(InlineKeyboardButton.builder().text(getTextDate(3)).callbackData(getTextDateWithYear(3)).build());
        keyboard.add(past1);
        List<InlineKeyboardButton> past2 = new ArrayList<>();
        past2.add(InlineKeyboardButton.builder().text(getTextDate(4)).callbackData(getTextDateWithYear(4)).build());
        past2.add(InlineKeyboardButton.builder().text(getTextDate(5)).callbackData(getTextDateWithYear(5)).build());
        past2.add(InlineKeyboardButton.builder().text(getTextDate(6)).callbackData(getTextDateWithYear(6)).build());
        keyboard.add(past2);

        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        markupInline.setKeyboard(keyboard);

        return markupInline;
    }

    public static String getTextDate(int days) {
        LocalDate fecha = LocalDate.now(ZoneId.of("Europe/Madrid")).plusDays(days);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM");
        return fecha.format(formatter);
    }

    public static String getTextDateWithYear(int days) {
        LocalDate fecha = LocalDate.now(ZoneId.of("Europe/Madrid")).plusDays(days);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/YYYY");
        return fecha.format(formatter);
    }
}
