package main.courses.menuchats;

import blue.underwater.telegram.admin.TelegramAdmin;
import java.util.logging.Level;
import java.util.logging.Logger;
import main.courses.menus.CourseConfirmMenu;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class CourseConfirmMenuChat implements MenuChat {

    private final TelegramAdmin telegram;
    private final long chatId;
    private final String text;
    private final String isoDate;
    private final int courseIndex;
    private final String times;
    private final String locs;
    private long messageId;

    public CourseConfirmMenuChat(TelegramAdmin telegram, long chatId, String text,
                                 String isoDate, int courseIndex, String times, String locs) {
        this.telegram = telegram;
        this.chatId = chatId;
        this.text = text;
        this.isoDate = isoDate;
        this.courseIndex = courseIndex;
        this.times = times;
        this.locs = locs;
    }

    @Override
    public void reply() {
        try {
            SendMessage message = new SendMessage();
            message.setChatId(String.valueOf(chatId));
            message.setText(text);
            message.setParseMode("HTML");
            message.setReplyMarkup(new CourseConfirmMenu(isoDate, courseIndex, times, locs).getMenu());
            var response = telegram.execute(message);
            this.messageId = response.getMessageId();
        } catch (TelegramApiException ex) {
            Logger.getLogger(CourseConfirmMenuChat.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void callbackQueryReceived(CallbackQuery callbackQuery) {
        // Handled by TelegramChatMain via prefix "course_confirm_send/cancel:"
    }

    @Override
    public long getMessageId() {
        return messageId;
    }
}
