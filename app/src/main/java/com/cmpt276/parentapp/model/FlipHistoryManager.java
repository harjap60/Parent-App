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
            if (getFlip(i).getChild() == child) {
                history.remove(getFlip(i));
                i--;
                // i-- is done because once we remove an item from array list, all the elements
                // after that index are shifted up by one and the size is also decreased, so to
                // overcome this problem, we check at the same index again to make sure all the
                // instances of that element have been deleted
            }
        }
//        PrefConfig.writeFlipHistoryInPref(context, history);
    }

    public int getCurrentFlipIndex(ChildManager childManager) {
        if (childManager.size() == 0) {
            return -1;
        } else {
            if (size() == 0) {    // No one has flipped a coin yet
                return 0;
            } else {
                // get the last child to flip the coin
                Child child = history.get(size() - 1).getChild();

                // if a new child is added after a bunch of flips when no child was added,
                // the round robin should start from 0
                if (child == null) {
                    return 0;
                }
                // get the index of the child from the childManager
                int index = childManager.getChildIndex(child);

                // increment the index and return it
                index = (index + 1) % childManager.size();
                // this is done to keep the round robin going for the coin flip

                return index;
            }
        }
    }

    public int getPreviousFlipIndex(ChildManager childManager) {
        if (childManager.size() == 0) {
            return -1;
        } else {
            if (size() == 0) {    // No one has flipped a coin yet
                return -1;
            } else {
                // get the last child to flip the coin
                Child child = history.get(size() - 1).getChild();

                // To handle null pointer exception when a new child is added after
                // a bunch of flips when no child was added
                if (child == null) {
                    return -1;
                }

                // get the index of the child from the childManger
                // return the index of the child
                return childManager.getChildIndex(child);
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

    public void setHistory(List<CoinFlip> history) {
        this.history = history;
    }

    public int size() {
        return history.size();
    }
}
