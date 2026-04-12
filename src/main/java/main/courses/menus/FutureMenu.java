package main.courses.menus;

import java.util.ArrayList;
import java.util.List;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

/**
 *
 * @author bott_ma
 */
public class FutureMenu implements InlineMenu
{

    @Override
    public InlineKeyboardMarkup getMenu() {
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        List<InlineKeyboardButton> row1 = new ArrayList<>();
        row1.add(InlineKeyboardButton.builder().text("+1").callbackData("future_1").build());
        row1.add(InlineKeyboardButton.builder().text("+2").callbackData("future_2").build());
        row1.add(InlineKeyboardButton.builder().text("+3").callbackData("future_3").build());
        row1.add(InlineKeyboardButton.builder().text("+4").callbackData("future_4").build());
        keyboard.add(row1);

        List<InlineKeyboardButton> row2 = new ArrayList<>();
        row1.add(InlineKeyboardButton.builder().text("+5").callbackData("future_5").build());
        row1.add(InlineKeyboardButton.builder().text("+6").callbackData("future_6").build());
        row1.add(InlineKeyboardButton.builder().text("+7").callbackData("future_7").build());
        row1.add(InlineKeyboardButton.builder().text("+8").callbackData("future_8").build());
        keyboard.add(row2);

        List<InlineKeyboardButton> row3 = new ArrayList<>();
        row3.add(InlineKeyboardButton.builder().text("Cancel").callbackData("cancel").build());
        keyboard.add(row3);

        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        markupInline.setKeyboard(keyboard);

        return markupInline;
    }
}
