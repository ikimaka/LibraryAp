package util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import settings.Preferences;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LibraryAssistantUtil {
    private static final String IMAGE_LOC = "/resources/icon.png";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss a");

    public static void setStageIcon(Stage stage) {
        stage.getIcons().add(new Image(IMAGE_LOC));
    }

    public static void loadWindow(URL loc, String title, Stage parentStage) {
        try {
            Parent parent = FXMLLoader.load(loc);
            Stage stage = null;
            if (parentStage != null) {
                stage = parentStage;
            } else {
                stage = new Stage(StageStyle.DECORATED);
            }
            stage.setTitle(title);
            stage.setScene(new Scene(parent));
            setStageIcon(stage);
            stage.show();

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public static Float getFineAmount(int totalDays) {
        Preferences pref = Preferences.getPreferences();
        Integer fineDays = totalDays - pref.getnDaysWithoutFine();
        Float fine = 0f;
        if (fineDays > 0) {
            fine = fineDays * pref.getFinePerDay();
        }
        return fine;
    }

    public static String formatDateTimeString(Date date)
    {
        return DATE_FORMAT.format(date);
    }


}
