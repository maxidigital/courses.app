package main.courses;

import blue.underwater.commons.datetime.XDate;
import blue.underwater.commons.logging.XLogger;
import blue.underwater.telegram.admin.TelegramAdmin;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import main.telegram.TelegramCenter;
import main.calendar.CalendarService;
import main.calendar.Course;
import main.courses.menuchats.MultiMedicalFormReminderMenuChat;
import main.contacts.Contact;
import main.sheets.RemindersSheetsAdmin;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/**
 *
 * @author bott_ma
 */
public class CoursesDaySelector {

    private final TelegramAdmin telegram;
    private final long chatId;
    private final long messageId;

    /**
     *
     * @param telegram
     * @param chatId
     * @param messageId
     */
    public CoursesDaySelector(TelegramAdmin telegram, long chatId, long messageId) {
        this.telegram = telegram;
        this.chatId = chatId;
        this.messageId = messageId;
    }

    public void updateReceived(String option) throws TelegramApiException {
        CoursesTelegramMessage coursesMenuMessage = new CoursesTelegramMessage();
        
        // Log to root that a date was selected
        TelegramCenter.getInstance().toRoot(chatId,
            "📅 <b>Course lookup</b>\nDate: %s", option
        );

        TelegramCenter.getInstance().getMain().editMessage(chatId, messageId, "⏳ Looking up courses for " + option + "... Just a moment!");
        String messageText;

        XDate xdate = XDate.parseDate(option);

        List<Course> courses;
        try {
            courses = CalendarService.getInstance().getCoursesForDay(xdate);
            ContactsFinder.findContacts(courses);
            
            // Accumulate all contacts without medical forms
            List<Contact> contactsWithoutMedical = new ArrayList<>();
            
            // Create a callback to handle students without medical forms
            CoursesTelegramMessage.MedicalFormCallback callback = (Contact contact, String courseName) -> {
                contactsWithoutMedical.add(contact);
            };
            
            messageText = coursesMenuMessage.getTelegramMessage(xdate, courses, callback);

            if (messageText.isEmpty()) {
                messageText = "📅 No courses scheduled for " + option + ".";
            }

            XLogger.info("Returning: " + messageText);
            TelegramCenter.getInstance().getMain().editMessage(chatId, messageId, messageText);
            
            // Send consolidated notification about missing medical forms if any
            if (!contactsWithoutMedical.isEmpty()) {
                StringBuilder missingFormsNotification = new StringBuilder();
                missingFormsNotification.append("⚠️ <b>Missing medical forms for ").append(xdate.format("dd MMM yyyy")).append("</b>\n");
                missingFormsNotification.append("<i>Total: ").append(contactsWithoutMedical.size()).append(" participant(s)</i>\n\n");
                
                for (Contact contact : contactsWithoutMedical) {
                    missingFormsNotification.append("• ").append(contact.getEmail()).append("\n");
                }
                
                TelegramCenter.getInstance().toRoot(chatId, missingFormsNotification.toString());
            }
            
            // If there are contacts without medical forms and user is admin, check if any still need reminders
            if (!contactsWithoutMedical.isEmpty() && TelegramCenter.getInstance().isAdmin(chatId)) {
                // Filter out contacts that were already reminded
                List<Contact> contactsNeedingReminder = new ArrayList<>();
                for (Contact contact : contactsWithoutMedical) {
                    try {
                        if (!RemindersSheetsAdmin.getInstance().isMedicalFormMissedReminderSent(contact.getEmail())) {
                            contactsNeedingReminder.add(contact);
                        }
                    } catch (Exception e) {
                        // On error, include the contact to be safe
                        contactsNeedingReminder.add(contact);
                    }
                }
                
                // Only show menu if there are contacts that haven't been reminded yet
                if (!contactsNeedingReminder.isEmpty()) {
                    // Get the course name (use the first course type as reference)
                    String courseName = !courses.isEmpty() ? courses.get(0).getType() : "Course";
                    
                    MultiMedicalFormReminderMenuChat multiReminderChat = new MultiMedicalFormReminderMenuChat(
                        telegram, chatId, contactsNeedingReminder, courseName
                    );
                    multiReminderChat.reply();
                }
            }
            
        } catch (IOException ex) {
            String errorMessage = "\uD83D\uDD10 Authentication problem: Google token has expired or was revoked. "
                       + "\n\nThe issue will be reported to the system administrator. \uD83D\uDEE0\uFE0F";
            
            TelegramCenter.getInstance().getMain().editMessage(chatId, messageId, errorMessage);           
            TelegramCenter.getInstance().toRoot(CoursesDaySelector.class.getName() + "\n\n" + ex.getMessage());
            
            Logger.getLogger(CoursesDaySelector.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
