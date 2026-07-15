
package com.campus.system;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

public class ClientHandler implements Runnable {
    private Socket clientSocket;
    private DatabaseManager dbManager;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    public ClientHandler(Socket socket, DatabaseManager dbManager) {
        this.clientSocket = socket;
        this.dbManager = dbManager;
    }

    @Override
    public void run() {
        try {
            out = new ObjectOutputStream(clientSocket.getOutputStream());
            in = new ObjectInputStream(clientSocket.getInputStream());

            while (true) {
                String action = null;
                try {
                    action = (String) in.readObject();
                } catch (EOFException e) {
                    System.out.println("Client disconnected gracefully: " + clientSocket.getInetAddress());
                    break; // Exit loop on client disconnection
                }

                switch (action) {
                    case "ADD_ITEM":
                        LostItem newItem = (LostItem) in.readObject();
                        dbManager.addItem(newItem.getItemName(), newItem.getDescription(), newItem.getLocation(), newItem.getContactInfo(), newItem.getStatus());
                        out.writeObject("Item added successfully.");
                        break;
                    case "SEARCH_ITEMS":
                        String keyword = (String) in.readObject();
                        List<LostItem> searchResults = dbManager.searchItems(keyword);
                        out.writeObject(searchResults);
                        break;
                    case "GET_ALL_ITEMS":
                        List<LostItem> allItems = dbManager.getAllItems();
                        out.writeObject(allItems);
                        break;
                    case "DELETE_ITEM":
                        int idToDelete = (int) in.readObject();
                        dbManager.deleteItem(idToDelete);
                        out.writeObject("Item deleted successfully.");
                        break;
                    case "UPDATE_STATUS":
                        int idToUpdate = (int) in.readObject();
                        String newStatus = (String) in.readObject();
                        dbManager.updateItemStatus(idToUpdate, newStatus);
                        out.writeObject("Item status updated successfully.");
                        break;
                    case "EXIT":
                        System.out.println("Client requested exit: " + clientSocket.getInetAddress());
                        return;
                    default:
                        out.writeObject("Unknown action.");
                        break;
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("ClientHandler error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (clientSocket != null && !clientSocket.isClosed()) {
                    clientSocket.close();
                }
            } catch (IOException e) {
                System.err.println("Error closing client socket: " + e.getMessage());
            }
        }
    }
}
