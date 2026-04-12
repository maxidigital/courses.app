/*
 *
 */
package main.contacts;

import main.sheets.RegisterSheetsAdmin;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import blue.underwater.commons.logging.XLogger;
import main.telegram.TelegramCenter;

/**
 *
 * @author maxi
 */
class ContactsReader {
    private final ContactsService service;
    private RegisterSheetsAdmin sheets = RegisterSheetsAdmin.getInstance();

    public ContactsReader(ContactsService service) {        
        this.service = service;
    }
    
    public static void main(String[] args) throws IOException, GeneralSecurityException {
        ContactsReader up = new ContactsReader(ContactsService.getInstance());
        up.update();
    }
    
    public synchronized void update() {
        try {
            int lastRow = getLastOccupiedRow();
            System.out.println("ContactsReader: Reading from A2:Z" + lastRow);
            List<List<Object>> list = sheets.readCells("A2:Z" + lastRow);
            System.out.println("ContactsReader: Found " + list.size() + " rows");
            
            for (List<Object> ll : list) {
                if(!ll.isEmpty()) {
                    Contact contact = ContactFactory.getInstance().createFromSpreadsheetRow(ll);
                    if (contact != null) {
                        System.out.println("ContactsReader: Added contact: " + contact.getEmail());
                        service.addContact(contact);
                    }
                }
            }
        } catch (IOException ex) {
            TelegramCenter.getInstance().toRoot("Authentication problem. %s", ex);
        } catch (GeneralSecurityException ex) {
            XLogger.severe(ContactsReader.class, ex);
        }
    }
    
    /**
     * Returns the last occupied row in the spreadsheet.
     * This method reads a large range to find the actual last row with data.
     * 
     * @return The row number of the last occupied row
     * @throws IOException
     * @throws GeneralSecurityException
     */
    public int getLastOccupiedRow() throws IOException, GeneralSecurityException {
        // Read a large range to check for data (e.g., up to row 1000)
        List<List<Object>> data = sheets.readCells("A2:A1000");
        
        // Find the last non-empty row
        int lastOccupiedRow = 1; // Default to row 1
        
        for (int i = 0; i < data.size(); i++) {
            List<Object> row = data.get(i);
            if (row != null && !row.isEmpty() && row.get(0) != null && !row.get(0).toString().trim().isEmpty()) {
                lastOccupiedRow = i + 2; // +2 because we start from row 2 (0-indexed)
            }
        }
        
        return lastOccupiedRow;
    }
}
