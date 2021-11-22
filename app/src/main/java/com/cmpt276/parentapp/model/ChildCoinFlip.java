package com.cmpt276.parentapp.model;

import androidx.room.Embedded;

/**
 * This is a helper class for room database to represent
 * a relationship between a child and a coin flip.
 */
public class ChildCoinFlip {

    @Embedded
    private Child child;

    @Embedded(prefix = "cf_")
    private CoinFlip coinFlip;

    public ChildCoinFlip(Child child, CoinFlip coinFlip) {
        this.child = child;
        this.coinFlip = coinFlip;
    }

    public CoinFlip getCoinFlip() {

        return coinFlip;
    }

    public Child getChild() {

        return child;
    }
}