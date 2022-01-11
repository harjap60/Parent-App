package main.model;

import androidx.room.Embedded;

/**
 * This is a helper class for room database to represent
 * a relationship between a child and a coin flip.
 */
public class ChildCoinFlip {

    @Embedded
    public Child child;

    @Embedded(prefix = "cf_")
    public CoinFlip coinFlip;
}