package settings;


import alert.AlertMaker;
import com.google.gson.Gson;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.*;

public class Preferences {
    public static final String CONFIG_FILE = "config.txt";

    private int nDaysWithoutFine;
    private float finePerDay;
    private String username;
    private String password;

    public Preferences () {
        nDaysWithoutFine = 14;
        finePerDay = 2;
        username = "admin";
        setPassword("admin");
    }

    public static void writePreferenceToFile(Preferences preference) {
        Writer writer = null;
        try {
            Gson gson = new Gson();
            writer = new FileWriter(CONFIG_FILE);
            gson.toJson(preference, writer);
            AlertMaker.showSimpleAlert("Success", "Settings updated");
        } catch (IOException e) {
            e.getMessage();
            AlertMaker.showErrorMessage("Failed", "Can't save configuration file");
        } finally {
            try {
                writer.close();
            } catch (IOException e){
                e.getMessage();
            }
        }
    }

    public static void initConfig() {
        Writer writer = null;

        try {
            Preferences preference = new Preferences();
            Gson gson = new Gson();
            writer = new FileWriter(CONFIG_FILE);
            gson.toJson(preference, writer);
        } catch (IOException e) {
            e.getMessage();
        } finally {
            try {
                writer.close();
            } catch (IOException e){
                e.getMessage();
            }
        }
    }

    public static Preferences getPreferences() {
        Gson gson = new Gson();
        Preferences preferences = new Preferences();
        try {
            preferences = gson.fromJson(new FileReader(CONFIG_FILE), Preferences.class);

        } catch (FileNotFoundException e) {
            Preferences.initConfig();
            e.printStackTrace();
        }
        return preferences;
    }

    public int getnDaysWithoutFine() {
        return nDaysWithoutFine;
    }

    public void setnDaysWithoutFine(int nDaysWithoutFine) {
        this.nDaysWithoutFine = nDaysWithoutFine;
    }

    public float getFinePerDay() {
        return finePerDay;
    }

    public void setFinePerDay(float finePerDay) {
        this.finePerDay = finePerDay;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        if (password.length() < 16) {
            this.password = DigestUtils.shaHex(password);
        } else {
            this.password = password;
        }
    }
}
