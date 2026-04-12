/*
 */
package main.courses.menuchats;

import blue.underwater.telegram.admin.TelegramAdmin;
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
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/**
 *
 * @author maxi
 */
public class ListMenuChat implements MenuChat {
    
    private final long chatId;
    private long messageId = 0;
    private final TelegramAdmin telegram;
    private int round = 1;

    /**
     * 
     * @param telegram
     * @param chatId 
     */
    public ListMenuChat(TelegramAdmin telegram, long chatId) {
        this.chatId = chatId;
        this.telegram = telegram;
    }
    
    @Override
    public void callbackQueryReceived(CallbackQuery callbackQuery) {
        String callData = callbackQuery.getData();
        
        TelegramCenter.getInstance().toRoot(chatId, callData);
        
        if(callData.equals("more")) {
            reply();
        }
        else if(callData.equals("cancel")) {
            try {
                //this.telegram.deleteMessage(chatId, messageId);
                telegram.editMessage(chatId, messageId, "Contacts list closed");
            } catch (TelegramApiException ex) {
                Logger.getLogger(ListMenuChat.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            Contact contact = ContactsService.getInstance().findByEmail(callData);
            
            // Log contact selection to root
            if (contact != null) {
                TelegramCenter.getInstance().toRoot(chatId,
                    "👤 Contact selected: %s", contact.getEmail()
                );
            }
            
            String tm = ContactTelegramMessage.toTelegramMessageFull(contact);
            try {
                telegram.editMessage(chatId, messageId, tm);
            } catch (TelegramApiException ex) {
                Logger.getLogger(ListMenuChat.class.getName()).log(Level.SEVERE, null, ex);
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
            List<Contact> contacts = ContactsService.getInstance().getLastContacts(10 * round++);
            
            // Log list request to root
            TelegramCenter.getInstance().toRoot(chatId,
                "📋 Contact list requested - Showing %d contacts", contacts.size()
            );
            
            ListMenu menu = new ListMenu(contacts, true);
            
            if(messageId == 0) {
                Message message = this.telegram.sendReplyKeyboard(chatId, "Select contact:", menu.getMenu());
                this.messageId = message.getMessageId();
            }
            else {
                this.telegram.editMessageWithMenu(chatId, messageId, "", menu.getMenu());
            }
        } catch (TelegramApiException ex) {
            Logger.getLogger(ListMenuChat.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
