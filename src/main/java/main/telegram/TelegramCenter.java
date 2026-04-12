package main.telegram;

import blue.underwater.telegram.admin.TelegramAdmin;
import blue.underwater.telegram.admin.TelegramTokens;
import blue.underwater.telegram.admin.TelegramUsers;
import java.io.Serializable;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/**
 *
 * @author bott_ma
 */
public final class TelegramCenter 
{
    private final static TelegramCenter instance = new TelegramCenter();
    private final TelegramAdmin main = TelegramAdmin.create(TelegramTokens.FREMA_10);
    //private final TelegramAdmin admin = TelegramAdmin.create(TelegramTokens.FREEMA_ADMIN);
    //private final TelegramAdmin root = TelegramAdmin.create(TelegramTokens.V2XTOOLS);
    
    // Array of admin users for consistent usage across methods
    private final static TelegramUsers.TelegramUser[] ADMINS = {TelegramUsers.ISMA};
    // Array of root users
    private final static TelegramUsers.TelegramUser[] ROOTS = {TelegramUsers.MAXI};
    
    private TelegramCenter() {
        this.start();
    }

    public static TelegramCenter getInstance() {
        return instance;
    }

    public boolean isAdmin(long chatId) {
        for (TelegramUsers.TelegramUser tu : ADMINS) {        
            if(tu.getId() == chatId)
                return true;
        }
        return false;
    }
    
    public TelegramAdmin getMain() {
        return main;
    }
    
    public void start() {
        this.main.start();
        //this.admin.start();
        //this.root.start();
    }
    
    public void toUser(long chatId, String text, Object... args) {
        this.main.sendTextMessage(chatId, String.format(text, args));
    }
    
    public void toAdmin(String text, Object... args) {
        for (TelegramUsers.TelegramUser tu : ADMINS) {        
            getMain().sendTextMessage(tu.getId(), String.format(text, args));
        }
    }

    public void toRoot(long chatId, String text, Object... args) {
        if (!TelegramUsers.isRoot(chatId))
            toRoot(text, args);
    }
    
    public void toRoot(String text, Object... args) {
        //getRoot().sendTextMessage(TelegramUsers.MAXI.getId(), String.format(text, args));
        
        for (TelegramUsers.TelegramUser tu : ROOTS) {
            getMain().sendTextMessage(tu.getId(), String.format(text, args));
        }
    }
    
    public <T extends Serializable, Method extends BotApiMethod<T>> T executeMain(Method method) throws TelegramApiException {
        return main.execute(method);
    }
    
    /**
     * Sends a MenuChat to all admin users
     * @param menuChatCreator Function that creates a MenuChat for a specific user ID
     */
    public void sendMenuToAdmins(MenuChatCreator menuChatCreator) {
        for (TelegramUsers.TelegramUser admin : ADMINS) {
            try {
                menuChatCreator.create(admin.getId()).reply();
            } catch (Exception e) {
                // Log error but continue with other admins
                System.err.println("Error sending menu to admin " + admin.getName() + ": " + e.getMessage());
            }
        }
    }
    
    /**
     * Sends a MenuChat to all root users
     * @param menuChatCreator Function that creates a MenuChat for a specific user ID
     */
    public void sendMenuToRoots(MenuChatCreator menuChatCreator) {
        for (TelegramUsers.TelegramUser root : ROOTS) {
            try {
                menuChatCreator.create(root.getId()).reply();
            } catch (Exception e) {
                // Log error but continue with other roots
                System.err.println("Error sending menu to root " + root.getName() + ": " + e.getMessage());
            }
        }
    }
    
    /**
     * Functional interface for creating MenuChat objects
     */
    @FunctionalInterface
    public interface MenuChatCreator {
        main.courses.menuchats.MenuChat create(long chatId);
    }
}
