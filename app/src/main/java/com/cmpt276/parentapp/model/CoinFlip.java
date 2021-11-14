package com.cmpt276.parentapp.model;

import static androidx.room.ForeignKey.CASCADE;
import static androidx.room.ForeignKey.SET_NULL;

import androidx.room.Delete;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import java.time.LocalDateTime;

/**
 * Store information about a flip
 * Stores:
 * name of child
 * the choice they made
 * Whether or not they won
 * The time the flip happened
 */
@Entity
public class CoinFlip {

    @PrimaryKey(autoGenerate = true)
    private int uid;

//    @ForeignKey(entity = Child.class,
//            parentColumns = "uid",
//            childColumns = "childId",
//            onDelete = CASCADE)
    private final int childId;

    private final Choice choice;

    private final boolean isWinner;

    private final LocalDateTime date;

    public CoinFlip(int childId, Choice choice, boolean isWinner, LocalDateTime date) {
        this.childId = childId;
        this.choice = choice;
        this.isWinner = isWinner;
        this.date = date;
    }

    public int getChildId() {
        return this.childId;
    }

    public Choice getChoice() {
        return this.choice;
    }

    public boolean isWinner() {

        return this.isWinner;
    }

    public LocalDateTime getDate() {

        return this.date;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public enum Choice {HEADS, TAILS}

}
