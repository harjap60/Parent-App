package com.cmpt276.parentapp.model;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

/**
 * Store history of all flips done.
 * Contains list of CoinFlip objects
 */

public class FlipHistoryManager {
    private List<CoinFlip> history;

    private static FlipHistoryManager instance;

    private FlipHistoryManager(Context context) {

        //Reads flip history data from shared preferences
        history = PrefConfig.readFlipHistoryFromPref(context);

        //If history doesn't exist in shared preferences create new one
        if (history == null) {

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
                history.remove(getFlip(i));
                i--;
                // i-- because once we remove an item list, all the elements are shifted up by one
                // and the size decreased
            }
        }
    }

    public String getCurrentChild(ChildManager childManager) {
        if (childManager.size() == 0) {
            return "";
        }
        if (size() == 0) {
            // if there are children but the flip history is empty
            return childManager.getChild(0).getChildName();
        }
        String name = history.get(size() - 1).getChild().getChildName();
        for (int i = 0; i < childManager.size(); i++) {
            Child child = childManager.getChild(i);
            if (child.getChildName().equals(name)) {
                return childManager.getChild((i + 1) % childManager.size()).getChildName();
            }
        }
        return "";
    }

    public String getPreviousChild(ChildManager childManager) {
        if (childManager.size() == 0) {
            return "";
        } else {
            if (size() == 0) {
                return "";
            } else {
                return history.get(size() - 1).getChild().getChildName();
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
