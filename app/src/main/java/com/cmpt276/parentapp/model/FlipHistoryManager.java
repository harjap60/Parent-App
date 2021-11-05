package com.cmpt276.parentapp.model;

import java.util.ArrayList;

public class FlipHistoryManager {
    ArrayList<CoinFlip> history = new ArrayList<>();

    int currentFlipIndex = 0;
    int previousFlipIndex = 0;

    private static FlipHistoryManager instance;
    private FlipHistoryManager(){}

    public static FlipHistoryManager getInstance(){
        if(instance == null){
            instance = new FlipHistoryManager();
        }
        return instance;
    }

    public void setCurrentFlipIndex(int i){
        currentFlipIndex = i;
    }

    public int getCurrentFlipIndex(){
        return currentFlipIndex;
    }
    public void setPreviousFlipIndex(int i){
        previousFlipIndex = i;
    }

    public int getPreviousFlipIndex(){
        return previousFlipIndex;
    }

    public void addFlip(CoinFlip flip){
        history.add(flip);
    }

    public CoinFlip getFlip(int i){
        return history.get(i);
    }

    public ArrayList<CoinFlip> getFullHistory() {
        return history;
    }
}
