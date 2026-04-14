package main.reminder;

import blue.underwater.commons.datetime.XDate;
import blue.underwater.commons.logging.XLogger;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import main.calendar.CalendarService;
import main.calendar.Course;
import main.calendar.EventDetailsParser;
import main.courses.menus.CourseLocationMenu;
import main.courses.menus.CourseStartTimeMenu;
import main.courses.menuchats.Day2ConfirmMenuChat;
import main.telegram.TelegramCenter;

public class Day2ReminderService {

    private static final int REMINDER_HOUR = 17;
    private static final ZoneId TIMEZONE = ZoneId.of("Europe/Madrid");
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    public void start() {
        long initialDelay = computeInitialDelay();
        scheduler.scheduleAtFixedRate(() -> {
            try {
                checkDay2();
            } catch (Exception e) {
                XLogger.severe(this, "Day2ReminderService uncaught error: %s", e.getMessage());
            }
        }, initialDelay, TimeUnit.DAYS.toSeconds(1), TimeUnit.SECONDS);
        XLogger.info(this, "Day2ReminderService started, next run in %d seconds", initialDelay);
    }

    private long computeInitialDelay() {
        ZonedDateTime now = ZonedDateTime.now(TIMEZONE);
        ZonedDateTime nextRun = now.withHour(REMINDER_HOUR).withMinute(0).withSecond(0).withNano(0);
        if (!now.isBefore(nextRun)) {
            nextRun = nextRun.plusDays(1);
        }
        return nextRun.toEpochSecond() - now.toEpochSecond();
    }

    public void checkDay2() {
        try {
            LocalDate today = LocalDate.now(TIMEZONE);
            String isoDate = today.format(DateTimeFormatter.ISO_LOCAL_DATE);
            List<Course> courses = CalendarService.getInstance().getCoursesStartingOn(XDate.parseDate(isoDate));

            for (int i = 0; i < courses.size(); i++) {
                Course course = courses.get(i);
                if (!"Freediver Course".equals(course.getType())) continue;

                EventDetailsParser.Result details = EventDetailsParser.parse(
                        course.getXEvent().getDescription().getRawText());

                final int courseIndex = i;

                if (!details.found) {
                    TelegramCenter.getInstance().sendMenuToAdmins(chatId ->
                        new Day2ConfirmMenuChat(TelegramCenter.getInstance().getMain(), chatId,
                            "⚠️ <b>Freediver Course today but Day 2 details are not set.</b>\nRun /check48 to configure.",
                            isoDate, courseIndex)
                    );
                    continue;
                }

                LocalDate day2Date = today.plusDays(1);
                String day2Formatted = day2Date.format(DateTimeFormatter.ofPattern("EEEE, d MMMM"));
                int tSlot = Character.getNumericValue(details.times.charAt(1));
                int lSlot = Character.getNumericValue(details.locs.charAt(1));
                String timeStr = CourseStartTimeMenu.slotToTimeStr(tSlot);
                String locName = CourseLocationMenu.getName(lSlot);
                String locUrl  = CourseLocationMenu.getUrl(lSlot);

                String text = String.format(
                    "🌊 <b>Freediver Course — Day 2 Reminder</b>\n\n"
                    + "📅 <b>%s</b>\n"
                    + "📍 %s\n"
                    + "🕤 %s\n\n"
                    + "Send Day 2 reminder emails to participants?",
                    day2Formatted, locName, timeStr);

                TelegramCenter.getInstance().sendMenuToAdmins(chatId ->
                    new Day2ConfirmMenuChat(TelegramCenter.getInstance().getMain(), chatId,
                        text, isoDate, courseIndex)
                );

                XLogger.info(this, "Day2 reminder prompt sent to admins for %s", isoDate);
            }
        } catch (Exception e) {
            XLogger.severe(this, "Day2ReminderService error: %s", e.getMessage());
        }
    }
}
