package com.cmpt276.parentapp.model;

import static androidx.room.ForeignKey.CASCADE;

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
@Entity(foreignKeys = {
        @ForeignKey(
                entity = Child.class,
                parentColumns = "uid",
                childColumns = "childId",
                onDelete = CASCADE
        )
})
public class CoinFlip {


    final int childId;

    private final Choice choice;
    private final boolean isWinner;
    private final LocalDateTime date;
    @PrimaryKey(autoGenerate = true)
    private int uid;

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

    @Override
    public String toString() {
        return "CoinFlip{" +
                "uid=" + uid +
                ", childId=" + childId +
                ", choice=" + choice +
                ", isWinner=" + isWinner +
                ", date=" + date +
                '}';
    }

    public enum Choice {HEADS, TAILS}

}
