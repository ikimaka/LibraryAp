package listmember;

import addbook.BookAddController;
import addmember.MemberAddController;
import alert.AlertMaker;
import database.DatabaseHandler;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import util.LibraryAssistantUtil;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class MemberListController implements Initializable {

    @FXML private TableView<Member> tableView;
    @FXML private TableColumn<Member, String> nameCol;
    @FXML private TableColumn<Member, String> idCol;
    @FXML private TableColumn<Member, String> mobileCol;
    @FXML private TableColumn<Member, String> emailCol;

    @FXML private StackPane rootPane;
    @FXML private AnchorPane contentPane;

    ObservableList<Member> list = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initCol();
        loadData();
    }

    private void loadData() {
        list.clear();
        DatabaseHandler handler = DatabaseHandler.getInstance();
        String qu = "SELECT * FROM Member";
        ResultSet rs = handler.execQuery(qu);
        try {
            while (rs.next()) {
                String name = rs.getString("name");
                String mobile = rs.getString("mobile");
                String id = rs.getString("id");
                String email = rs.getString("email");

                list.add(new Member(name, id, mobile, email));
            }
        } catch(SQLException e) {
            System.out.println(e.getMessage());
        }

        tableView.setItems(list);
    }

    private void initCol() {
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        mobileCol.setCellValueFactory(new PropertyValueFactory<>("mobile"));
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
    }

    @FXML private Stage getStage() {
        return (Stage) tableView.getScene().getWindow();
    }

    @FXML private void handleRefresh() {
        loadData();
    }

    @FXML private void handleMemberEdit() {
        Member selectedForEdit = tableView.getSelectionModel().getSelectedItem();
        if (selectedForEdit == null) {
            AlertMaker.showErrorMessage("No member is selected", "Please select a member fo edit");
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/addmember/member_add.fxml"));
            Parent parent = loader.load();

            MemberAddController controller = loader.getController();
            controller.inflateUI(selectedForEdit);

            Stage stage = new Stage(StageStyle.DECORATED);
            stage.setTitle("Edit Member");
            stage.setScene(new Scene(parent));
            LibraryAssistantUtil.setStageIcon(stage);
            stage.show();
            stage.setOnCloseRequest((event) -> {
                handleRefresh();
            });
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    @FXML private void handleMemberDelete() {
        Member selectedForDeletion = tableView.getSelectionModel().getSelectedItem();
        if (selectedForDeletion == null) {
            AlertMaker.showErrorMessage("No member is selected", "Please select a member fo deletion");
            return;
        }
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Deleting a member");
        alert.setContentText("Are you sure want to delete a member" + selectedForDeletion.getName() + "?");
        Optional<ButtonType> answer = alert.showAndWait();
        if (answer.get() == ButtonType.OK) {
            Boolean result = DatabaseHandler.getInstance().deleteMember(selectedForDeletion);
            if (result) {
                AlertMaker.showSimpleAlert("Member is deleted", selectedForDeletion.getName() + "was deleted successfully");
                list.remove(selectedForDeletion);
            } else {
                AlertMaker.showSimpleAlert("Failed", selectedForDeletion.getName() + "could not be deleted");
            }
        } else {
            AlertMaker.showSimpleAlert("Deletion is cancelled", "Deletion process is cancelled");
        }
    }

    @FXML
    private void exportAsPDF(ActionEvent event) {
        List<List> printData = new ArrayList<>();
        String[] headers = {"   Name", "ID", "Mobile", "    Email   "};
        printData.add(Arrays.asList(headers));
        for (Member member : list) {
            List<String> row = new ArrayList<>();
            row.add(member.getName());
            row.add(member.getId());
            row.add(member.getMobile());
            row.add(member.getEmail());
            printData.add(row);
        }
        LibraryAssistantUtil.initPDFExport(rootPane, contentPane, getStage(), printData);
    }

    @FXML private void closeStage(ActionEvent event) {
        getStage().close();
    }

    public static class Member {
        private SimpleStringProperty name;
        private SimpleStringProperty id;
        private SimpleStringProperty mobile;
        private SimpleStringProperty email;

        public Member (String name, String id, String mobile, String email) {
            this.name = new SimpleStringProperty(name);
            this.id = new SimpleStringProperty(id);
            this.mobile = new SimpleStringProperty(mobile);
            this.email = new SimpleStringProperty(email);
        }

        public String getName() {
            return name.get();
        }
        public String getId() {
            return id.get();
        }
        public String getMobile() {
            return mobile.get();
        }
        public String getEmail() {
            return email.get();
        }
    }

}
