package main.server;

import blue.underwater.calendar.admin.event.Tools;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDate;
import java.util.List;
import main.calendar.CalendarService;
import main.calendar.Course;
import main.calendar.Student;
import blue.underwater.commons.datetime.XDate;
import blue.underwater.commons.logging.XLogger;

public class ConfirmHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String query = exchange.getRequestURI().getQuery();
        String date = null;
        String token = null;

        if (query != null) {
            for (String param : query.split("&")) {
                if (param.startsWith("date=")) date = param.substring(5);
                else if (param.startsWith("token=")) token = param.substring(6);
            }
        }

        String html;
        if (date == null || token == null) {
            html = errorPage("Invalid confirmation link.");
        } else {
            html = processConfirmation(date, token);
        }

        byte[] bytes = html.getBytes("UTF-8");
        exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
        exchange.sendResponseHeaders(200, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private String processConfirmation(String isoDate, String token) {
        try {
            List<Course> courses = CalendarService.getInstance().getCoursesForDay(XDate.parseDate(isoDate));
            for (Course course : courses) {
                Student student = course.getEventStudents().getStudentByEmailHash(token);
                if (student != null) {
                    CalendarService.getInstance().markStudentAsConfirmed(course.getXEvent(), student.getEmail());
                    XLogger.info(this, "Confirmed: %s", student.getEmail());
                    return successPage();
                }
            }
            return errorPage("Confirmation link not found.");
        } catch (Exception e) {
            XLogger.severe(this, "Confirmation error: %s", e.getMessage());
            return errorPage("Something went wrong. Please contact us.");
        }
    }

    private String successPage() {
        return "<!DOCTYPE html><html lang=\"en\"><head><meta charset=\"UTF-8\">"
            + "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">"
            + "<link rel=\"icon\" href=\"https://www.freedive-mallorca.com/favicon.ico\">"
            + "<title>Freedive Mallorca</title>"
            + "<style>body{font-family:Arial,sans-serif;display:flex;justify-content:center;align-items:center;"
            + "min-height:100vh;margin:0;background:#f0f8ff;}"
            + ".card{text-align:center;padding:48px 40px;background:white;border-radius:16px;"
            + "box-shadow:0 4px 20px rgba(0,119,182,0.1);max-width:420px;}"
            + "h1{color:#0077b6;font-size:1.4em;margin-bottom:16px;}"
            + "p{color:#444;line-height:1.6;}"
            + ".emoji{font-size:3em;margin-bottom:16px;}</style></head>"
            + "<body><div class=\"card\">"
            + "<div class=\"emoji\">✅</div>"
            + "<h1>Thank you! Your spot is confirmed.</h1>"
            + "<p>We look forward to seeing you in the water!</p>"
            + "</div></body></html>";
    }

    private String errorPage(String message) {
        return "<!DOCTYPE html><html lang=\"en\"><head><meta charset=\"UTF-8\">"
            + "<link rel=\"icon\" href=\"https://www.freedive-mallorca.com/favicon.ico\">"
            + "<title>Freedive Mallorca</title>"
            + "<style>body{font-family:Arial,sans-serif;display:flex;justify-content:center;align-items:center;"
            + "min-height:100vh;margin:0;background:#f0f8ff;}"
            + ".card{text-align:center;padding:48px 40px;background:white;border-radius:16px;"
            + "box-shadow:0 4px 20px rgba(0,119,182,0.1);max-width:420px;}"
            + "h1{color:#555;font-size:1.2em;}"
            + ".emoji{font-size:3em;margin-bottom:16px;}</style></head>"
            + "<body><div class=\"card\">"
            + "<div class=\"emoji\">❌</div>"
            + "<h1>" + message + "</h1>"
            + "</div></body></html>";
    }
}
