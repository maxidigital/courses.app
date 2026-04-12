package main.courses.menus;

import java.util.ArrayList;
import java.util.List;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

public class CourseStartTimeMenu implements InlineMenu {

    private final String isoDate;
    private final int courseIndex;

    public CourseStartTimeMenu(String isoDate, int courseIndex) {
        this.isoDate = isoDate;
        this.courseIndex = courseIndex;
    }

    @Override
    public InlineKeyboardMarkup getMenu() {
        List<InlineKeyboardButton> row = new ArrayList<>();
        row.add(InlineKeyboardButton.builder()
                .text("9:00 AM")
                .callbackData("course_remind_time:" + isoDate + ":" + courseIndex + ":9")
                .build());
        row.add(InlineKeyboardButton.builder()
                .text("10:00 AM")
                .callbackData("course_remind_time:" + isoDate + ":" + courseIndex + ":10")
                .build());

        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        keyboard.add(row);

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        markup.setKeyboard(keyboard);
        return markup;
    }
}
