/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package main.sheets.medical;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.logging.Level;
import java.util.logging.Logger;
import main.contacts.Contact;
import main.contacts.ContactsService;
import main.courses.menuchats.MedicalIssueMenuChat;
import main.courses.menuchats.AllSetMenuChat;
import main.sheets.InternalSheetsAdmin;
import main.telegram.TelegramCenter;
import blue.underwater.telegram.admin.TelegramUsers;

/**
 *
 * @author maxi
 */
class NewMedicalFormHandler implements MedicalWatcher.Listener {

    @Override
    public void newItem(int oldRow, int newRow) {
        try {
            for (int i = oldRow + 1; i <= newRow; i++) {
                MedicalForm medicalForm = MedicalSheetsAdmin.getInstance().getMedicalForm(i);
                                
                if (medicalForm != null) {
                    MedicalFormsService.getInstance().add(medicalForm);
                    
                    // Log to root about the new medical form
                    String medicalStatus = medicalForm.hasAnyMedicalIssues() ? "⚠️ WITH ISSUES" : "✅ ALL CLEAR";
                    TelegramCenter.getInstance().toRoot(
                        "🏥 <b>New Medical Form</b>\n📧 %s\n%s", 
                        medicalForm.getEmail(), medicalStatus
                    );
                    
                    // Get detailed issue information if present
                    if (medicalForm.hasAnyMedicalIssues()) {
                        StringBuilder issuesDetails = new StringBuilder("<i>Issues found:</i>\n");
                        for (MedicalFormQuestion issue : medicalForm.getMedicalIssues()) {
                            issuesDetails.append("• ").append(issue.getShortDescription()).append("\n");
                        }
                        
                        // Send issues details to root
                        TelegramCenter.getInstance().toRoot(issuesDetails.toString());
                        
                        // Send to admins with inline menu for action
                        TelegramCenter.getInstance().sendMenuToAdmins(chatId -> new MedicalIssueMenuChat(
                            TelegramCenter.getInstance().getMain(),
                            chatId,
                            medicalForm
                        ));
                    } else {
                        // Send to admins with inline menu for "all set" action
                        TelegramCenter.getInstance().sendMenuToAdmins(chatId -> new AllSetMenuChat(
                            TelegramCenter.getInstance().getMain(),
                            chatId,
                            medicalForm
                        ));
                    }
                }

                Thread.sleep(3000);
            }
            
            InternalSheetsAdmin.getInstance().writeLastMedicalFormRow(newRow);
            
        } catch (IOException ex) {
            Logger.getLogger(NewMedicalFormHandler.class.getName()).log(Level.SEVERE, null, ex);
        } catch (GeneralSecurityException ex) {
            Logger.getLogger(NewMedicalFormHandler.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(NewMedicalFormHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
