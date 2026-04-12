/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package main.courses.menuchats;

import blue.underwater.telegram.admin.TelegramAdmin;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import main.contacts.Contact;
import main.contacts.ContactsService;
import main.courses.ContactTelegramMessage;
import main.courses.menus.ListMenu;
import main.telegram.TelegramCenter;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/**
 * Menu chat for searching contacts by name or email
 * 
 * @author maxi
 */
public class FindMenuChat implements MenuChat {
    
    private final long chatId;
    private long messageId = 0;
    private final TelegramAdmin telegram;
    private String searchTerm;
    private final TelegramChatMain chatMain;

    /**
     * Constructor
     * 
     * @param telegram Telegram admin instance
     * @param chatId Chat ID to send messages to
     * @param searchTerm Initial search term
     * @param chatMain Reference to the main chat handler
     */
    public FindMenuChat(TelegramAdmin telegram, long chatId, String searchTerm, TelegramChatMain chatMain) {
        this.chatId = chatId;
        this.telegram = telegram;
        this.searchTerm = searchTerm;
        this.chatMain = chatMain;
    }
    
    @Override
    public void callbackQueryReceived(CallbackQuery callbackQuery) {
        String callData = callbackQuery.getData();
        
        TelegramCenter.getInstance().toRoot(chatId, callData);
        
        if(callData.equals("cancel")) {
            try {
                telegram.editMessage(chatId, messageId, "Search cancelled");
            } catch (TelegramApiException ex) {
                Logger.getLogger(FindMenuChat.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else if(callData.equals("search_again")) {
            try {
                telegram.editMessage(chatId, messageId, "Please send a new search term");
                // Enable find mode to accept the next message as a search term
                chatMain.setFindMode(true);
            } catch (TelegramApiException ex) {
                Logger.getLogger(FindMenuChat.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            // User selected a contact
            Contact contact = ContactsService.getInstance().findByEmail(callData);
            if(contact != null) {
                // Log contact selection to root
                TelegramCenter.getInstance().toRoot(
                    "Contact selected: " + contact.getEmail() + " [chatId: " + chatId + "]"
                );
                
                String tm = ContactTelegramMessage.toTelegramMessageFull(contact);
                try {
                    telegram.editMessage(chatId, messageId, tm);
                } catch (TelegramApiException ex) {
                    Logger.getLogger(FindMenuChat.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    @Override
    public long getMessageId() {
        return messageId;
    }

    @Override
    public void reply() {
        try {
            // Search for contacts using the search term
            List<Contact> contacts = ContactsService.getInstance().find(searchTerm);
            
            // Log search to root
            TelegramCenter.getInstance().toRoot(
                "Contact search: \"" + searchTerm + "\" - Found " + contacts.size() + " contacts [chatId: " + chatId + "]"
            );
            
            String messageText;
            InlineKeyboardMarkup keyboard;
            
            if(contacts.isEmpty()) {
                messageText = "No contacts found for: \"" + searchTerm + "\"";
                keyboard = createEmptyResultsMenu();
            } else {
                messageText = "Found " + contacts.size() + " contacts for: \"" + searchTerm + "\"";
                // Use ListMenu to display search results
                ListMenu menu = new ListMenu(contacts, false); // false = don't show "more" button
                keyboard = menu.getMenu();
            }
            
            if(messageId == 0) {
                Message message = this.telegram.sendReplyKeyboard(chatId, messageText, keyboard);
                this.messageId = message.getMessageId();
            } else {
                this.telegram.editMessageWithMenu(chatId, messageId, messageText, keyboard);
            }
        } catch (TelegramApiException ex) {
            Logger.getLogger(FindMenuChat.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Creates a menu for when no results are found
     * 
     * @return InlineKeyboardMarkup with search again and cancel options
     */
    private InlineKeyboardMarkup createEmptyResultsMenu() {
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        
        // First row: Search Again
        List<InlineKeyboardButton> rowInline = new ArrayList<>();
        InlineKeyboardButton searchAgainButton = InlineKeyboardButton.builder()
                .text("🔍 Search Again")
                .callbackData("search_again")
                .build();
        rowInline.add(searchAgainButton);
        rowsInline.add(rowInline);
        
        // Second row: Cancel
        rowInline = new ArrayList<>();
        InlineKeyboardButton cancelButton = InlineKeyboardButton.builder()
                .text("❌ Cancel")
                .callbackData("cancel")
                .build();
        rowInline.add(cancelButton);
        rowsInline.add(rowInline);
        
        markupInline.setKeyboard(rowsInline);
        return markupInline;
    }
    
    /**
     * Updates the search term and performs a new search
     * 
     * @param newSearchTerm The new search term to use
     */
    public void updateSearchTerm(String newSearchTerm) {
        this.searchTerm = newSearchTerm;
        reply();
    }
}