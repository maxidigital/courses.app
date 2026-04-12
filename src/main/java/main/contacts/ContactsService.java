/*
 *
 */
package main.contacts;

import blue.underwater.commons.logging.XLogger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author maxi
 */
public final class ContactsService {
    private final static ContactsService instance = new ContactsService();
    private final Map<String, Contact> items = new HashMap<>();
    private final List<Contact> list = new ArrayList();

    private ContactsService() {        
    }

    /**
     * 
     * @return 
     */
    public static ContactsService getInstance() {
        return instance;
    }
    
    /**
     * Returns the last N contacts added to the system.
     * If count is greater than the total number of contacts, returns all contacts.
     * 
     * @param count The number of most recent contacts to return
     * @return A list containing the last N contacts (most recent first)
     */
    public synchronized List<Contact> getLastContacts(int count) {
        if (count <= 0) {
            return new ArrayList<>();
        }
        
        int startIndex = Math.max(0, list.size() - count);
        int endIndex = list.size();
        
        List<Contact> lastContacts = new ArrayList<>(list.subList(startIndex, endIndex));
        Collections.reverse(lastContacts); // Reverse to get most recent first
        return lastContacts;
    }
    
    
    /**
     * 
     */
    public void init() {
        update();        
        ContactsWatcher.instance.setListener(new NewContactHandler());
        ContactsWatcher.instance.start();
    }
    
    /**
     * 
     */
    public synchronized void update() {
        this.items.clear();
        new ContactsReader(this).update();
    }
    
    /**
     * 
     * @param email
     * @return 
     */
    public synchronized Contact findByEmail(String email) {
        XLogger.info("Fetching contact info for " + email); 
        // Normalize email to lowercase to ensure matching
        String normalizedEmail = email != null ? email.toLowerCase() : "";
        Contact contact = items.get(normalizedEmail);
        XLogger.info("Found " + contact);
        return contact;
    }

    /**
     * 
     * @param contact 
     */
    void addContact(Contact contact) {        
        if(contact != null) {
            XLogger.info("Adding contact: %s", contact);
            // Store with lowercase email for consistent lookup
            String emailKey = contact.getEmail().toLowerCase();
            this.items.put(emailKey, contact);
            this.list.add(contact);
        }
        else
            XLogger.warning("Trying to add null contact");
    }
    
    /**
     * Searches for contacts matching the given search string.
     * First searches for matches in the full name (case-insensitive),
     * then searches for matches in the email address.
     * 
     * @param searchString The string to search for
     * @return A list of contacts that match the search criteria
     */
    public synchronized List<Contact> find(String searchString) {
        if (searchString == null || searchString.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        String searchLower = searchString.toLowerCase().trim();
        List<Contact> results = new ArrayList<>();
        
        // First, search by full name
        for (Contact contact : list) {
            String fullName = "";
            if (contact.getFistName() != null) {
                fullName = contact.getFistName();
            }
            if (contact.getLastName() != null) {
                fullName = fullName.isEmpty() ? contact.getLastName() : fullName + " " + contact.getLastName();
            }
            
            if (fullName.toLowerCase().contains(searchLower)) {
                results.add(contact);
            }
        }
        
        // Then, search by email (only if not already found by name)
        for (Contact contact : list) {
            if (!results.contains(contact) && contact.getEmail().toLowerCase().contains(searchLower)) {
                results.add(contact);
            }
        }
        
        return results;
    }
}
