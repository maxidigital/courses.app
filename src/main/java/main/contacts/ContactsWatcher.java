/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package main.contacts;

import blue.underwater.commons.logging.XLogger;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import main.sheets.InternalSheetsAdmin;
import main.sheets.RegisterSheetsAdmin;

/**
 *
 * @author maxi
 */
final class ContactsWatcher implements Runnable {
    
    public final static ContactsWatcher instance = new ContactsWatcher();
    private Listener listener;
    private ScheduledExecutorService scheduler;
    private boolean isRunning = false;

    ContactsWatcher() {        
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
        //scheduler.scheduleAtFixedRate(this, 0, 10, TimeUnit.SECONDS);
        isRunning = true;
        XLogger.info(ContactsWatcher.class.getSimpleName() + " started, checking every minute");
    }
    
    /**
     * Stops the watcher
     */
    public void stop() {
        if (!isRunning) return;
        
        scheduler.shutdown();
        isRunning = false;
        XLogger.info(ContactsWatcher.class.getSimpleName() + " stopped");        
    }
    
    @Override
    public void run() {
        try {
            int lastKnown = InternalSheetsAdmin.getInstance().getLastRegistrationFormRow();
            int lastEmpty = RegisterSheetsAdmin.getInstance().getLastEmptyRow(lastKnown);
            XLogger.info("Reading register last row to " + lastKnown);
            
            if(lastEmpty > lastKnown) {
                if(listener != null)
                    listener.newItem(lastKnown, lastEmpty);                                
            }                
            
        } catch (GeneralSecurityException ex) {
            Logger.getLogger(ContactsWatcher.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ContactsWatcher.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public interface Listener {
        void newItem(int oldRow, int newRow);
    }
}
