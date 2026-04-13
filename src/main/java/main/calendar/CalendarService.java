/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package main.calendar;

import blue.underwater.calendar.admin.GoogleCalendarAdmin;
import blue.underwater.calendar.admin.event.ConfirmationState;
import blue.underwater.calendar.admin.event.XEvent;
import blue.underwater.commons.datetime.XDate;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import blue.underwater.commons.logging.XLogger;
import blue.underwater.security.TokenManagerType;

/**
 *
 * @author maxi
 */
public class CalendarService {
    private final static CalendarService instance = new CalendarService();
    private GoogleCalendarAdmin admin;

    /**
     * 
     * @return 
     */
    public static CalendarService getInstance() {
        return instance;
    }

    private CalendarService() {
        String credentialsPath = System.getProperty("credentials.calendar.path", "../credentials/freedivemallorcaadmin-1a2eb9366fad.json");
        this.admin = new GoogleCalendarAdmin("cachito", TokenManagerType.SERVICE_ACCOUNT, credentialsPath);
        
        this.admin.addCalendar("Freedive Mallorca", "freedive.mallorca.info@gmail.com");
        this.admin.addCalendar("Discover Freediving", "s5lctuu1kc6b95sqtn58gku1t4@group.calendar.google.com");
        this.admin.addCalendar("Advance Freediver Course", "d99bv57os6guublq8tcr92cnkk@group.calendar.google.com");
        this.admin.addCalendar("Coaching", "ba2sskbm7phih0vgo60goouhnc@group.calendar.google.com");
        this.admin.addCalendar("Expedition", "q9fs9cu3d6306kqeg1kig8brr8@group.calendar.google.com");
        this.admin.addCalendar("Freediver Course", "thrg60a3s2bholfv13d38u50f0@group.calendar.google.com");
        this.admin.addCalendar("Private Freediver Course", "cfcddaa477ab2fbff8583a2fe541b3ef6d0520286523f80457841ac1cda44220@group.calendar.google.com");
        this.admin.addCalendar("Private Freediving Adventures", "72d69257f1baa04d4d759fec9be4be6e7182d2dd415ec67c0ec20e7c4220d3ce@group.calendar.google.com");
        this.admin.addCalendar("Static Apnea", "4q4muuh8hnblqjrhvve8ahfs2s@group.calendar.google.com");
        this.admin.addCalendar("Training", "lopj6ibk6kdv9ukao8ii21oa34@group.calendar.google.com");        
    }

    public static void main(String[] args) throws IOException {
        List<Course> courses = getInstance().getCoursesForDay(XDate.parseDate("2025-04-27"));
        for (Course course : courses) {
            System.out.println(course);
        }
    }
    
    public void markStudentAsConfirmed(XEvent event, String email) {
        try {
            String raw = event.getDescription().getRawText();
            String updated = raw.replaceAll(
                "(\\+\\+\\+" + java.util.regex.Pattern.quote(email) + ")(\\s*(#PENDING#|#CONFIRMED#|#IGNORE#))?",
                "$1  " + ConfirmationState.CONFIRMED.marker
            );
            event.getDescription().setText(updated);
            admin.updateEvent(event);
            XLogger.info(this, "Marked %s as CONFIRMED in calendar", email);
        } catch (IOException | GeneralSecurityException e) {
            XLogger.severe(this, "Failed to update calendar for %s: %s", email, e.getMessage());
            main.telegram.TelegramCenter.getInstance().toRoot("❌ <b>markStudentAsConfirmed failed</b>\nEmail: %s\nError: %s", email, e.getMessage());
        }
    }

    public void markStudentAsPending(XEvent event, String email) {
        try {
            String raw = event.getDescription().getRawText();
            // Remove any existing state marker on this email's line, then add #PENDING#
            String updated = raw.replaceAll(
                "(\\+\\+\\+" + java.util.regex.Pattern.quote(email) + ")(\\s*(#PENDING#|#CONFIRMED#|#IGNORE#))?",
                "$1  " + ConfirmationState.PENDING.marker
            );
            event.getDescription().setText(updated);
            admin.updateEvent(event);
            XLogger.info(this, "Marked %s as PENDING in calendar", email);
        } catch (IOException | GeneralSecurityException e) {
            XLogger.severe(this, "Failed to update calendar for %s: %s", email, e.getMessage());
        }
    }

    public List<Course> getCoursesForDay(XDate date) throws IOException {
        List<Course> courses = new ArrayList<>();
        
        try {
            List<XEvent> events = admin.getEventsForDay(date);
            
            for (XEvent event : events) {
                Course co = new Course(event);
                courses.add(co);
            }                    
        } catch (GeneralSecurityException ex) {
            XLogger.severe(CalendarService.class, ex);
        }
        
        return courses;
    }

    public void markStudentsAsPendingAndSetDetails(XEvent event, List<String> emails, String details) {
        try {
            String updated = event.getDescription().getRawText();
            for (String email : emails) {
                updated = updated.replaceAll(
                    "(\\+\\+\\+" + java.util.regex.Pattern.quote(email) + ")(\\s*(#PENDING#|#CONFIRMED#|#IGNORE#))?",
                    "$1  " + ConfirmationState.PENDING.marker
                );
            }
            String block = "\n─────────────────────\n#DETAILS#{\n" + details + "\n}";
            if (updated.contains("#DETAILS#{")) {
                updated = updated.replaceAll("(?s)\n?─+\n#DETAILS#\\{.*?\\}", block);
            } else {
                updated = updated + block;
            }
            event.getDescription().setText(updated);
            admin.updateEvent(event);
            XLogger.info(this, "Marked %d students as PENDING and saved details", emails.size());
        } catch (IOException | GeneralSecurityException e) {
            XLogger.severe(this, "Failed to update event: %s", e.getMessage());
        }
    }

    public void setEventDetails(XEvent event, String details) {
        try {
            String raw = event.getDescription().getRawText();
            String block = "\n─────────────────────\n#DETAILS#{\n" + details + "\n}";
            String updated;
            if (raw.contains("#DETAILS#{")) {
                updated = raw.replaceAll("(?s)\n?─+\n#DETAILS#\\{.*?\\}", block);
            } else {
                updated = raw + block;
            }
            event.getDescription().setText(updated);
            admin.updateEvent(event);
            XLogger.info(this, "Updated event details tag");
        } catch (IOException | GeneralSecurityException e) {
            XLogger.severe(this, "Failed to set event details: %s", e.getMessage());
        }
    }

    public List<Course> getCoursesStartingOn(XDate date) throws IOException {
        List<Course> all = getCoursesForDay(date);
        List<Course> starting = new ArrayList<>();
        String dateStr = date.format("yyyy-MM-dd");
        for (Course course : all) {
            if (course.getXEvent().getStart().format("yyyy-MM-dd").equals(dateStr)) {
                starting.add(course);
            }
        }
        return starting;
    }

}
