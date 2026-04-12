package main.calendar;

import blue.underwater.commons.datetime.XDate;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Service to retrieve information about students who participated in recent courses.
 */
public class RecentCoursesService {
    
    private final CalendarService calendarService;
    
    /**
     * Create a new RecentCoursesService with the default CalendarService
     */
    public RecentCoursesService() {
        this.calendarService = CalendarService.getInstance();
    }
    
    /**
     * Create a new RecentCoursesService with a specific CalendarService (useful for testing)
     * 
     * @param calendarService The calendar service to use
     */
    public RecentCoursesService(CalendarService calendarService) {
        this.calendarService = calendarService;
    }
    
    /**
     * Gets a list of all students who participated in courses during the specified range of days.
     * Students are unique by email, and sorted alphabetically by email.
     * 
     * @param days The number of days to include in the range. 
     *            Negative values: Include today and previous N days (e.g., -2 means today, yesterday, and the day before)
     *            Positive values: Include today and following N days (e.g., 3 means today, tomorrow, day after tomorrow, and the day after that)
     *            Zero value: Only include today
     * @return List of unique Student objects from the specified range of days, sorted by email
     * @throws IOException If there's an error retrieving course information
     */
    public List<Student> getStudents(int days) throws IOException {
        // Use Set to ensure students are unique (no duplicates by email - using equals/hashcode)
        Set<Student> uniqueStudents = new HashSet<>();
        
        // Get today's date
        XDate today = XDate.today();
        
        int absCount = Math.abs(days);
        int startOffset = 0;
        int endOffset = 0;
        
        if (days < 0) {
            // Negative: Include today and previous N days
            startOffset = 0;
            endOffset = absCount;
        } else if (days > 0) {
            // Positive: Include today and following N days
            startOffset = 0;
            endOffset = -days; // Negative because we're going forward in time
        } else {
            // Zero: Only include today
            startOffset = 0;
            endOffset = 0;
        }
        
        // Collect courses for the specified range of days
        for (int i = startOffset; i <= endOffset; i++) {
            XDate date;
            if (days < 0) {
                // For negative days, go backwards from today
                date = today.minusDays(i);
            } else {
                // For positive days, go forwards from today
                date = today.plusDays(i);
            }
            
            List<Course> courses = calendarService.getCoursesForDay(date);
            
            for (Course course : courses) {
                EventStudents eventStudents = course.getEventStudents();
                if (eventStudents != null && eventStudents.hasStudents()) {
                    // Add all students from this course to our unique set
                    uniqueStudents.addAll(eventStudents.getStudents());
                }
            }
        }
        
        // Convert the set back to a list
        List<Student> studentList = new ArrayList<>(uniqueStudents);
        
        // Sort the list alphabetically by email
        studentList.sort((s1, s2) -> s1.getEmail().compareToIgnoreCase(s2.getEmail()));
        
        return studentList;
    }
    
    /**
     * Gets a list of all students who participated in courses during the last 3 days.
     * Students are unique by email, and sorted alphabetically by email.
     * This method is kept for backward compatibility and calls getStudents(-2).
     * 
     * @return List of unique Student objects from the last 3 days of courses sorted by email
     * @throws IOException If there's an error retrieving course information
     */
    public List<Student> getStudents() throws IOException {
        return getStudents(-2);
    }
}