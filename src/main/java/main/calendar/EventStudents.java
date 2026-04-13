package main.calendar;

import blue.underwater.calendar.admin.event.ConfirmationState;
import blue.underwater.calendar.admin.event.EventDescription;
import blue.underwater.calendar.admin.event.Tools;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/**
 * Manages student information for an event
 */
public final class EventStudents implements Serializable, Iterable<Student> {
    private static final long serialVersionUID = 1L;
    
    private final List<Student> students = new ArrayList<>();
    private final EventDescription eventDescription;
    
    /**
     * Creates an EventStudents object for an event description
     * 
     * @param eventDescription The event description to parse
     */
    public EventStudents(EventDescription eventDescription) {
        this.eventDescription = eventDescription;
        parseStudentsFromDescription();
    }

    public int size() {
        return students.size();
    }
    
    public Student get(int i) {
        return students.get(i);
    }
    /**
     * Parses student information from the event description
     */
    private void parseStudentsFromDescription() {
        students.clear();
        
        if (eventDescription == null) {
            return;
        }
                
        String text = eventDescription.getText();
        if (text == null || text.isEmpty()) {
            return;
        }
        
        String rawText = eventDescription.getRawText();
        if (rawText != null) rawText = rawText.replaceAll("<br\\s*/?>", "\n");
        String[] emails = EmailExtractor.extractEmails(rawText);

        for (String email : emails) {
            Student student = new Student(email);
            // Find the line containing this email and check for state markers
            for (String line : rawText.split("\n")) {
                if (line.contains("+++" + email)) {
                    if (line.contains(ConfirmationState.CONFIRMED.marker)) {
                        student.setState(ConfirmationState.CONFIRMED);
                    } else if (line.contains(ConfirmationState.PENDING.marker)) {
                        student.setState(ConfirmationState.PENDING);
                    }
                    break;
                }
            }
            addStudent(student);
        }
    }
    
    /**
     * Simple validation for email format
     * 
     * @param email Email to validate
     * @return true if email appears valid
     */
    private boolean isValidEmail(String email) {
        // Simple email validation - contains @ and at least one dot after @
        return email != null && 
               email.contains("@") && 
               email.indexOf('@') < email.lastIndexOf('.') &&
               email.lastIndexOf('.') < email.length() - 1;
    }
    
    /**
     * Finds a student by email hash
     *
     * @param emailHash The email hash to search for
     * @return The student or null if not found
     */
    public Student getStudentByEmailHash(String emailHash) {
        for (Student s : getStudents()) {
            if (Tools.emailHash(s.getEmail()).equals(emailHash))
                return s;
        }
        return null;
    }
    
    /**
     * Finds a student by email
     *
     * @param email The email to search for
     * @return The student or null if not found
     */
    public Student getStudentByEmail(String email) {
        String normalizedEmail = email.toLowerCase().trim();
        for (Student s : getStudents()) {
            if (s.getEmail().equals(normalizedEmail)) {
                return s;
            }
        }
        return null;
    }
    
    /**
     * Adds a student to the event
     *
     * @param student The student to add
     */
    public void addStudent(Student student) {
        if (student == null) {
            return;
        }
        
        // Check if student already exists by email
        Student existing = getStudentByEmail(student.getEmail());
        if (existing != null) {
            // Update existing student's state if needed
            if (student.getState() != existing.getState()) {
                existing.setState(student.getState());
            }
            return;
        }
        
        // Add new student
        students.add(student);
    }
    
    /**
     * Adds a student by email
     *
     * @param email The student's email
     */
    public void addStudent(String email) {
        if (email == null || email.trim().isEmpty()) {
            return;
        }
        addStudent(new Student(email.trim()));
    }
    
    /**
     * Removes a student from the event
     *
     * @param student The student to remove
     * @return True if student was removed, false otherwise
     */
    public boolean removeStudent(Student student) {
        if (student == null) {
            return false;
        }
        
        for (int i = students.size() - 1; i >= 0; i--) {
            Student s = students.get(i);
            if (s.getEmail().equals(student.getEmail())) {
                students.remove(i);
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Removes a student by email
     *
     * @param email The email of the student to remove
     * @return True if student was removed, false otherwise
     */
    public boolean removeStudentByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        
        Student student = getStudentByEmail(email);
        if (student != null) {
            return removeStudent(student);
        }
        return false;
    }
    
    /**
     * Updates a student's confirmation state
     *
     * @param email The student's email
     * @param state The new state
     * @return True if student was updated, false otherwise
     */
    public boolean updateStudentState(String email, ConfirmationState state) {
        Student student = getStudentByEmail(email);
        if (student != null) {
            student.setState(state);
            return true;
        }
        return false;
    }
    
    /**
     * Gets all students in the event
     *
     * @return List of students
     */
    public List<Student> getStudents() {
        return Collections.unmodifiableList(students);
    }
    
    /**
     * Checks if the event has any students
     * 
     * @return True if there are students
     */
    public boolean hasStudents() {
        return !students.isEmpty();
    }
    
    /**
     * Gets the number of students
     * 
     * @return Student count
     */
    public int getStudentCount() {
        return students.size();
    }
    
    /**
     * Gets the number of confirmed students
     * 
     * @return Confirmed student count
     */
    public int getConfirmedStudentCount() {
        int count = 0;
        for (Student student : students) {
            if (student.isConfirmed()) {
                count++;
            }
        }
        return count;
    }
    
    /**
     * Gets the number of pending students
     * 
     * @return Pending student count
     */
    public int getPendingStudentCount() {
        int count = 0;
        for (Student student : students) {
            if (student.isPending()) {
                count++;
            }
        }
        return count;
    }
    
    /**
     * Gets a text representation of the students for inclusion in event description
     * 
     * @return The formatted student text
     */
    public String getStudentText() {
        StringBuilder sb = new StringBuilder();
        
        for (Student student : students) {
            sb.append(student.getLine()).append("\n");
        }
        
        return sb.toString();
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EventStudents that = (EventStudents) o;
        return Objects.equals(students, that.students);
    }

    @Override
    public int hashCode() {
        return Objects.hash(students);
    }
    
    @Override
    public String toString() {
        if (students.isEmpty()) {
            return "No students";
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("Students: ");
        
        boolean first = true;
        for (Student student : students) {
            if (!first) {
                sb.append(", ");
            }
            sb.append(student.getEmail());
            first = false;
        }
        
        return sb.toString();
    }

    @Override
    public Iterator<Student> iterator() {
        return this.students.iterator();
    }
}