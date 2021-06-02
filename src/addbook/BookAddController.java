package addbook;

import alert.AlertMaker;
import database.DatabaseHandler;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import listbook.BookListController;

import java.net.URL;
import java.util.ResourceBundle;

public class BookAddController implements Initializable {
    @FXML private TextField title;
    @FXML private TextField id;
    @FXML private TextField author;
    @FXML private TextField publisher;
    @FXML private Button saveButton;
    @FXML private Button cancelButton;

    private Boolean isInEditMode = false;
    DatabaseHandler databaseHandler;

    @FXML private void addBook(ActionEvent event) {
        String bookId = id.getText();
        String bookAuthor = author.getText();
        String bookName = title.getText();
        String bookPublisher = publisher.getText();

        if (bookId.isEmpty()||bookAuthor.isEmpty()||bookName.isEmpty()||bookPublisher.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("Please enter all fields");
            alert.showAndWait();
            return;
        }
        if (isInEditMode){
            handleEditOperation();
            return;
        }
        
        String qu = "INSERT INTO Book VALUES (" +
                "'" + bookId + "'," +
                "'" + bookName + "'," +
                "'" + bookAuthor + "'," +
                "'" + bookPublisher + "'," +
                "" + true + "" +
                ")";
        //System.out.println(qu);
        if (databaseHandler.execAction(qu)) {
            AlertMaker.showSimpleAlert("New book added", "The book " + bookName + " was added");
            clearEntries();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("Failed");
            alert.showAndWait();
        }
    }

    private void clearEntries() {
        title.clear();
        id.clear();
        author.clear();
        publisher.clear();
    }

    private void handleEditOperation() {
        BookListController.Book book = new BookListController.Book(title.getText(), id.getText(), author.getText(), publisher.getText(), true);
        if (databaseHandler.updateBook(book)) {
            AlertMaker.showSimpleAlert("Success", "Book is updated");
        } else {
            AlertMaker.showErrorMessage("Failed", "Can not update the book");
        }
    }

    public void inflateUI(BookListController.Book book) {
        title.setText(book.getTitle());
        id.setText(book.getId());
        author.setText(book.getAuthor());
        publisher.setText(book.getPublisher());

        id.setEditable(false);
        isInEditMode = true;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        databaseHandler = DatabaseHandler.getInstance();
    }
}
