package listbook;

import addbook.BookAddController;
import alert.AlertMaker;
import database.DatabaseHandler;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import util.LibraryAssistantUtil;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;

public class BookListController implements Initializable {
    @FXML private TableView<Book> tableView;
    @FXML private TableColumn<Book, String> titleCol;
    @FXML private TableColumn<Book, String> idCol;
    @FXML private TableColumn<Book, String> authorCol;
    @FXML private TableColumn<Book, String> publisherCol;
    @FXML private TableColumn<Book, Boolean> availabiltyCol;

    ObservableList<Book> list = FXCollections.observableArrayList();

    @Override public void initialize(URL location, ResourceBundle resources) {
        initCol();
        loadData();
    }

    private void loadData() {
        list.clear();
        DatabaseHandler handler = DatabaseHandler.getInstance();
        String qu = "SELECT * FROM Book";
        ResultSet rs = handler.execQuery(qu);
        try {
            while (rs.next()) {
                String title = rs.getString("title");
                String author = rs.getString("author");
                String id = rs.getString("id");
                String publisher = rs.getString("publisher");
                Boolean avail = rs.getBoolean("isAvail");

                list.add(new Book(title, id, author, publisher, avail));
            }
        } catch(SQLException e) {
            System.out.println(e.getMessage());
        }

        tableView.setItems(list);
    }

    private void initCol() {
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        authorCol.setCellValueFactory(new PropertyValueFactory<>("author"));
        publisherCol.setCellValueFactory(new PropertyValueFactory<>("publisher"));
        availabiltyCol.setCellValueFactory(new PropertyValueFactory<>("availabilty"));
    }

    @FXML private void handleBookDeleteOption() {
        Book selectedForDeletion = tableView.getSelectionModel().getSelectedItem();
        if (selectedForDeletion == null) {
            AlertMaker.showErrorMessage("No book is selected", "Please select a book fo deletion");
            return;
        }
        if (DatabaseHandler.getInstance().isBookAlreadyIssued(selectedForDeletion)) {
            AlertMaker.showErrorMessage("Can't be deleted", "This book is already issued and can not be deleted");
            return;
        }
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Deleting a book");
        alert.setContentText("Are you sure want to delete a book" + selectedForDeletion.getTitle() + "?");
        Optional<ButtonType> answer = alert.showAndWait();
        if (answer.get() == ButtonType.OK) {
            Boolean result = DatabaseHandler.getInstance().deleteBook(selectedForDeletion);
            if (result) {
                AlertMaker.showSimpleAlert("Book is deleted", selectedForDeletion.getTitle() + "was deleted successfully");
                list.remove(selectedForDeletion);
            } else {
                AlertMaker.showSimpleAlert("Failed", selectedForDeletion.getTitle() + "could not be deleted");
            }
        } else {
            AlertMaker.showSimpleAlert("Deletion is cancelled", "Deletion process is cancelled");
        }
    }

    @FXML private void handleBookEditOption() {
        Book selectedForEdit = tableView.getSelectionModel().getSelectedItem();
        if (selectedForEdit == null) {
            AlertMaker.showErrorMessage("No book is selected", "Please select a book fo edit");
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/addbook/add_book.fxml"));
            Parent parent = loader.load();

            BookAddController controller = loader.getController();
            controller.inflateUI(selectedForEdit);

            Stage stage = new Stage(StageStyle.DECORATED);
            stage.setTitle("Edit Book");
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

    @FXML private void handleRefresh() {
        loadData();
    }

    public static class Book {
        private SimpleStringProperty title;
        private SimpleStringProperty id;
        private SimpleStringProperty author;
        private SimpleStringProperty publisher;
        private SimpleStringProperty availabilty;

        public Book (String title, String id, String author, String pub, Boolean avail) {
            this.title = new SimpleStringProperty(title);
            this.id = new SimpleStringProperty(id);
            this.author = new SimpleStringProperty(author);
            this.publisher = new SimpleStringProperty(pub);
            if (avail) {
                this.availabilty = new SimpleStringProperty("Available");
            } else {
                this.availabilty = new SimpleStringProperty("Issued");
            }
        }

        public String getId() {
            return id.get();
        }
        public String getTitle() {
            return title.get();
        }
        public String getAuthor() {
            return author.get();
        }
        public String getPublisher() {
            return publisher.get();
        }
        public String getAvailabilty() {
            return availabilty.get();
        }
    }
}
