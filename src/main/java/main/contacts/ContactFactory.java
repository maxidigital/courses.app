/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package main.contacts;

import blue.underwater.commons.datetime.XDate;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * Factory class responsible for creating Contact objects from various data sources
 * @author maxi
 */
class ContactFactory {
    
    // Column indices in the registration spreadsheet
    private static final int TIMESTAMP = 0;
    private static final int EMAIL = 1;
    private static final int FIRST_NAME = 2;
    private static final int SURNAME = 3;
    private static final int BIRTH_DATE = 4;
    private static final int PHONE_NUMBER = 5;
    private static final int NATIONALITY = 6;
    private static final int STAYING_AT = 7;
    private static final int WETSUIT = 8;
    private static final int HEIGHT = 9;
    private static final int WEIGHT = 10;
    private static final int SHOE_SIZE = 11;
    private static final int HEAR_ABOUT = 12;
    private static final int PAYMENT = 13;
    private static final int EMERGENCY_NAME = 14;
    private static final int EMERGENCY_PHONE = 15;
    private static final int PARENT_CONSENT = 16;
    private static final int NOTES = 17;
    private static final int FULL_NAME = 18;
    private static final int ID_NUMBER = 19;
    
    /**
     * Create a Contact object from a row in the registration spreadsheet
     * @param list The list of values from the spreadsheet row
     * @return A new Contact object, or null if the input is invalid
     */
    public Contact createFromSpreadsheetRow(List<Object> list) {
        if (list == null || list.isEmpty()) {
            return null;
        }
        
        // Create contact with basic required fields
        String firstName = getStringValue(list, FIRST_NAME);
        String surname = getStringValue(list, SURNAME);
        
        // If the surname is empty and firstName contains multiple words, split them
        if ((surname == null || surname.trim().isEmpty()) && firstName != null && firstName.contains(" ")) {
            String[] nameParts = firstName.trim().split("\\s+", 2);
            firstName = nameParts[0];
            surname = nameParts.length > 1 ? nameParts[1] : "";
        }
        
        Contact contact = new Contact(firstName, surname, getStringValue(list, EMAIL));
        
        // Set birth date
        String birthDate = getStringValue(list, BIRTH_DATE);        
        if(!birthDate.isEmpty()) {            
            contact.setBirthDate(XDate.parseDateFlexible(birthDate));                    
        }
        
        // Set remaining fields
        contact.setPhoneNumber(getStringValue(list, PHONE_NUMBER));
        contact.setNationality(getStringValue(list, NATIONALITY));
        contact.setStayingAt(getStringValue(list, STAYING_AT));
        contact.setWetsuitPreference(getStringValue(list, WETSUIT));
        contact.setHeight(getStringValue(list, HEIGHT));
        contact.setWeight(getStringValue(list, WEIGHT));
        contact.setShoeSize(getStringValue(list, SHOE_SIZE));
        contact.setNotes(getStringValue(list, NOTES));
        
        // Convert timestamp from spreadsheet to LocalDateTime
        String timestampStr = getStringValue(list, TIMESTAMP);
        LocalDateTime createdAt = parseTimestamp(timestampStr);
        contact.setCreatedAt(createdAt);
        
        return contact;
    }
    
    /**
     * Helper method to get a string value from the list
     * @param list The list of values
     * @param index The index to get
     * @return The string value at the index, or an empty string if not found
     */
    private String getStringValue(List<Object> list, int index) {
        if (list.size() <= index || list.get(index) == null) {
            return "";
        }
        return list.get(index).toString();
    }
    
    /**
     * Parse a timestamp string from the spreadsheet to LocalDateTime
     * @param timestampStr The timestamp string
     * @return The parsed LocalDateTime, or now if the timestamp is invalid
     */
    private LocalDateTime parseTimestamp(String timestampStr) {
        if (timestampStr == null || timestampStr.isEmpty()) {
            return LocalDateTime.now(); // Default value if no timestamp
        }
        
        try {
            // Expected format: dd/MM/yyyy HH:mm:ss (like: 02/08/2024 14:00:06)
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
            return LocalDateTime.parse(timestampStr, formatter);
        } catch (DateTimeParseException e) {
            System.err.println("Error parsing timestamp: " + timestampStr);
            return LocalDateTime.now(); // Default value in case of error
        }
    }
    
    // Singleton pattern
    private static final ContactFactory instance = new ContactFactory();
    
    private ContactFactory() {
        // Private constructor for singleton
    }
    
    /**
     * Get the singleton instance of ContactFactory
     * @return The ContactFactory instance
     */
    public static ContactFactory getInstance() {
        return instance;
    }
}