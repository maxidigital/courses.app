package main.reminder;

import blue.underwater.commons.datetime.XDate;
import blue.underwater.commons.logging.XLogger;
import java.io.IOException;
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
import main.telegram.TelegramCenter;

public class DailyReminderService {

    private static final int REMINDER_HOUR = 18;
    private static final ZoneId TIMEZONE = ZoneId.of("Europe/Madrid");
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    public void start() {
        checkAndNotify();
        long initialDelay = computeInitialDelay();
        scheduler.scheduleAtFixedRate(this::checkAndNotify, initialDelay, TimeUnit.DAYS.toSeconds(1), TimeUnit.SECONDS);
        XLogger.info(this, "DailyReminderService started, next run in %d seconds", initialDelay);
    }

    private long computeInitialDelay() {
        ZonedDateTime now = ZonedDateTime.now(TIMEZONE);
        ZonedDateTime nextRun = now.withHour(REMINDER_HOUR).withMinute(0).withSecond(0).withNano(0);
        if (!now.isBefore(nextRun)) {
            nextRun = nextRun.plusDays(1);
        }
        return nextRun.toEpochSecond() - now.toEpochSecond();
    }

    private void checkAndNotify() {
        try {
            LocalDate targetDate = LocalDate.now(TIMEZONE).plusDays(2);
            String dateStr = targetDate.format(DateTimeFormatter.ISO_LOCAL_DATE);
            List<Course> courses = CalendarService.getInstance().getCoursesForDay(XDate.parseDate(dateStr));

            if (courses.isEmpty()) {
                XLogger.info(this, "No courses found for %s", dateStr);
                return;
            }

            StringBuilder msg = new StringBuilder();
            msg.append(String.format("🗓 Courses in 2 days (%s):\n\n", dateStr));
            for (Course course : courses) {
                msg.append("• ").append(course.toString()).append("\n\n");
            }

            TelegramCenter.getInstance().toAdmin("%s", msg.toString());

        } catch (IOException e) {
            XLogger.severe(this, "DailyReminderService error: %s", e.getMessage());
        }
    }
}
