package main.courses.menuchats;

import blue.underwater.commons.datetime.XDate;
import blue.underwater.commons.logging.XLogger;
import blue.underwater.telegram.admin.TelegramAdmin;
import blue.underwater.telegram.admin.TelegramChat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import main.calendar.CalendarService;
import main.calendar.Course;
import main.contacts.Contact;
import main.contacts.ContactsService;
import main.courses.menus.CourseConfirmMenu;
import main.courses.menus.CourseLocationMenu;
import main.courses.menus.CourseStartTimeMenu;
import main.reminder.CourseReminderEmailBuilder;
import main.reminder.DailyReminderService;
import blue.underwater.email.admin.Email;
import blue.underwater.email.admin.EmailAdmin;
import blue.underwater.email.admin.EmailBuilder;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/**
 *
 * @author bott_ma
 */
public class TelegramChatMain implements TelegramChat
{

    private final TelegramAdmin telegram;
    private final long chatId;
    private final List<MenuChat> menus = new ArrayList<>();

    /**
     *
     * @param telegram
     * @param chatId
     */
    public TelegramChatMain(TelegramAdmin telegram, long chatId) {
        this.chatId = chatId;
        this.telegram = telegram;

    }

    @Override
    public void callbackQueryReceived(CallbackQuery callbackQuery) {
        long messageId = callbackQuery.getMessage().getMessageId();
        String callbackData = callbackQuery.getData();
        
        // Get username if available
        String userName = "Unknown";
        if (callbackQuery.getFrom() != null) {
            if (callbackQuery.getFrom().getUserName() != null) {
                userName = "@" + callbackQuery.getFrom().getUserName();
            } else if (callbackQuery.getFrom().getFirstName() != null) {
                userName = callbackQuery.getFrom().getFirstName();
                if (callbackQuery.getFrom().getLastName() != null) {
                    userName += " " + callbackQuery.getFrom().getLastName();
                }
            }
        }
        

        // Check if this is a medical callback
        if (callbackData != null && (callbackData.startsWith("medical_email:") || callbackData.startsWith("medical_cancel:"))) {
            handleMedicalCallback(callbackQuery);
            return;
        }
        
        // Check if this is an all set callback
        if (callbackData != null && (callbackData.startsWith("allset_email:") || callbackData.startsWith("allset_cancel:"))) {
            handleAllSetCallback(callbackQuery);
            return;
        }
        
        // Check if this is a medical reminder callback
        if (callbackData != null && (callbackData.startsWith("medical_reminder:") || callbackData.startsWith("reminder_cancel:") || callbackData.equals("reminder_cancel_all"))) {
            handleMedicalReminderCallback(callbackQuery);
            return;
        }

        // Course reminder flow
        if (callbackData != null && callbackData.startsWith("course_remind_select:")) {
            handleCourseRemindSelectCallback(callbackQuery);
            return;
        }
        if (callbackData != null && callbackData.startsWith("course_remind_time:")) {
            handleCourseRemindTimeCallback(callbackQuery);
            return;
        }
        if (callbackData != null && callbackData.startsWith("course_remind_loc:")) {
            handleCourseRemindLocCallback(callbackQuery);
            return;
        }
        if (callbackData != null && (callbackData.startsWith("course_confirm_send:") || callbackData.startsWith("course_confirm_cancel:"))) {
            handleCourseConfirmCallback(callbackQuery);
            return;
        }

        //Optional<MenuChat> menu = ActiveMenus.getInstance().findMenu(messageId);
        Optional<MenuChat> menu = findMenu(messageId);
        if (menu.isPresent()) {
            menu.get().callbackQueryReceived(callbackQuery);
        } else {
            try {
                XLogger.info("Sending 'Menu expired' to: chatId(%s) messageId(%s)", chatId, messageId);
                this.telegram.editMessage(chatId, messageId, "Menu expired");
            } catch (TelegramApiException ex) {
                Logger.getLogger(TelegramChatMain.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    private void handleMedicalCallback(CallbackQuery callbackQuery) {
        // Extract email from callback data
        String callbackData = callbackQuery.getData();
        String email = callbackData.substring(callbackData.indexOf(':') + 1);
        
        // Get the medical form
        main.sheets.medical.MedicalForm medicalForm = main.sheets.medical.MedicalFormsService.getInstance().findByEmail(email);
        
        if (medicalForm != null) {
            MedicalIssueMenuChat menuChat = new MedicalIssueMenuChat(telegram, chatId, medicalForm);
            // Set the message ID from the callback query
            menuChat.setMessageId(callbackQuery.getMessage().getMessageId());
            menuChat.callbackQueryReceived(callbackQuery);
        } else {
            // Medical form not found - just log the error
            Logger.getLogger(TelegramChatMain.class.getName()).log(Level.WARNING, "Medical form not found for email: " + email);
        }
    }
    
    private void handleAllSetCallback(CallbackQuery callbackQuery) {
        // Extract email from callback data
        String callbackData = callbackQuery.getData();
        String email = callbackData.substring(callbackData.indexOf(':') + 1);
        
        // Get the medical form
        main.sheets.medical.MedicalForm medicalForm = main.sheets.medical.MedicalFormsService.getInstance().findByEmail(email);
        
        if (medicalForm != null) {
            AllSetMenuChat menuChat = new AllSetMenuChat(telegram, chatId, medicalForm);
            // Set the message ID from the callback query
            menuChat.setMessageId(callbackQuery.getMessage().getMessageId());
            menuChat.callbackQueryReceived(callbackQuery);
        } else {
            // Medical form not found - just log the error
            Logger.getLogger(TelegramChatMain.class.getName()).log(Level.WARNING, "Medical form not found for email: " + email);
        }
    }
    
    private void handleMedicalReminderCallback(CallbackQuery callbackQuery) {
        // Let the menu handle its own callback - just find it by message ID
        long messageId = callbackQuery.getMessage().getMessageId();
        Optional<MenuChat> menu = findMenu(messageId);
        
        if (menu.isPresent() && (menu.get() instanceof MedicalFormReminderMenuChat || menu.get() instanceof MultiMedicalFormReminderMenuChat)) {
            menu.get().callbackQueryReceived(callbackQuery);
        } else {
            // Handle callback manually when menu is not found
            String callbackData = callbackQuery.getData();
            
            if (callbackData.equals("reminder_cancel_all")) {
                // Handle multi-menu cancel
                try {
                    telegram.editMessage(chatId, messageId, "❌ <b>Action cancelled</b>");
                } catch (TelegramApiException ex) {
                    Logger.getLogger(TelegramChatMain.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else if (callbackData.startsWith("medical_reminder:")) {
                // Extract email from callback data
                String email = callbackData.substring(callbackData.indexOf(':') + 1);
                
                // Find the contact by email
                main.contacts.Contact contact = main.contacts.ContactsService.getInstance().findByEmail(email);
                
                if (contact != null) {
                    MedicalFormReminderMenuChat menuChat = new MedicalFormReminderMenuChat(telegram, chatId, contact, "Course");
                    menuChat.setMessageId(messageId);
                    menuChat.callbackQueryReceived(callbackQuery);
                } else {
                    Logger.getLogger(TelegramChatMain.class.getName()).log(Level.WARNING, "Contact not found for email: " + email);
                }
            }
        }
    }

    // course_remind_select:DATE:INDEX
    private void handleCourseRemindSelectCallback(CallbackQuery callbackQuery) {
        String[] parts = callbackQuery.getData().split(":");
        String isoDate = parts[1];
        int courseIndex = Integer.parseInt(parts[2]);
        long messageId = callbackQuery.getMessage().getMessageId();
        try {
            List<Course> courses = CalendarService.getInstance().getCoursesForDay(XDate.parseDate(isoDate));
            if (courseIndex >= courses.size()) return;
            Course course = courses.get(courseIndex);
            String formattedDate = LocalDate.parse(isoDate).format(DateTimeFormatter.ofPattern("dd MMM yyyy"));
            String courseText = DailyReminderService.buildCourseDetails(course, formattedDate);
            editMessageWithMenu(messageId, courseText + "What time should the course start?",
                new CourseStartTimeMenu(isoDate, courseIndex).getMenu());
        } catch (Exception ex) {
            Logger.getLogger(TelegramChatMain.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // course_remind_time:DATE:INDEX:HOUR
    private void handleCourseRemindTimeCallback(CallbackQuery callbackQuery) {
        String[] parts = callbackQuery.getData().split(":");
        String isoDate = parts[1];
        int courseIndex = Integer.parseInt(parts[2]);
        String hour = parts[3];
        String timeStr = hour.equals("9") ? "9:00 AM" : "10:00 AM";
        long messageId = callbackQuery.getMessage().getMessageId();
        try {
            List<Course> courses = CalendarService.getInstance().getCoursesForDay(XDate.parseDate(isoDate));
            if (courseIndex >= courses.size()) return;
            Course course = courses.get(courseIndex);
            String formattedDate = LocalDate.parse(isoDate).format(DateTimeFormatter.ofPattern("dd MMM yyyy"));
            String courseText = DailyReminderService.buildCourseDetails(course, formattedDate);
            editMessageWithMenu(messageId,
                courseText + "⏰ Start time: <b>" + timeStr + "</b>\n\nWhere will the course take place?",
                new CourseLocationMenu(isoDate, courseIndex, hour).getMenu());
        } catch (Exception ex) {
            Logger.getLogger(TelegramChatMain.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // course_remind_loc:DATE:INDEX:HOUR:LOC
    private void handleCourseRemindLocCallback(CallbackQuery callbackQuery) {
        String[] parts = callbackQuery.getData().split(":");
        String isoDate = parts[1];
        int courseIndex = Integer.parseInt(parts[2]);
        String hour = parts[3];
        int locIndex = Integer.parseInt(parts[4]);
        String timeStr = hour.equals("9") ? "9:00 AM" : "10:00 AM";
        String location = CourseLocationMenu.LOCATIONS[locIndex];
        long messageId = callbackQuery.getMessage().getMessageId();
        try {
            List<Course> courses = CalendarService.getInstance().getCoursesForDay(XDate.parseDate(isoDate));
            if (courseIndex >= courses.size()) return;
            Course course = courses.get(courseIndex);
            String formattedDate = LocalDate.parse(isoDate).format(DateTimeFormatter.ofPattern("dd MMM yyyy"));
            String courseText = DailyReminderService.buildCourseDetails(course, formattedDate);
            int participantCount = course.getEventStudents().getStudentCount();
            editMessageWithMenu(messageId,
                courseText +
                "⏰ Start time: <b>" + timeStr + "</b>\n" +
                "📍 Location: <b>" + location + "</b>\n\n" +
                "Send confirmation emails to <b>" + participantCount +
                " participant" + (participantCount == 1 ? "" : "s") + "</b>?",
                new CourseConfirmMenu(isoDate, courseIndex, hour, locIndex).getMenu());
        } catch (Exception ex) {
            Logger.getLogger(TelegramChatMain.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // course_confirm_send/cancel:DATE:INDEX:HOUR:LOC
    private void handleCourseConfirmCallback(CallbackQuery callbackQuery) {
        String callbackData = callbackQuery.getData();
        boolean sending = callbackData.startsWith("course_confirm_send:");
        String[] parts = callbackData.split(":");
        String isoDate = parts[1];
        int courseIndex = Integer.parseInt(parts[2]);
        String hour = parts[3];
        int locIndex = Integer.parseInt(parts[4]);
        String timeStr = hour.equals("9") ? "9:00 AM" : "10:00 AM";
        String location = CourseLocationMenu.LOCATIONS[locIndex];
        String mapsUrl = CourseLocationMenu.LOCATION_MAPS_URLS[locIndex];
        long messageId = callbackQuery.getMessage().getMessageId();

        if (!sending) {
            try {
                telegram.editMessage(chatId, messageId, "❌ <b>Action cancelled</b>");
            } catch (TelegramApiException ex) {
                Logger.getLogger(TelegramChatMain.class.getName()).log(Level.SEVERE, null, ex);
            }
            return;
        }

        try {
            List<Course> courses = CalendarService.getInstance().getCoursesForDay(XDate.parseDate(isoDate));
            if (courseIndex >= courses.size()) return;
            Course course = courses.get(courseIndex);

            LocalDate day1Date = LocalDate.parse(isoDate);
            String day1 = day1Date.format(DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy"));
            String day2 = day1Date.plusDays(1).format(DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy"));

            int sent = 0;
            for (main.calendar.Student student : course.getEventStudents().getStudents()) {
                String email = student.getEmail();
                String firstName = resolveFirstName(email);
                String html = CourseReminderEmailBuilder.build(firstName, day1, day2, timeStr, location, mapsUrl);
                Email msg = EmailBuilder.create("info@freedive-mallorca.com", email, "Freedive Mallorca")
                    .addBcc("info@freedive-mallorca.com")
                    .setSubject("Your Freediver Course – See You Soon!")
                    .setHtmlContent(html);
                EmailAdmin.getInstance().send(msg);
                sent++;
            }

            String formattedDate = day1Date.format(DateTimeFormatter.ofPattern("dd MMM yyyy"));
            String courseText = DailyReminderService.buildCourseDetails(course, formattedDate);
            telegram.editMessage(chatId, messageId,
                courseText +
                "⏰ Start time: <b>" + timeStr + "</b>\n" +
                "📍 Location: <b>" + location + "</b>\n\n" +
                "✅ <b>Emails sent to " + sent + " participant" + (sent == 1 ? "" : "s") + "</b>");

        } catch (Exception ex) {
            Logger.getLogger(TelegramChatMain.class.getName()).log(Level.SEVERE, null, ex);
            try {
                telegram.editMessage(chatId, messageId, "❌ <b>Failed to send emails</b>: " + ex.getMessage());
            } catch (TelegramApiException tex) {
                Logger.getLogger(TelegramChatMain.class.getName()).log(Level.SEVERE, null, tex);
            }
        }
    }

    private String resolveFirstName(String email) {
        Contact contact = ContactsService.getInstance().findByEmail(email);
        if (contact != null && contact.getFistName() != null && !contact.getFistName().trim().isEmpty()) {
            return contact.getFistName().trim();
        }
        String prefix = email.contains("@") ? email.substring(0, email.indexOf('@')) : email;
        return prefix.isEmpty() ? "there" : Character.toUpperCase(prefix.charAt(0)) + prefix.substring(1);
    }

    private void editMessageWithMenu(long messageId, String text, InlineKeyboardMarkup markup) throws TelegramApiException {
        EditMessageText edit = new EditMessageText();
        edit.setChatId(String.valueOf(chatId));
        edit.setMessageId((int) messageId);
        edit.setText(text);
        edit.setParseMode("HTML");
        edit.setReplyMarkup(markup);
        telegram.execute(edit);
    }

    private static main.reminder.DailyReminderService reminderService;

    public static void setReminderService(main.reminder.DailyReminderService service) {
        reminderService = service;
    }

    private boolean find = false;
    
    /**
     * Sets the find flag to expect a search term
     */
    public void setFindMode(boolean find) {
        this.find = find;
    }
    
    @Override
    public void updateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            String userName = "Unknown";
            
            // Get username if available
            if (update.getMessage().getFrom() != null) {
                if (update.getMessage().getFrom().getUserName() != null) {
                    userName = "@" + update.getMessage().getFrom().getUserName();
                } else if (update.getMessage().getFrom().getFirstName() != null) {
                    userName = update.getMessage().getFrom().getFirstName();
                    if (update.getMessage().getFrom().getLastName() != null) {
                        userName += " " + update.getMessage().getFrom().getLastName();
                    }
                }
            }
            
            
            if (find) {
                // User is entering search text after /find command
                find = false; // Reset flag
                MenuChat menuChat = createFindMenu(telegram, chatId, messageText);
                menuChat.reply();
            } else if (messageText.equals("/list")) {
                MenuChat menuChat = createListMenu(telegram, chatId);
                menuChat.reply();
            } else if (messageText.equals("/find")) {
                telegram.sendTextMessage(chatId, "Enter text:");
                find = true;
            } else if (messageText.equals("/courses")) {
                MenuChat menuChat = createCoursesMenu(telegram, chatId);
                menuChat.reply();
            } else if (messageText.equals("/check48")) {
                if (reminderService != null) {
                    telegram.sendTextMessage(chatId, "🔍 Checking courses in 48h...");
                    new Thread(reminderService::checkAndNotify).start();
                }
            } else {
                this.telegram.sendTextMessage(chatId, "Command not found: " + messageText);
            }
        }
    }

    /**
     * 
     * @param telegram
     * @param chatId
     * @return 
     */
    public MenuChat createCoursesMenu(TelegramAdmin telegram, long chatId) {
        MenuChat menuChat = new CoursesMenuChat(telegram, chatId);
        this.menus.add((MenuChat) menuChat);
        return menuChat;
    }

    /**
     * 
     * @param telegram
     * @param chatId
     * @return 
     */
    public MenuChat createListMenu(TelegramAdmin telegram, long chatId) {
        MenuChat menuChat = new ListMenuChat(telegram, chatId);
        this.menus.add(menuChat);
        return menuChat;
    }
    
    /**
     * Creates a FindMenuChat for searching contacts
     * 
     * @param telegram The Telegram admin instance
     * @param chatId The chat ID
     * @param searchTerm The search term to use
     * @return The created FindMenuChat
     */
    public MenuChat createFindMenu(TelegramAdmin telegram, long chatId, String searchTerm) {
        MenuChat menuChat = new FindMenuChat(telegram, chatId, searchTerm, this);
        this.menus.add(menuChat);
        return menuChat;
    }
    
    /**
     * 
     * @param messageId
     * @return 
     */
    public Optional<MenuChat> findMenu(long messageId) {
        for (MenuChat menu : menus) {
            if (menu.getMessageId() == messageId)
                return Optional.of(menu);
        }
        return Optional.empty();
    }
    
    @Override
    public long getChatId() {
        return chatId;
    }
    
}
