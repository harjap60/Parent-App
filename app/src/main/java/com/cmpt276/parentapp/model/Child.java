package com.cmpt276.parentapp.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Child class - This will store the information of the child
 * currently it's just the name
 */

@Entity
public class Child {

    @PrimaryKey(autoGenerate = true)
    public int uid;

    @ColumnInfo(name = "name")
    private String name;

    public Child(String name) {

        this.name = name;
    }

    public String getName() {

        return name;
    }

    public void setName(String name) {

        this.name = name;
    }

    public int getUid() {
        return uid;
    }
}

