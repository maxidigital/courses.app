package main.courses.menus;

import java.util.ArrayList;
import java.util.List;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

public class CourseStartTimeMenu implements InlineMenu {

    private final String isoDate;

    public CourseStartTimeMenu(String isoDate) {
        this.isoDate = isoDate;
    }

    @Override
    public InlineKeyboardMarkup getMenu() {
        List<InlineKeyboardButton> row = new ArrayList<>();
        row.add(InlineKeyboardButton.builder()
                .text("9:00 AM")
                .callbackData("course_time:" + isoDate + ":9")
                .build());
        row.add(InlineKeyboardButton.builder()
                .text("10:00 AM")
                .callbackData("course_time:" + isoDate + ":10")
                .build());

        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        keyboard.add(row);

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        markup.setKeyboard(keyboard);
        return markup;
    }
}
