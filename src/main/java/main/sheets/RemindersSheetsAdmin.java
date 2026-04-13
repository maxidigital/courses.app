package main.sheets;

import blue.underwater.commons.datetime.XDate;
import blue.underwater.commons.logging.XLogger;
import blue.underwater.security.TokenManagerType;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

/**
 * Manages the Reminders sheet for tracking sent email notifications
 * Sheet columns: email, medical form missed, medical form with issues, all set
 */
public final class RemindersSheetsAdmin {
    
    private static final RemindersSheetsAdmin instance = new RemindersSheetsAdmin();
    public final static String REMINDERS_SHEET_ID = "1pAkhmzkBymm-Ls7eYzruO78yln56ow5CZZ7INHLzxzg";
    private final blue.underwater.sheets.admin.SheetsAdmin reminderSheet;
    private String credentialsPath = "../credentials/freedivemallorcaadmin-1a2eb9366fad.json";
    
    // Sheet-specific constants
    private final String DEFAULT_SHEET_NAME = "Reminders";
    private final String COLUMN_EMAIL = "A";
    private final String COLUMN_MEDICAL_FORM_MISSED = "B";
    private final String COLUMN_MEDICAL_FORM_WITH_ISSUES = "C";
    private final String COLUMN_ALL_SET = "D";
    
    private RemindersSheetsAdmin() {
        this.reminderSheet = new blue.underwater.sheets.admin.SheetsAdmin(
            REMINDERS_SHEET_ID, 
            "freedive.mallorca.info2@gmail.com", 
            credentialsPath
        );
        this.reminderSheet.setCurrentSheet(DEFAULT_SHEET_NAME);
        
        // Initialize sheet headers if needed
        try {
            initializeHeaders();
        } catch (Exception e) {
            XLogger.warning("Could not initialize headers: " + e.getMessage());
        }
    }
    
    public static RemindersSheetsAdmin getInstance() {
        return instance;
    }
    
    /**
     * Initializes the sheet headers if they don't exist
     */
    private void initializeHeaders() throws IOException, GeneralSecurityException {
        try {
            List<List<Object>> headers = reminderSheet.read("A1:D1");
            
            if (headers == null || headers.isEmpty() || headers.get(0).isEmpty()) {
                List<List<Object>> headerRow = Arrays.asList(
                    Arrays.asList("Email", "Medical Form Missed", "Medical Form With Issues", "All Set")
                );
                reminderSheet.write("A1:D1", headerRow);
            }
        } catch (com.google.api.client.googleapis.json.GoogleJsonResponseException e) {
            if (e.getStatusCode() == 404) {
                // Sheet doesn't exist - create it with headers
                XLogger.warning("Reminders sheet not found. Please create a sheet named 'Reminders' in the spreadsheet.");
                // You could also create the sheet programmatically here if the API supports it
            } else {
                throw e;
            }
        }
    }
    
    /**
     * Records when a medical form missed reminder was sent
     */
    public void addMedicalFormMissedReminderSent(String email) throws IOException, GeneralSecurityException {
        int row = findOrCreateEmailRow(email);
        updateCell(COLUMN_MEDICAL_FORM_MISSED + row, getCurrentTimestamp());
    }
    
    /**
     * Records when a medical form with issues reminder was sent
     */
    public void addMedicalFormWithIssuesReminderSent(String email) throws IOException, GeneralSecurityException {
        int row = findOrCreateEmailRow(email);
        updateCell(COLUMN_MEDICAL_FORM_WITH_ISSUES + row, getCurrentTimestamp());
    }
    
    /**
     * Records when an all set reminder was sent
     */
    public void addAllSetReminderSent(String email) throws IOException, GeneralSecurityException {
        int row = findOrCreateEmailRow(email);
        updateCell(COLUMN_ALL_SET + row, getCurrentTimestamp());
    }
    
    /**
     * Finds the row for an email or creates a new one if it doesn't exist
     * @return Row number (starting from 2 to skip header)
     */
    private int findOrCreateEmailRow(String email) throws IOException, GeneralSecurityException {
        // Read all emails in column A (starting from row 2)
        List<List<Object>> emails = reminderSheet.read("A2:A1000");
        
        if (emails != null) {
            for (int i = 0; i < emails.size(); i++) {
                if (!emails.get(i).isEmpty() && email.equalsIgnoreCase(emails.get(i).get(0).toString())) {
                    return i + 2; // Row number (adding 2 because we start from row 2)
                }
            }
        }
        
        // Email not found, create new row
        int newRow = (emails == null || emails.isEmpty()) ? 2 : emails.size() + 2;
        updateCell(COLUMN_EMAIL + newRow, email);
        return newRow;
    }
    
    /**
     * Updates a single cell with the given value
     */
    private void updateCell(String cell, String value) throws IOException, GeneralSecurityException {
        List<List<Object>> data = Arrays.asList(
            Arrays.asList(value)
        );
        reminderSheet.write(cell + ":" + cell, data);
    }
    
    /**
     * Gets the current timestamp in a readable format with Mallorca timezone
     */
    private String getCurrentTimestamp() {
        // Use Mallorca timezone (Europe/Madrid)
        ZonedDateTime mallorcanTime = ZonedDateTime.now(ZoneId.of("Europe/Madrid"));
        return mallorcanTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
    
    /**
     * Reads all reminder records from the sheet
     */
    public List<List<Object>> readAllReminders() throws IOException, GeneralSecurityException {
        try {
            return reminderSheet.read("A2:D1000");
        } catch (com.google.api.client.googleapis.json.GoogleJsonResponseException e) {
            if (e.getStatusCode() == 404) {
                // Sheet doesn't exist - return empty list
                XLogger.warning("Reminders sheet not found. Returning empty list.");
                return new ArrayList<>();
            } else {
                throw e;
            }
        }
    }
    
    /**
     * Gets the reminder history for a specific email
     */
    public List<Object> getReminderHistory(String email) throws IOException, GeneralSecurityException {
        List<List<Object>> allData = readAllReminders();
        
        if (allData != null) {
            for (List<Object> row : allData) {
                if (!row.isEmpty() && email.equalsIgnoreCase(row.get(0).toString())) {
                    return row;
                }
            }
        }
        
        return Collections.emptyList();
    }
    
    /**
     * Checks if a medical form missed reminder was sent to this email
     */
    public boolean isMedicalFormMissedReminderSent(String email) throws IOException, GeneralSecurityException {
        List<Object> history = getReminderHistory(email);
        return history.size() > 1 && history.get(1) != null && !history.get(1).toString().isEmpty();
    }
    
    /**
     * Checks if a medical form with issues reminder was sent to this email
     */
    public boolean isMedicalFormWithIssuesReminderSent(String email) throws IOException, GeneralSecurityException {
        List<Object> history = getReminderHistory(email);
        return history.size() > 2 && history.get(2) != null && !history.get(2).toString().isEmpty();
    }
    
    /**
     * Checks if an all set reminder was sent to this email
     */
    public boolean isAllSetReminderSent(String email) throws IOException, GeneralSecurityException {
        List<Object> history = getReminderHistory(email);
        return history.size() > 3 && history.get(3) != null && !history.get(3).toString().isEmpty();
    }
}