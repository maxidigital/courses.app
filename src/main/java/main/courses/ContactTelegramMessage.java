package main.courses;

import blue.underwater.telegram.admin.F;
import main.contacts.Contact;
import main.sheets.medical.MedicalForm;
import main.sheets.medical.MedicalFormQuestion;
import main.sheets.medical.MedicalFormsService;

/**
 * Utility class to convert Contact objects to formatted Telegram messages for courses
 *
 * @author maxi
 */
public class ContactTelegramMessage {

    /**
     * Converts a Contact to a Telegram-formatted HTML message
     *
     * @param contact The contact to format
     * @return Formatted Telegram message with HTML tags
     */
    public static String toTelegramMessage(Contact contact) {
        if (contact == null) {
            return "";
        }

        StringBuilder sb = new StringBuilder();

        // Get name info
        String email = contact.getEmail();
        String displayName;
        String fullName = getFullName(contact);

        // Use full name if available, otherwise extract from email
        if (fullName != null && !fullName.trim().isEmpty()) {
            displayName = fullName;
        } else {
            displayName = extractUsername(email);
        }

        int age = contact.getAge();

        // Full name with age in bold
        if (age > 0) {
            sb.append("<b>").append(displayName).append(" (").append(age).append(")</b>\n");
        } else {
            sb.append("<b>").append(displayName).append("</b>\n");
        }

        // Email
        sb.append("  ").append(email).append("\n");

        // Phone number
        String phone = contact.getPhoneNumber();
        if (phone != null && !phone.isEmpty()) {
            String formattedPhoneNumber = phone.replaceAll("\\s+", "");
            sb.append("  ").append(formattedPhoneNumber).append("\n");
        }

        // Height, weight, shoe size on one line
        String height = contact.getHeight();
        String weight = contact.getWeight();
        String shoeSize = contact.getShoeSize();
        
        sb.append("  H:").append(height != null && !height.isEmpty() ? height : "-")
          .append("   W:").append(weight != null && !weight.isEmpty() ? weight : "-")
          .append("   S:").append(shoeSize != null && !shoeSize.isEmpty() ? shoeSize : "-")
          .append("\n");

        // Accommodation info
        String stayingAt = contact.getStayingAt();
        if (stayingAt != null && !stayingAt.isEmpty()) {
            sb.append("  Staying at: ").append(stayingAt).append("\n");
        }

        String notes = contact.getNotes();
        if (notes != null && !notes.isEmpty()) {
            sb.append("  Notes: ").append(notes).append("\n");
        }
        
        // Medical status section - always check regardless of contact
        MedicalForm mf = MedicalFormsService.getInstance().findByEmail(email);
        
        if (mf == null) {
            sb.append("   ❔ Medical form not submitted\n");
        } else if (mf.hasAnyMedicalIssues()) {
            sb.append("   ⚠ Medical issues:\n");
            for (MedicalFormQuestion issue : mf.getMedicalIssues()) {
                sb.append("     • ").append(issue.getShortDescription()).append("\n");
            }
        } else {
            sb.append("   ✅ Medical form OK\n");
        }

        return sb.toString();
    }

    /**
     * Extracts the username portion from an email address
     *
     * @param email Email address
     * @return Username part with first letter capitalized
     */
    private static String extractUsername(String email) {
        if (email == null || email.isEmpty()) {
            return "";
        }

        // Extract username (part before @)
        String username = email;
        if (email.contains("@")) {
            username = email.substring(0, email.indexOf('@'));
        }

        // Capitalize first letter
        if (!username.isEmpty()) {
            username = Character.toUpperCase(username.charAt(0)) + username.substring(1);
        }

        return username;
    }

    /**
     * Gets the full name (first + last name) from a Contact
     *
     * @param contact The contact object
     * @return Full name or null if both first and last name are not set
     */
    private static String getFullName(Contact contact) {
        if (contact == null) {
            return null;
        }

        String firstName = contact.getFistName();
        String lastName = contact.getLastName();

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
     * Format multiple contacts for Telegram
     *
     * @param contacts Array of Contact objects
     * @return Combined Telegram message
     */
    public static String toTelegramMessage(Contact[] contacts) {
        if (contacts == null || contacts.length == 0) {
            return "No contacts";
        }

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < contacts.length; i++) {
            sb.append(toTelegramMessage(contacts[i]));

            // Add separator between contacts
            if (i < contacts.length - 1) {
                sb.append("\n").append("———————————————").append("\n");
            }
        }

        return sb.toString();
    }
    
