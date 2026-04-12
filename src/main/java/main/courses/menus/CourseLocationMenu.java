package main.courses.menus;

import java.util.ArrayList;
import java.util.List;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

public class CourseLocationMenu implements InlineMenu {

    public static final String[] LOCATIONS = {
        "Port de Sóller",
        "Cala Deia",
        "Puerto Portals",
        "Cala Llombards"
    };

    private final String isoDate;
    private final int courseIndex;
    private final String hour;

    public CourseLocationMenu(String isoDate, int courseIndex, String hour) {
        this.isoDate = isoDate;
        this.courseIndex = courseIndex;
        this.hour = hour;
    }

    @Override
    public InlineKeyboardMarkup getMenu() {
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        for (int i = 0; i < LOCATIONS.length; i += 2) {
            List<InlineKeyboardButton> row = new ArrayList<>();
            for (int j = i; j < Math.min(i + 2, LOCATIONS.length); j++) {
                row.add(InlineKeyboardButton.builder()
                        .text(LOCATIONS[j])
                        .callbackData("course_remind_loc:" + isoDate + ":" + courseIndex + ":" + hour + ":" + j)
                        .build());
            }
            keyboard.add(row);
        }
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        markup.setKeyboard(keyboard);
        return markup;
    }
}
