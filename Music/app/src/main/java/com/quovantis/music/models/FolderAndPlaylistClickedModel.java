package com.quovantis.music.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * When folder or playlist is clicked, this model is used for passing information like songs list and title.
 */

public class FolderAndPlaylistClickedModel implements Parcelable {
    private List<SongsModel> mSongsList;
    private String mTitle;

    public FolderAndPlaylistClickedModel() {
    }


    private FolderAndPlaylistClickedModel(Parcel in) {
        mTitle = in.readString();
        mSongsList = in.createTypedArrayList(SongsModel.CREATOR);
    }

    public static final Creator<FolderAndPlaylistClickedModel> CREATOR = new Creator<FolderAndPlaylistClickedModel>() {
        @Override
        public FolderAndPlaylistClickedModel createFromParcel(Parcel in) {
            return new FolderAndPlaylistClickedModel(in);
        }

        @Override
        public FolderAndPlaylistClickedModel[] newArray(int size) {
            return new FolderAndPlaylistClickedModel[size];
        }
    };

    public List<SongsModel> getSongsList() {
        return mSongsList;
    }

    public void setSongsList(List<SongsModel> mSongsList) {
        this.mSongsList = mSongsList;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mTitle);
        parcel.writeTypedList(mSongsList);
    }
}
