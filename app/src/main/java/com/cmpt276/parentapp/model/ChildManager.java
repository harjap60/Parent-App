/**
 * ChildManager class - This class stores the information
 * of all the children currently in the list
 */
package com.cmpt276.parentapp.model;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

public class ChildManager {
    private List<Child> children;

    private static ChildManager instance;

    private ChildManager(Context context){

        // the following code reads the data from shared preferences
        // which contains the list of children
        children = PrefConfig.readChildListFromPref(context);
        if(children == null) {
            // if the 'children' variable(list) is null, it means this is the first time that
            // the user is running the app and 'children' will have a value of null (empty list), so
            // - if the 'children' variable(list) is null, then make an empty list of children
            // - if the 'children' variable(list) is not null, then the 'children' variable(list)
            // now consists of the list of children saved from the previous run

            children = new ArrayList<>();
        }
    }

    public static ChildManager getInstance(Context context){
        if(instance == null){
            instance = new ChildManager(context);
        }
        return instance;
    }

    public void addChild(Child child){
        children.add(child);
    }

    public int getChildIndex(Child c){
        for(int i = 0; i < size(); i++){
            Child child = getChild(i);
            if(child.equals(c)){
                return i;
            }
        }
        // TODO: need to replace -1 with size();
        return -1;
    }

    public void removeChild(int index){
        children.remove(index);
    }

    public Child getChild(int index){
        return children.get(index);
    }

    public Child getChildFromName(String name){
        if(name.equals("")){
            return null;
        }
        for(Child c: children){
            if(c.getChildName().equals(name)){
                return c;
            }
        }
        return null;
    }

    public List<Child> getAllChildren(){
        return children;
    }

    public int size(){
        return children.size();
    }

    public void setChildren(List<Child> children){
        this.children = children;
    }
}
