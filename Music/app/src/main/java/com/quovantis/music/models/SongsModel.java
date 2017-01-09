package com.quovantis.music.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

import io.realm.RealmObject;

/**
 * Song Model contains all information about song
 */

public class SongsModel extends RealmObject implements Parcelable {
    private String mSongID;
    private String mSongTitle;
    private String mSongArtist;
    private String mSongPath;
    private long mAlbumId;

    public SongsModel() {

    }

    protected SongsModel(Parcel in) {
        mSongID = in.readString();
        mSongTitle = in.readString();
        mSongArtist = in.readString();
        mSongPath = in.readString();
        mAlbumId = in.readLong();
    }

    public static final Creator<SongsModel> CREATOR = new Creator<SongsModel>() {
        @Override
        public SongsModel createFromParcel(Parcel in) {
            return new SongsModel(in);
        }

        @Override
        public SongsModel[] newArray(int size) {
            return new SongsModel[size];
        }
    };

    public long getAlbumId() {
        return mAlbumId;
    }

    public void setAlbumId(long albumId) {
        mAlbumId = albumId;
    }

    public String getSongID() {
        return mSongID;
    }

    public void setSongID(String mSongID) {
        this.mSongID = mSongID;
    }

    public String getSongTitle() {
        return mSongTitle;
    }

    public void setSongTitle(String mSongTitle) {
        this.mSongTitle = mSongTitle;
    }

    public String getSongArtist() {
        return mSongArtist;
    }

    public void setSongArtist(String mSongArtist) {
        this.mSongArtist = mSongArtist;
    }

    public String getSongPath() {
        return mSongPath;
    }

    public void setSongPath(String mSongPath) {
        this.mSongPath = mSongPath;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mSongID);
        parcel.writeString(mSongTitle);
        parcel.writeString(mSongArtist);
        parcel.writeString(mSongPath);
        parcel.writeLong(mAlbumId);
    }
}