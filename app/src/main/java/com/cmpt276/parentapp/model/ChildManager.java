package com.cmpt276.parentapp.model;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

/**
 * ChildManager class - This class stores the information
 * of all the children currently in the list
 */
public class ChildManager {
    private List<Child> children;

    private static ChildManager instance;

    private ChildManager(Context context) {

        //reads the list of children from shared preferences
        children = PrefConfig.readChildListFromPref(context);

        //If list null create one
        if (children == null) {
            children = new ArrayList<>();
        }
    }

    public static ChildManager getInstance(Context context) {
        if (instance == null) {
            instance = new ChildManager(context);
        }
        return instance;
    }

    public void addChild(Child child) {
        children.add(child);
    }

    public void removeChild(int index) {
        children.remove(index);
    }

    public Child getChild(int index) {
        try {
            return children.get(index);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("There are no children in the list");
        }

    }

    public Child getChildFromName(String name) {
        if (name.equals("")) {
            return null;
        }
        for (Child c : children) {
            if (c.getChildName().equals(name)) {
                return c;
            }
        }
        return null;
    }

    public List<Child> getAllChildren() {
        return children;
    }

    public int size() {
        return children.size();
    }

    public void setChildren(List<Child> children) {
        this.children = children;
    }
}
