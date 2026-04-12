/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package main.sheets.medical;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import blue.underwater.commons.logging.XLogger;
import java.util.logging.Level;
import java.util.logging.Logger;
import main.sheets.InternalSheetsAdmin;

/**
 *
 * @author maxi
 */
final class MedicalWatcher implements Runnable {
    
    public final static MedicalWatcher instance = new MedicalWatcher();    
    private Listener listener;
    private ScheduledExecutorService scheduler;
    private boolean isRunning = false;

    MedicalWatcher() {
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }
    
    /**
     * Starts the watcher to check for updates every minute
     */
    public void start() {
        if (isRunning) return;
        
        scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(this, 0, 1, TimeUnit.MINUTES);        
        isRunning = true;
        XLogger.info(MedicalWatcher.class, "MedicalWatcher started, checking every minute");
    }
    
    /**
     * Stops the watcher
     */
    public void stop() {
        if (!isRunning) return;
        
        scheduler.shutdown();
        isRunning = false;
        XLogger.info(MedicalWatcher.class, "MedicalWatcher stopped");
    }
    
    @Override
    public void run() {
        try {
            int lastKnown = InternalSheetsAdmin.getInstance().getLastMedicalFormRow();
            int lastEmpty = MedicalSheetsAdmin.getInstance().getLastEmptyRow(lastKnown);
            XLogger.info("Medical register last row: " + lastKnown);
            
            if(lastEmpty > lastKnown) {
                if(listener != null)
                    listener.newItem(lastKnown, lastEmpty);                                
            }                
            
        } catch (GeneralSecurityException ex) {
            Logger.getLogger(MedicalWatcher.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(MedicalWatcher.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public interface Listener {
        void newItem(int oldRow, int newRow);
    }
}
