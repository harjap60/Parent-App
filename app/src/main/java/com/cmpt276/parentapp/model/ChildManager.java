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
        children = PrefConfig.readListFromPref(context);
        if(children == null){
            // if the 'children' variable(list) is null, it means this is the first time that
            // the user is running the app and children will have a value of null (empty list), so
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

    public void removeChild(int index){
        children.remove(index);
    }

    public Child retrieveChildByIndex(int index){
        return children.get(index);
    }

    public List<Child> getAllChildren(){
        return children;
    }

    public  void setChildren(List<Child> children){
        this.children = children;
    }

    public int size(){
        return children.size();
    }
}
