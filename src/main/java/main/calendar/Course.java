/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package main.calendar;

import blue.underwater.calendar.admin.event.XEvent;
import blue.underwater.commons.datetime.XDate;
import java.util.List;

/**
 *
 * @author maxi
 */
public class Course {
    private XDate date;
    private String type;
    private EventStudents eventStudents;
    private String description;
    private final XEvent xEvent;

    public Course(XEvent event) {
        this.date = event.getStart();
        this.type = event.getCalendarName();
        this.eventStudents = new EventStudents(event.getDescription());
        this.description = event.getDescription().getCleanedText();
        this.xEvent = event;
    }

    public XEvent getXEvent() {
        return xEvent;
    }

    public XDate getDate() {
        return date;
    }    

    public String getType() {
        return type;
    }    

    public EventStudents getEventStudents() {
        return eventStudents;
    }

    public String getDescription() {
        return description;
    }
    
    /**
     * Returns a string representation of this course
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        
        // Format course type and date
        sb.append(type);
        
        // Add date and time information
        if (date != null) {
            // Format date nicely
            sb.append(" - ");
            
            if (date.isAllDay()) {
                sb.append(date.format("EEE, d MMM"));
            } else {
                sb.append(date.format("EEE, d MMM HH:mm"));
            }
        }
        
        // Add student count
        if (eventStudents != null) {
            int total = eventStudents.getStudentCount();
            
            if (total > 0) {
                sb.append("\n");
                sb.append(total).append(" students:");
                
                // List student emails
                sb.append("\n");
                List<Student> studentList = eventStudents.getStudents();
                for (int i = 0; i < studentList.size(); i++) {
                    Student student = studentList.get(i);
                    sb.append(student.getEmail());
                    
                    if (i < studentList.size() - 1) {
                        sb.append("\n");
                    }
                }
            }
        }
        
        return sb.toString();
    }
}
