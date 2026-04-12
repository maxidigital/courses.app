package main.courses.menuchats;

import blue.underwater.telegram.admin.TelegramAdmin;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import main.calendar.Course;
import main.courses.menus.CourseSelectionMenu;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class CourseSelectionMenuChat implements MenuChat {

    private final TelegramAdmin telegram;
    private final long chatId;
    private final String isoDate;
    private final String formattedDate;
    private final List<Course> courses;
    private long messageId;

    public CourseSelectionMenuChat(TelegramAdmin telegram, long chatId, String isoDate, String formattedDate, List<Course> courses) {
        this.telegram = telegram;
        this.chatId = chatId;
        this.isoDate = isoDate;
        this.formattedDate = formattedDate;
        this.courses = courses;
    }

    @Override
    public void reply() {
        try {
            SendMessage message = new SendMessage();
            message.setChatId(String.valueOf(chatId));
            message.setText(buildText());
            message.setParseMode("HTML");
            message.setReplyMarkup(new CourseSelectionMenu(isoDate, courses).getMenu());
            var response = telegram.execute(message);
            this.messageId = response.getMessageId();
        } catch (TelegramApiException ex) {
            Logger.getLogger(CourseSelectionMenuChat.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private String buildText() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("🗓 Courses in 2 days (<b>%s</b>)\n\n", formattedDate));
        for (Course course : courses) {
            sb.append("• ").append(course.getType()).append("\n");
        }
        sb.append("\nSelect a course to send reminders:");
        return sb.toString();
    }

    @Override
    public void callbackQueryReceived(CallbackQuery callbackQuery) {
        // Handled by TelegramChatMain via prefix "course_remind_select:"
    }

    @Override
    public long getMessageId() {
        return messageId;
    }
}
