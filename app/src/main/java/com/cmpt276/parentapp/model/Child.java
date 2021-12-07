package com.cmpt276.parentapp.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Child class - This will store the information of the child
 * currently it's just the name.
 * <p>
 * This is an entity for the room database and reflects the structure of the child table.
 * <p>
 * Room assigns a unique value to UID for the child that can be used to find the
 * child in the database.
 */
@Entity
public class Child {

    @PrimaryKey(autoGenerate = true)
    private Long childId;

    @ColumnInfo(name = "name")
    private String name;

    private int coinFlipOrder;

    private String imagePath;

    public Child(String name, int coinFlipOrder, String imagePath) {
        this.name = name;
        this.coinFlipOrder = coinFlipOrder;
        this.imagePath = imagePath;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCoinFlipOrder() {
        return coinFlipOrder;
    }

    public void setCoinFlipOrder(int coinFlipOrder) {
        this.coinFlipOrder = coinFlipOrder;
    }

    public Long getChildId() {
        return childId;
    }

    public void setChildId(Long childId) {
        this.childId = childId;
    }

    @NonNull
    @Override
    public String toString() {
        return "Child{" +
                "childId=" + childId +
                ", name='" + name + '\'' +
                ", coinFlipOrder=" + coinFlipOrder +
                ", imagePath='" + imagePath + '\'' +
                '}';
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}

