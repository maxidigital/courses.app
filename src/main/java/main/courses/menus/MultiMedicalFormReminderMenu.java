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
                String lastName = contact.getLastName();
                
                // Debug logging
                System.out.println("DEBUG - Original firstName: '" + firstName + "', lastName: '" + lastName + "'");
                
                if (firstName == null || firstName.trim().isEmpty()) {
                    firstName = contact.getEmail();
                } else {
                    // Extract only the first word if the firstName contains multiple words
                    String[] nameParts = firstName.trim().split("\\s+");
                    if (nameParts.length > 1) {
                        System.out.println("DEBUG - firstName contains multiple words: " + nameParts.length);
                    }
                    firstName = nameParts[0];
                }
                
                System.out.println("DEBUG - Using firstName for button: '" + firstName + "'");
                
                List<InlineKeyboardButton> row = new ArrayList<>();
                row.add(InlineKeyboardButton.builder()
                        .text("Remind " + firstName + " about medical form")
                        .callbackData("medical_reminder:" + contact.getEmail())
                        .build());
                keyboard.add(row);
            } catch (Exception e) {
                // Log error but continue with next contact
                e.printStackTrace();
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