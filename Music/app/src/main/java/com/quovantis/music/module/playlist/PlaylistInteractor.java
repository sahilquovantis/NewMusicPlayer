package com.quovantis.music.module.playlist;

import com.quovantis.music.models.UserPlaylistModel;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Used for getting user Playlist
 */

public class PlaylistInteractor implements IPlaylist.Interactor {
    @Override
    public void getUserPlaylist(Listener listener) {
        Realm realm = Realm.getDefaultInstance();
        RealmResults<UserPlaylistModel> list = realm.where(UserPlaylistModel.class).
                greaterThan("mPlaylistId", 0).findAll();
        ArrayList<UserPlaylistModel> lists = new ArrayList<>();
        lists.addAll(list);
        listener.onGettingPlaylist(lists);
    }
}
