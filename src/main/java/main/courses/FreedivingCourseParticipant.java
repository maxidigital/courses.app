package main.courses;

import blue.underwater.commons.datetime.XDate;
import blue.underwater.commons.enums.MedicalState;
import blue.underwater.telegram.admin.F;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;
import main.contacts.Contact;

class FreedivingCourseParticipant
{
    // Contact information is now delegated to the Contact class
    private Contact contact;
    
    // Course-specific fields
    private XDate courseDate;
    private String courseType;
    private String additionalLocation;
    private MedicalState medicalState = MedicalState.UNKNOWN;

    // Default constructor
    public FreedivingCourseParticipant() {
        this.contact = null;
    }
    
    /**
     * Constructor that takes an existing Contact object
     * 
     * @param contact The contact information for this participant
     */
    public FreedivingCourseParticipant(Contact contact) {
        this.contact = contact;
    }
    
    /**
     * Get the underlying Contact object
     * 
     * @return The Contact object
     */
    public Contact getContact() {
        return contact;
    }
    
    /**
     * Set the Contact object
     * 
     * @param contact The Contact object to set
     */
    public void setContact(Contact contact) {
        this.contact = contact;
    }
    
    /**
     * Creates a new Contact if one doesn't exist
     * 
     * @param firstName First name
     * @param lastName Last name
     * @param email Email address
     * @return The newly created Contact
     */
    public Contact createContact(String firstName, String lastName, String email) {
        this.contact = new Contact(firstName, lastName, email);
        return this.contact;
    }

