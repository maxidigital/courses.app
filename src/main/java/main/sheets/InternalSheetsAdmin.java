/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package main.sheets;

import blue.underwater.commons.logging.XLogger;
import blue.underwater.security.TokenManagerType;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author maxi
 */
public final class InternalSheetsAdmin {
    private final static InternalSheetsAdmin instance = new InternalSheetsAdmin();
    public final static String INTERN_SHEET_ID = "1pAkhmzkBymm-Ls7eYzruO78yln56ow5CZZ7INHLzxzg";
    private final blue.underwater.sheets.admin.SheetsAdmin internalSheet;
    private final String REGISTER_CELL = "B1";
    private final String MEDICAL_CELL = "B2";
    private String credentialsPath = "../credentials/freedivemallorcaadmin-1a2eb9366fad.json";

    /**
     * 
     * @return 
     */
    public static InternalSheetsAdmin getInstance() {
        return instance;
    }

    /**
     * 
     */
    public InternalSheetsAdmin() {
        this.internalSheet = new blue.underwater.sheets.admin.SheetsAdmin(INTERN_SHEET_ID, "freedive.mallorca.info2@gmail.com", credentialsPath);
        this.internalSheet.setCurrentSheet("Internal");
    }
    
    public void writeValue(String cell, Object value) throws IOException, GeneralSecurityException {
        List<List<Object>> values = Collections.singletonList(Collections.singletonList(value));
        this.internalSheet.write(cell, values);
    }

    public void writeLastMedicalFormRow(int row) throws IOException, GeneralSecurityException {
        writeValue(MEDICAL_CELL, row);
    }
    
    public void writeLastRegistrationFormRow(int row) throws IOException, GeneralSecurityException {
        writeValue(REGISTER_CELL, row);
    }
    
    public Optional<Object> readValue(String cell) throws IOException, GeneralSecurityException {
        List<List<Object>> read = this.internalSheet.read(cell);

        if (read != null)
            return Optional.of(read.get(0).get(0));
        return Optional.empty();
    }
    
    /**
     * 
     * @return
     * @throws GeneralSecurityException
     * @throws IOException 
     */
    public int getLastRegistrationFormRow() throws GeneralSecurityException, IOException {
        Optional<Object> value = readValue(REGISTER_CELL);

        if (value.isPresent())
            return Integer.valueOf((String) value.get());
        return 10000;
    }
    
    public int getLastMedicalFormRow() throws GeneralSecurityException, IOException {
        Optional<Object> value = readValue(MEDICAL_CELL);

        if (value.isPresent())
            return Integer.valueOf((String) value.get());
        return 10000;
    }
    
    
}
