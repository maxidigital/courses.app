package main.courses.menus;

import java.util.ArrayList;
import java.util.List;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

public class CourseLocationMenu implements InlineMenu {

    public static final String[] LOCATIONS = {
        "Cala Sant Vicent",
        "Cala Deia",
        "Puerto de Pollença",
        "Puerto de Andratx"
    };

    public static final String[] LOCATION_MAPS_URLS = {
        "https://www.google.com/maps/search/?api=1&query=Cala+Sant+Vicent+Mallorca",
        "https://www.google.com/maps/search/?api=1&query=Cala+Deia+Mallorca",
        "https://www.google.com/maps/search/?api=1&query=Puerto+de+Pollença+Mallorca",
        "https://www.google.com/maps/search/?api=1&query=Puerto+de+Andratx+Mallorca"
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
