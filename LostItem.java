
package com.campus.system;

import java.io.Serializable;

public class LostItem implements Serializable {
    private static final long serialVersionUID = 1L;
    private int id;
    private String itemName;
    private String description;
    private String location;
    private String contactInfo;
    private String status;

    public LostItem(int id, String itemName, String description, String location, String contactInfo, String status) {
        this.id = id;
        this.itemName = itemName;
        this.description = description;
        this.location = location;
        this.contactInfo = contactInfo;
        this.status = status;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getItemName() {
        return itemName;
    }

    public String getDescription() {
        return description;
    }

    public String getLocation() {
        return location;
    }

    public String getContactInfo() {
        return contactInfo;
    }

    public String getStatus() {
        return status;
    }

    // Setters (if needed, for simplicity only status is made mutable for update)
    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "ID: " + id + ", Item: " + itemName + ", Desc: " + description + ", Loc: " + location + ", Contact: " + contactInfo + ", Status: " + status;
    }
}
