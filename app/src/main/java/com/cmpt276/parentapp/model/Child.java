package com.cmpt276.parentapp.model;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
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
    private int childId;

    @ColumnInfo(name = "name")
    private String name;

    private int coinFlipOrder;

    private Bitmap image;

    public Child(String name, int coinFlipOrder, Bitmap image) {
        this.name = name;
        this.coinFlipOrder = coinFlipOrder;
        this.image = image;
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

    public int getChildId() {

        return childId;
    }

    public void setChildId(int childId) {

        this.childId = childId;
    }

    @NonNull
    @Override
    public String toString() {
        return "Child{" +
                "uid=" + childId +
                ", name='" + name + '\'' +
                ", coinFlipOrder=" + coinFlipOrder +
                '}';
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap childImageBitmap) {
        this.image = childImageBitmap;
    }
}

