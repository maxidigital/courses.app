package main.reminder;

public class CourseReminderEmailBuilder {

    public static String build1Day(String firstName, String day1, String startTime,
                                   String location, String mapsUrl, String duration, String confirmationUrl,
                                   String day1Desc) {
        return head()
            + "  <p>Hi " + firstName + ",</p>\n"
            + "\n"
            + "  <p>We are excited to have you join us for your upcoming session.</p>\n"
            + "\n"
            + "  <p>Here are all the details:</p>\n"
            + "\n"
            + "  <div class=\"day-title\">" + day1 + "</div>\n"
            + "  <div class=\"detail\">\n"
            + "    Location: " + location + "<br>\n"
            + "    <a href=\"" + mapsUrl + "\">📍 View on Google Maps</a><br>\n"
            + "    Time: " + startTime + " (approx. " + duration + ")<br>\n"
            + expectLine(day1Desc)
            + "  </div>\n"
            + "\n"
            + confirmBlock(confirmationUrl)
            + whatToBring()
            + tips()
            + footer();
    }

    public static String build2Day(String firstName,
                                   String day1, String startTime1, String loc1, String url1,
                                   String day2, String startTime2, String loc2, String url2,
                                   String duration, String confirmationUrl,
                                   String day1Desc, String day2Desc) {
        return head()
            + "  <p>Hi " + firstName + ",</p>\n"
            + "\n"
            + "  <p>We are excited to have you join us for your upcoming Freediver Course.</p>\n"
            + "\n"
            + "  <p>Here are all the details for our days together:</p>\n"
            + "\n"
            + "  <div class=\"day-title\">Day 1: " + day1 + "</div>\n"
            + "  <div class=\"detail\">\n"
            + "    Location: " + loc1 + "<br>\n"
            + "    <a href=\"" + url1 + "\">📍 View on Google Maps</a><br>\n"
            + "    Time: " + startTime1 + " (approx. " + duration + ")<br>\n"
            + expectLine(day1Desc)
            + "  </div>\n"
            + "\n"
            + "  <div class=\"day-title\">Day 2: " + day2 + "</div>\n"
            + "  <div class=\"detail\">\n"
            + "    Location: " + loc2 + "<br>\n"
            + "    <a href=\"" + url2 + "\">📍 View on Google Maps</a><br>\n"
            + "    Time: " + startTime2 + " (approx. " + duration + ")<br>\n"
            + expectLine(day2Desc)
            + "  </div>\n"
            + "\n"
            + confirmBlock(confirmationUrl)
            + whatToBring()
            + tips()
            + footer();
    }

    public static String build3Day(String firstName,
                                   String day1, String startTime1, String loc1, String url1,
                                   String day2, String startTime2, String loc2, String url2,
                                   String day3, String startTime3, String loc3, String url3,
                                   String duration, String confirmationUrl,
                                   String day1Desc, String day2Desc, String day3Desc) {
        return head()
            + "  <p>Hi " + firstName + ",</p>\n"
            + "\n"
            + "  <p>We are excited to have you join us for your upcoming Advance Freediver Course.</p>\n"
            + "\n"
            + "  <p>Here are all the details for our days together:</p>\n"
            + "\n"
            + "  <div class=\"day-title\">Day 1: " + day1 + "</div>\n"
            + "  <div class=\"detail\">\n"
            + "    Location: " + loc1 + "<br>\n"
            + "    <a href=\"" + url1 + "\">📍 View on Google Maps</a><br>\n"
            + "    Time: " + startTime1 + " (approx. " + duration + ")<br>\n"
            + expectLine(day1Desc)
            + "  </div>\n"
            + "\n"
            + "  <div class=\"day-title\">Day 2: " + day2 + "</div>\n"
            + "  <div class=\"detail\">\n"
            + "    Location: " + loc2 + "<br>\n"
            + "    <a href=\"" + url2 + "\">📍 View on Google Maps</a><br>\n"
            + "    Time: " + startTime2 + " (approx. " + duration + ")<br>\n"
            + expectLine(day2Desc)
            + "  </div>\n"
            + "\n"
            + "  <div class=\"day-title\">Day 3: " + day3 + "</div>\n"
            + "  <div class=\"detail\">\n"
            + "    Location: " + loc3 + "<br>\n"
            + "    <a href=\"" + url3 + "\">📍 View on Google Maps</a><br>\n"
            + "    Time: " + startTime3 + " (approx. " + duration + ")<br>\n"
            + expectLine(day3Desc)
            + "  </div>\n"
            + "\n"
            + confirmBlock(confirmationUrl)
            + whatToBring()
            + tips()
            + footer();
    }

