package main.courses.menuchats;

import blue.underwater.telegram.admin.TelegramAdmin;
import blue.underwater.email.admin.EmailAdmin;
import blue.underwater.email.admin.EmailBuilder;
import blue.underwater.email.admin.Email;
import main.courses.menus.MedicalIssueMenu;
import main.sheets.medical.MedicalForm;
import main.sheets.medical.MedicalFormsService;
import main.sheets.RemindersSheetsAdmin;
import main.contacts.Contact;
import main.contacts.ContactsService;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Menu chat handler for medical form notifications with health issues
 * Handles sending email alerts or canceling the notification
 *
 * @author maxi
 */
public class MedicalIssueMenuChat implements MenuChat {
    
    private final TelegramAdmin telegram;
    private final long chatId;
    private long messageId;
    private final MedicalForm medicalForm;
    
    public MedicalIssueMenuChat(TelegramAdmin telegram, long chatId, MedicalForm medicalForm) {
        this.telegram = telegram;
        this.chatId = chatId;
        this.medicalForm = medicalForm;
    }
    
    public void setMessageId(long messageId) {
        this.messageId = messageId;
    }

    @Override
    public void callbackQueryReceived(CallbackQuery callbackQuery) {
        String callbackData = callbackQuery.getData();
        
        try {
            if (callbackData.startsWith("medical_email:")) {
                handleSendEmail();
            } else if (callbackData.startsWith("medical_cancel:")) {
                handleCancel();
            }
        } catch (TelegramApiException ex) {
            Logger.getLogger(MedicalIssueMenuChat.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void handleSendEmail() throws TelegramApiException {
        try {
            // Create email using the EmailBuilder factory
            Email email = EmailBuilder.create("info@freedive-mallorca.com", medicalForm.getEmail(), "Freedive Mallorca")
                    .addBcc("info@freedive-mallorca.com")
                    .setSubject("About your medical questionnaire")
                    .setHtmlContent(buildEmailBody());
            
            // Send the email
            EmailAdmin.getInstance().send(email);
            
            // Record the reminder in the sheet
            RemindersSheetsAdmin.getInstance().addMedicalFormWithIssuesReminderSent(medicalForm.getEmail());
            
            // Removed duplicate notification - root already knows about the medical form
            
            // Update the telegram message to show email sent
            telegram.editMessage(chatId, messageId, 
                medicalForm.toTelegramMessage() + "\n\n✅ <b>Email sent to participant</b>");
            
        } catch (Exception ex) {
            Logger.getLogger(MedicalIssueMenuChat.class.getName()).log(Level.SEVERE, null, ex);
            telegram.editMessage(chatId, messageId, 
                medicalForm.toTelegramMessage() + "\n\n❌ <b>Failed to send email</b>");
        }
    }
    
    private void handleCancel() throws TelegramApiException {
        // Update the message to show action cancelled
        telegram.editMessage(chatId, messageId, 
            medicalForm.toTelegramMessage() + "\n\n❌ <b>Action cancelled</b>");
    }
    
    private String buildEmailBody() {
        StringBuilder body = new StringBuilder();
        
        // Get contact to find first name
        Contact contact = ContactsService.getInstance().findByEmail(medicalForm.getEmail());
        String name = medicalForm.getEmail(); // Default to email if no contact found
        
        if (contact != null) {
            String firstName = contact.getFistName();
            
            if (firstName != null && !firstName.trim().isEmpty()) {
                // Use only the first name part (in case firstName contains multiple words)
                String[] nameParts = firstName.trim().split("\\s+");
                name = nameParts[0]; // Take only the first part
            }
        }
        
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
        body.append("    }\n");
        body.append("    .option {\n");
        body.append("      margin-bottom: 15px;\n");
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
        body.append("  <p>Hi ").append(name).append(",</p>\n");
        body.append("\n");
        body.append("  <p>Thank you for sending in the forms! 🙌</p>\n");
        body.append("\n");
        body.append("  <hr>\n");
        body.append("\n");
        body.append("  <h2>Just a quick heads-up about the medical questionnaire:</h2>\n");
        body.append("\n");
        body.append("  <p>For insurance purposes, <span class=\"highlight\">all answers must be marked as \"No.\"</span></p>\n");
        body.append("\n");
        body.append("  <p>If there's a <strong>\"Yes\"</strong> to any of the questions, we kindly ask for a short medical certificate from a doctor confirming you're fit to freedive.<br />\n");
        body.append("  🩺 It's usually a simple check, and most general practitioners are familiar with this kind of evaluation.</p>\n");
        body.append("\n");
        body.append("  <h2>You have two options:</h2>\n");
        body.append("\n");
        body.append("  <div class=\"option\">\n");
        body.append("    🔹 <strong>Option 1:</strong><br />\n");
        body.append("    Get a brief note from a doctor saying you're fit to freedive.\n");
        body.append("  </div>\n");
        body.append("\n");
        body.append("  <div class=\"option\">\n");
        body.append("    🔹 <strong>Option 2:</strong><br />\n");
        body.append("    If you're confident there's no actual issue, feel free to fill out the questionnaire again with all answers as \"No.\"\n");
        body.append("  </div>\n");
        body.append("\n");
        body.append("  <p>We're not medical professionals ourselves, so this process helps us meet our insurance requirements — and keeps everything safe and clear for everyone.</p>\n");
        body.append("\n");
        body.append("  <p>Let me know what works best for you —<br />\n");
        body.append("  <strong>we're really looking forward to diving with you soon!</strong> 🌊</p>\n");
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

    @Override
    public long getMessageId() {
        return messageId;
    }

    @Override
    public void reply() {
        try {
            MedicalIssueMenu menu = new MedicalIssueMenu(medicalForm.getEmail());
            SendMessage message = new SendMessage();
            message.setChatId(String.valueOf(chatId));
            message.setText(medicalForm.toTelegramMessage());
            message.setParseMode("HTML");
            message.setReplyMarkup(menu.getMenu());
            
            var response = telegram.execute(message);
            this.messageId = response.getMessageId();
            
        } catch (TelegramApiException ex) {
            Logger.getLogger(MedicalIssueMenuChat.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}