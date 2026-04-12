package main.sheets;

import blue.underwater.commons.logging.XLogger;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SettingsService {

    private static final SettingsService instance = new SettingsService();
    private static final String SHEET_ID = "1pAkhmzkBymm-Ls7eYzruO78yln56ow5CZZ7INHLzxzg";

    private final blue.underwater.sheets.admin.SheetsAdmin sheet;
    private List<String> locationNames = new ArrayList<>();
    private List<String> locationUrls = new ArrayList<>();

    private SettingsService() {
        String credentialsPath = System.getProperty("credentials.calendar.path");
        this.sheet = new blue.underwater.sheets.admin.SheetsAdmin(
                SHEET_ID,
                "freedive.mallorca.info2@gmail.com",
                credentialsPath
        );
        this.sheet.setCurrentSheet("Settings");
    }

    public static SettingsService getInstance() {
        return instance;
    }

    public synchronized void load() {
        try {
            List<List<Object>> data = sheet.read("A3:B100");
            locationNames.clear();
            locationUrls.clear();
            if (data == null) return;
            for (List<Object> row : data) {
                if (row.isEmpty()) continue;
                String name = row.get(0).toString().trim();
                if (name.isEmpty()) continue;
                String url = row.size() > 1 ? row.get(1).toString().trim() : "";
                locationNames.add(name);
                locationUrls.add(url);
            }
            XLogger.info(this, "Loaded %d locations from Settings sheet", locationNames.size());
        } catch (IOException | GeneralSecurityException e) {
            XLogger.severe(this, "Failed to load settings: %s", e.getMessage());
        }
    }

    public List<String> getLocationNames() {
        return Collections.unmodifiableList(locationNames);
    }

    public List<String> getLocationUrls() {
        return Collections.unmodifiableList(locationUrls);
    }
}
