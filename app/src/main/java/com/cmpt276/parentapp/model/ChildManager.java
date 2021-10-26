package com.cmpt276.parentapp.model;

import java.util.ArrayList;
import java.util.List;

public class ChildManager {
    private List<Child> children = new ArrayList<>();

    public void addKid(Child child){
        children.add(child);
    }

    public void removeChild(int index){
        children.remove(index);
    }

    public Child retrieveChildByIndex(int index){
        return children.get(index);
    }
}
