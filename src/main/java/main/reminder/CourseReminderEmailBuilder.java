package main.reminder;

public class CourseReminderEmailBuilder {

    private static final String DAY2_LOCATION = "Bon Aire (Alcúdia)";
    private static final String DAY2_MAPS_URL =
        "https://www.google.com/maps/search/?api=1&query=Bon+Aire+Alcudia+Mallorca";

    /**
     * Builds the HTML body for the Freediver Course confirmation email.
     *
     * @param firstName  Participant's first name
     * @param day1       Day 1 formatted date, e.g. "Monday, 14 April 2026"
     * @param day2       Day 2 formatted date, e.g. "Tuesday, 15 April 2026"
     * @param startTime  Start time string, e.g. "9:00 AM" or "10:00 AM"
     * @param day1Location  Location name for Day 1
     * @param day1MapsUrl   Google Maps URL for Day 1 location
     */
    public static String build(String firstName, String day1, String day2,
                               String startTime, String day1Location, String day1MapsUrl) {
        return "<!DOCTYPE html>\n"
            + "<html lang=\"en\">\n"
            + "<head>\n"
            + "  <meta charset=\"UTF-8\" />\n"
            + "  <style>\n"
            + "    body { font-family: Arial, sans-serif; color: #222; line-height: 1.7; padding: 20px; max-width: 600px; }\n"
            + "    .day-title { font-weight: bold; color: #0077b6; font-size: 1.05em; margin-top: 24px; }\n"
            + "    .detail { margin-left: 16px; margin-top: 4px; }\n"
            + "    .section-title { font-weight: bold; margin-top: 24px; }\n"
            + "    ul { margin: 4px 0 0 16px; }\n"
            + "    a { color: #0077b6; }\n"
            + "    .footer { margin-top: 40px; color: #555; }\n"
            + "  </style>\n"
            + "</head>\n"
            + "<body>\n"
            + "\n"
            + "  <p>Hi " + firstName + ",</p>\n"
            + "\n"
            + "  <p>We are excited to have you join us for your upcoming Freediver Course.</p>\n"
            + "\n"
            + "  <p>Here are all the details for our days together:</p>\n"
            + "\n"
            + "  <div class=\"day-title\">Day 1: " + day1 + "</div>\n"
            + "  <div class=\"detail\">\n"
            + "    Location: " + day1Location + "<br>\n"
            + "    <a href=\"" + day1MapsUrl + "\">📍 View on Google Maps</a><br>\n"
            + "    Time: " + startTime + " – approx. 2:00/3:00 PM<br>\n"
            + "    What to expect: We'll start with a theory session (around 1.5–2 hours), followed by your first session in the water exploring duck-dives and body positioning.\n"
            + "  </div>\n"
            + "\n"
            + "  <div class=\"day-title\">Day 2: " + day2 + "</div>\n"
            + "  <div class=\"detail\">\n"
            + "    Location: " + DAY2_LOCATION + "<br>\n"
            + "    <a href=\"" + DAY2_MAPS_URL + "\">📍 View on Google Maps</a><br>\n"
            + "    Time: " + startTime + " – approx. 2:00/3:00 PM<br>\n"
            + "    What to expect: We'll dive deeper into breathing techniques (Pranayamas) and safety procedures before heading back into the blue.\n"
            + "  </div>\n"
            + "\n"
            + "  <div class=\"section-title\">What to bring:</div>\n"
            + "  <ul>\n"
            + "    <li>👙 Swimsuit (to wear under the wetsuit)</li>\n"
            + "    <li>Towel</li>\n"
            + "    <li>💧 Water to stay hydrated during the theory session</li>\n"
            + "  </ul>\n"
            + "\n"
            + "  <div class=\"section-title\">A few tips for the best experience:</div>\n"
            + "  <ul>\n"
            + "    <li>Try to avoid alcohol the night before and coffee in the morning.</li>\n"
            + "    <li>A light breakfast is recommended so you don't freedive on a full stomach.</li>\n"
            + "    <li>Relax! There is no performance pressure — our focus is entirely on your comfort and safety.</li>\n"
            + "  </ul>\n"
            + "\n"
            + "  <p>We provide all the professional equipment you'll need, so just bring your curiosity and your smile!</p>\n"
            + "\n"
            + "  <p>If you have any questions at all, please just let me know. We are looking forward to seeing you soon in the water!</p>\n"
            + "\n"
            + "  <div class=\"footer\">\n"
            + "    Saludos,<br>\n"
            + "    <strong>Team Freedive Mallorca</strong>\n"
            + "  </div>\n"
            + "\n"
            + "</body>\n"
            + "</html>";
    }
}
