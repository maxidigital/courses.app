package main.server;

import blue.underwater.commons.logging.XLogger;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import main.courses.EmailDispatch;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Receives Mailjet event webhooks and tells admins when one of our emails
 * could not be delivered (bounced or blocked).
 *
 * The endpoint is closed until a key is configured (MAILJET_WEBHOOK_KEY env
 * var or mailjet.webhook.key property); Mailjet must call it with ?key=...
 */
public class MailjetEventHandler implements HttpHandler {

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
        String reason = failureReason(event);
        if (reason == null) return;

        String email = event.optString("email", "").toLowerCase();
        XLogger.info(this, "Email not delivered to %s: %s", email, reason);

        String who = email;
        main.contacts.Contact contact = main.contacts.ContactsService.getInstance().findByEmail(email);
        if (contact != null && contact.getFistName() != null && !contact.getFistName().trim().isEmpty()) {
            who = contact.getFistName().trim() + " (" + email + ")";
        }
        String notice = String.format("📭 <b>Email not delivered</b>\n%s\nReason: %s\n\nCheck the address in the calendar.",
            escape(who), escape(reason));
        main.telegram.TelegramCenter.getInstance().toAdmin("%s", notice);
        main.telegram.TelegramCenter.getInstance().toRoot("%s", notice);
    }

    /**
     * @return why the email did not arrive, or null if this event is not a
     * delivery failure of one of our emails
     */
    static String failureReason(JSONObject event) {
        String type = event.optString("event", "");
        if (!type.equals("bounce") && !type.equals("blocked")) return null;
        if (!EmailDispatch.CUSTOM_ID.equals(event.optString("CustomID", ""))) return null;
        String email = event.optString("email", "");
        if (email.isEmpty() || email.equalsIgnoreCase(EmailDispatch.BCC_ADDRESS)) return null;

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
