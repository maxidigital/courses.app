package main;

import main.telegram.TelegramCenter;
import blue.underwater.commons.Options;
import blue.underwater.commons.enums.LogLevel;
import blue.underwater.commons.logging.XLogger;
import blue.underwater.telegram.admin.TelegramAdmin;
import blue.underwater.telegram.admin.TelegramChat;
import java.io.IOException;
import main.contacts.ContactsService;
import main.courses.menuchats.TelegramChatMain;
import main.sheets.medical.MedicalFormsService;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 *
 * @author bott_ma
 */
public class Main implements TelegramAdmin.Listener
{
    
    public static void main(String[] args) throws Exception {
        
        for (String arg : args) {
            if (arg.equals(Options.OPTION_DEBUG))
                XLogger.setLevel(LogLevel.DEBUG);
        }

        Main main = new Main();
        main.start();
    }

    public void start() throws IOException {
        // Initialize global credential management first to prevent repeated scope additions
        blue.underwater.security.GoogleSheetsManager.initialize();
        System.out.println("GoogleSheetsManager initialized with required scopes");
        
        ConfigManager.getInstance().readConfig();

        TelegramCenter.getInstance().getMain().addListener(this);
        
        System.out.println("Starting ContactsService initialization...");
        ContactsService.getInstance().init();
        System.out.println("ContactsService initialized");
        
        System.out.println("Starting MedicalFormsService initialization...");
        MedicalFormsService.getInstance().init();
        System.out.println("MedicalFormsService initialized");
        
        TelegramCenter.getInstance().toRoot("🌊 Starting Freedive Mallorca Bot 🤿✨");
    }

    @Override
    public void onUpdateReceived(Update update) {
        
        Long chatId;
        if (update.hasMessage()) {
            chatId = update.getMessage().getChatId();
            
            if (update.getMessage().hasText()) {
                // Handle text message
                XLogger.info(this, "Text message from name(%s) chatId(%s)", update.getMessage().getFrom().getFirstName(), chatId);
                
                String userInfo = String.format("💬 <b>%s</b> (@%s)\n<i>%s</i>", 
                    update.getMessage().getFrom().getFirstName(),
                    update.getMessage().getFrom().getUserName() != null ? update.getMessage().getFrom().getUserName() : "no-username",
                    update.getMessage().getText().length() > 100 ? 
                        update.getMessage().getText().substring(0, 100) + "..." : 
                        update.getMessage().getText()
                );
                TelegramCenter.getInstance().toRoot(chatId, userInfo);
    
                TelegramChat chat = getChat(chatId);
                chat.updateReceived(update);
                
            } else if (update.getMessage().hasVoice()) {
                // Handle voice message
                XLogger.info(this, "Voice message received from name(%s) chatId(%s)", update.getMessage().getFrom().getFirstName(), chatId);
                
                Integer duration = update.getMessage().getVoice().getDuration();
                String userInfo = String.format("🎤 <b>Voice message from %s</b> (@%s)\n<i>Duration: %d seconds</i>", 
                    update.getMessage().getFrom().getFirstName(),
                    update.getMessage().getFrom().getUserName() != null ? update.getMessage().getFrom().getUserName() : "no-username",
                    duration
                );
                TelegramCenter.getInstance().toRoot(chatId, userInfo);
                
                // Get voice message details
                String fileId = update.getMessage().getVoice().getFileId();
                String mimeType = update.getMessage().getVoice().getMimeType();
                
                XLogger.info(this, "Voice message file_id: %s, duration: %d sec, mime type: %s", 
                        fileId, duration, mimeType);
                
                // Respond to the user
                TelegramCenter.getInstance().getMain().sendTextMessage(chatId, 
                        "Recibí tu mensaje de voz. Duración: " + duration + " segundos.");
            } else if (update.getMessage().hasVideo()) {
                XLogger.info(this, "Video received from name(%s) chatId(%s)", update.getMessage().getFrom().getFirstName(), chatId);            
            } else if (update.getMessage().hasAudio()) {
                // Handle audio message
                XLogger.info(this, "Audio received from name(%s) chatId(%s)", update.getMessage().getFrom().getFirstName(), chatId);
                
                String title = update.getMessage().getAudio().getTitle();
                Integer duration = update.getMessage().getAudio().getDuration();
                String userInfo = String.format("🎵 <b>Audio from %s</b> (@%s)\n<i>%s - Duration: %d seconds</i>", 
                    update.getMessage().getFrom().getFirstName(),
                    update.getMessage().getFrom().getUserName() != null ? update.getMessage().getFrom().getUserName() : "no-username",
                    title != null ? title : "Untitled",
                    duration
                );
                TelegramCenter.getInstance().toRoot(chatId, userInfo);
                
                // Get audio details
                String fileId = update.getMessage().getAudio().getFileId();
                String performer = update.getMessage().getAudio().getPerformer();
                
                XLogger.info(this, "Audio file_id: %s, title: %s, performer: %s, duration: %d sec", 
                        fileId, title, performer, duration);
                
                // Respond to the user
                TelegramCenter.getInstance().getMain().sendTextMessage(chatId, 
                        "Recibí tu audio. " + 
                        (title != null ? "Título: " + title + ". " : "") +
                        (performer != null ? "Artista: " + performer + ". " : "") +
                        (duration != null ? "Duración: " + duration + " segundos." : ""));
            } else if (update.getMessage().hasPhoto()) {
                // Handle photo message
                XLogger.info(this, "Photo received from name(%s) chatId(%s)", update.getMessage().getFrom().getFirstName(), chatId);
                                
                TelegramCenter.getInstance().toRoot(chatId, "Photo received from name(%s) chatId(%s)", 
                            update.getMessage().getFrom().getFirstName(), chatId);
                
                // You can get the photo file_id from the PhotoSize list
                String fileId = update.getMessage().getPhoto().get(0).getFileId();
                XLogger.info(this, "Photo file_id: %s", fileId);
                
                // Handle the photo message - for now, we'll just inform the user we received it
                TelegramCenter.getInstance().getMain().sendTextMessage(chatId, 
                        "Recibí tu imagen. El fileId es: " + fileId);
                
                // You could also process this through the chat system if needed
                // TelegramChat chat = getChat(chatId);
                // chat.updateReceived(update);
            }

        } else if (update.hasCallbackQuery()) {
            String callData = update.getCallbackQuery().getData();
            long messageId = update.getCallbackQuery().getMessage().getMessageId();
            chatId = update.getCallbackQuery().getMessage().getChatId();
            
            TelegramCenter.getInstance().toRoot(chatId, callData);

            XLogger.info("Callback de " + callData);
            XLogger.info("ChatId: " + chatId);
            XLogger.info("MessageId: " + messageId);
            XLogger.info("callback: " + callData);
            XLogger.info("callback(%s)", callData);

            TelegramChat chat = getChat(chatId);
            chat.callbackQueryReceived(update.getCallbackQuery());
        }
    }

    private TelegramChat getChat(long chatId) {
        TelegramChat chat = TelegramCenter.getInstance().getMain().getChat(chatId);
        if (chat == null) {
            chat = new TelegramChatMain(TelegramCenter.getInstance().getMain(), chatId);
            TelegramCenter.getInstance().getMain().registerChat(chatId, chat);
        }
        return chat;
    }
}
