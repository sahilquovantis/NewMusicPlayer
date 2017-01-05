package com.quovantis.music.models;

import android.graphics.Bitmap;

/**
 * Model used for creating notification
 */

public class NotificationModel {
    private String mTitle;
    private String mArtist;
    private Bitmap mBitmap;

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getArtist() {
        return mArtist;
    }

    public void setArtist(String mArtist) {
        this.mArtist = mArtist;
    }

    public Bitmap getBitmap() {
        return mBitmap;
    }

    public void setBitmap(Bitmap mBitmap) {
        this.mBitmap = mBitmap;
    }
}
