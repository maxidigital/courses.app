package main;

import blue.underwater.telegram.admin.TelegramTokens;
import java.util.ArrayList;
import java.util.List;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class MiBot extends TelegramLongPollingBot
{

    private final List<Listener> listeners = new ArrayList<>();

    public static void main(String[] args) {
        try {
            // Crear la instancia de TelegramBotsApi
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);

            // Registrar el bot
            botsApi.registerBot(new MiBot());

            System.out.println("Bot iniciado correctamente");
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void addListener(Listener listener) {
        this.listeners.add(listener);
    }

    @Override
    public void onUpdateReceived(Update update) {

        System.out.println("Update from: " + update.getMessage().getFrom().getFirstName());
        for (Listener listener : listeners) {
            listener.onUpdateReceived(update);
        }
    }

    @Override
    public String getBotUsername() {
        return TelegramTokens.FREMA_10.getBotName();
    }

    @Override
    public String getBotToken() {
        return TelegramTokens.FREMA_10.getBotToken();
    }

    public static interface Listener
    {

        void onUpdateReceived(Update update);
    }
}
