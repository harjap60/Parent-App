package com.cmpt276.parentapp.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import android.graphics.Bitmap;
import android.net.Uri;

/**
 * Child class - This will store the information of the child
 * currently it's just the name.
 *
 * This is an entity for the room database and reflects the structure of the child table.
 *
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

    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    private Bitmap childImageBitmap= null;

    public Child(String name, int coinFlipOrder) {

        this.name = name;
        this.coinFlipOrder = coinFlipOrder;
    }

    public String getName() {

        return name;
    }

    public void setName(String name) {

        this.name = name;
    }

    public void setCoinFlipOrder(int coinFlipOrder) {

        this.coinFlipOrder = coinFlipOrder;
    }

    public int getCoinFlipOrder() {

        return coinFlipOrder;
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

    public Bitmap getChildImageBitmap() {
        return childImageBitmap;
    }

    public void setChildImageBitmap(Bitmap childImageBitmap) {
        this.childImageBitmap = childImageBitmap;
    }
}

