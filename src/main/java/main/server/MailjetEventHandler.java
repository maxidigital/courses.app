package main.server;

import blue.underwater.commons.logging.XLogger;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import main.courses.EmailDispatch;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Receives Mailjet event webhooks and tells admins when one of our emails
 * could not be delivered (bounced or blocked) or was opened for the first time.
 *
 * The endpoint is closed until a key is configured (MAILJET_WEBHOOK_KEY env
 * var or mailjet.webhook.key property); Mailjet must call it with ?key=...
 */
public class MailjetEventHandler implements HttpHandler {

    private static final int MAX_REMEMBERED_OPENS = 5000;

    /**
     * Messages whose first open was already reported. Kept in memory only, so
     * a restart forgets them and a later re-open is reported once more.
     */
    private static final Set<String> reportedOpens = Collections.synchronizedSet(
        Collections.newSetFromMap(new LinkedHashMap<String, Boolean>() {
            @Override
            protected boolean removeEldestEntry(Map.Entry<String, Boolean> eldest) {
                return size() > MAX_REMEMBERED_OPENS;
            }
        }));

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!isAuthorized(exchange.getRequestURI().getQuery())) {
            respond(exchange, 403, "forbidden");
            return;
        }
        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            respond(exchange, 405, "method not allowed");
            return;
        }

        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8).trim();
        try {
            // Mailjet sends either one event or a group of them
            JSONArray events = body.startsWith("[") ? new JSONArray(body) : new JSONArray().put(new JSONObject(body));
            for (int i = 0; i < events.length(); i++) {
                process(events.getJSONObject(i));
            }
        } catch (Exception e) {
            XLogger.severe(this, "Mailjet event error: %s", e.getMessage());
        }
        // Always acknowledge: Mailjet retries anything that is not a 200
        respond(exchange, 200, "ok");
    }

    private void process(JSONObject event) {
        String email = event.optString("email", "").toLowerCase();

        String reason = failureReason(event);
        if (reason != null) {
            XLogger.info(this, "Email not delivered to %s: %s", email, reason);
            notifyAll(String.format("📭 <b>Email not delivered</b>\n%s\nReason: %s\n\nCheck the address in the calendar.",
                escape(describe(email)), escape(reason)));
            return;
        }

        String openKey = openKey(event);
        if (openKey != null && reportedOpens.add(openKey)) {
            XLogger.info(this, "Email opened by %s", email);
            String subject = event.optString("Payload", "");
            notifyAll(String.format("👀 <b>Email opened</b>\n%s%s",
                escape(describe(email)), subject.isEmpty() ? "" : "\n" + escape(subject)));
        }
    }

    private static String describe(String email) {
        main.contacts.Contact contact = main.contacts.ContactsService.getInstance().findByEmail(email);
        if (contact != null && contact.getFistName() != null && !contact.getFistName().trim().isEmpty()) {
            return contact.getFistName().trim() + " (" + email + ")";
        }
        return email;
    }

    private static void notifyAll(String notice) {
        main.telegram.TelegramCenter.getInstance().toAdmin("%s", notice);
        main.telegram.TelegramCenter.getInstance().toRoot("%s", notice);
    }

    private static boolean isOurs(JSONObject event) {
        if (!EmailDispatch.CUSTOM_ID.equals(event.optString("CustomID", ""))) return false;
        String email = event.optString("email", "");
        return !email.isEmpty() && !email.equalsIgnoreCase(EmailDispatch.BCC_ADDRESS);
    }

    /**
     * @return what identifies the opened message (Mailjet gives every
     * recipient its own MessageID), or null if this is not an open of one of
     * our emails
     */
    static String openKey(JSONObject event) {
        if (!event.optString("event", "").equals("open") || !isOurs(event)) return null;
        String messageId = event.optString("MessageID", "");
        return messageId.isEmpty() ? event.optString("email", "").toLowerCase() : messageId;
    }

    /**
     * @return why the email did not arrive, or null if this event is not a
     * delivery failure of one of our emails
     */
    static String failureReason(JSONObject event) {
        String type = event.optString("event", "");
        if (!type.equals("bounce") && !type.equals("blocked")) return null;
        if (!isOurs(event)) return null;

        String reason;
        if (type.equals("blocked")) reason = "blocked";
        else reason = event.optBoolean("hard_bounce", false) ? "hard bounce" : "soft bounce";

        String error = event.optString("error", "");
        if (!error.isEmpty()) reason += ": " + error;
        String relatedTo = event.optString("error_related_to", "");
        if (!relatedTo.isEmpty()) reason += " (" + relatedTo + ")";
        return reason;
    }

    private static boolean isAuthorized(String query) {
        String expected = System.getProperty("mailjet.webhook.key", System.getenv("MAILJET_WEBHOOK_KEY"));
        if (expected == null || expected.trim().isEmpty() || query == null) return false;
        for (String param : query.split("&")) {
            if (param.startsWith("key=")) {
                return MessageDigest.isEqual(
                    param.substring(4).getBytes(StandardCharsets.UTF_8),
                    expected.trim().getBytes(StandardCharsets.UTF_8));
            }
        }
        return false;
    }

    private static String escape(String text) {
        return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    private static void respond(HttpExchange exchange, int status, String text) throws IOException {
        byte[] bytes = text.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "text/plain; charset=UTF-8");
        exchange.sendResponseHeaders(status, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }
}
