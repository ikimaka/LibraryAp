package main;

import alert.AlertMaker;
import com.jfoenix.controls.*;
import com.jfoenix.controls.events.JFXDialogEvent;
import com.jfoenix.transitions.hamburger.HamburgerSlideCloseTransition;
import database.DatabaseHandler;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.effect.BoxBlur;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import util.LibraryAssistantUtil;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Date;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

public class MainController implements Initializable {
    private static final String BOOK_NOT_AVAILABLE = "Not Available";
    private static final String NO_SUCH_BOOK_AVAILABLE = "No Such Book Available";
    private static final String NO_SUCH_MEMBER_AVAILABLE = "No Such Member Available";
    private static final String BOOK_AVAILABLE = "Available";

    @FXML private TextField bookIdInput;
    @FXML private Button renewButton;
    @FXML private Button submissionButton;
    @FXML private HBox submissionDataContainer;
    @FXML private TextField bookID;
    @FXML private Text bookName;
    @FXML private Text bookAuthor;
    @FXML private Text bookStatus;
    @FXML private Text memberName;
    @FXML private Text memberMobile;
    @FXML private TextField memberIdInput;
    @FXML private StackPane rootPane;
    @FXML private AnchorPane rootAnchorPane;
    @FXML private StackPane bookInfoContainer;
    @FXML private StackPane memberInfoContainer;
    @FXML private JFXTabPane mainTabPane;
    @FXML private Tab bookIssueTab;
    @FXML private Tab renewTab;
    @FXML private JFXHamburger hamburger;
    @FXML private JFXDrawer drawer;
    private Boolean isReadyForSubmission = false;
    PieChart bookChart;
    PieChart memberChart;
    @FXML private Text memberNameHolder;
    @FXML private Text memberEmailHolder;
    @FXML private Text memberContactHolder;
    @FXML private Text bookNameHolder;
    @FXML private Text bookAuthorHolder;
    @FXML private Text bookPublisherHolder;
    @FXML private Text issueDateHolder;
    @FXML private Text numberDaysHolder;
    @FXML private Text fineInfoHolder;
    DatabaseHandler databaseHandler;

