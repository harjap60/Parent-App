package com.cmpt276.parentapp.model;

/**
 * Child class - This will store the information of the child
 * currently it's just the name
 */

public class Child {
    private String childName;

    public Child(String childName) {
        this.childName = childName;
    }

    public String getChildName() {
        return childName;
    }

    public void setChildName(String childName) {
        this.childName = childName;
    }
}