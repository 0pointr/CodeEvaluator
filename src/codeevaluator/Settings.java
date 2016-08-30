/*
 * Copyright (C) 2016 debd92 [@] hotmail.com
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package codeevaluator;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

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
