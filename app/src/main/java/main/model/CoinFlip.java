package main.model;

import static androidx.room.ForeignKey.CASCADE;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import java.time.LocalDateTime;

/**
 * Store information about a flip
 * Stores:
 * Child ID
 * the choice (enum)
 * Whether or not they won
 * The time the flip happened
 * <p>
 * This class represents an entity in the Room database. It represents the structure of the
 * CoinFlip table in Room.
 * <p>
 * The ChildId represents a reference to a child.
 */
@Entity(foreignKeys = {
        @ForeignKey(
                entity = Child.class,
                parentColumns = "childId",
                childColumns = "childId",
                onDelete = CASCADE
        )
})
public class CoinFlip {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(index = true)
    private long coinFlipId;

    @ColumnInfo(index = true)
    private final long childId;

    private final Choice choice;
    private final Boolean isWinner;
    private final LocalDateTime date;


    public CoinFlip(long childId, Choice choice, Boolean isWinner, LocalDateTime date) {
        this.childId = childId;
        this.choice = choice;
        this.isWinner = isWinner;
        this.date = date;
    }

    public long getChildId() {
        return this.childId;
    }

    public Choice getChoice() {
        return this.choice;
    }

    public Boolean isWinner() {
        return this.isWinner;
    }

    public LocalDateTime getDate() {
        return this.date;
    }

    public long getCoinFlipId() {
        return coinFlipId;
    }

    public void setCoinFlipId(long coinFlipId) {
        this.coinFlipId = coinFlipId;
    }

    @NonNull
    @Override
    public String toString() {
        return "CoinFlip{" +
                "uid=" + coinFlipId +
                ", childId=" + childId +
                ", choice=" + choice +
                ", isWinner=" + isWinner +
                ", date=" + date +
                '}';
    }

    public enum Choice {HEADS, TAILS}
}
