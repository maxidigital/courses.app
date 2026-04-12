/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package main.calendar;

import blue.underwater.commons.datetime.XDate;
import java.io.IOException;
import java.util.List;

/**
 *
 * @author maxi
 */
public class CalendarServiceTest {
    public static void main(String[] args) throws IOException {
        List<Course> coursesForDay = CalendarService.getInstance().getCoursesForDay(XDate.now());
        
        for (Course course : coursesForDay) {
            System.out.println(course);
        }
    }
}
