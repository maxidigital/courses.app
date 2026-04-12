package main.courses;

import java.util.List;

/**
 *
 * @author bott_ma
 */
class CoursesParser
{

    public static String getCsv(List<Object> list) {
        StringBuilder sb = new StringBuilder();
        for (Object object : list) {
            sb.append(",").append(object);
        }
        String toString = sb.toString();
        toString = toString.substring(1);

        return toString;
    }

    public static FreedivingCourseParticipant setValues(List<Object> list) {
        FreedivingCourseParticipant fdc = new FreedivingCourseParticipant();

        String csv = getCsv(list);
        if (csv.contains("Not present")) {
            String givenNameNP = null;
            String lastNameNP = null;
            String emailAddressNP = null;
            
            for (int i = 0; i < 15; i++) {
                Object get = list.get(i);
                if (i == 0)
                    fdc.setCourseDate((String) get);
                else if (i == 1)
                    fdc.setCourseType((String) get);
                else if (i == 4)
                    emailAddressNP = (String) get;
            }
            
            // Create the Contact for the "Not present" case
            if (emailAddressNP != null) {
                fdc.createContact(givenNameNP, lastNameNP, emailAddressNP);
            }
        } else {
            // First, collect the contact information to create the Contact object
            String givenName = null;
            String lastName = null;
            String emailAddress = null;
            
            for (int i = 0; i < 15; i++) {
                Object get = list.get(i);
                if (i == 2)
                    givenName = (String) get;
                else if (i == 3)
                    lastName = (String) get;
                else if (i == 4)
                    emailAddress = (String) get;
            }
            
            // Create the Contact and set it on the FreedivingCourseParticipant
            if (emailAddress != null) {
                fdc.createContact(givenName, lastName, emailAddress);
            }
            
            // Now set all the fields
            for (int i = 0; i < 15; i++) {
                Object get = list.get(i);

                if (i == 0)
                    fdc.setCourseDate((String) get);
                else if (i == 1)
                    fdc.setCourseType((String) get);
                else if (i == 5)
                    fdc.setLocation((String) get);
                else if (i == 6) {
                    // Date of birth
                    String dateOfBirth = (String) get;
                    if (dateOfBirth != null && !dateOfBirth.isEmpty()) {
                        fdc.setDateOfBirth(dateOfBirth);
                    }
                } else if (i == 7) {
                    String cm = (String) get;
                    cm = cm.replace("cm", "");
                    try {
                        double parseDouble = Double.parseDouble(cm);
                        fdc.setHeightCm((int) parseDouble);
                    } catch (Exception e) {
                        fdc.setHeightCm(cm);
                    }
                } else if (i == 8) {
                    String kg = (String) get;
                    kg = kg.replace("kg", "");
                    try {
                        double parseDouble = Double.parseDouble(kg);
                        fdc.setWeightKg((int) parseDouble);
                    } catch (Exception e) {
                        fdc.setWeightKg(kg);
                    }
                } else if (i == 9)
                    fdc.setShoeSize((String) get);
                else if (i == 10)
                    fdc.setNotes((String) get);
                else if (i == 11) {
                    // Country
                    String country = (String) get;
                    if (country != null && !country.isEmpty()) {
                        fdc.setCountry(country);
                    }
                } else if (i == 13) {
                    // Age is calculated from birthDate in Contact, not set directly
                    // If we need to set age, we need to calculate birthDate from it
                    // For now, we'll just skip this as age is usually calculated
                } else if (i == 14)
                    fdc.setPhoneNumber((String) get);
            }
        }

        return fdc;
    }
}
