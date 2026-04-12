package main.calendar;

import blue.underwater.calendar.admin.Global;
import blue.underwater.calendar.admin.event.ConfirmationState;

import static blue.underwater.calendar.admin.event.ConfirmationState.PENDING;
import main.contacts.Contact;

/**
 * Student class that represents a contact in the context of a course/event.
 * Uses composition to extend Contact functionality with event-specific state.
 */
public final class Student {

    private final String email;
    private ConfirmationState state = ConfirmationState.NONE;
    private Contact contact;

    /**
     * Create a new Student object using the provided email
     *
     * @param email The email address of the student
     */
    Student(String email) {
        this.email = email.toLowerCase();        
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append(email);

        // Add status indicator if not NONE
        if (state != ConfirmationState.NONE) {
            sb.append(" [");

            // Use friendly names for states
            switch (state) {
                case CONFIRMED:
                    sb.append("✓ Confirmed");
                    break;
                case PENDING:
                    sb.append("⏳ Pending");
                    break;
                default:
                    sb.append(state.name());
            }

            sb.append("]");
        }

        if (contact != null) {
            // Add contact details if available
            String phone = contact.getPhoneNumber();
            if (phone != null && !phone.isEmpty()) {
                sb.append(" - ☎️ ").append(phone);
            }

            String stayingAt = contact.getStayingAt();
            if (stayingAt != null && !stayingAt.isEmpty()) {
                sb.append(" - 🏠 ").append(stayingAt);
            }

            int age = contact.getAge();
            if (age > 0) {
                sb.append(" - 👤 ").append(age).append("y");
            }
        }

        return sb.toString();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        Student other = (Student) obj;
        return this.email != null && this.email.equalsIgnoreCase(other.email);
    }
    
    @Override
    public int hashCode() {
        return email != null ? email.toLowerCase().hashCode() : 0;
    }

    public String getLine() {
        String line = "";

        if (!email.isEmpty()) {
            line = Global.STUDENT_MARKER + email;

            if (state != ConfirmationState.NONE) {
                line += "  " + state.marker;
            }
        }
        return line;
    }

    public String getEmail() {
        return email.toLowerCase();
    }

    public ConfirmationState getState() {
        return state;
    }

    public boolean isPending() {
        return state == ConfirmationState.PENDING;
    }

    public boolean isConfirmed() {
        return state == ConfirmationState.CONFIRMED;
    }

    public void setState(ConfirmationState state) {
        this.state = state;
    }

    public void setConfirmed() {
        this.state = ConfirmationState.CONFIRMED;
    }

    /**
     * Gets the full name (first + last name)
     *
     * @return Full name or null if both first and last name are not set
     */
    public String getFullName() {
        if (getContact() == null) {
            return null;  // Don't return email here
        }

        String firstName = getContact().getFistName();
        String lastName = getContact().getLastName();

        if (firstName == null && lastName == null) {
            return null;
        }

        if (firstName == null) {
            return lastName;
        }

        if (lastName == null) {
            return firstName;
        }

        return firstName + " " + lastName;
    }

    /**
     * Gets the underlying Contact object
     *
     * @return The Contact object for this student
     */
    public Contact getContact() {
        return contact;
    }
}
