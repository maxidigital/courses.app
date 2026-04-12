package main.courses.menus;

import java.util.ArrayList;
import java.util.List;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

/**
 * Inline menu for medical form submission reminders
 * Provides options to send reminder email or cancel
 *
 * @author maxi
 */
public class MedicalFormReminderMenu implements InlineMenu {

    private final String email;
    private final String firstName;
    
    public MedicalFormReminderMenu(String email, String firstName) {
        this.email = email;
        // Extract only the first word if the firstName contains multiple words
        String[] nameParts = firstName.trim().split("\\s+");
        this.firstName = nameParts[0];
    }

    @Override
    public InlineKeyboardMarkup getMenu() {
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        // One row for the reminder button
        List<InlineKeyboardButton> row1 = new ArrayList<>();
        row1.add(InlineKeyboardButton.builder()
                .text("Remind " + firstName + " about medical form")
                .callbackData("medical_reminder:" + email)
                .build());
        keyboard.add(row1);
        
        // Separate row for cancel button
        List<InlineKeyboardButton> row2 = new ArrayList<>();
        row2.add(InlineKeyboardButton.builder()
                .text("Cancel")
                .callbackData("reminder_cancel:" + email)
                .build());
        keyboard.add(row2);

        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        markupInline.setKeyboard(keyboard);

        return markupInline;
    }
}