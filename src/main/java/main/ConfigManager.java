package main;

import blue.underwater.commons.logging.XLogger;
import java.io.*;
import java.util.Properties;

public class ConfigManager {

    private static final ConfigManager instance = new ConfigManager();    
    private static final String CONFIG_FILE = "config.properties";

    public static void main(String[] args) {
        ConfigManager configManager = new ConfigManager();        
        configManager.readConfig();
    }

    public static ConfigManager getInstance() {
        return instance;
    }

    private ConfigManager() {        
    }

    public void readConfig() {
        try (InputStream input = new FileInputStream(new File(CONFIG_FILE).getAbsolutePath())) {
            Properties prop = new Properties();
            prop.load(input);

            for (String key : prop.stringPropertyNames()) {
                String value = prop.getProperty(key);

                XLogger.info(this, "Reading property: %s = %s", key, value);
                System.setProperty(key, value);
            }
            
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
