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
 * @author bott_ma
 */
public final class RegisterSheetsAdmin {

    private static final RegisterSheetsAdmin instance = new RegisterSheetsAdmin();
    public final static String CONTACTS_SHEET_ID = "1pAkhmzkBymm-Ls7eYzruO78yln56ow5CZZ7INHLzxzg";
    private final blue.underwater.sheets.admin.SheetsAdmin registerSheet;
    private String credentialsPath = "../credentials/freedivemallorcaadmin-1a2eb9366fad.json";

    /**
     *
     */
    public RegisterSheetsAdmin() {
        /*this.registerSheet = new blue.underwater.sheets.admin.SheetsAdmin(CONTACTS_SHEET_ID, 
                "freedive.mallorca.info2@gmail.com", 
                "/home/maxi/gits/underwater/apps/credentials/freedivemallorcaadmin-1a2eb9366fad.json");*/

        String credentialsPath = System.getProperty("credentials.calendar.path");
        this.registerSheet = new blue.underwater.sheets.admin.SheetsAdmin(
                CONTACTS_SHEET_ID,
                "freedive.mallorca.info2@gmail.com",
                credentialsPath
        );
        this.registerSheet.setCurrentSheet("Registration");
    }

    public static RegisterSheetsAdmin getInstance() {
        return instance;
    }

    public List<Object> readRow(int row) throws IOException, GeneralSecurityException {
        return readCells(String.format("A%s:T%s", row, row)).get(0);
    }

    public List<List<Object>> readRows(int from, int to) throws IOException, GeneralSecurityException {
        return readCells(String.format("A%s:T%s", from, to));
    }

    public List<List<Object>> readCells(String cells) throws IOException, GeneralSecurityException {
        return registerSheet.read(cells);
    }

    /**
     * Finds the last non-empty row in a specified column and returns the next
     * row number. This helps identify where to add new data in a sheet.
     *
     * @param sheetName The name of the sheet to search in
     * @param column The column to check for empty cells (e.g., "A", "B")
     * @param startRow The row to start checking from
     * @param maxRows The maximum number of rows to check
     * @return The row number of the first empty row after the last data row
     * @throws GeneralSecurityException
     * @throws IOException
     */
    private int getLastEmptyRow(String sheetName, String column, int startRow, int maxRows)
            throws GeneralSecurityException, IOException {
        // Save current sheet
        String previousSheet = this.registerSheet.getCurrentSheet();

        try {
            // Set the sheet to search in
            if (sheetName != null && !sheetName.isEmpty()) {
                this.registerSheet.setCurrentSheet(sheetName);
            }

            // Build range to check (e.g., "A1:A1000")
            String range = column + (startRow + 1) + ":" + column + (startRow + maxRows);

            // Read the data from the column
            List<List<Object>> data = this.registerSheet.read(range);

            if (data == null || data.isEmpty()) {
                return startRow;
            }

            return startRow + data.size();
        } catch (Exception e) {
            XLogger.severe(InternalSheetsAdmin.class, "Error finding last empty row: " + e.getMessage());
            throw e;
        } finally {
            // Restore original sheet
            if (previousSheet != null && !previousSheet.equals(this.registerSheet.getCurrentSheet())) {
                this.registerSheet.setCurrentSheet(previousSheet);
            }
        }
    }

    /**
     * Convenience method to get the last empty row in the current sheet's
     * column A, starting from row 1, checking up to 1000 rows.
     *
     * @return The row number of the first empty row
     * @throws GeneralSecurityException
     * @throws IOException
     */
    public int getLastEmptyRow(int startRow) throws GeneralSecurityException, IOException {
        return getLastEmptyRow(null, "A", startRow, 10);
    }
}
