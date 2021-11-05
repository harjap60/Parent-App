package com.cmpt276.parentapp.model;

import java.util.ArrayList;

public class HistoryOfFlips {
    ArrayList<SingleFlipInformation> history = new ArrayList<>();

    int index = 0;

    private static HistoryOfFlips instance;
    private HistoryOfFlips(){}

    public static HistoryOfFlips getInstance(){
        if(instance == null){
            instance = new HistoryOfFlips();
        }
        return instance;
    }

    public void setFlipIndex(int i){
        index = i;
    }

    public int getFlipIndex(){
        return index;
    }

    public void addFlip(SingleFlipInformation flip){
        history.add(flip);
    }

    public SingleFlipInformation getFlip(int i){
        return history.get(i);
    }

    public ArrayList<SingleFlipInformation> getFullHistory() {
        return history;
    }
}