    // Flexible date parsing method
    private XDate parseDate(String dateString) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE MMM dd yyyy", Locale.ENGLISH);
        try {
            LocalDate localDate = LocalDate.parse(dateString, formatter);
            return XDate.of(localDate);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date format: " + dateString, e);
        }
    }

    public void setMedicalState(MedicalState medicalState) {
        this.medicalState = medicalState;
    }

    public MedicalState getMedicalState() {
        return medicalState;
    }

    public XDate getCourseDate() {
        return courseDate;
    }

    public void setCourseDate(String courseDateStr) {
        this.courseDate = parseDate(courseDateStr);
    }

    public void setCourseDate(LocalDate courseDate) {
        this.courseDate = XDate.of(courseDate);
    }
    
    public void setCourseDate(XDate courseDate) {
        this.courseDate = courseDate;
    }

    public String getCourseType() {
        return courseType;
    }

    public void setCourseType(String courseType) {
        this.courseType = courseType;
    }

    public String getGivenName() {
        return contact != null ? contact.getFistName() : null;
    }
    
    public void setGivenName(String givenName) {
        if (contact == null) {
            createContact(givenName, null, null);
        }
        // Note: Contact doesn't have a setFirstName method as it's final, so this is handled in createContact
    }

    public int getAge() {
        return contact != null ? contact.getAge() : 0;
    }

    public String getLastName() {
        return contact != null ? contact.getLastName() : null;
    }

    public void setLastName(String lastName) {
        // Note: Contact doesn't have a setLastName method as it's final
        // This would typically be handled at Contact creation time
    }

    public String getEmailAddress() {
        return contact != null ? contact.getEmail() : null;
    }

    public void setEmailAddress(String emailAddress) {
        // Note: Contact doesn't have a setEmail method as it's final
        // This would typically be handled at Contact creation time
        if (emailAddress != null && contact == null) {
            createContact(null, null, emailAddress);
        }
    }

    public String getLocation() {
        return contact != null ? contact.getStayingAt() : null;
    }

    public void setLocation(String location) {
        if (contact != null) {
            contact.setStayingAt(location);
        }
    }

    public XDate getDateOfBirth() {
        return contact != null ? contact.getBirthDate() : null;
    }
    
    public void setDateOfBirth(String dateOfBirthStr) {
        if (contact != null) {
            contact.setBirthDate(parseDate(dateOfBirthStr));
        }
    }
    
    public void setDateOfBirth(LocalDate dateOfBirth) {
        if (contact != null) {
            contact.setBirthDate(XDate.of(dateOfBirth));
        }
    }
    
    public void setDateOfBirth(XDate dateOfBirth) {
        if (contact != null) {
            contact.setBirthDate(dateOfBirth);
        }
    }

    public String getHeightCm() {
        return contact != null ? contact.getHeight() : null;
    }

    public void setHeightCm(int heightCm) {
        if (contact != null) {
            contact.setHeight(String.valueOf(heightCm));
        }
    }

    public void setHeightCm(String heightCm) {
        if (contact != null) {
            contact.setHeight(heightCm);
        }
    }

    public String getWeightKg() {
        return contact != null ? contact.getWeight() : null;
    }

    public void setWeightKg(int weightKg) {
        if (contact != null) {
            contact.setWeight(String.valueOf(weightKg));
        }
    }

    public void setWeightKg(String weightKg) {
        if (contact != null) {
            contact.setWeight(weightKg);
        }
    }

    public String getShoeSize() {
        return contact != null ? contact.getShoeSize() : null;
    }

    public void setShoeSize(String shoeSize) {
        if (contact != null) {
            contact.setShoeSize(shoeSize);
        }
    }

    public String getNotes() {
        return contact != null ? contact.getNotes() : null;
    }

    public void setNotes(String notes) {
        if (contact != null) {
            contact.setNotes(notes);
        }
    }

    public String getCountry() {
        return contact != null ? contact.getNationality() : null;
    }

    public void setCountry(String country) {
        if (contact != null) {
            contact.setNationality(country);
        }
    }

    public String getPhoneNumber() {
        return contact != null ? contact.getPhoneNumber() : null;
    }

    public void setPhoneNumber(String phoneNumber) {
        if (contact != null) {
            contact.setPhoneNumber(phoneNumber);
        }
    }

    public String getAdditionalLocation() {
        return additionalLocation;
    }

    public void setAdditionalLocation(String additionalLocation) {
        this.additionalLocation = additionalLocation;
    }

    public String getTelegramMessage() {
        StringBuilder sb = new StringBuilder();

        if (contact != null) {
            String givenName = contact.getFistName();
            String lastName = contact.getLastName();
            int age = contact.getAge();
            
            if (givenName != null) {
                String name = "<b>%s %s (%s)</b>";
                name = String.format(name, givenName, lastName, age);
                sb.append(name).append("\n");

                String envelope = F.hexToEmoji("U+1F4E7");
                sb.append(" ").append(" ").append(contact.getEmail()).append("\n");
                
                String phone = F.hexToEmoji("U+1F4DE");
                String phoneNumber = contact.getPhoneNumber();
                if (phoneNumber != null) {
                    String formattedPhoneNumber = phoneNumber.replaceAll("\\s+", "");
                    sb.append(" ").append(" ").append(formattedPhoneNumber).append("\n");
                }

                String bio = "  H:%s   W:%s   S:%s";
                bio = String.format(bio, contact.getHeight(), contact.getWeight(), contact.getShoeSize());
                sb.append(bio).append("\n");

                String location = contact.getStayingAt();
                if (location != null && !location.isEmpty()) {
                    sb.append("  Staying at: ").append(location).append("\n");
                }

                String emoji = "";
                if (medicalState == MedicalState.OK) {
                    emoji = F.hexToEmoji("U+2705");
                } else if (medicalState == MedicalState.MISSING || medicalState == MedicalState.UNKNOWN) {
                    medicalState = MedicalState.MISSING;
                    emoji = F.hexToEmoji("U+2753");
                } else if (medicalState == MedicalState.NOT_OK) {
                    emoji = F.hexToEmoji("U+1F6AB");
                }
                sb.append("  Medical form: ").append(medicalState).append(" ").append(emoji).append("\n");

                String notes = contact.getNotes();
                if (notes(notes)) {
                    emoji = F.hexToEmoji("U+1F4DD");
                    sb.append("  Notes: ").append(F.italic(notes)).append(" ").append(emoji).append("\n");
                }
            }
        } else if (getEmailAddress() != null) {
            String email = String.format("<b>%s</b>", getEmailAddress());
            sb.append(email).append("\n");
        }

        return sb.toString();
    }

    private boolean notes(String notes) {
        if (notes == null || notes.isEmpty())
            return false;

        notes = notes.trim();
        if (notes.toLowerCase().equals("no"))
            return false;
        if (notes.toLowerCase().equals("n/a"))
            return false;

        return true;
    }
    
    /**
     * Returns a complete Telegram message with all available fields of the participant.
     * Unlike getTelegramMessage(), this includes all data fields regardless of emptiness.
     * 
     * @return A formatted string with all participant data for Telegram
     */
    public String toTelegramMessageFull() {
        StringBuilder sb = new StringBuilder();
        
        // Get contact data
        String givenName = getGivenName();
        String lastName = getLastName();
        String emailAddress = getEmailAddress();
        int age = getAge();
        String phoneNumber = getPhoneNumber();
        String location = getLocation();
        String country = getCountry();
        String heightCm = getHeightCm();
        String weightKg = getWeightKg();
        String shoeSize = getShoeSize();
        XDate dateOfBirth = getDateOfBirth();
        String notes = getNotes();
        
        // Header with name and age (if available)
        if (givenName != null && !givenName.isEmpty()) {
            String fullName = String.format("<b>%s %s", givenName, lastName != null ? lastName : "");
            if (age > 0) {
                fullName += String.format(" (%d)", age);
            }
            fullName += "</b>";
            sb.append(fullName).append("\n\n");
        } else if (emailAddress != null && !emailAddress.isEmpty()) {
            sb.append("<b>").append(emailAddress).append("</b>\n\n");
        }
        
        // Course information
        if (courseType != null && !courseType.isEmpty()) {
            sb.append(F.bold("Course: ")).append(courseType).append("\n");
        }
        
        if (courseDate != null) {
            sb.append(F.bold("Date: ")).append(courseDate.format("EEE, dd MMM yyyy")).append("\n");
        }
        
        // Contact details
        sb.append("\n").append(F.bold("Contact Information:")).append("\n");
        String envelope = F.hexToEmoji("U+1F4E7");
        sb.append(envelope).append(" ").append(emailAddress != null ? emailAddress : "No email").append("\n");
        
        String phone = F.hexToEmoji("U+1F4DE");
        String formattedPhoneNumber = phoneNumber != null ? phoneNumber.replaceAll("\\s+", "") : "No phone";
        sb.append(phone).append(" ").append(formattedPhoneNumber).append("\n");
        
        // Location information
        sb.append("\n").append(F.bold("Location Details:")).append("\n");
        String locationEmoji = F.hexToEmoji("U+1F3E0"); // house emoji
        sb.append(locationEmoji).append(" Staying at: ").append(location != null && !location.isEmpty() ? location : "Not provided").append("\n");
        
        if (additionalLocation != null && !additionalLocation.isEmpty()) {
            sb.append(locationEmoji).append(" Additional: ").append(additionalLocation).append("\n");
        }
        
        String flagEmoji = F.hexToEmoji("U+1F6A9"); // flag emoji
        sb.append(flagEmoji).append(" Country: ").append(country != null && !country.isEmpty() ? country : "Not provided").append("\n");
        
        // Physical details
        sb.append("\n").append(F.bold("Physical Information:")).append("\n");
        sb.append("Height: ").append(heightCm != null && !heightCm.isEmpty() ? heightCm + " cm" : "Not provided").append("\n");
        sb.append("Weight: ").append(weightKg != null && !weightKg.isEmpty() ? weightKg + " kg" : "Not provided").append("\n");
        sb.append("Shoe Size: ").append(shoeSize != null && !shoeSize.isEmpty() ? shoeSize : "Not provided").append("\n");
        
        if (dateOfBirth != null) {
            sb.append("Date of Birth: ").append(dateOfBirth.format("dd/MM/yyyy")).append("\n");
        } else {
            sb.append("Date of Birth: Not provided\n");
        }
        
        // Medical status
        sb.append("\n").append(F.bold("Medical Information:")).append("\n");
        String emoji = "";
        if (medicalState == MedicalState.OK) {
            emoji = F.hexToEmoji("U+2705"); // check mark
        } else if (medicalState == MedicalState.MISSING || medicalState == MedicalState.UNKNOWN) {
            emoji = F.hexToEmoji("U+2753"); // question mark
        } else if (medicalState == MedicalState.NOT_OK) {
            emoji = F.hexToEmoji("U+1F6AB"); // prohibited
        }
        sb.append("Medical form: ").append(medicalState).append(" ").append(emoji).append("\n");
        
        // Notes
        if (notes != null && !notes.isEmpty()) {
            sb.append("\n").append(F.bold("Notes:")).append("\n");
            String noteEmoji = F.hexToEmoji("U+1F4DD"); // memo emoji
            sb.append(noteEmoji).append(" ").append(F.italic(notes)).append("\n");
        }
        
        return sb.toString();
    }

    @Override
    public String toString() {
        return "FreedivingCourseParticipant{"
                + "contact=" + (contact != null ? contact.toString() : "null")
                + ", courseDate=" + courseDate
                + ", courseType='" + courseType + '\''
                + ", additionalLocation='" + additionalLocation + '\''
                + ", medicalState=" + medicalState
                + '}';
    }

    public String toCSV() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE MMM dd yyyy", Locale.ENGLISH);
        return String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%d,%s,%s",
                courseDate != null ? courseDate.format("EEE MMM dd yyyy") : "",
                courseType, 
                getGivenName(), 
                getLastName(), 
                getEmailAddress(), 
                getLocation(),
                getDateOfBirth() != null ? getDateOfBirth().format("EEE MMM dd yyyy") : "",
                getHeightCm(), 
                getWeightKg(), 
                getShoeSize(), 
                getNotes(), 
                getCountry(), 
                getAge(), 
                getPhoneNumber(),
                additionalLocation);
    }
}
