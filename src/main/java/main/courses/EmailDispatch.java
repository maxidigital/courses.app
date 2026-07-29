package main.courses;

import blue.underwater.email.admin.Email;
import blue.underwater.email.admin.EmailAdmin;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Email sending with per-recipient isolation and retries.
 *
 * Mailjet sometimes resets the connection during the TLS handshake. A plain
 * "for (Email e : emails) send(e)" turns that into a batch-wide failure: the
 * remaining recipients are never attempted and the bookkeeping that follows the
 * loop is skipped, so nothing records who was already notified. Sending through
 * here keeps one bad recipient from taking the rest down, and rides out
 * transient errors.
 */
public class EmailDispatch {

    private static final int MAX_ATTEMPTS = 3;
    private static final long BASE_BACKOFF_MS = 1500;

    private EmailDispatch() {
    }

    /** Outcome of a batch: which recipients went out, and why the others didn't. */
    public static class Result {

        public final List<String> sent = new ArrayList<>();
        public final Map<String, String> failed = new LinkedHashMap<>();

        public boolean isEmpty() {
            return sent.isEmpty() && failed.isEmpty();
        }

        public int total() {
            return sent.size() + failed.size();
        }

        /** One "• email: reason" per line, for the Telegram reply. */
        public String describeFailures() {
            StringBuilder sb = new StringBuilder();
            for (Map.Entry<String, String> entry : failed.entrySet()) {
                if (sb.length() > 0) sb.append("\n");
                sb.append("• ").append(entry.getKey()).append(": ").append(entry.getValue());
            }
            return sb.toString();
        }
    }

    /**
     * Sends one email, retrying with a growing delay. Rethrows the last failure
     * if every attempt is exhausted.
     */
    public static void sendWithRetry(Email email) throws Exception {
        Exception last = null;
        for (int attempt = 1; attempt <= MAX_ATTEMPTS; attempt++) {
            try {
                EmailAdmin.getInstance().send(email);
                return;
            } catch (Exception ex) {
                last = ex;
                if (attempt == MAX_ATTEMPTS) break;
                Logger.getLogger(EmailDispatch.class.getName()).log(Level.WARNING,
                        "Email attempt {0}/{1} failed, retrying: {2}",
                        new Object[]{attempt, MAX_ATTEMPTS, ex.getMessage()});
                try {
                    Thread.sleep(BASE_BACKOFF_MS * attempt);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
        throw last;
    }

    /**
     * Sends every email independently, so a failure only costs that recipient.
     * The addresses list must be in the same order as emails; callers use the
     * returned Result to mark as notified only what actually went out.
     */
    public static Result sendBatch(List<Email> emails, List<String> addresses) {
        Result result = new Result();
        for (int i = 0; i < emails.size(); i++) {
            String address = i < addresses.size() ? addresses.get(i) : "unknown";
            try {
                sendWithRetry(emails.get(i));
                result.sent.add(address);
            } catch (Exception ex) {
                String reason = ex.getMessage() == null ? ex.getClass().getSimpleName() : ex.getMessage();
                result.failed.put(address, reason);
                Logger.getLogger(EmailDispatch.class.getName()).log(Level.SEVERE,
                        "Giving up on " + address + " after " + MAX_ATTEMPTS + " attempts", ex);
            }
        }
        return result;
    }
}
