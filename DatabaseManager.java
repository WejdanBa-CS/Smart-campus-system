
package com.campus.system;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    static {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.err.println("SQLite JDBC driver not found: " + e.getMessage());
        }
    }
    private static final String DB_URL = "jdbc:sqlite:campus_lost_found.db";

    public DatabaseManager() {
        createNewDatabase();
        createNewTable();
    }

    private void createNewDatabase() {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            if (conn != null) {
                System.out.println("Database created successfully.");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private void createNewTable() {
        String sql = "CREATE TABLE IF NOT EXISTS lost_items (\n" +
                     " id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                     " item_name TEXT NOT NULL,\n" +
                     " description TEXT,\n" +
                     " location TEXT NOT NULL,\n" +
                     " contact_info TEXT NOT NULL,\n" +
                     " status TEXT NOT NULL\n" +
                     ");";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Table created successfully.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public Connection connect() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(DB_URL);
        } catch (SQLException e) {
            System.err.println("Database connection error: " + e.getMessage());
        }
        return conn;
    }

    public void addItem(String itemName, String description, String location, String contactInfo, String status) {
        String sql = "INSERT INTO lost_items(item_name, description, location, contact_info, status) VALUES(?,?,?,?,?)";

        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, itemName);
            pstmt.setString(2, description);
            pstmt.setString(3, location);
            pstmt.setString(4, contactInfo);
            pstmt.setString(5, status);
            pstmt.executeUpdate();
            System.out.println("Item added successfully.");
        } catch (SQLException e) {
            System.err.println("Error adding item: " + e.getMessage());
        }
    }

    public List<LostItem> searchItems(String keyword) {
        List<LostItem> items = new ArrayList<>();
        String sql = "SELECT id, item_name, description, location, contact_info, status FROM lost_items WHERE item_name LIKE ? OR description LIKE ? OR location LIKE ? OR status LIKE ?";

        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "%" + keyword + "%");
            pstmt.setString(2, "%" + keyword + "%");
            pstmt.setString(3, "%" + keyword + "%");
            pstmt.setString(4, "%" + keyword + "%");
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                items.add(new LostItem(
                        rs.getInt("id"),
                        rs.getString("item_name"),
                        rs.getString("description"),
                        rs.getString("location"),
                        rs.getString("contact_info"),
                        rs.getString("status")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error searching items: " + e.getMessage());
        }
        return items;
    }

    public List<LostItem> getAllItems() {
        List<LostItem> items = new ArrayList<>();
        String sql = "SELECT id, item_name, description, location, contact_info, status FROM lost_items";

        try (Connection conn = this.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                items.add(new LostItem(
                        rs.getInt("id"),
                        rs.getString("item_name"),
                        rs.getString("description"),
                        rs.getString("location"),
                        rs.getString("contact_info"),
                        rs.getString("status")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error getting all items: " + e.getMessage());
        }
        return items;
    }

    public void deleteItem(int id) {
        String sql = "DELETE FROM lost_items WHERE id = ?";

        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            System.out.println("Item deleted successfully.");
        } catch (SQLException e) {
            System.err.println("Error deleting item: " + e.getMessage());
        }
    }

    public void updateItemStatus(int id, String newStatus) {
        String sql = "UPDATE lost_items SET status = ? WHERE id = ?";

        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newStatus);
            pstmt.setInt(2, id);
            pstmt.executeUpdate();
            System.out.println("Item status updated successfully.");
        } catch (SQLException e) {
            System.err.println("Error updating item status: " + e.getMessage());
        }
    }
}
