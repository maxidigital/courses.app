package main.courses.menus;

import java.util.ArrayList;
import java.util.List;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

public class CourseStartTimeMenu implements InlineMenu {

    private static final String[] LABELS   = {"9:00", "9:30", "10:00", "10:30", "11:00", "11:30", "12:00", "12:30"};
    private static final String[] TIME_STR = {"9:00 AM", "9:30 AM", "10:00 AM", "10:30 AM", "11:00 AM", "11:30 AM", "12:00 PM", "12:30 PM"};

    private final String isoDate;
    private final int courseIndex;
    private final int day;
    private final String timesPrev;
    private final String locsPrev;

    public CourseStartTimeMenu(String isoDate, int courseIndex, int day, String timesPrev, String locsPrev) {
        this.isoDate = isoDate;
        this.courseIndex = courseIndex;
        this.day = day;
        this.timesPrev = timesPrev;
        this.locsPrev = locsPrev;
    }

    public static String slotLabel(int slot) {
        return LABELS[slot];
    }

    public static String slotToTimeStr(int slot) {
        return TIME_STR[slot];
    }

    @Override
    public InlineKeyboardMarkup getMenu() {
        String base = isoDate + ":" + courseIndex + ":" + day + ":" + timesPrev + ":" + locsPrev + ":";
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        // 4 buttons per row
        for (int i = 0; i < LABELS.length; i += 4) {
            List<InlineKeyboardButton> row = new ArrayList<>();
            for (int j = i; j < Math.min(i + 4, LABELS.length); j++) {
                row.add(InlineKeyboardButton.builder()
                        .text(LABELS[j])
                        .callbackData("course_remind_time:" + base + j)
                        .build());
            }
            keyboard.add(row);
        }
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        markup.setKeyboard(keyboard);
        return markup;
    }
}
