/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package main.calendar;

import blue.underwater.calendar.admin.event.XEvent;
import blue.underwater.commons.datetime.XDate;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

/**
 *
 * @author maxi
 */
class Test {
    
    public static void main(String[] args) throws IOException, GeneralSecurityException {
        List<Course> courses = CalendarService.getInstance().getCoursesForDay(XDate.parseDate("2025-04-15"));
        
        for (Course course : courses) {
            System.out.println(course);
        }
    }
}
