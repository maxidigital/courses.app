package main.courses.menuchats;

import blue.underwater.telegram.admin.TelegramAdmin;
import blue.underwater.email.admin.EmailAdmin;
import blue.underwater.email.admin.EmailBuilder;
import blue.underwater.email.admin.Email;
import main.courses.menus.MedicalFormReminderMenu;
import main.contacts.Contact;
import main.contacts.ContactsService;
import main.sheets.RemindersSheetsAdmin;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Menu chat handler for medical form submission reminders
 * Handles sending reminder emails or canceling the action
 *
 * @author maxi
 */
public class MedicalFormReminderMenuChat implements MenuChat {
    
    private final TelegramAdmin telegram;
    private final long chatId;
    private long messageId;
    private final Contact contact;
    private final String courseName;
    
    public MedicalFormReminderMenuChat(TelegramAdmin telegram, long chatId, Contact contact, String courseName) {
        this.telegram = telegram;
        this.chatId = chatId;
        this.contact = contact;
        this.courseName = courseName;
    }
    
    public void setMessageId(long messageId) {
        this.messageId = messageId;
    }

    @Override
    public void callbackQueryReceived(CallbackQuery callbackQuery) {
        String callbackData = callbackQuery.getData();
        
        try {
            if (callbackData.startsWith("medical_reminder:")) {
                handleSendReminder();
            } else if (callbackData.startsWith("reminder_cancel:")) {
                handleCancel();
            }
        } catch (TelegramApiException ex) {
            Logger.getLogger(MedicalFormReminderMenuChat.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void handleSendReminder() throws TelegramApiException {
        try {
            // Create email using the EmailBuilder factory
            Email email = EmailBuilder.create("info@freedive-mallorca.com", contact.getEmail(), "Freedive Mallorca")
                    .addBcc("info@freedive-mallorca.com")
                    .setSubject("Medical form reminder")
                    .setHtmlContent(buildReminderEmailBody());
            
            // Send the email
            EmailAdmin.getInstance().send(email);
            
            // Record the reminder in the sheet
            RemindersSheetsAdmin.getInstance().addMedicalFormMissedReminderSent(contact.getEmail());
            
            // Notify root admin about the action
            main.telegram.TelegramCenter.getInstance().toRoot(
                "Medical Form Reminder sent to: " + contact.getEmail()
            );
            
            // Update the telegram message to show reminder sent
            telegram.editMessage(chatId, messageId, 
                buildMessageText() + "\n\n✅ <b>Reminder sent to " + contact.getFistName() + "</b>");
            
        } catch (Exception ex) {
            Logger.getLogger(MedicalFormReminderMenuChat.class.getName()).log(Level.SEVERE, null, ex);
            telegram.editMessage(chatId, messageId, 
                buildMessageText() + "\n\n❌ <b>Failed to send reminder</b>");
        }
    }
    
    private void handleCancel() throws TelegramApiException {
        // Update the message to show action cancelled
        telegram.editMessage(chatId, messageId, 
            buildMessageText() + "\n\n❌ <b>Action cancelled</b>");
    }
    
    private String buildReminderEmailBody() {
        String firstName = contact.getFistName();
        if (firstName == null || firstName.trim().isEmpty()) {
            firstName = contact.getEmail();
        }
        
        StringBuilder body = new StringBuilder();
        body.append("<!DOCTYPE html>\n");
        body.append("<html lang=\"en\">\n");
        body.append("<head>\n");
        body.append("  <meta charset=\"UTF-8\" />\n");
        body.append("  <style>\n");
        body.append("    body {\n");
        body.append("      font-family: Arial, sans-serif;\n");
        body.append("      color: #222;\n");
        body.append("      line-height: 1.6;\n");
        body.append("      padding: 20px;\n");
        body.append("    }\n");
        body.append("    h2 {\n");
        body.append("      color: #0077b6;\n");
        body.append("    }\n");
        body.append("    .highlight {\n");
        body.append("      font-weight: bold;\n");
        body.append("      color: #d62828;\n");
        body.append("    }\n");
        body.append("    .footer {\n");
        body.append("      margin-top: 40px;\n");
        body.append("      font-style: italic;\n");
        body.append("      color: #555;\n");
        body.append("    }\n");
        body.append("  </style>\n");
        body.append("</head>\n");
        body.append("<body>\n");
        body.append("\n");
        body.append("  <p>Hi " + firstName + ",</p>\n");
        body.append("\n");
        body.append("  <p>We understand that sometimes things can slip your mind — just a friendly reminder that completing the medical questionnaire is <span class=\"highlight\">mandatory</span> before going into the water with us.</p>\n");
        body.append("\n");
        body.append("  <p>Please fill it out as soon as possible so we can prepare your insurance and ensure your safety:  \n");
        body.append("  <a href=\"https://forms.gle/N7SMB7iAnRBXe4PQ6\">Medical Questionnaire Form</a></p>\n");
        body.append("\n");
        body.append("  <p>If you have any questions or need assistance, feel free to get in touch.</p>\n");
        body.append("\n");
        body.append("  <p>Looking forward to sharing an amazing experience together!</p>\n");
        body.append("\n");
        body.append("  <div class=\"footer\">\n");
        body.append("    Saludos,<br />\n");
        body.append("    <strong>Team Freedive Mallorca</strong><br />\n");
        body.append("    <em>Helping you feel at home underwater</em>\n");
        body.append("  </div>\n");
        body.append("\n");
        body.append("</body>\n");
        body.append("</html>");
        
        return body.toString();
    }
    
    private String buildMessageText() {
        return "Medical form reminder";
    }

    @Override
    public long getMessageId() {
        return messageId;
    }

    @Override
    public void reply() {
        try {
            String firstName = contact.getFistName();
            if (firstName == null || firstName.trim().isEmpty()) {
                firstName = contact.getEmail(); // fallback to email
            }
            
            MedicalFormReminderMenu menu = new MedicalFormReminderMenu(contact.getEmail(), firstName);
            SendMessage message = new SendMessage();
            message.setChatId(String.valueOf(chatId));
            message.setText("Medical form reminder");
            message.setParseMode("HTML");
            message.setReplyMarkup(menu.getMenu());
            
            var response = telegram.execute(message);
            this.messageId = response.getMessageId();
            
        } catch (TelegramApiException ex) {
            Logger.getLogger(MedicalFormReminderMenuChat.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}