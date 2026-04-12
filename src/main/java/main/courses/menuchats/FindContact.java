/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package main.courses.menuchats;

import java.util.List;
import main.contacts.Contact;
import main.contacts.ContactsService;
import main.telegram.TelegramCenter;

/**
 *
 * @author maxi
 */
public class FindContact {
    private final String string;

    /**
     * 
     * @param string 
     */
    public FindContact(String string) {
        this.string = string;
    }

    public void find() {
        List<Contact> find = ContactsService.getInstance().find(string);
        //TelegramCenter.getInstance().
    }
    
}
