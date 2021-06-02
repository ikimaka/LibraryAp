package addmember;

import alert.AlertMaker;
import database.DatabaseHandler;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import listbook.BookListController;
import listmember.MemberListController;

import java.net.URL;
import java.util.ResourceBundle;

public class MemberAddController implements Initializable {
    DatabaseHandler handler;
    @FXML private TextField name;
    @FXML private TextField id;
    @FXML private TextField mobile;
    @FXML private TextField email;

    private Boolean isInEditMode = false;

    @FXML private void cancel() {
        ((Stage)name.getScene().getWindow()).close();
    }

    @FXML
    void addMember(ActionEvent event) {
        String mName = name.getText();
        String mID = id.getText();
        String mMobile = mobile.getText();
        String mEmail = email.getText();

        Boolean flag = mName.isEmpty()||mID.isEmpty()||mMobile.isEmpty()||mEmail.isEmpty();
        if (flag) {
            AlertMaker.showErrorMessage("Can't process member", "Please enter all fields");
            return;
        }
        if (isInEditMode) {
            handleUpdateMember();
            return;
        }
        String st = "INSERT INTO Member VALUES (" +
                "'" + mID + "'," +
                "'" + mName + "'," +
                "'" + mMobile + "'," +
                "'" + mEmail + "'" +
                ")";
        System.out.println(st);
        if(handler.execAction(st)) {
            AlertMaker.showSimpleAlert("New Member Added", mName + " has been added");
            clearEntries();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("Failed");
            alert.showAndWait();
        }
    }

    private void clearEntries() {
        name.clear();
        id.clear();
        mobile.clear();
        email.clear();
    }

    private void handleUpdateMember() {
        MemberListController.Member member = new MemberListController.Member(name.getText(), id.getText(), mobile.getText(), email.getText());
        if (handler.updateMember(member)) {
            AlertMaker.showSimpleAlert("Success", "Member is updated");
        } else {
            AlertMaker.showErrorMessage("Failed", "Can not update the member");
        }
    }

    @Override public void initialize(URL location, ResourceBundle resources) {
        handler = DatabaseHandler.getInstance();
    }

    public void inflateUI(MemberListController.Member member) {
        name.setText(member.getName());
        id.setText(member.getId());
        id.setEditable(false);
        mobile.setText(member.getMobile());
        email.setText(member.getEmail());
        isInEditMode = true;

    }
}
