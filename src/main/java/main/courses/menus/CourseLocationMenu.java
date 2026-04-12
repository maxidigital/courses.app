package main.courses.menus;

import java.util.ArrayList;
import java.util.List;
import main.sheets.SettingsService;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

public class CourseLocationMenu implements InlineMenu {

    private final String isoDate;
    private final int courseIndex;
    private final int day;
    private final String times;
    private final String locsPrev;

    public CourseLocationMenu(String isoDate, int courseIndex, int day, String times, String locsPrev) {
        this.isoDate = isoDate;
        this.courseIndex = courseIndex;
        this.day = day;
        this.times = times;
        this.locsPrev = locsPrev;
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
        String base = isoDate + ":" + courseIndex + ":" + day + ":" + times + ":" + locsPrev + ":";
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        for (int i = 0; i < names.size(); i += 2) {
            List<InlineKeyboardButton> row = new ArrayList<>();
            for (int j = i; j < Math.min(i + 2, names.size()); j++) {
                row.add(InlineKeyboardButton.builder()
                        .text(names.get(j))
                        .callbackData("course_remind_loc:" + base + j)
                        .build());
            }
            keyboard.add(row);
        }
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        markup.setKeyboard(keyboard);
        return markup;
    }
}
