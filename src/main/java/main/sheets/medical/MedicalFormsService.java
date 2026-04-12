package main.sheets.medical;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import blue.underwater.commons.logging.XLogger;
import java.security.GeneralSecurityException;

/**
 *
 * @author bott_ma
 */
public final class MedicalFormsService
{

    private final static MedicalFormsService instance = new MedicalFormsService();
    private final Map<String, MedicalForm> items = new HashMap<>();            

    public static MedicalFormsService getInstance() {
        return instance;
    }

    MedicalFormsService() {        
    }

    public synchronized void init() {
        update();
        MedicalWatcher.instance.setListener(new NewMedicalFormHandler());
        MedicalWatcher.instance.start();
    }
    
    public synchronized void add(MedicalForm mf) {
        this.items.put(mf.getEmail(), mf);
    }
    
    public synchronized MedicalForm findByEmail(String email) {
        return items.get(email);
    }
    
    public synchronized void update() {
        items.clear();
        try {
            List<MedicalForm> allMedicalForms = MedicalSheetsAdmin.getInstance().getAllMedicalForms();
            for (MedicalForm mf : allMedicalForms) {
                XLogger.info("Adding medical form: " + mf.toString());
                this.add(mf);                
            }                        
        } catch (IOException ex) {
            Logger.getLogger(MedicalFormsService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (GeneralSecurityException ex) {
            Logger.getLogger(MedicalFormsService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
