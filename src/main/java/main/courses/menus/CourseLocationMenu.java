package main.courses.menus;

import java.util.ArrayList;
import java.util.List;
import main.sheets.SettingsService;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

public class CourseLocationMenu implements InlineMenu {

    private final String isoDate;
    private final int courseIndex;
    private final String hour;

    public CourseLocationMenu(String isoDate, int courseIndex, String hour) {
        this.isoDate = isoDate;
        this.courseIndex = courseIndex;
        this.hour = hour;
    }

    public static String getName(int index) {
        List<String> names = SettingsService.getInstance().getLocationNames();
        return index < names.size() ? names.get(index) : "Unknown";
    }

    public static String getUrl(int index) {
        List<String> urls = SettingsService.getInstance().getLocationUrls();
        return index < urls.size() ? urls.get(index) : "";
    }

    @Override
    public InlineKeyboardMarkup getMenu() {
        List<String> names = SettingsService.getInstance().getLocationNames();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        for (int i = 0; i < names.size(); i += 2) {
            List<InlineKeyboardButton> row = new ArrayList<>();
            for (int j = i; j < Math.min(i + 2, names.size()); j++) {
                row.add(InlineKeyboardButton.builder()
                        .text(names.get(j))
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
