package com.example.troye.customnavigationdrawer;

import java.io.Serializable;

/**
 * Created by Troye on 6/21/2015.
 */
public class ContactObject{

    private String contactName;
    private String contactNo;
    private String image;
    private boolean selected;

    public ContactObject() {

    }

    public ContactObject(String contactName, String contactNo, String image) {
        this.contactName = contactName;
        this.contactNo = contactNo;
        this.image = image;
    }

    public String getName() {
        return contactName;
    }

    public void setName(String contactName) {
        this.contactName = contactName;
    }

    public String getNumber() {
        return contactNo;
    }

    public void setNumber(String contactNo) {
        this.contactNo = contactNo;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

}
