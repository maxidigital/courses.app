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

    public static String buildDay2Reminder(String firstName, String day2,
                                           String startTime, String location, String mapsUrl) {
        return head()
            + "  <p>Hi " + firstName + " 🌊</p>\n"
            + "\n"
            + "  <p>Great job today — Day 1 is always a big step, and you did really well. You've already built the foundation, and from here things start to feel much more natural.</p>\n"
            + "\n"
            + "  <p>For tomorrow, everything becomes easier and more fluid. Your body already understands what we're doing, so we'll simply refine the technique, improve your comfort, and let the dives come naturally — always at your own pace, with no pressure at all.</p>\n"
            + "\n"
            + "  <hr style=\"border:none;border-top:1px solid #ddd;margin:24px 0;\">\n"
            + "\n"
            + "  <div class=\"section-title\">🫁 Preparation for Day 2</div>\n"
            + "  <p>To get the most out of tomorrow, please take a few minutes today to go through these exercises:</p>\n"
            + "\n"
            + "  <div class=\"section-title\">Equalization exercise (very important)</div>\n"
            + "  <p>This will help you improve control and make equalization smoother in the water:</p>\n"
            + "  <ol>\n"
            + "    <li>Equalize your ears and hold the pressure for 5 seconds, then relax</li>\n"
            + "    <li>Equalize again and hold for 10 seconds, then relax</li>\n"
            + "    <li>Repeat and hold for 15 seconds, then relax</li>\n"
            + "    <li>Finally, hold for 20 seconds, then relax</li>\n"
            + "  </ol>\n"
            + "  <p>The key is to keep the pressure gentle and steady, never forced. This helps train the muscles so equalization becomes more effortless while diving.</p>\n"
            + "\n"
            + "  <hr style=\"border:none;border-top:1px solid #ddd;margin:24px 0;\">\n"
            + "\n"
            + "  <div class=\"section-title\">Breathing exercise (10 cycles)</div>\n"
            + "  <p><strong>Goal:</strong> Complete 10 slow breathing cycles + 1 final breath</p>\n"
            + "  <p><strong>How to do it:</strong></p>\n"
            + "  <ol>\n"
            + "    <li>Sit comfortably and fully relax your body</li>\n"
            + "    <li>Perform 10 slow, continuous breathing cycles (belly → chest → long, relaxed exhale with a soft \"tssss\")</li>\n"
            + "    <li>The full exercise should take 4–5 minutes</li>\n"
            + "    <li>If it's too fast, simply slow down your exhale</li>\n"
            + "    <li>Finish with one final full breath</li>\n"
            + "  </ol>\n"
            + "  <p>We'll check your timing tomorrow before going in the water 👍</p>\n"
            + "\n"
            + "  <hr style=\"border:none;border-top:1px solid #ddd;margin:24px 0;\">\n"
            + "\n"
            + "  <div class=\"section-title\">⏰ Reminder for tomorrow</div>\n"
            + "  <p>\n"
            + "    Day 2: <strong>" + day2 + "</strong><br>\n"
            + "    📍 Location: <strong>" + location + "</strong><br>\n"
            + "    <a href=\"" + mapsUrl + "\">" + mapsUrl + "</a><br>\n"
            + "    🕤 Time: <strong>" + startTime + "</strong>\n"
            + "  </p>\n"
            + "  <p>Please arrive a few minutes early so we can start relaxed and without rush.</p>\n"
            + "\n"
            + "  <hr style=\"border:none;border-top:1px solid #ddd;margin:24px 0;\">\n"
            + "\n"
            + "  <p>🌿 Day 2 is usually where everything \"clicks\". Trust the process, stay relaxed, and don't try to force anything — the depth will come naturally as your technique improves.</p>\n"
            + "\n"
            + "  <p>Looking forward to seeing you tomorrow and continuing this journey together 💙</p>\n"
            + "\n"
            + "  <div class=\"footer\">\n"
            + "    Un abrazo,<br>\n"
            + "    <strong>Ismael</strong><br>\n"
            + "    Freedive Mallorca\n"
            + "  </div>\n"
            + "\n"
            + "</body>\n"
            + "</html>";
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
        return "  <div style=\"background:#e8f4fb;border-radius:10px;padding:36px 20px;margin:24px 0;text-align:center;\">\n"
            + "  <p style=\"margin:0 0 16px 0;\">Please just click below so I know you've received everything and we're all set 👍</p>\n"
            + "  <a href=\"" + confirmationUrl + "\" style=\"background:#0077b6;color:white;padding:12px 28px;"
            + "border-radius:8px;text-decoration:none;font-weight:bold;\">Everything looks good ✅</a>\n"
            + "  </div>\n"
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
