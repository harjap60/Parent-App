package com.cmpt276.parentapp.model;

import java.time.LocalDateTime;

public class SingleFlipInformation {
    private String childName;
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

    public String getChildName(){
        return this.childName;
    }

    public void setChildName(String childName) {
        this.childName = childName;
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

