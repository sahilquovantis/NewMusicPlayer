package com.quovantis.music.models;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * User Playlist
 */

public class UserPlaylistModel extends RealmObject {
    @PrimaryKey
    private long mPlaylistId;
    private String mPlaylistName;
    private RealmList<SongsModel> mPlaylist = new RealmList<>();

    public long getPlaylistId() {
        return mPlaylistId;
    }

    public void setPlaylistId(long mPlaylistId) {
        this.mPlaylistId = mPlaylistId;
    }

    public String getPlaylistName() {
        return mPlaylistName;
    }

    public void setPlaylistName(String mPlaylistName) {
        this.mPlaylistName = mPlaylistName;
    }

    public RealmList<SongsModel> getPlaylist() {
        return mPlaylist;
    }

    public void setPlaylist(RealmList<SongsModel> mPlaylist) {
        this.mPlaylist = mPlaylist;
    }
}
