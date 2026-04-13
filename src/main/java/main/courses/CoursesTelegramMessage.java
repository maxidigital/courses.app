package main.courses;

import blue.underwater.commons.datetime.XDate;
import blue.underwater.telegram.admin.F;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import main.calendar.Course;
import blue.underwater.calendar.admin.event.ConfirmationState;
import main.calendar.EventStudents;
import main.calendar.Student;
import main.sheets.medical.MedicalForm;
import main.sheets.medical.MedicalFormQuestion;
import main.sheets.medical.MedicalFormsService;

/**
 *
 * @author bott_ma
 */
class CoursesTelegramMessage {

    /**
     *
     */
    public CoursesTelegramMessage() {
    }

    /**
     * 
     * @param date
     * @param courses
     * @param callback The callback to be invoked when a student needs medical form reminder
     * @return 
     */
    public String getTelegramMessage(XDate date, List<Course> courses, MedicalFormCallback callback) {        
        StringBuilder sb = new StringBuilder();

        if (!courses.isEmpty()) {
            String formattedDate = date.format(DateTimeFormatter.ofPattern("dd MMM yyyy"));

            String cal = F.hexToEmoji("U+1F4C5");
            sb.append("<b>").append(cal).append(" ").append(formattedDate).append("").append("</b>\n\n");
        }

        for (Course course : courses) {
            sb.append("   <u>").append(course.getType()).append("</u>\n");
            String savedDetails = main.calendar.EventDetailsParser.getRawContent(course.getXEvent().getDescription().getRawText());
            if (savedDetails != null) {
                sb.append("<code>").append(savedDetails).append("</code>\n");
            }

            EventStudents students = course.getEventStudents();
            if (students.size() == 0) {
                if (course.getDescription().isEmpty()) {
                    sb.append("    No participants\n");
                } else {
                    sb.append("   <i>").append(course.getDescription()).append("</i>\n");
                }
                sb.append("\n");
            } else {
                for (int i = 0; i < students.size(); i++) {
                    Student student = students.get(i);                    
                    String studentEmail = student.getEmail();

                    String mask = F.hexToEmoji("U+1F93F");
                    String studentMessage = ContactTelegramMessage.toTelegramMessage(student.getContact());
                    
                    // Check if medical form is missing and callback is provided
                    MedicalForm mf = MedicalFormsService.getInstance().findByEmail(studentEmail);
                    if (mf == null && callback != null && student.getContact() != null) {
                        callback.onMedicalFormMissing(student.getContact(), course.getType());
                    }
                    
                    // If no contact info, at least show the email
                    if (studentMessage == null || studentMessage.trim().isEmpty()) {
                        studentMessage = "<b>" + studentEmail + "</b>\n  ❓ Contact info not available\n";
                    }

                    String stateTag = "";
                    if (student.getState() == ConfirmationState.CONFIRMED) stateTag = "  ☀️ Details confirmed\n";
                    else if (student.getState() == ConfirmationState.PENDING) stateTag = "  ⏳ Details pending\n";

                    sb.append(i + 1).append(". ").append(mask).append(" ")
                            .append(studentMessage).append(stateTag).append("\n");
                }
            }
        }

        return sb.toString();
    }
    
    /**
     * Callback interface for handling students without medical forms
     */
    public interface MedicalFormCallback {
        void onMedicalFormMissing(main.contacts.Contact contact, String courseName);
    }

}
