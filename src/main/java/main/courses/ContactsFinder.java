/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package main.courses;

import java.util.List;
import main.calendar.Course;
import main.calendar.Student;
import main.contacts.Contact;
import main.contacts.ContactsService;

/**
 *
 * @author maxi
 */
public class ContactsFinder {
    
    public static void findContacts(List<Course> courses) {
        for (Course course : courses) {
            for (Student student : course.getEventStudents()) {
                String email = student.getEmail();
                Contact contact = ContactsService.getInstance().findByEmail(email);
                
                if(contact != null)
                    student.setContact(contact);
            }
        }
    }
}
