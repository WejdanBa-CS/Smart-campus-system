
package com.campus.system;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Vector;

public class LostAndFoundGUI extends JFrame {
    private Client client;
    private JTable itemTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;

    public LostAndFoundGUI() {
        client = new Client();
        setTitle("Smart Campus Lost and Found System");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initComponents();
        refreshTable();

        // Auto-refresh using SwingWorker (Multithreading)
        Timer timer = new Timer(5000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                refreshTable();
            }
        });
        timer.start();
    }

    private void initComponents() {
        // Panel for input and buttons
        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Labels and Text Fields
        JLabel nameLabel = new JLabel("Item Name:");
        JTextField nameField = new JTextField(20);
        JLabel descLabel = new JLabel("Description:");
        JTextField descField = new JTextField(20);
        JLabel locLabel = new JLabel("Location Found:");
        JTextField locField = new JTextField(20);
        JLabel contactLabel = new JLabel("Contact Info:");
        JTextField contactField = new JTextField(20);
        JLabel statusLabel = new JLabel("Status:");
        String[] statuses = {"Lost", "Found", "Claimed"};
        JComboBox<String> statusComboBox = new JComboBox<>(statuses);
        statusComboBox.setSelectedItem("Found"); // Default status

        gbc.gridx = 0; gbc.gridy = 0; inputPanel.add(nameLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 0; inputPanel.add(nameField, gbc);
        gbc.gridx = 0; gbc.gridy = 1; inputPanel.add(descLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 1; inputPanel.add(descField, gbc);
        gbc.gridx = 0; gbc.gridy = 2; inputPanel.add(locLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 2; inputPanel.add(locField, gbc);
        gbc.gridx = 0; gbc.gridy = 3; inputPanel.add(contactLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 3; inputPanel.add(contactField, gbc);
        gbc.gridx = 0; gbc.gridy = 4; inputPanel.add(statusLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 4; inputPanel.add(statusComboBox, gbc);

        // Buttons
        JButton addButton = new JButton("Add Item");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = nameField.getText();
                String desc = descField.getText();
                String loc = locField.getText();
                String contact = contactField.getText();
                String status = (String) statusComboBox.getSelectedItem();

                if (name.isEmpty() || loc.isEmpty() || contact.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Item Name, Location, and Contact Info cannot be empty.", "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                LostItem newItem = new LostItem(0, name, desc, loc, contact, status);
                String response = client.addItem(newItem);
                JOptionPane.showMessageDialog(null, response);
                refreshTable();
                // Clear fields
                nameField.setText("");
                descField.setText("");
                locField.setText("");
                contactField.setText("");
                statusComboBox.setSelectedItem("Found");
            }
        });

        JButton updateStatusButton = new JButton("Update Status");
        updateStatusButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = itemTable.getSelectedRow();
                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(null, "Please select an item to update.", "Selection Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                int itemId = (int) tableModel.getValueAt(selectedRow, 0);
                String currentStatus = (String) tableModel.getValueAt(selectedRow, 5);
                String newStatus = (String) JOptionPane.showInputDialog(
                        null,
                        "Select new status for item ID " + itemId,
                        "Update Item Status",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        statuses,
                        currentStatus);

                if (newStatus != null && !newStatus.equals(currentStatus)) {
                    String response = client.updateItemStatus(itemId, newStatus);
                    JOptionPane.showMessageDialog(null, response);
                    refreshTable();
                }
            }
        });

        JButton deleteButton = new JButton("Delete Item");
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = itemTable.getSelectedRow();
                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(null, "Please select an item to delete.", "Selection Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                int itemId = (int) tableModel.getValueAt(selectedRow, 0);
                int confirm = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete item ID " + itemId + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    String response = client.deleteItem(itemId);
                    JOptionPane.showMessageDialog(null, response);
                    refreshTable();
                }
            }
        });

        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2; inputPanel.add(addButton, gbc);
        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2; inputPanel.add(updateStatusButton, gbc);
        gbc.gridx = 0; gbc.gridy = 7; gbc.gridwidth = 2; inputPanel.add(deleteButton, gbc);

        // Search Panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchField = new JTextField(25);
        JButton searchButton = new JButton("Search");
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchItems();
            }
        });
        JButton clearSearchButton = new JButton("Clear Search");
        clearSearchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchField.setText("");
                refreshTable();
            }
        });
        searchPanel.add(new JLabel("Search Keyword:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(clearSearchButton);

        // Table for displaying items
        String[] columnNames = {"ID", "Item Name", "Description", "Location", "Contact Info", "Status"};
        tableModel = new DefaultTableModel(columnNames, 0);
        itemTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(itemTable);

        // Main Layout
        setLayout(new BorderLayout());
        add(inputPanel, BorderLayout.WEST);
        add(searchPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void refreshTable() {
        new SwingWorker<List<LostItem>, Void>() {
            @Override
            protected List<LostItem> doInBackground() throws Exception {
                return client.getAllItems();
            }

            @Override
            protected void done() {
                try {
                    List<LostItem> items = get();
                    updateTable(items);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null, "Error refreshing table: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    private void searchItems() {
        String keyword = searchField.getText().trim();
        if (keyword.isEmpty()) {
            refreshTable();
            return;
        }

        new SwingWorker<List<LostItem>, Void>() {
            @Override
            protected List<LostItem> doInBackground() throws Exception {
                return client.searchItems(keyword);
            }

            @Override
            protected void done() {
                try {
                    List<LostItem> items = get();
                    updateTable(items);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null, "Error searching items: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    private void updateTable(List<LostItem> items) {
        tableModel.setRowCount(0); // Clear existing data
        if (items != null) {
            for (LostItem item : items) {
                Vector<Object> row = new Vector<>();
                row.add(item.getId());
                row.add(item.getItemName());
                row.add(item.getDescription());
                row.add(item.getLocation());
                row.add(item.getContactInfo());
                row.add(item.getStatus());
                tableModel.addRow(row);
            }
        }
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            new LostAndFoundGUI().setVisible(true);
        });
    }
}
