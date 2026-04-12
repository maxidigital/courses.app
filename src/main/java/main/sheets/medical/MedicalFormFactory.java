package main.sheets.medical;

import blue.underwater.commons.datetime.XDate;
import blue.underwater.commons.logging.XLogger;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Factory class for creating MedicalForm objects from spreadsheet data
 */
public class MedicalFormFactory {
    
    /**
     * Creates a MedicalForm object from a spreadsheet row
     * 
     * @param row List of Objects representing a row from the medical spreadsheet
     * @return new MedicalForm object with data from the row
     */
    public static MedicalForm createFromRow(List<Object> row) {
        if (row == null || row.size() < 9) {
            throw new IllegalArgumentException("Row data incomplete, requires at least 9 columns");
        }
        
        try {
            // Extract and parse data from row
            String timestampStr = row.get(0).toString();
            String email = row.get(1).toString();
            
            // Parse medical questions (Yes/No answers)
            boolean hasLungOrHeartIssues = isYesAnswer(row.get(2));
            boolean hasEyeEarSinusIssues = isYesAnswer(row.get(3));
            boolean hasHeadInjuryHistory = isYesAnswer(row.get(4));
            boolean hasPsychologicalIssues = isYesAnswer(row.get(5));
            boolean hasRecentSurgery = isYesAnswer(row.get(6));
            boolean hasBackProblems = isYesAnswer(row.get(7));
            boolean hasRecentStomachIssues = isYesAnswer(row.get(8));
            boolean isTakingMedications = row.size() > 9 ? isYesAnswer(row.get(9)) : false;
            
            // Parse timestamp using XDate.parse which now supports "dd/MM/yyyy HH:mm:ss" format
            XDate timestamp = null;
            try {
                if (timestampStr != null && !timestampStr.isEmpty()) {
                    // Using XDate.parse with European date format support
                    timestamp = XDate.parse(timestampStr, false);
                    XLogger.info("Successfully parsed timestamp: " + timestampStr);
                }
            } catch (Exception e) {
                XLogger.warning("Could not parse timestamp: " + timestampStr + ", error: " + e.getMessage());
                // Instead of using current date, leave as null to indicate it couldn't be parsed
            }
            
            // Create and return medical form
            return new MedicalForm(
                timestamp, 
                email,
                hasLungOrHeartIssues,
                hasEyeEarSinusIssues,
                hasHeadInjuryHistory,
                hasPsychologicalIssues,
                hasRecentSurgery,
                hasBackProblems,
                hasRecentStomachIssues,
                isTakingMedications
            );
        } catch (Exception e) {
            XLogger.severe("Error creating medical form from row data: " + e.getMessage());
            throw new RuntimeException("Failed to create medical form from row data", e);
        }
    }
    
    // parseTimestamp method removed - now using XDate.parseTimestampOrNow directly
    
    /**
     * Determines if an answer is affirmative
     * 
     * @param value The answer value from the spreadsheet
     * @return true if the answer indicates "Yes" or an affirmative response
     */
    private static boolean isYesAnswer(Object value) {
        if (value == null) {
            return false;
        }
        
        String answer = value.toString().toLowerCase().trim();
        return answer.contains("yes") || answer.contains("si") || answer.contains("sí") || 
               answer.startsWith("y");
    }
}