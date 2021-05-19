package database;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.PieChart;
import listbook.BookListController;
import listmember.MemberListController;

import java.sql.*;

public class DatabaseHandler {
    private static DatabaseHandler handler = null;

    private static Connection conn = null;
    private static Statement stmt = null;

    private DatabaseHandler () {
        createConnection();
    }

    public static DatabaseHandler getInstance() {
        if (handler == null) {
            handler = new DatabaseHandler();
        }
        return handler;
    }

    void createConnection() {
        try {
            conn = DriverManager.getConnection("jdbc:mysql://185.12.94.152:3306/my_java?characterEncoding=UTF-8&useTimezone=true&serverTimezone=Europe/Moscow",
                "webspider", "jhemX672SaQ");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ResultSet execQuery(String query) {
        ResultSet result;
        try {
            stmt = conn.createStatement();
            result = stmt.executeQuery(query);
        } catch(SQLException ex) {
            System.out.println(ex.getLocalizedMessage());
            return null;
        }
        return result;
    }

    public Boolean execAction(String qu) {
        try {
            stmt = conn.createStatement();
            stmt.execute(qu);
            return true;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public Boolean deleteBook(BookListController.Book book) {
        try {
        String deleteStatement = "DELETE FROM Book WHERE id = ?";
        PreparedStatement stmt = null;
        stmt = conn.prepareStatement(deleteStatement);
        stmt.setString(1, book.getId());
        int res = stmt.executeUpdate();
        if (res == 1) {
            return true;
        }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Boolean isBookAlreadyIssued(BookListController.Book book) {
        try {
            String checkstmt = "SELECT COUNT(*) FROM Issue WHERE bookID = ?";
            PreparedStatement stmt =  conn.prepareStatement(checkstmt);
            stmt.setString(1, book.getId());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int count = rs.getInt(1);
                return (count > 0);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Boolean updateBook(BookListController.Book book) {
        try {
            String update = "UPDATE Book SET title = ?, author = ?, publisher = ? WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(update);
            stmt.setString(1, book.getTitle());
            stmt.setString(2, book.getAuthor());
            stmt.setString(3, book.getPublisher());
            stmt.setString(4, book.getId());
            int res = stmt.executeUpdate();
            return (res > 0);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateMember(MemberListController.Member member) {
        try {
            String update = "UPDATE Member SET name = ?, email = ?, mobile = ? WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(update);
            stmt.setString(1, member.getName());
            stmt.setString(2, member.getEmail());
            stmt.setString(3, member.getMobile());
            stmt.setString(4, member.getId());
            int res = stmt.executeUpdate();
            return (res > 0);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Boolean deleteMember(MemberListController.Member member) {
        try {
            String deleteStatement = "DELETE FROM Member WHERE id = ?";
            PreparedStatement stmt = null;
            stmt = conn.prepareStatement(deleteStatement);
            stmt.setString(1, member.getId());
            int res = stmt.executeUpdate();
            if (res == 1) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public ObservableList<PieChart.Data> getBookGraphStatistics() {
        ObservableList<PieChart.Data> data = FXCollections.observableArrayList();
        try {
            String qu1 = "SELECT COUNT(*) FROM Book";
            String qu2 = "SELECT COUNT(*) FROM Issue";
            ResultSet rs = execQuery(qu1);
            if (rs.next()) {
                int count = rs.getInt(1);
                data.add(new PieChart.Data("Total Books (" + count + ")", count));
            }
            rs = execQuery(qu2);
            if (rs.next()) {
                int count = rs.getInt(1);
                data.add(new PieChart.Data("Issue Books (" + count + ")", count));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return data;
    }

    public ObservableList<PieChart.Data> getMemberGraphStatistics() {
        ObservableList<PieChart.Data> data = FXCollections.observableArrayList();
        try {
            String qu1 = "SELECT COUNT(*) FROM Member";
            String qu2 = "SELECT COUNT(DISTINCT memberID) FROM Issue";
            ResultSet rs = execQuery(qu1);
            if (rs.next()) {
                int count = rs.getInt(1);
                data.add(new PieChart.Data("Total Members (" + count + ")", count));
            }
            rs = execQuery(qu2);
            if (rs.next()) {
                int count = rs.getInt(1);
                data.add(new PieChart.Data("Members with Books (" + count + ")", count));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return data;
    }

}
