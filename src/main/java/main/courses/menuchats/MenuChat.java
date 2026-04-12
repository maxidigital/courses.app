package main.courses.menuchats;

import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

/**
 *
 * @author bott_ma
 */
public interface MenuChat
{

    void callbackQueryReceived(CallbackQuery callbackQuery);

    long getMessageId();

    void reply();
}
