/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package main.contacts;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import main.sheets.InternalSheetsAdmin;
import main.sheets.RegisterSheetsAdmin;
import main.telegram.TelegramCenter;

/**
 *
 * @author maxi
 */
class NewContactHandler implements ContactsWatcher.Listener {

    @Override
    public void newItem(int oldRow, int newRow) {

        try {
            for (int i = oldRow + 1; i <= newRow; i++) {
                List<Object> list = RegisterSheetsAdmin.getInstance().readRow(i);
                Contact contact = ContactFactory.getInstance().createFromSpreadsheetRow(list);
                if (contact != null) {
                    ContactsService.getInstance().addContact(contact);
                    TelegramCenter.getInstance().toAdmin(contact.toTelegramMessage());
                }

                Thread.sleep(3000);
            }
            
            InternalSheetsAdmin.getInstance().writeLastRegistrationFormRow(newRow);
            
        } catch (IOException ex) {
            Logger.getLogger(NewContactHandler.class.getName()).log(Level.SEVERE, null, ex);
        } catch (GeneralSecurityException ex) {
            Logger.getLogger(NewContactHandler.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(NewContactHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
       
    }

}