    @FXML private void loadIssueOperation(ActionEvent event) {
        if (checkForIssueValidity()) {
            JFXButton btn = new JFXButton("Okay!");
            AlertMaker.showMaterialDialog(rootPane, rootAnchorPane, Arrays.asList(btn), "Invalid Input", null);
            return;
        }
        if (bookStatus.getText().equals(BOOK_NOT_AVAILABLE)) {
            JFXButton btn = new JFXButton("Okay!");
            JFXButton viewDetails = new JFXButton("View Details");
            viewDetails.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent e)->{
                String bookToBeLoaded = bookIdInput.getText();
                bookID.setText(bookToBeLoaded);
                mainTabPane.getSelectionModel().select(renewTab);
                bookID.fireEvent(new ActionEvent());
            });
            AlertMaker.showMaterialDialog(rootPane, rootAnchorPane, Arrays.asList(btn, viewDetails), "Already issued book", "This book is already issued. Cant process issue request");
            return;
        }

        String memberID = memberIdInput.getText();
        String bookID = bookIdInput.getText();

        JFXButton yesButton = new JFXButton("Yes");
        yesButton.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent event1) -> {
            String str = "INSERT INTO Issue(memberID, bookID) VALUES ('" + memberID + "', '" + bookID + "')";
            String str2 = "UPDATE Book SET isAvail = false WHERE id = '" + bookID + "'";

            if (databaseHandler.execAction(str) && databaseHandler.execAction(str2)) {
                JFXButton button = new JFXButton("Done!");
                AlertMaker.showMaterialDialog(rootPane, rootAnchorPane, Arrays.asList(button), "Book issue is completed", null);
                refreshGraphs();
            } else {
                JFXButton button = new JFXButton("Ok. I'll check");
                AlertMaker.showMaterialDialog(rootPane, rootAnchorPane, Arrays.asList(button), "Issue operation is failed", null);
            }
            clearIssueEntries();
        });
        JFXButton noButton = new JFXButton("No");
        noButton.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent event1) -> {
            JFXButton button = new JFXButton("That's OK");
            AlertMaker.showMaterialDialog(rootPane, rootAnchorPane, Arrays.asList(button), "Issue operation is cancelled", null);
            clearIssueEntries();
        });
        AlertMaker.showMaterialDialog(rootPane, rootAnchorPane, Arrays.asList(yesButton, noButton), "Confirm Issue", "Are you sure want to issue the book" + bookName.getText() + " to " + memberName.getText() + "?");
    }

    @FXML private void loadMemberInfo() {
        clearMemberCache();
        enableDisableGraph(false);
        String id = memberIdInput.getText();
        String qu = "SELECT * FROM Member WHERE id = '" + id + "'";
        ResultSet rs = databaseHandler.execQuery(qu);
        Boolean flag = false;

        try {
            while (rs.next()) {
                String mName = rs.getString("name");
                String mMobile = rs.getString("mobile");
                memberName.setText(mName);
                memberMobile.setText(mMobile);
                flag = true;
            }
            if (!flag) {
                memberName.setText(NO_SUCH_MEMBER_AVAILABLE);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @FXML private void loadBookInfo() {
        clearBookCache();
        enableDisableGraph(false);
        String id = bookIdInput.getText();
        String qu = "SELECT * FROM Book WHERE id = '" + id + "'";
        ResultSet rs = databaseHandler.execQuery(qu);
        Boolean flag = false;

        try {
            while (rs.next()) {
                String bName = rs.getString("title");
                String bAuthor = rs.getString("author");
                Boolean bStatus = rs.getBoolean("isAvail");
                bookName.setText(bName);
                bookAuthor.setText(bAuthor);
                String status = (bStatus) ? BOOK_AVAILABLE : BOOK_NOT_AVAILABLE;
                bookStatus.setText(status);
                flag = true;
            }
            if (!flag) {
                bookName.setText(NO_SUCH_BOOK_AVAILABLE);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @FXML private void loadBookInfo2() {
        clearEntries();
        ObservableList<String> issueData = FXCollections.observableArrayList();
        isReadyForSubmission = false;
        try {
            String id = bookID.getText();
            String myQuery = "SELECT Issue.bookID, Issue.memberID, Issue.issueTime, Issue.renew_count, "
                    + "Member.name, Member.mobile, Member.email, "
                    + "Book.title, Book.author, Book.publisher "
                    + "FROM Issue "
                    + "LEFT JOIN Member "
                    + "ON Issue.memberID = Member.ID "
                    + "LEFT JOIN Book "
                    + "ON Issue.bookID = Book.ID "
                    + "WHERE Issue.bookID ='" + id + "'";
            ResultSet rs = databaseHandler.execQuery(myQuery);
            if (rs.next()) {
                memberNameHolder.setText(rs.getString("name"));
                memberContactHolder.setText(rs.getString("mobile"));
                memberEmailHolder.setText(rs.getString("email"));

                bookNameHolder.setText(rs.getString("title"));
                bookAuthorHolder.setText(rs.getString("author"));
                bookPublisherHolder.setText(rs.getString("publisher"));

                Timestamp mIssueTime = rs.getTimestamp("issueTime");
                Date dateOfIssue = new Date(mIssueTime.getTime());
                issueDateHolder.setText(LibraryAssistantUtil.formatDateTimeString(dateOfIssue));
                Long timeElapsed = System.currentTimeMillis() - mIssueTime.getTime();
                Long days = TimeUnit.DAYS.convert(timeElapsed, TimeUnit.MILLISECONDS) + 1;
                String daysElapsed = String.format("Used %d days", days);
                numberDaysHolder.setText(daysElapsed);
                Float fine = LibraryAssistantUtil.getFineAmount(days.intValue());
                if (fine > 0) {
                    fineInfoHolder.setText(String.format("Fine : %.2f", LibraryAssistantUtil.getFineAmount(days.intValue())));
                } else {
                    fineInfoHolder.setText("");
                }


                isReadyForSubmission = true;
                disableEnableControls(true);
                submissionDataContainer.setOpacity(1);
            } else {
                JFXButton button = new JFXButton("Ok, I'll check");
                AlertMaker.showMaterialDialog(rootPane, rootAnchorPane, Arrays.asList(button), "No such book exists in issue records", null);
            }
        } catch (SQLException e) {
            e.getMessage();
        }
    }

    private void clearEntries() {
        memberNameHolder.setText("");
        memberEmailHolder.setText("");
        memberContactHolder.setText("");

        bookNameHolder.setText("");
        bookPublisherHolder.setText("");
        bookAuthorHolder.setText("");

        issueDateHolder.setText("");
        numberDaysHolder.setText("");
        fineInfoHolder.setText("");

        disableEnableControls(false);
        submissionDataContainer.setOpacity(0);
    }

    private void disableEnableControls(Boolean enableFlag) {
        if (enableFlag) {
            renewButton.setDisable(false);
            submissionButton.setDisable(false);
        } else {
            renewButton.setDisable(true);
            submissionButton.setDisable(true);
        }
    }

    private void clearBookCache() {
        bookName.setText("");
        bookAuthor.setText("");
        bookStatus.setText("");
    }

    private void clearMemberCache() {
        memberName.setText("");
        memberMobile.setText("");
    }

    @FXML private void loadSubmissionOp() {
        if(!isReadyForSubmission) {
            JFXButton btn = new JFXButton("Okay");
            AlertMaker.showMaterialDialog(rootPane, rootAnchorPane, Arrays.asList(btn), "Please select a book to submit", "Can't simply submit a null book :))");
            return;
        }
        JFXButton yesButton = new JFXButton("Yes");
        yesButton.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent ev) -> {
            String id = bookID.getText();
            String ac1 = "DELETE FROM Issue WHERE bookID = '" + id + "'";
            String ac2 = "UPDATE Book SET isAvail = TRUE WHERE id = '" + id + "'";
            if (databaseHandler.execAction(ac1) && databaseHandler.execAction(ac2)) {
                JFXButton btn = new JFXButton("Done");
                AlertMaker.showMaterialDialog(rootPane, rootAnchorPane, Arrays.asList(btn), "Book has been submitted", null);
                disableEnableControls(false);
                submissionDataContainer.setOpacity(0);
            } else {
                JFXButton btn = new JFXButton("OK, I'll check");
                AlertMaker.showMaterialDialog(rootPane, rootAnchorPane, Arrays.asList(btn), "Submission has been failed", null);
            }
        });
        JFXButton noButton = new JFXButton("No");
        noButton.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent ev) -> {
            JFXButton btn = new JFXButton("OK");
            AlertMaker.showMaterialDialog(rootPane, rootAnchorPane, Arrays.asList(btn), "Submission operation is cancelled", null);
        });
        AlertMaker.showMaterialDialog(rootPane, rootAnchorPane, Arrays.asList(yesButton, noButton), "Confirm submission operation", "Are you sure want to return the book?");
    }

    @FXML private void loadRenewOperation() {
        if(!isReadyForSubmission) {
            JFXButton button = new JFXButton("Okay");
            AlertMaker.showMaterialDialog(rootPane, rootAnchorPane, Arrays.asList(button), "Please select a book to renew", "Can't simply renew a null book :))");
            return;
        }
        JFXButton yesButton = new JFXButton("YES");
        yesButton.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent ev) -> {
            String ac = "UPDATE Issue SET issueTime = CURRENT_TIMESTAMP, renew_count = renew_count+1 WHERE bookID = '" + bookID.getText() + "'";
            if (databaseHandler.execAction(ac)) {
                JFXButton button = new JFXButton("Done");
                AlertMaker.showMaterialDialog(rootPane, rootAnchorPane, Arrays.asList(button), "Book has been renewed", null);
                loadBookInfo2();
            } else {
                JFXButton button = new JFXButton("Ok, I'll check");
                AlertMaker.showMaterialDialog(rootPane, rootAnchorPane, Arrays.asList(button), "Renew has been failed", null);
            }
        });
        JFXButton noButton = new JFXButton("NO");
        noButton.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent ev) -> {
            JFXButton button = new JFXButton("That's OK");
            AlertMaker.showMaterialDialog(rootPane, rootAnchorPane, Arrays.asList(button), "Renew operation is cancelled", null);
        });

        AlertMaker.showMaterialDialog(rootPane,rootAnchorPane, Arrays.asList(yesButton, noButton), "Confirm renew operation", "Are you sure want to renew the book?");
    }

    @FXML private void handleMenuClose() {
        ((Stage)rootPane.getScene().getWindow()).close();
    }

    @FXML private void handleMenuAddBook() {
        LibraryAssistantUtil.loadWindow(getClass().getResource("/addbook/add_book.fxml"), "Add Book", null);
    }

    @FXML private void handleMenuAddMember() {
        LibraryAssistantUtil.loadWindow(getClass().getResource("/addmember/member_add.fxml"), "Add Member", null);
    }

    @FXML private void handleMenuViewBook() {
        LibraryAssistantUtil.loadWindow(getClass().getResource("/listbook/book_list.fxml"), "Book List", null);
    }

    @FXML private void handleMenuViewMember() {
        LibraryAssistantUtil.loadWindow(getClass().getResource("/listmember/member_list.fxml"), "Member List", null);
    }

    @FXML private void handleMenuFullScreen() {
        Stage stage = (Stage)rootPane.getScene().getWindow();
        stage.setFullScreen(!stage.isFullScreen());
    }

    private void initDrawer() {
        try {
            VBox toolbar = FXMLLoader.load(getClass().getResource("/main/toolbar/toolbar.fxml"));
            drawer.setSidePane(toolbar);
        } catch (IOException e) {
            e.printStackTrace();
        }
        HamburgerSlideCloseTransition task = new HamburgerSlideCloseTransition(hamburger);
        task.setRate(-1);
        hamburger.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<Event>() {
            @Override
            public void handle(Event event) {
                task.setRate(task.getRate()*(-1));
                task.play();
                if (drawer.isOpened()) {
                    drawer.close();
                } else {
                    drawer.open();
                }
            }
        });
    }

    private void clearIssueEntries() {
        bookIdInput.clear();
        memberIdInput.clear();

        bookName.setText("");
        bookAuthor.setText("");
        bookStatus.setText("");

        memberName.setText("");
        memberMobile.setText("");
        enableDisableGraph(true);
    }

    private void initGraphs() {
        bookChart = new PieChart(databaseHandler.getBookGraphStatistics());
        bookInfoContainer.getChildren().add(bookChart);

        memberChart = new PieChart(databaseHandler.getMemberGraphStatistics());
        memberInfoContainer.getChildren().add(memberChart);
    }

    private void enableDisableGraph(Boolean status) {
        if (status) {
            bookChart.setOpacity(1);
            memberChart.setOpacity(1);
        } else {
            bookChart.setOpacity(0);
            memberChart.setOpacity(0);
        }
    }

    private void refreshGraphs() {
        bookChart.setData(databaseHandler.getBookGraphStatistics());
        memberChart.setData(databaseHandler.getMemberGraphStatistics());
    }

    private boolean checkForIssueValidity() {
        bookIdInput.fireEvent(new ActionEvent());
        memberIdInput.fireEvent(new ActionEvent());
        return bookIdInput.getText().isEmpty() || memberIdInput.getText().isEmpty()
                || memberName.getText().isEmpty() || bookName.getText().isEmpty()
                || bookName.getText().equals(NO_SUCH_BOOK_AVAILABLE) || memberName.getText().equals(NO_SUCH_MEMBER_AVAILABLE);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        databaseHandler = DatabaseHandler.getInstance();
        initDrawer();
        initGraphs();
        bookIssueTab.setOnSelectionChanged(new EventHandler<Event>() {
            @Override
            public void handle(Event event) {
                clearIssueEntries();
                if (bookIssueTab.isSelected()) {
                    refreshGraphs();
                }
            }
        });
    }
}