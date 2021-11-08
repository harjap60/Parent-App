package com.cmpt276.parentapp.model;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

public class FlipHistoryManager {
    private List<CoinFlip> history;

    private static FlipHistoryManager instance;

    private FlipHistoryManager(Context context) {

        // the following code reads the data from shared preferences
        // which contains the history of flips
        history = PrefConfig.readFlipHistoryFromPref(context);
        if (history == null) {
            // if the 'history' variable(list) is null, it means this is the first time that
            // the user is running the app and 'history' will have a value of null (empty list), so
            // - if the 'history' variable(list) is null, then make an empty list of flipsHistory
            // - if the 'children' variable(list) is not null, then the 'history' variable(list)
            // now consists of the history of flips saved from the previous run
            history = new ArrayList<>();
        }
    }

    public static FlipHistoryManager getInstance(Context context) {
        if (instance == null) {
            instance = new FlipHistoryManager(context);
        }
        return instance;
    }

    public void deleteFlipHistoryOfChild(Child child) {
        for (int i = 0; i < history.size(); i++) {
            if (getFlip(i).getChild().getChildName().equals(child.getChildName())) {
                // TODO: might also need to change to getFlip(i).getChild().equals(child);
                history.remove(getFlip(i));
                i--;
                // i-- is done because once we remove an item from array list, all the elements
                // after that index are shifted up by one and the size is also decreased, so to
                // overcome this problem, we check at the same index again to make sure all the
                // instances of that element have been deleted
            }
        }
    }

    public String getCurrentChild(ChildManager childManager){
        if(childManager.size() == 0){
            return "";
        }
        else{
            if(size() == 0){
                return childManager.getChild(0).getChildName();
            }
            else{
                String name = history.get(size() - 1).getChild().getChildName();
                for(int i = 0; i < childManager.size(); i++){
                    Child child = childManager.getChild(i);
                    if(child.getChildName().equals(name)){
                        return childManager.getChild((i+1)% childManager.size()).getChildName();
                    }
                }
                return "";
            }
        }
    }

    public String getPreviousChild(ChildManager childManager) {
        if (childManager.size() == 0) {
            return "";
        } else {
            if (size() == 0) {
                return "";
            } else {
                String name = history.get(size() - 1).getChild().getChildName();
                return name;
            }
        }
    }

    public void addFlip(CoinFlip flip) {
        history.add(flip);
    }

    public CoinFlip getFlip(int i) {
        return history.get(i);
    }

    public List<CoinFlip> getFullHistory() {
        return history;
    }

    public int size() {
        return history.size();
    }
}
