package com.quovantis.music.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Folder Model
 */

public class FoldersModel implements Serializable{
    private long mAlbumId;
    private String mPath;
    private String mDirectory;
    private List<SongsModel> mSongs;

    public FoldersModel() {
        mSongs = new ArrayList<>();
    }

    public void addSong(SongsModel songsModel) {
        mSongs.add(songsModel);
    }

    public long getAlbumId() {
        return mAlbumId;
    }

    public void setAlbumId(long mAlbumId) {
        this.mAlbumId = mAlbumId;
    }

    public String getPath() {
        return mPath;
    }

    public void setPath(String mPath) {
        this.mPath = mPath;
    }

    public String getDirectory() {
        return mDirectory;
    }

    public void setDirectory(String mDirectory) {
        this.mDirectory = mDirectory;
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null && (obj instanceof FoldersModel) && this.getPath().equalsIgnoreCase(((FoldersModel) obj).getPath());
    }

    public List<SongsModel> getSongs() {
        return mSongs;
    }

}