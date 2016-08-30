/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codeevaluator;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 *
 * @author debd_admin
 */
public class Settings {
    public String compiler_path;
    public String config_path = "config.txt";
    
    private String settings_file = "CODEEVALUATOR.INI";
    private Properties prop;
    private Properties def;
    
    public Settings (String file_name) {
        if (file_name != null) 
            settings_file = file_name;
        def = new Properties();
        def.put("compiler_path", "C:\\MinGW\\bin\\mingw32-gcc.exe");
        def.put("config_path", "config.txt");
        prop = new Properties(def);
    }
    
    public final void LoadSettingsFile () {
        System.out.print("Reading settings file... ");
        FileInputStream fin = null;
        try {
            fin = new FileInputStream(settings_file);
            prop.load(fin);
            System.out.println("Done");
        } catch (FileNotFoundException e) {
            System.out.println("Settings file doesn't exist. Using default values");
            WriteSettingsFile();
        } catch (IOException e) {
            System.err.println(e.getClass() + " : " + e.getMessage());
        } finally {
            try {
                if (fin != null)
                    fin.close();
            } catch (IOException e) {}
        }
    }
    
    public void WriteSettingsFile () {
        System.out.print("Writing settings file... ");
        FileOutputStream fout = null;
        try {
            fout = new FileOutputStream(settings_file);
            prop.store(fout, "CodeEvaluator Settings File");
            System.out.println("Done");
        } catch (IOException e) {
            System.err.println(e.getClass() + " : " + e.getMessage());
        } finally {
            try {
                if (fout != null)
                    fout.close();
            } catch (IOException e) {}
        }
    }
    
    public String GetProp (String key) {
        return prop.getProperty(key);
    }
    
    public void SetProp (String key, String val) {
        prop.setProperty(key, val);
    }
}