    private static String expectLine(String desc) {
        if (desc == null || desc.trim().isEmpty()) return "";
        return "    What to expect: " + desc + "\n";
    }

    private static String head() {
        return "<!DOCTYPE html>\n"
            + "<html lang=\"en\">\n"
            + "<head>\n"
            + "  <meta charset=\"UTF-8\" />\n"
            + "  <style>\n"
            + "    body { font-family: Arial, sans-serif; color: #222; line-height: 1.7; padding: 20px; max-width: 600px; text-align: justify; }\n"
            + "    .day-title { font-weight: bold; color: #0077b6; font-size: 1.05em; margin-top: 24px; text-align: left; }\n"
            + "    .detail { margin-left: 16px; margin-top: 4px; }\n"
            + "    .section-title { font-weight: bold; margin-top: 24px; text-align: left; }\n"
            + "    ul { margin: 4px 0 0 16px; }\n"
            + "    a { color: #0077b6; }\n"
            + "    .footer { margin-top: 40px; color: #555; text-align: left; }\n"
            + "  </style>\n"
            + "</head>\n"
            + "<body>\n"
            + "\n";
    }

    private static String confirmBlock(String confirmationUrl) {
        return "  <hr style=\"border:none;border-top:1px solid #ddd;margin:24px 0;\">\n"
            + "  <p>Just click below so I know you've received everything and we're all set 👍</p>\n"
            + "  <p style=\"text-align:center;margin-top:16px;\">"
            + "<a href=\"" + confirmationUrl + "\" style=\"background:#0077b6;color:white;padding:12px 28px;"
            + "border-radius:8px;text-decoration:none;font-weight:bold;\">Everything looks good ✅</a></p>\n"
            + "  <hr style=\"border:none;border-top:1px solid #ddd;margin:24px 0;\">\n"
            + "\n";
    }

    private static String whatToBring() {
        return "  <div class=\"section-title\">What to bring:</div>\n"
            + "  <ul>\n"
            + "    <li>👙 Swimsuit (to wear under the wetsuit)</li>\n"
            + "    <li>🧴 Towel</li>\n"
            + "    <li>💧 Water to stay hydrated during the theory session</li>\n"
            + "  </ul>\n"
            + "\n";
    }

    private static String tips() {
        return "  <div class=\"section-title\">A few tips for the best experience:</div>\n"
            + "  <ul>\n"
            + "    <li>Try to avoid alcohol the night before and coffee in the morning.</li>\n"
            + "    <li>A light breakfast is recommended so you don't freedive on a full stomach.</li>\n"
            + "    <li>Relax! There is no performance pressure — our focus is entirely on your comfort and safety.</li>\n"
            + "  </ul>\n"
            + "\n"
            + "  <p>We provide all the professional equipment you'll need, so just bring your curiosity and your smile!</p>\n"
            + "\n"
            + "  <p>If you have any questions at all, please just let me know. We are looking forward to seeing you soon in the water!</p>\n"
            + "\n";
    }

    private static String footer() {
        return "  <div class=\"footer\">\n"
            + "    Saludos,<br>\n"
            + "    <strong>Team Freedive Mallorca</strong>\n"
            + "  </div>\n"
            + "\n"
            + "</body>\n"
            + "</html>";
    }
}
