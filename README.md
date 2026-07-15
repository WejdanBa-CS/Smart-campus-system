# Smart Campus Service System: Lost and Found Application

**Course:** 223CCS-3 Advanced Object-Oriented Programming
**Semester:** II Academic Year 2025-2026

## 1. Project Title
**Smart Campus Lost and Found System**

## 2. Problem Selection and Justification
**Selected Topic:** Students lose personal items.

**Why this topic was chosen:**
In a busy university environment, students frequently misplace personal belongings such as ID cards, keys, electronics, and notebooks. Currently, the process of reporting a lost item or finding a misplaced one is often disorganized, relying on word-of-mouth or physical bulletin boards. This leads to a low recovery rate and frustration among students.

**How the proposed system solves the problem:**
The "Smart Campus Lost and Found System" provides a centralized, digital platform for managing lost and found items. It allows students or campus staff to quickly log items they have found or search for items they have lost. 

The system addresses the problem through the following features:
*   **Centralized Database:** All records are stored in a single SQLite database table (`lost_items`), ensuring data is persistent and easily accessible.
*   **Client-Server Architecture:** A server handles database operations, while clients (the GUI application) connect to it. This allows multiple users across the campus network to access the system simultaneously.
*   **User-Friendly GUI:** A Java Swing interface provides an intuitive way to add new items, search for specific keywords (e.g., "iPhone", "Library"), update the status of an item (e.g., from "Found" to "Claimed"), and delete records.
*   **Real-time Updates (Multithreading):** The client application utilizes a background thread (`SwingWorker` via a `Timer`) to automatically refresh the list of items every 5 seconds. This ensures that users always see the most up-to-date information without needing to manually refresh the page.

## 3. Required Technologies Implemented
*   **Java Swing GUI:** Used to create the main application window (`LostAndFoundGUI.java`) with input fields, buttons, and a data table.
*   **Object-Oriented Programming:** The system is designed using OOP principles, with classes representing entities (`LostItem.java`), database management (`DatabaseManager.java`), network communication (`Server.java`, `Client.java`, `ClientHandler.java`), and the user interface.
*   **JDBC Database:** SQLite is used as the database engine. `DatabaseManager.java` uses JDBC to connect to `campus_lost_found.db` and execute SQL queries for CRUD operations.
*   **Networking (Client / Server):** The application uses Java Sockets. `Server.java` listens for incoming connections, and `Client.java` sends requests (e.g., "ADD_ITEM", "GET_ALL_ITEMS") over the network.
*   **Multithreading:** 
    *   The server uses multithreading (`ClientHandler` implements `Runnable`) to handle multiple client connections concurrently.
    *   The client GUI uses a `Timer` and `SwingWorker` to periodically fetch updated data from the server in the background, preventing the UI from freezing during network calls.

## 4. Database Schema
The system uses a single table named `lost_items`.

**Table: `lost_items`**
*   `id` (INTEGER PRIMARY KEY AUTOINCREMENT): Unique identifier for each record.
*   `item_name` (TEXT NOT NULL): The name of the lost/found item.
*   `description` (TEXT): Additional details about the item.
*   `location` (TEXT NOT NULL): Where the item was lost or found.
*   `contact_info` (TEXT NOT NULL): How to reach the person who found/lost the item.
*   `status` (TEXT NOT NULL): Current status (e.g., "Lost", "Found", "Claimed").

