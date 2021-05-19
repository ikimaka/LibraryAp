package settings;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class SettingsController implements Initializable {
    @FXML private TextField nDaysWithoutFine;
    @FXML private TextField finePerDay;
    @FXML private TextField username;
    @FXML private PasswordField password;

    @FXML
    void handleCancelButtonAction(ActionEvent event) {
        ((Stage)nDaysWithoutFine.getScene().getWindow()).close();
    }

    @FXML
    void handleSaveButtonAction(ActionEvent event) {
        Preferences preferences = Preferences.getPreferences();
        preferences.setnDaysWithoutFine(Integer.parseInt(nDaysWithoutFine.getText()));
        preferences.setFinePerDay(Float.parseFloat(finePerDay.getText()));
        preferences.setUsername(username.getText());
        preferences.setPassword(password.getText());

        Preferences.writePreferenceToFile(preferences);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initDefaultValues();
    }

    private void initDefaultValues() {
        Preferences preferences = Preferences.getPreferences();
        nDaysWithoutFine.setText(String.valueOf(preferences.getnDaysWithoutFine()));
        finePerDay.setText(String.valueOf(preferences.getFinePerDay()));
        username.setText(preferences.getUsername());
        password.setText(preferences.getPassword());
    }
}
