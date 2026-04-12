package main.sheets.medical;

import java.io.IOException;
import java.security.GeneralSecurityException;
import blue.underwater.commons.logging.XLogger;
import blue.underwater.security.TokenManagerType;
import java.util.ArrayList;
import java.util.List;
import main.sheets.InternalSheetsAdmin;

/**
 *
 * @author bott_ma
 */
public class MedicalSheetsAdmin {

    private static final MedicalSheetsAdmin instance = new MedicalSheetsAdmin();
    public final static String MEDICAL_SHEET_ID = "1pAkhmzkBymm-Ls7eYzruO78yln56ow5CZZ7INHLzxzg";
    private final blue.underwater.sheets.admin.SheetsAdmin sheet;
    private String credentialsPath = "../credentials/freedivemallorcaadmin-1a2eb9366fad.json";

    /**
     *
     */
    public MedicalSheetsAdmin() {
        /*this.sheet = new blue.underwater.sheets.admin.SheetsAdmin(MEDICAL_SHEET_ID,
                "freedive.mallorca.info2@gmail.com",
                "/home/maxi/gits/underwater/apps/credentials/freedivemallorcaadmin-1a2eb9366fad.json");*/

        this.sheet = new blue.underwater.sheets.admin.SheetsAdmin(
                MEDICAL_SHEET_ID,
                "freedive.mallorca.info2@gmail.com",
                System.getProperty("credentials.path")
        );

        this.sheet.setCurrentSheet("Medical");
    }

    public static MedicalSheetsAdmin getInstance() {
        return instance;
    }

    /**
     *
     * @param range
     * @param values
     * @throws IOException
     * @throws GeneralSecurityException
     */
    public void write(String range, List<List<Object>> values) throws IOException, GeneralSecurityException {
        sheet.write(range, values);
    }

    /**
     *
     * @param cells
     * @return
     * @throws IOException
     * @throws GeneralSecurityException
     */
    public List<List<Object>> read(String cells) throws IOException, GeneralSecurityException {
        return sheet.read(cells);
    }

    /**
     * Reads patient data from the medical spreadsheet
     *
     * @param toRow The last row to read
     * @return List of MedicalForm objects with medical information
     * @throws IOException If there's an I/O error
     * @throws GeneralSecurityException If there's a security error
     */
    public List<MedicalForm> getMedicalForms(int toRow) throws IOException, GeneralSecurityException {
        String range = "A2:J" + toRow; // Include column J for medication
        List<List<Object>> data = read(range);
        List<MedicalForm> forms = new ArrayList<>();

        for (List<Object> row : data) {
            if (row.size() < 9) {
                continue; // Skip incomplete rows
            }

            try {
                // Use MedicalFormFactory to create form from row data
                MedicalForm form = MedicalFormFactory.createFromRow(row);
                forms.add(form);
            } catch (Exception e) {
                // Log or handle parsing errors
                XLogger.severe("Error parsing medical form data: " + e.getMessage());
            }
        }

        return forms;
    }

    /**
     * Gets all medical forms from the spreadsheet
     *
     * @return List of all medical forms
     * @throws IOException
     * @throws GeneralSecurityException
     */
    public List<MedicalForm> getAllMedicalForms() throws IOException, GeneralSecurityException {
        int lastRow = getLastEmptyRow();
        return getMedicalForms(lastRow);
    }

    /**
     * Gets a single medical form from a specific row in the spreadsheet
     *
     * @param row The row number to read (1-based index, as in spreadsheet)
     * @return MedicalForm object created from the specified row
     * @throws IOException If there's an I/O error
     * @throws GeneralSecurityException If there's a security error
     * @throws IllegalArgumentException If the row doesn't contain valid form
     * data
     */
    public MedicalForm getMedicalForm(int row) throws IOException, GeneralSecurityException {
        if (row < 2) {
            throw new IllegalArgumentException("Row must be at least 2 (row 1 contains headers)");
        }

        // Read just the requested row (A:J columns)
        String range = "A" + row + ":J" + row;
        List<List<Object>> data = read(range);

        if (data == null || data.isEmpty()) {
            throw new IllegalArgumentException("No data found at row " + row);
        }

        List<Object> rowData = data.get(0);

        if (rowData.size() < 9) {
            throw new IllegalArgumentException("Incomplete data at row " + row
                    + " (found " + rowData.size() + " columns, need at least 9)");
        }

        return MedicalFormFactory.createFromRow(rowData);
    }

    private int getLastEmptyRow() throws GeneralSecurityException, IOException {
        return getLastEmptyRow(null, "A", 1, 1000);
    }

    public int getLastEmptyRow(int startRow) throws GeneralSecurityException, IOException {
        return getLastEmptyRow(null, "A", startRow, 10);
    }

    private int getLastEmptyRow(String sheetName, String column, int startRow, int maxRows)
            throws GeneralSecurityException, IOException {
        // Save current sheet
        String previousSheet = this.sheet.getCurrentSheet();

        try {
            // Set the sheet to search in
            if (sheetName != null && !sheetName.isEmpty()) {
                this.sheet.setCurrentSheet(sheetName);
            }

            // Build range to check (e.g., "A1:A1000")
            String range = column + (startRow + 1) + ":" + column + (startRow + maxRows);

            // Read the data from the column
            List<List<Object>> data = this.sheet.read(range);

            if (data == null || data.isEmpty()) {
                return startRow;
            }

            return startRow + data.size();
        } catch (Exception e) {
            XLogger.severe(InternalSheetsAdmin.class, "Error finding last empty row: " + e.getMessage());
            throw e;
        } finally {
            // Restore original sheet
            if (previousSheet != null && !previousSheet.equals(this.sheet.getCurrentSheet())) {
                this.sheet.setCurrentSheet(previousSheet);
            }
        }
    }
}
