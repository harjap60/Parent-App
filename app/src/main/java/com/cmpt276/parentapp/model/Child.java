package com.cmpt276.parentapp.model;

import android.graphics.Bitmap;
import android.net.Uri;

/**
 * Child class - This will store the information of the child
 * currently it's just the name
 */

public class Child {
    private String childName;
    private Bitmap childImageBitmap= null;

    public Child () {}

    public Child(String childName) {
        this.childName = childName;
    }

    public String getChildName() {
        return childName;
    }

    public void setChildName(String childName) {
        this.childName = childName;
    }

    public Bitmap getChildImageBitmap() {
        return childImageBitmap;
    }

    public void setChildImageBitmap(Bitmap childImageBitmap) {
        this.childImageBitmap = childImageBitmap;
    }
}