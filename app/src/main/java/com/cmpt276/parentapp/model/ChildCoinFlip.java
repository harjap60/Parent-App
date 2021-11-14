package com.cmpt276.parentapp.model;

import androidx.room.Embedded;
import androidx.room.Relation;

public class ChildCoinFlip {
    @Embedded
    Child child;

    @Relation(
            parentColumn = "uid",
            entityColumn = "childId",
            projection = {"uid", "choice", "isWinner", "date"}
    )
    CoinFlip coinFlip;

    public Child getChild() {
        return child;
    }

    public CoinFlip getCoinFlip() {
        return coinFlip;
    }
}