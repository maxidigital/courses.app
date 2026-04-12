package main.courses.menus;

import java.util.ArrayList;
import java.util.List;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

/**
 * Inline menu for medical form notifications without health issues
 * Provides options to send "all set" email or cancel
 *
 * @author maxi
 */
public class AllSetMenu implements InlineMenu {

    private final String email;
    
    public AllSetMenu(String email) {
        this.email = email;
    }

    @Override
    public InlineKeyboardMarkup getMenu() {
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        // Row with two buttons
        List<InlineKeyboardButton> row1 = new ArrayList<>();
        row1.add(InlineKeyboardButton.builder()
                .text("Send 'All set' email")
                .callbackData("allset_email:" + email)
                .build());
        row1.add(InlineKeyboardButton.builder()
                .text("Cancel")
                .callbackData("allset_cancel:" + email)
                .build());
        
        keyboard.add(row1);

        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        markupInline.setKeyboard(keyboard);

        return markupInline;
    }
}