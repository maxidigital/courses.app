package main.courses.menus;

import java.util.ArrayList;
import java.util.List;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

public class Day2ConfirmMenu implements InlineMenu {

    private final String isoDate;
    private final int courseIndex;

    public Day2ConfirmMenu(String isoDate, int courseIndex) {
        this.isoDate = isoDate;
        this.courseIndex = courseIndex;
    }

    @Override
    public InlineKeyboardMarkup getMenu() {
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        List<InlineKeyboardButton> row1 = new ArrayList<>();
        row1.add(InlineKeyboardButton.builder()
                .text("✅ Send Day 2 emails")
                .callbackData("day2_confirm_send:" + isoDate + ":" + courseIndex)
                .build());
        keyboard.add(row1);

        List<InlineKeyboardButton> row2 = new ArrayList<>();
        row2.add(InlineKeyboardButton.builder()
                .text("❌ Cancel")
                .callbackData("day2_confirm_cancel:" + isoDate + ":" + courseIndex)
                .build());
        keyboard.add(row2);

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        markup.setKeyboard(keyboard);
        return markup;
    }
}
