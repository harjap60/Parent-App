package com.cmpt276.parentapp.model;

import java.time.LocalDateTime;

public class CoinFlip {
    private int childNameIndex;
    private String choice;
    private boolean isWinner;
    private LocalDateTime flipTime;

    public String getChoice() {
        return this.choice;
    }

    public void setChoice(String choice) {
        this.choice = choice;
    }

    public void startFlip(){
        this.flipTime = LocalDateTime.now();
    }

    public int getChildNameIndex(){
        return this.childNameIndex;
    }
    public void setChildNameIndex(int childNameIndex) {
        this.childNameIndex = childNameIndex;
    }

    public boolean isWinner(){
        return this.isWinner;
    }

    public void setIsWinner(boolean winner) {
        this.isWinner = winner;
    }

    public LocalDateTime getFlipTime(){
        return this.flipTime;
    }
}

