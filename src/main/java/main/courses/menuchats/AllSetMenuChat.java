package main.courses.menuchats;

import blue.underwater.telegram.admin.TelegramAdmin;
import main.courses.menus.AllSetMenu;
import main.sheets.medical.MedicalForm;
import main.sheets.RemindersSheetsAdmin;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import blue.underwater.email.admin.EmailAdmin;
import blue.underwater.email.admin.EmailBuilder;
import blue.underwater.email.admin.Email;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Menu chat handler for medical forms without health issues
 * Allows admins to send an "all set" email to participants
 *
 * @author maxi
 */
public class AllSetMenuChat implements MenuChat {
    
    private final TelegramAdmin telegram;
    private final long chatId;
    private long messageId;
    private final MedicalForm medicalForm;
    
    public AllSetMenuChat(TelegramAdmin telegram, long chatId, MedicalForm medicalForm) {
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
            if (callbackData.startsWith("allset_email:")) {
                handleSendEmail();
            } else if (callbackData.startsWith("allset_cancel:")) {
                handleCancel();
            }
        } catch (TelegramApiException ex) {
            Logger.getLogger(AllSetMenuChat.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public long getMessageId() {
        return messageId;
    }

    @Override
    public void reply() {
        String message = "🟢 All clear! No medical issues\n\n" + 
                        medicalForm.toTelegramMessage() + 
                        "\n\nDo you want to send an 'All set' email?";
        
        AllSetMenu menu = new AllSetMenu(medicalForm.getEmail());
        
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText(message);
        sendMessage.setParseMode("HTML");
        sendMessage.setReplyMarkup(menu.getMenu());
        
        try {
            org.telegram.telegrambots.meta.api.objects.Message sentMessage = telegram.execute(sendMessage);
            this.messageId = sentMessage.getMessageId();
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
    
    private void handleSendEmail() throws TelegramApiException {
        try {
            // Create email using the EmailBuilder factory
            Email email = EmailBuilder.create("info@freedive-mallorca.com", medicalForm.getEmail(), "Freedive Mallorca")
                    .addBcc("info@freedive-mallorca.com")
                    .setSubject("You're all set – next step: the sea! 🌊")
                    .setHtmlContent(createAllSetEmailBody());
            
            // Send the email
            EmailAdmin.getInstance().send(email);
            
            // Record the reminder in the sheet
            RemindersSheetsAdmin.getInstance().addAllSetReminderSent(medicalForm.getEmail());
            
            // Removed duplicate notification - root already knows about the medical form
            
            // Update the telegram message to show email sent
            telegram.editMessage(chatId, messageId, 
                medicalForm.toTelegramMessage() + "\n\n✅ <b>'All set' email sent to participant</b>");
            
        } catch (Exception ex) {
            Logger.getLogger(AllSetMenuChat.class.getName()).log(Level.SEVERE, null, ex);
            telegram.editMessage(chatId, messageId, 
                medicalForm.toTelegramMessage() + "\n\n❌ <b>Failed to send email</b>");
        }
    }
    
    private void handleCancel() throws TelegramApiException {
        // Update the message to show action cancelled
        telegram.editMessage(chatId, messageId, 
            medicalForm.toTelegramMessage() + "\n\n❌ <b>Action cancelled</b>");
    }
    
    private String createAllSetEmailBody() {
        // Get contact to find first name (similar to MedicalIssueMenuChat)
        String name = medicalForm.getEmail(); // Default to email if no contact found
        
        try {
            main.contacts.Contact contact = main.contacts.ContactsService.getInstance().findByEmail(medicalForm.getEmail());
            if (contact != null) {
                String firstName = contact.getFistName();
                
                if (firstName != null && !firstName.trim().isEmpty()) {
                    // Use only the first name part (in case firstName contains multiple words)
                    String[] nameParts = firstName.trim().split("\\s+");
                    name = nameParts[0]; // Take only the first part
                }
            }
        } catch (Exception e) {
            Logger.getLogger(AllSetMenuChat.class.getName()).log(Level.WARNING, "Failed to get contact name", e);
        }
        
        return "<!DOCTYPE html>\n"
                + "<html lang=\"en\">\n"
                + "<head>\n"
                + "  <meta charset=\"UTF-8\" />\n"
                + "  <style>\n"
                + "    body {\n"
                + "      font-family: Arial, sans-serif;\n"
                + "      color: #222;\n"
                + "      line-height: 1.6;\n"
                + "      padding: 20px;\n"
                + "    }\n"
                + "    h2 {\n"
                + "      color: #0077b6;\n"
                + "    }\n"
                + "    .highlight {\n"
                + "      font-weight: bold;\n"
                + "      color: #d62828;\n"
                + "    }\n"
                + "    .footer {\n"
                + "      margin-top: 40px;\n"
                + "      font-style: italic;\n"
                + "      color: #555;\n"
                + "    }\n"
                + "    .social-link {\n"
                + "      color: #888;\n"
                + "      text-decoration: none;\n"
                + "      margin-top: 10px;\n"
                + "      display: inline-block;\n"
                + "    }\n"
                + "  </style>\n"
                + "</head>\n"
                + "<body>\n"
                + "\n"
                + "  <p>Hi " + name + ",</p>\n"
                + "\n"
                + "  <p>Thank you for filling out the registration form and medical questionnaire — everything looks great! ✅</p>\n"
                + "\n"
                + "  <p>You're all set for an unforgettable experience in the water with us! 🌊</p>\n"
                + "\n"
                + "  <hr>\n"
                + "\n"
                + "  <h2>What happens next?</h2>\n"
                + "\n"
                + "  <p>We'll be in touch again closer to the date of your course or activity with all the final details. As we always choose the location based on the weather and sea conditions, this helps us make sure you get the best possible experience on the day.</p>\n"
                + "\n"
                + "  <p>In the meantime, feel free to check out our homepage <a href=\"https://www.freedive-mallorca.com\">freedive-mallorca.com</a> or follow us on Instagram <a href=\"https://www.instagram.com/freedivemallorca?igsh=dzAydDlnY3czZjM5\">@freedivemallorca</a> to see what we're up to, get a glimpse of the magic that awaits, and feel the vibe of our little underwater world. 🐚📸</p>\n"
                + "\n"
                + "  <p>If anything comes up or if you have any questions, feel free to reach out. Since we work in <strong>very small groups (max 3 students per instructor)</strong>, every spot counts — so the earlier we know about any changes, the better. 🤿</p>\n"
                + "\n"
                + "  <p>We can't wait to welcome you and share the water together soon!</p>\n"
                + "\n"
                + "  <div class=\"footer\">\n"
                + "    Saludos,<br />\n"
                + "    <strong>Team Freedive Mallorca</strong><br />\n"
                + "    <em>Helping you feel at home underwater</em><br />\n"
                + "    <a href=\"https://www.freedive-mallorca.com\">www.freedive-mallorca.com</a>\n"
                + "  </div>\n"
                + "\n"
                + "</body>\n"
                + "</html>";
    }
}