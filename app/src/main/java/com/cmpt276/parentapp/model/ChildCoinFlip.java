package com.cmpt276.parentapp.model;

import androidx.room.DatabaseView;

import java.time.LocalDateTime;

@DatabaseView("SELECT cf.date, cf.choice, cf.isWinner, c.name as childName, cf.uid as coinFlipUid from CoinFlip cf LEFT OUTER JOIN Child c on c.uid = cf.childId")
public class ChildCoinFlip {
    private int coinFlipUid;
    private LocalDateTime date;
    private CoinFlip.Choice choice;
    private Boolean isWinner;
    private String childName;

    public ChildCoinFlip(int coinFlipUid, LocalDateTime date, CoinFlip.Choice choice, Boolean isWinner, String childName) {
        this.coinFlipUid = coinFlipUid;
        this.date = date;
        this.choice = choice;
        this.isWinner = isWinner;
        this.childName = childName;
    }

    public int getCoinFlipUid() {
        return coinFlipUid;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public CoinFlip.Choice getChoice() {
        return choice;
    }

    public Boolean isWinner() {
        return isWinner;
    }

    public String getChildName() {
        return childName;
    }
}