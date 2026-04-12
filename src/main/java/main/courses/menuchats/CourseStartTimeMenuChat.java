package main.courses.menuchats;

import blue.underwater.telegram.admin.TelegramAdmin;
import java.util.logging.Level;
import java.util.logging.Logger;
import main.courses.menus.CourseStartTimeMenu;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class CourseStartTimeMenuChat implements MenuChat {

    private final TelegramAdmin telegram;
    private final long chatId;
    private final String messageText;
    private final String isoDate;
    private long messageId;

    public CourseStartTimeMenuChat(TelegramAdmin telegram, long chatId, String messageText, String isoDate) {
        this.telegram = telegram;
        this.chatId = chatId;
        this.messageText = messageText;
        this.isoDate = isoDate;
    }

    @Override
    public void reply() {
        try {
            SendMessage message = new SendMessage();
            message.setChatId(String.valueOf(chatId));
            message.setText(messageText + "\n\nWhat time should the course start?");
            message.setParseMode("HTML");
            message.setReplyMarkup(new CourseStartTimeMenu(isoDate).getMenu());

            var response = telegram.execute(message);
            this.messageId = response.getMessageId();
        } catch (TelegramApiException ex) {
            Logger.getLogger(CourseStartTimeMenuChat.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void callbackQueryReceived(CallbackQuery callbackQuery) {
        // Handled by TelegramChatMain via prefix "course_time:"
    }

    @Override
    public long getMessageId() {
        return messageId;
    }
}
