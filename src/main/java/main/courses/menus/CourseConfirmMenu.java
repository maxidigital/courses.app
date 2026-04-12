package main.courses.menus;

import java.util.ArrayList;
import java.util.List;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

public class CourseConfirmMenu implements InlineMenu {

    private final String isoDate;
    private final int courseIndex;
    private final String times;
    private final String locs;

    public CourseConfirmMenu(String isoDate, int courseIndex, String times, String locs) {
        this.isoDate = isoDate;
        this.courseIndex = courseIndex;
        this.times = times;
        this.locs = locs;
    }

    @Override
    public InlineKeyboardMarkup getMenu() {
        String base = isoDate + ":" + courseIndex + ":" + times + ":" + locs;
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        List<InlineKeyboardButton> row1 = new ArrayList<>();
        row1.add(InlineKeyboardButton.builder()
                .text("✉️ Send emails")
                .callbackData("course_confirm_send:" + base)
                .build());
        keyboard.add(row1);

        List<InlineKeyboardButton> row2 = new ArrayList<>();
        row2.add(InlineKeyboardButton.builder()
                .text("❌ Cancel")
                .callbackData("course_confirm_cancel:" + base)
                .build());
        keyboard.add(row2);

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        markup.setKeyboard(keyboard);
        return markup;
    }
}
