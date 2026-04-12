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
    private final String courseText;
    private final String isoDate;
    private final int courseIndex;
    private long messageId;

    public CourseStartTimeMenuChat(TelegramAdmin telegram, long chatId, String courseText, String isoDate, int courseIndex) {
        this.telegram = telegram;
        this.chatId = chatId;
        this.courseText = courseText;
        this.isoDate = isoDate;
        this.courseIndex = courseIndex;
    }

    @Override
    public void reply() {
        try {
            SendMessage message = new SendMessage();
            message.setChatId(String.valueOf(chatId));
            message.setText(courseText + "Day 1 — What time should it start?");
            message.setParseMode("HTML");
            message.setReplyMarkup(new CourseStartTimeMenu(isoDate, courseIndex, 1, "", "").getMenu());

            var response = telegram.execute(message);
            this.messageId = response.getMessageId();
        } catch (TelegramApiException ex) {
            Logger.getLogger(CourseStartTimeMenuChat.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void callbackQueryReceived(CallbackQuery callbackQuery) {
        // Handled by TelegramChatMain via prefix "course_remind_time:"
    }

    @Override
    public long getMessageId() {
        return messageId;
    }
}
