package main.toolbar;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import util.LibraryAssistantUtil;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ToolbarController implements Initializable {

    @FXML void loadAddBook(ActionEvent event) {
        LibraryAssistantUtil.loadWindow(getClass().getResource("/addbook/add_book.fxml"), "Add Book", null);
    }

    @FXML void loadAddMember(ActionEvent event) {
        LibraryAssistantUtil.loadWindow(getClass().getResource("/addmember/member_add.fxml"), "Add Member", null);
    }

    @FXML void loadBookTable(ActionEvent event) {
        LibraryAssistantUtil.loadWindow(getClass().getResource("/listbook/book_list.fxml"), "Book List", null);
    }

    @FXML void loadMemberTable(ActionEvent event) {
        LibraryAssistantUtil.loadWindow(getClass().getResource("/listmember/member_list.fxml"), "Member List", null);
    }

    @FXML private void loadSettings(ActionEvent event) {
        LibraryAssistantUtil.loadWindow(getClass().getResource("/settings/settings.fxml"), "Settings", null);
    }



    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
