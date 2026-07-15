
package com.campus.system;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

public class Client {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 12345;

    public String addItem(LostItem item) {
        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

            out.writeObject("ADD_ITEM");
            out.writeObject(item);
            return (String) in.readObject();

        } catch (IOException | ClassNotFoundException e) {
            return "Error adding item: " + e.getMessage();
        }
    }

    public List<LostItem> searchItems(String keyword) {
        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

            out.writeObject("SEARCH_ITEMS");
            out.writeObject(keyword);
            return (List<LostItem>) in.readObject();

        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error searching items: " + e.getMessage());
            return null;
        }
    }

    public List<LostItem> getAllItems() {
        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

            out.writeObject("GET_ALL_ITEMS");
            return (List<LostItem>) in.readObject();

        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error getting all items: " + e.getMessage());
            return null;
        }
    }

    public String deleteItem(int id) {
        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

            out.writeObject("DELETE_ITEM");
            out.writeObject(id);
            return (String) in.readObject();

        } catch (IOException | ClassNotFoundException e) {
            return "Error deleting item: " + e.getMessage();
        }
    }

    public String updateItemStatus(int id, String newStatus) {
        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

            out.writeObject("UPDATE_STATUS");
            out.writeObject(id);
            out.writeObject(newStatus);
            return (String) in.readObject();

        } catch (IOException | ClassNotFoundException e) {
            return "Error updating item status: " + e.getMessage();
        }
    }
}
