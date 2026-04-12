/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package main.calendar;

import java.util.regex.*;
import java.util.*;

public class EmailExtractor {

    public static String[] extractEmails(String texto) {
        List<String> emails = new ArrayList<>();
        Pattern patron = Pattern.compile("\\+\\+\\+([a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,})");
        Matcher matcher = patron.matcher(texto);

        while (matcher.find()) {
            emails.add(matcher.group(1));  // El grupo 1 captura el email sin los +++
        }

        return emails.toArray(new String[0]);
    }

    // Ejemplo de uso
    public static void main(String[] args) {
        String texto = "Will Lyons  +++williamlyonschem@gmail.com             +++maxidigital@gmail.com";

        String[] resultado = extractEmails(texto);
        for (String email : resultado) {
            System.out.println(email);
        }
    }
}
