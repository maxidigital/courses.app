package main.courses.menus;

import java.util.ArrayList;
import java.util.List;
import main.contacts.Contact;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

/**
 * Inline menu for multiple medical form submission reminders
 * Provides options to send reminder emails to multiple participants
 *
 * @author maxi
 */
public class MultiMedicalFormReminderMenu implements InlineMenu {

    private final List<Contact> contactsWithoutMedical;
    
    public MultiMedicalFormReminderMenu(List<Contact> contactsWithoutMedical) {
        this.contactsWithoutMedical = contactsWithoutMedical;
    }

    @Override
    public InlineKeyboardMarkup getMenu() {
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        // One row per participant without medical form (excluding those already reminded)
        for (Contact contact : contactsWithoutMedical) {
            try {
                // Skip if reminder was already sent
                if (main.sheets.RemindersSheetsAdmin.getInstance().isMedicalFormMissedReminderSent(contact.getEmail())) {
                    continue;
                }
                
                String firstName = contact.getFistName();
                if (firstName == null || firstName.trim().isEmpty()) {
                    firstName = contact.getEmail();
                } else {
                    firstName = firstName.trim().split("\\s+")[0];
                }
                
                List<InlineKeyboardButton> row = new ArrayList<>();
                row.add(InlineKeyboardButton.builder()
                        .text("Remind " + firstName + " about medical form")
                        .callbackData("medical_reminder:" + contact.getEmail())
                        .build());
                keyboard.add(row);
            } catch (Exception e) {
                java.util.logging.Logger.getLogger(MultiMedicalFormReminderMenu.class.getName())
                    .log(java.util.logging.Level.SEVERE, "Error building button for contact", e);
            }
        }
        
        // Cancel button at the bottom
        List<InlineKeyboardButton> cancelRow = new ArrayList<>();
        cancelRow.add(InlineKeyboardButton.builder()
                .text("Cancel")
                .callbackData("reminder_cancel_all")
                .build());
        keyboard.add(cancelRow);

        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        markupInline.setKeyboard(keyboard);

        return markupInline;
    }
}