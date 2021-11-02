/**
 * ChildManager class - This class stores the information
 * of all the children currently in the list
 */
package com.cmpt276.parentapp.model;

import java.util.ArrayList;
import java.util.List;

public class ChildManager {
    private List<Child> children = new ArrayList<>();

    private static ChildManager instance;
    private ChildManager(){}

    public static ChildManager getInstance(){
        if(instance == null){
            instance = new ChildManager();
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
