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
import main.calendar.Student;
import main.courses.menuchats.CourseSelectionMenuChat;
import main.courses.menuchats.CourseStartTimeMenuChat;
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

    public void checkAndNotify() {
        checkAndNotify(-1);
    }

    public void checkAndNotify(long requesterChatId) {
        try {
            LocalDate targetDate = LocalDate.now(TIMEZONE).plusDays(2);
            String isoDate = targetDate.format(DateTimeFormatter.ISO_LOCAL_DATE);
            String formattedDate = targetDate.format(DateTimeFormatter.ofPattern("dd MMM yyyy"));
            List<Course> courses = CalendarService.getInstance().getCoursesForDay(XDate.parseDate(isoDate));

            if (courses.isEmpty()) {
                XLogger.info(this, "No courses found for %s", isoDate);
                if (requesterChatId > 0) {
                    TelegramCenter.getInstance().toUser(requesterChatId, "📭 No courses found for %s", formattedDate);
                }
                return;
            }

            if (courses.size() == 1) {
                Course course = courses.get(0);
                String courseText = buildCourseDetails(course, formattedDate);
                TelegramCenter.getInstance().sendMenuToAdmins(chatId ->
                    new CourseStartTimeMenuChat(TelegramCenter.getInstance().getMain(), chatId, courseText, isoDate, 0)
                );
            } else {
                TelegramCenter.getInstance().sendMenuToAdmins(chatId ->
                    new CourseSelectionMenuChat(TelegramCenter.getInstance().getMain(), chatId, isoDate, formattedDate, courses)
                );
            }

        } catch (IOException e) {
            XLogger.severe(this, "DailyReminderService error: %s", e.getMessage());
            if (requesterChatId > 0) {
                TelegramCenter.getInstance().toUser(requesterChatId, "❌ Error checking courses: %s", e.getMessage());
            }
        }
    }

    public static String buildCourseDetails(Course course, String formattedDate) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("🗓 <b>%s</b>\n", course.getType()));
        sb.append(String.format("📅 %s\n", formattedDate));

        List<Student> students = course.getEventStudents().getStudents();
        if (!students.isEmpty()) {
            sb.append(String.format("\n<b>%d participant%s:</b>\n", students.size(), students.size() == 1 ? "" : "s"));
            for (Student student : students) {
                sb.append("• ").append(student.getEmail()).append("\n");
            }
        }
        sb.append("\n");
        return sb.toString();
    }
}