    /**
     * Converts a Contact to a fully detailed Telegram-formatted HTML message
     * including all available data fields regardless of emptiness
     *
     * @param contact The contact to format
     * @return Comprehensive formatted Telegram message with HTML tags
     */
    public static String toTelegramMessageFull(Contact contact) {
        if (contact == null) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        
        // Get basic information
        String email = contact.getEmail();
        String displayName;
        String fullName = getFullName(contact);
        
        // Use full name if available, otherwise extract from email
        if (fullName != null && !fullName.trim().isEmpty()) {
            displayName = fullName;
        } else {
            displayName = extractUsername(email);
        }
        
        // Header section with name
        sb.append("<b>").append(displayName).append("</b>");
        sb.append("\n\n");
        
        // Contact information section
        sb.append(F.bold("Contact Information:")).append("\n");
        sb.append("📧 Email: ").append(email).append("\n");
        
            // Phone number
            String phone = contact.getPhoneNumber();
            sb.append("📱 Phone: ");
            if (phone != null && !phone.isEmpty()) {
                String formattedPhoneNumber = phone.replaceAll("\\s+", "");
                sb.append(formattedPhoneNumber);
            } else {
                sb.append("Not provided");
            }
            sb.append("\n");
            
            // Personal details section
            sb.append("\n").append(F.bold("Personal Details:")).append("\n");
            
            // Age/Birth date
            sb.append("🎂 Age: ");
            int age = contact.getAge();
            if (age > 0) {
                sb.append(age).append(" years");
            } else {
                sb.append("Not provided");
            }
            sb.append("\n");
            
            // Birth date if available
            if (contact.getBirthDate() != null) {
                sb.append("📅 Birth date: ").append(contact.getBirthDate().format("dd/MM/yyyy")).append("\n");
            }
            
            // Nationality if available
            sb.append("🌍 Nationality: ");
            if (contact.getNationality() != null && !contact.getNationality().isEmpty()) {
                sb.append(contact.getNationality());
            } else {
                sb.append("Not provided");
            }
            sb.append("\n");
            
            // Physical details section
            sb.append("\n").append(F.bold("Physical Details:")).append("\n");
            
            // Height
            sb.append("📏 Height: ");
            if (contact.getHeight() != null && !contact.getHeight().isEmpty()) {
                sb.append(contact.getHeight()).append(" cm");
            } else {
                sb.append("Not provided");
            }
            sb.append("\n");
            
            // Weight
            sb.append("⚖️ Weight: ");
            if (contact.getWeight() != null && !contact.getWeight().isEmpty()) {
                sb.append(contact.getWeight()).append(" kg");
            } else {
                sb.append("Not provided");
            }
            sb.append("\n");
            
            // Shoe size
            sb.append("👞 Shoe size: ");
            if (contact.getShoeSize() != null && !contact.getShoeSize().isEmpty()) {
                sb.append(contact.getShoeSize());
            } else {
                sb.append("Not provided");
            }
            sb.append("\n");
            
            // Accommodation information
            sb.append("\n").append(F.bold("Accommodation:")).append("\n");
            sb.append("🏨 Staying at: ");
            String stayingAt = contact.getStayingAt();
            if (stayingAt != null && !stayingAt.isEmpty()) {
                sb.append(stayingAt);
            } else {
                sb.append("Not provided");
            }
            sb.append("\n");
            
            // Equipment preferences
            sb.append("\n").append(F.bold("Equipment:")).append("\n");
            sb.append("🥽 Wetsuit preference: ");
            if (contact.getWetsuitPreference() != null && !contact.getWetsuitPreference().isEmpty()) {
                sb.append(contact.getWetsuitPreference());
            } else {
                sb.append("Not provided");
            }
            sb.append("\n");
            
            // Notes section
            String notes = contact.getNotes();
            if (notes != null && !notes.isEmpty()) {
                sb.append("\n").append(F.bold("Notes:")).append("\n");
                sb.append("📝 ").append(notes).append("\n");
            }
            
            // Medical status section with proper data
            sb.append("\n").append(F.bold("Medical Status:")).append("\n");
            MedicalForm mf = main.sheets.medical.MedicalFormsService.getInstance().findByEmail(contact.getEmail());
            
            if (mf == null) {
                String warning = F.hexToEmoji("U+2754");
                sb.append("🏥 Medical form submitted: No").append("\n");
                sb.append("   Status: ").append(warning).append(" Not received").append("\n");
            } else if (mf.hasAnyMedicalIssues()) {
                String warning = F.hexToEmoji("U+26A0");
                sb.append("🏥 Medical form submitted: Yes").append("\n");
                sb.append("   Status: ").append(warning).append(" Attention needed").append("\n");
                if (mf.getTimestamp() != null) {
                    sb.append("   📅 Submitted on: ").append(mf.getTimestamp().format("dd/MM/yyyy HH:mm")).append("\n");
                }
                sb.append("   Medical issues:").append("\n");
                
                for (main.sheets.medical.MedicalFormQuestion issue : mf.getMedicalIssues()) {
                    sb.append("   • ").append(issue.getShortDescription()).append("\n");
                }
            } else {
                String check = F.hexToEmoji("U+2705");
                sb.append("🏥 Medical form submitted: Yes").append("\n");
                sb.append("   Status: ").append(check).append(" OK - No issues reported").append("\n");
                if (mf.getTimestamp() != null) {
                    sb.append("   📅 Submitted on: ").append(mf.getTimestamp().format("dd/MM/yyyy HH:mm")).append("\n");
                }
            }
            
            // Registration timestamp if available
            if (contact.getCreatedAt() != null) {
                sb.append("\n").append(F.bold("Registration:")).append("\n");
                sb.append("📆 Registered on: ").append(
                    contact.getCreatedAt().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
                ).append("\n");
            }
        
        return sb.toString();
    }
    
    /**
     * Format multiple contacts for Telegram with full details
     *
     * @param contacts Array of Contact objects
     * @return Combined full Telegram message
     */
    public static String toTelegramMessageFull(Contact[] contacts) {
        if (contacts == null || contacts.length == 0) {
            return "No contacts";
        }

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < contacts.length; i++) {
            sb.append(toTelegramMessageFull(contacts[i]));

            // Add separator between contacts
            if (i < contacts.length - 1) {
                sb.append("\n\n").append("———————————————").append("\n\n");
            }
        }

        return sb.toString();
    }
}