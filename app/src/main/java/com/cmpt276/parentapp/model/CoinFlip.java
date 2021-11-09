package com.cmpt276.parentapp.model;

import java.time.LocalDateTime;

/**
 * Store information about a flip
 * Stores:
 * name of child
 * the choice they made
 * Whether or not they won
 * The time the flip happened
 */
public class CoinFlip {
    private Child child;
    private String choice;
    private boolean isWinner;
    private LocalDateTime flipTime;

    public void setChild(Child child) {
        this.child = child;
    }

    public Child getChild() {
        return this.child;
    }

    public String getChoice() {
        return this.choice;
    }

    public void setChoice(String choice) {
        this.choice = choice;
    }

    public void startFlip() {
        this.flipTime = LocalDateTime.now();
    }

    public boolean isWinner() {
        return this.isWinner;
    }

    public void setIsWinner(boolean winner) {
        this.isWinner = winner;
    }

    public LocalDateTime getFlipTime() {
        return this.flipTime;
    }
}

