/**
 * 
 */
package main.contacts;

import blue.underwater.commons.datetime.XDate;
import java.time.LocalDateTime;

/**
 *
 * @author maxi
 */
public final class Contact {
    private LocalDateTime createdAt;
    private final String fistName;
    private final String lastName;
    private final String email;
    private String phoneNumber;
    private String stayingAt;
    private String height;
    private String weight;
    private String shoeSize;
    private XDate birthDate;
    private String wetsuitPreference;
    private String nationality;
    private String notes;

    /**
     * 
     * @param firstName
     * @param lastName
     * @param email 
     */
    public Contact(String firstName, String lastName, String email) {
        this.fistName = firstName;
        this.lastName = lastName;
        this.email = email.toLowerCase();        
    }
    
    // Getters
    public String getFistName() {
        return fistName;
    }
    
    public String getLastName() {
        return lastName;
    }
    
    public String getEmail() {
        return email;
    }
    
    /**
     * Calculates age based on the birthDate. 
     * If birthDate is null, returns 0.
     * Uses a precise calculation that considers years bisiestos
     * and exact month/day boundaries.
     * 
     * @return Age in years or 0 if birthDate is not set
     */
    public int getAge() {
        if (birthDate == null) {
            return 0;
        }
        
        // Calculate age using Period which correctly handles leap years
        // and exact month/day boundaries
        XDate today = XDate.today();
        
        try {
            // Use Java's Period to calculate the years between dates
            // This is more accurate than simply dividing days
            java.time.Period period = birthDate.getPeriod(today);
            
            // Get absolute value in case the dates are in wrong order
            int years = Math.abs(period.getYears());
            
            // Sanity check - age should be reasonable
            if (years > 120) {
                System.err.println("Warning: Calculated age is over 120 years: " + years + " for birthDate: " + birthDate);
                return 0; // Return 0 for unreasonable ages
            }
            
            return years;
        } catch (Exception e) {
            System.err.println("Error calculating age from birthDate: " + birthDate + " - " + e.getMessage());
            return 0; // Return 0 in case of calculation errors
        }
    }
    
    public String getPhoneNumber() {
        return phoneNumber;
    }
    
    public String getStayingAt() {
        return stayingAt;
    }
    
    public String getHeight() {
        return height;
    }
    
    public String getWeight() {
        return weight;
    }
    
    public String getShoeSize() {
        return shoeSize;
    }
    
    public XDate getBirthDate() {
        return birthDate;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public String getWetsuitPreference() {
        return wetsuitPreference;
    }
    
    public String getNationality() {
        return nationality;
    }
    
    public String getNotes() {
        return notes;
    }
    
    // setAge removed as age is now calculated from birthDate
    
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    
    public void setStayingAt(String stayingAt) {
        this.stayingAt = stayingAt;
    }
    
    public void setHeight(String height) {
        this.height = height;
    }
    
    public void setWeight(String weight) {
        this.weight = weight;
    }
    
    public void setShoeSize(String shoeSize) {
        this.shoeSize = shoeSize;
    }
    
    public void setBirthDate(XDate birthDate) {
        this.birthDate = birthDate;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public void setWetsuitPreference(String wetsuitPreference) {
        if(wetsuitPreference.contains("Select this option")) {
            int a = wetsuitPreference.indexOf("(");
            wetsuitPreference = wetsuitPreference.substring(0, a).trim();
        }
        if(wetsuitPreference.contains("I will bring my own wetsuit")) {
            int a = wetsuitPreference.indexOf("(");
            if(a != -1) {
                wetsuitPreference = wetsuitPreference.substring(0, a).trim();
            }
        }
        this.wetsuitPreference = wetsuitPreference;
    }
    
    public void setNationality(String nationality) {
        this.nationality = nationality;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    @Override
    public String toString() {
        return "Contact{" +
                "firstName='" + fistName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", age=" + getAge() +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", nationality='" + nationality + '\'' +
                ", stayingAt='" + stayingAt + '\'' +
                ", wetsuitPreference='" + wetsuitPreference + '\'' +
                ", height='" + height + '\'' +
                ", weight='" + weight + '\'' +
                ", shoeSize='" + shoeSize + '\'' +
                ", birthDate='" + birthDate + '\'' +
                ", createdAt=" + createdAt +
                ", notes='" + notes + '\'' +
                '}';
    }
    
    /**
     * Formats the contact information for a Telegram message.
     * Uses HTML formatting for better readability when displayed in Telegram.
     * 
     * @return A formatted string suitable for sending as a Telegram message with HTML formatting
     */
    public String toTelegramMessage() {
        StringBuilder sb = new StringBuilder();
        
        // Name and basic info with bold formatting
        sb.append("<b>Contact: ").append(fistName).append(" ").append(lastName).append("</b>\n\n");
        
        // Key contact details
        sb.append("📧 Email: <code>").append(email).append("</code>\n");
        
        if (phoneNumber != null && !phoneNumber.isEmpty()) {
            sb.append("📱 Phone: <code>").append(phoneNumber).append("</code>\n");
        }
        
        // Add age or birthdate info
        if (getAge() > 0) {
            sb.append("🎂 Age: ").append(getAge()).append(" years\n");
        } else if (birthDate != null) {
            sb.append("🎂 Birth date: ").append(birthDate.format("dd/MM/yyyy")).append("\n");
        }
        
        // Additional information if available
        if (nationality != null && !nationality.isEmpty()) {
            sb.append("🌍 Nationality: ").append(nationality).append("\n");
        }
        
        if (stayingAt != null && !stayingAt.isEmpty()) {
            sb.append("🏨 Staying at: ").append(stayingAt).append("\n");
        }
        
        // Physical characteristics section if any are present
        boolean hasPhysicalInfo = (height != null && !height.isEmpty()) || 
                                 (weight != null && !weight.isEmpty()) || 
                                 (shoeSize != null && !shoeSize.isEmpty());
        
        if (hasPhysicalInfo) {
            sb.append("\n<b>Physical characteristics:</b>\n");
            
            if (height != null && !height.isEmpty()) {
                sb.append("📏 Height: ").append(height).append("\n");
            }
            
            if (weight != null && !weight.isEmpty()) {
                sb.append("⚖️ Weight: ").append(weight).append("\n");
            }
            
            if (shoeSize != null && !shoeSize.isEmpty()) {
                sb.append("👞 Shoe size: ").append(shoeSize).append("\n");
            }
        }
        
        // Equipment preferences
        if (wetsuitPreference != null && !wetsuitPreference.isEmpty()) {            
            sb.append("\n🥽 Wetsuit preference: ").append(wetsuitPreference).append("\n");
        }
        
        // Notes section if present
        if (notes != null && !notes.isEmpty()) {
            sb.append("\n<b>Notes:</b>\n").append(notes).append("\n");
        }
        
        // Add creation timestamp if available in a human-readable format
        if (createdAt != null) {
            sb.append("\n<i>Added on: ").append(createdAt.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))).append("</i>");
        }
        
        return sb.toString();
    }
    
    /**
     * Formats the contact information as a simple one-line text.
     * Useful for lists or brief representations.
     * 
     * @return A short string representation of the contact
     */
    public String toShortString() {
        StringBuilder sb = new StringBuilder();
        sb.append(fistName).append(" ").append(lastName);
        
        if (email != null && !email.isEmpty()) {
            sb.append(" (").append(email).append(")");
        }
        
        if (phoneNumber != null && !phoneNumber.isEmpty()) {
            sb.append(" - ").append(phoneNumber);
        }
        
        return sb.toString();
    }
}
