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
import main.courses.CourseType;
import main.courses.menus.CourseConfirmMenu;
import main.courses.menus.CourseLocationMenu;
import main.courses.menus.CourseStartTimeMenu;
import main.reminder.CourseReminderEmailBuilder;
import main.reminder.DailyReminderService;
import main.telegram.TelegramCenter;
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
            editMessageWithMenu(messageId, courseText + "Day 1 — What time should it start?",
                new CourseStartTimeMenu(isoDate, courseIndex, 1, "", "").getMenu());
        } catch (Exception ex) {
            Logger.getLogger(TelegramChatMain.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // course_remind_time:DATE:CIDX:DAY:TPREV:LPREV:SLOT
    private void handleCourseRemindTimeCallback(CallbackQuery callbackQuery) {
        String[] parts = callbackQuery.getData().split(":");
        String isoDate = parts[1];
        int courseIndex = Integer.parseInt(parts[2]);
        int day = Integer.parseInt(parts[3]);
        String timesPrev = parts[4];
        String locsPrev = parts[5];
        int slot = Integer.parseInt(parts[6]);
        String newTimes = timesPrev + slot;
        long messageId = callbackQuery.getMessage().getMessageId();
        try {
            List<Course> courses = CalendarService.getInstance().getCoursesForDay(XDate.parseDate(isoDate));
            if (courseIndex >= courses.size()) return;
            Course course = courses.get(courseIndex);
            String formattedDate = LocalDate.parse(isoDate).format(DateTimeFormatter.ofPattern("dd MMM yyyy"));
            String courseText = DailyReminderService.buildCourseDetails(course, formattedDate);
            String timeStr = CourseStartTimeMenu.slotToTimeStr(slot);
            editMessageWithMenu(messageId,
                courseText + "⏰ Day " + day + " — <b>" + timeStr + "</b>\n\nWhere will Day " + day + " take place?",
                new CourseLocationMenu(isoDate, courseIndex, day, newTimes, locsPrev).getMenu());
        } catch (Exception ex) {
            Logger.getLogger(TelegramChatMain.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // course_remind_loc:DATE:CIDX:DAY:TIMES:LPREV:SLOT
    private void handleCourseRemindLocCallback(CallbackQuery callbackQuery) {
        String[] parts = callbackQuery.getData().split(":");
        String isoDate = parts[1];
        int courseIndex = Integer.parseInt(parts[2]);
        int day = Integer.parseInt(parts[3]);
        String times = parts[4];
        String locsPrev = parts[5];
        int slot = Integer.parseInt(parts[6]);
        String newLocs = locsPrev + slot;
        long messageId = callbackQuery.getMessage().getMessageId();
        try {
            List<Course> courses = CalendarService.getInstance().getCoursesForDay(XDate.parseDate(isoDate));
            if (courseIndex >= courses.size()) return;
            Course course = courses.get(courseIndex);
            int totalDays = CourseType.fromName(course.getType()).days;
            String formattedDate = LocalDate.parse(isoDate).format(DateTimeFormatter.ofPattern("dd MMM yyyy"));
            String courseText = DailyReminderService.buildCourseDetails(course, formattedDate);
            if (day < totalDays) {
                int nextDay = day + 1;
                editMessageWithMenu(messageId,
                    courseText + buildDaySummary(times, newLocs, isoDate) +
                    "\n\nDay " + nextDay + " — What time should it start?",
                    new CourseStartTimeMenu(isoDate, courseIndex, nextDay, times, newLocs).getMenu());
            } else {
                int participantCount = course.getEventStudents().getStudentCount();
                editMessageWithMenu(messageId,
                    courseText + buildDaySummary(times, newLocs, isoDate) +
                    "\n\nSend confirmation emails to <b>" + participantCount +
                    " participant" + (participantCount == 1 ? "" : "s") + "</b>?",
                    new CourseConfirmMenu(isoDate, courseIndex, times, newLocs).getMenu());
            }
        } catch (Exception ex) {
            Logger.getLogger(TelegramChatMain.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private String buildDaySummary(String times, String locs, String isoDate) {
        StringBuilder sb = new StringBuilder();
        java.time.LocalDate base = java.time.LocalDate.parse(isoDate);
        for (int i = 0; i < locs.length(); i++) {
            int tSlot = Character.getNumericValue(times.charAt(i));
            int lIdx = Character.getNumericValue(locs.charAt(i));
            String dayStr = base.plusDays(i).format(DateTimeFormatter.ofPattern("EEE d MMM"));
            sb.append("\n⏰ Day ").append(i + 1).append(" (").append(dayStr).append("): ")
              .append(CourseStartTimeMenu.slotToTimeStr(tSlot))
              .append(" @ ").append(CourseLocationMenu.getName(lIdx));
        }
        return sb.toString();
    }

    // course_confirm_send/cancel:DATE:CIDX:TIMES:LOCS
    private void handleCourseConfirmCallback(CallbackQuery callbackQuery) {
        String callbackData = callbackQuery.getData();
        boolean sending = callbackData.startsWith("course_confirm_send:");
        String[] parts = callbackData.split(":");
        String isoDate = parts[1];
        int courseIndex = Integer.parseInt(parts[2]);
        String times = parts[3];
        String locs = parts[4];
        long messageId = callbackQuery.getMessage().getMessageId();

        if (!sending) {
            try {
                telegram.editMessage(chatId, messageId, "❌ <b>Action cancelled</b>");
            } catch (TelegramApiException ex) {
                Logger.getLogger(TelegramChatMain.class.getName()).log(Level.SEVERE, null, ex);
            }
            return;
        }

        if (!TelegramCenter.getInstance().isAdmin(chatId) && !blue.underwater.telegram.admin.TelegramUsers.isRoot(chatId)) {
            try {
                telegram.editMessage(chatId, messageId, "❌ Not authorized");
            } catch (TelegramApiException ex) {
                Logger.getLogger(TelegramChatMain.class.getName()).log(Level.SEVERE, null, ex);
            }
            return;
        }

        try {
            List<Course> courses = CalendarService.getInstance().getCoursesForDay(XDate.parseDate(isoDate));
            if (courseIndex >= courses.size()) return;
            Course course = courses.get(courseIndex);
            CourseType courseType = CourseType.fromName(course.getType());
            int totalDays = courseType.days;
            String duration = courseType.duration;

            LocalDate day1Date = LocalDate.parse(isoDate);
            String[] dayFormatted = new String[totalDays];
            String[] timeStrs = new String[totalDays];
            String[] locNames = new String[totalDays];
            String[] locUrls = new String[totalDays];
            for (int i = 0; i < totalDays; i++) {
                dayFormatted[i] = day1Date.plusDays(i).format(DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy"));
                timeStrs[i] = CourseStartTimeMenu.slotToTimeStr(Character.getNumericValue(times.charAt(i)));
                locNames[i] = CourseLocationMenu.getName(Character.getNumericValue(locs.charAt(i)));
                locUrls[i] = CourseLocationMenu.getUrl(Character.getNumericValue(locs.charAt(i)));
            }

            int sent = 0;
            for (main.calendar.Student student : course.getEventStudents().getStudents()) {
                String email = student.getEmail();
                String firstName = resolveFirstName(email);
                String appUrl = System.getProperty("app.url", "");
                String token = blue.underwater.calendar.admin.event.Tools.emailHash(email);
                String confirmationUrl = appUrl + "/confirm?date=" + isoDate + "&token=" + token;
                String html;
                if (totalDays == 1) {
                    html = CourseReminderEmailBuilder.build1Day(firstName, dayFormatted[0], timeStrs[0], locNames[0], locUrls[0], duration, confirmationUrl);
                } else if (totalDays == 2) {
                    html = CourseReminderEmailBuilder.build2Day(firstName,
                        dayFormatted[0], timeStrs[0], locNames[0], locUrls[0],
                        dayFormatted[1], timeStrs[1], locNames[1], locUrls[1], duration, confirmationUrl);
                } else {
                    html = CourseReminderEmailBuilder.build3Day(firstName,
                        dayFormatted[0], timeStrs[0], locNames[0], locUrls[0],
                        dayFormatted[1], timeStrs[1], locNames[1], locUrls[1],
                        dayFormatted[2], timeStrs[2], locNames[2], locUrls[2], duration, confirmationUrl);
                }
                Email msg = EmailBuilder.create("info@freedive-mallorca.com", email, "Freedive Mallorca")
                    .addBcc("info@freedive-mallorca.com")
                    .setSubject("Your " + course.getType() + " – See You Soon!")
                    .setHtmlContent(html);
                EmailAdmin.getInstance().send(msg);
                CalendarService.getInstance().markStudentAsPending(course.getXEvent(), email);
                sent++;
            }

            String formattedDate = day1Date.format(DateTimeFormatter.ofPattern("dd MMM yyyy"));
            String courseText = DailyReminderService.buildCourseDetails(course, formattedDate);
            telegram.editMessage(chatId, messageId,
                courseText + buildDaySummary(times, locs, isoDate) +
                "\n\n✅ <b>Emails sent to " + sent + " participant" + (sent == 1 ? "" : "s") + "</b>");

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
                if (!TelegramCenter.getInstance().isAdmin(chatId) && !blue.underwater.telegram.admin.TelegramUsers.isRoot(chatId)) {
                    this.telegram.sendTextMessage(chatId, "Command not found: " + messageText);
                } else if (reminderService != null) {
                    telegram.sendTextMessage(chatId, "🔍 Checking courses in 48h...");
                    new Thread(() -> reminderService.checkAndNotify(chatId)).start();
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
