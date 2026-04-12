package main.courses.menus;

import java.util.ArrayList;
import java.util.List;
import main.calendar.Course;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

public class CourseSelectionMenu implements InlineMenu {

    private final String isoDate;
    private final List<Course> courses;

    public CourseSelectionMenu(String isoDate, List<Course> courses) {
        this.isoDate = isoDate;
        this.courses = courses;
    }

    @Override
    public InlineKeyboardMarkup getMenu() {
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        for (int i = 0; i < courses.size(); i++) {
            List<InlineKeyboardButton> row = new ArrayList<>();
            row.add(InlineKeyboardButton.builder()
                    .text(courses.get(i).getType())
                    .callbackData("course_remind_select:" + isoDate + ":" + i)
                    .build());
            keyboard.add(row);
        }
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        markup.setKeyboard(keyboard);
        return markup;
    }
}
