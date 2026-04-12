package main.courses.menus;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import main.contacts.Contact;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

/**
 * A menu that displays a list of contacts as buttons, sorted alphabetically by first name.
 * Each button displays the contact's first name, last name, and email.
 *
 * @author maxi
 */
public class ListMenu implements InlineMenu {
    
    private final List<Contact> contacts;
    private final boolean showMoreButton;
    
    /**
     * Creates a new ListMenu with the specified list of contacts
     * with "More..." button.
     * 
     * @param contacts The list of contacts to display in the menu
     */
    public ListMenu(List<Contact> contacts) {
        this(contacts, true);
    }
    
    /**
     * Creates a new ListMenu with the specified list of contacts
     * and control over the "More..." button.
     * 
     * @param contacts The list of contacts to display in the menu
     * @param showMoreButton Whether to show the "More..." button
     */
    public ListMenu(List<Contact> contacts, boolean showMoreButton) {
        this.contacts = contacts;
        this.showMoreButton = showMoreButton;
    }
    
    @Override
    public InlineKeyboardMarkup getMenu() {
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        
        // Sort contacts by first name (with null check to avoid NullPointerException)
        List<Contact> sortedContacts = contacts.stream()
                .sorted(Comparator.comparing(
                    c -> c.getFistName() != null ? c.getFistName().toLowerCase() : "", 
                    Comparator.nullsLast(String::compareTo)))
                .collect(Collectors.toList());
        
        for (Contact contact : sortedContacts) {
            // Create button text displaying name and email
            String buttonText = formatContactName(contact);
            
            // Create button with contact's email as callback data
            InlineKeyboardButton button = InlineKeyboardButton.builder()
                    .text(buttonText)
                    .callbackData(contact.getEmail())
                    .build();
            
            // Each contact gets its own row
            List<InlineKeyboardButton> row = new ArrayList<>();
            row.add(button);
            keyboard.add(row);
        }
        
        // Add "More..." button if enabled
        if (showMoreButton) {
            List<InlineKeyboardButton> moreRow = new ArrayList<>();
            InlineKeyboardButton moreButton = InlineKeyboardButton.builder()
                    .text("Show 10 more contacts...")
                    .callbackData("more")
                    .build();
            moreRow.add(moreButton);
            keyboard.add(moreRow);
        }
        
        // Always add "Cancel" button in the last row
        List<InlineKeyboardButton> cancelRow = new ArrayList<>();
        InlineKeyboardButton cancelButton = InlineKeyboardButton.builder()
                .text("Close")
                .callbackData("cancel")
                .build();
        cancelRow.add(cancelButton);
        keyboard.add(cancelRow);
        
        // Create and return the keyboard markup
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        markupInline.setKeyboard(keyboard);
        
        return markupInline;
    }
    
    /**
     * Formats the contact's name for display in a button.
     * 
     * @param contact The contact to format
     * @return A formatted string with the contact's name and email
     */
    private String formatContactName(Contact contact) {
        StringBuilder sb = new StringBuilder();
        
        // Add first name if available
        if (contact.getFistName() != null && !contact.getFistName().isEmpty()) {
            sb.append(contact.getFistName());
        }
        
        // Add last name if available
        if (contact.getLastName() != null && !contact.getLastName().isEmpty()) {
            if (sb.length() > 0) {
                sb.append(" ");
            }
            sb.append(contact.getLastName());
        }
        
        // Add email in parentheses
        if (sb.length() > 0) {
            sb.append(" (").append(contact.getEmail()).append(")");
        } else {
            // If no name available, just show email
            sb.append(contact.getEmail());
        }
        
        return sb.toString();
    }
}
