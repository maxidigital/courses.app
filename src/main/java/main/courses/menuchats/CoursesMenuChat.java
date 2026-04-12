package main.courses.menuchats;

import blue.underwater.telegram.admin.TelegramAdmin;
import java.util.logging.Level;
import java.util.logging.Logger;
import main.courses.CoursesDaySelector;
import main.courses.menus.CoursesMenu;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/**
 *
 * @author bott_ma
 */
public class CoursesMenuChat implements MenuChat
{

    private final TelegramAdmin telegram;
    private final long chatId;
    private CoursesDaySelector coursesDaySelector;
    private long messageId;

    public CoursesMenuChat(TelegramAdmin telegram, long chatId) {
        this.telegram = telegram;
        this.chatId = chatId;
    }

    @Override
    public void callbackQueryReceived(CallbackQuery callbackQuery) {
        String callData = callbackQuery.getData();
        try {
            this.coursesDaySelector.updateReceived(callData);
        } catch (TelegramApiException ex) {
            Logger.getLogger(CoursesMenuChat.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public long getMessageId() {
        return messageId;
    }

    @Override
    public void reply() {
        try {
            CoursesMenu coursesMenu = new CoursesMenu();
            Message message = this.telegram.sendReplyKeyboard(chatId, "Select a day:", coursesMenu.getMenu());
            this.messageId = message.getMessageId();

            this.coursesDaySelector = new CoursesDaySelector(telegram, chatId, messageId);
        } catch (TelegramApiException ex) {
            Logger.getLogger(CoursesMenuChat.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
