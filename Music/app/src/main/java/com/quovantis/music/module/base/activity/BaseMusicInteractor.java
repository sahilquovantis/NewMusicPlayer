package com.quovantis.music.module.base.activity;

import android.content.Context;

import com.quovantis.music.R;
import com.quovantis.music.models.SongsModel;
import com.quovantis.music.models.UserPlaylistModel;

import java.util.List;

import io.realm.Realm;

/**
 * Used for creating playlist or add songs to existing playlist.
 */
class BaseMusicInteractor implements IBaseMusic.Interactor {
    private Context mContext;

    BaseMusicInteractor(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public void createNewPlaylist(final List<SongsModel> songsList, final String name, final Listener listener) {
        final Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm1) {
                UserPlaylistModel userPlaylistModel = new UserPlaylistModel();
                userPlaylistModel.setPlaylistName(name);
                userPlaylistModel.setPlaylistId(getKey());
                userPlaylistModel.getPlaylist().addAll(songsList);
                realm.copyToRealm(userPlaylistModel);
                listener.onSuccess(true, mContext.getString(R.string.playlist_created));
            }
        });
    }

    @Override
    public void addSongsToExistingPlaylist(final List<SongsModel> songsList, final UserPlaylistModel model, final Listener listener) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                model.getPlaylist().addAll(songsList);
                realm.copyToRealmOrUpdate(model);
                listener.onSuccess(true, mContext.getString(R.string.playlist_created));
            }
        });
    }

    @Override
    public void deletePlaylist(final UserPlaylistModel model, final Listener listener) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                model.deleteFromRealm();
                listener.onSuccess(true, mContext.getString(R.string.playlist_deleted));
            }
        });
    }

    @Override
    public void renamePlaylist(final UserPlaylistModel model, final String newName, final IBaseMusic.Interactor.Listener listener) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                model.setPlaylistName(newName);
                realm.copyToRealmOrUpdate(model);
                listener.onSuccess(true, mContext.getString(R.string.playlist_renamed));
            }
        });
    }

    private int getKey() {
        Realm realm = Realm.getDefaultInstance();
        int key;
        try {
            key = realm.where(UserPlaylistModel.class).max("mPlaylistId").intValue() + 1;
        } catch (NullPointerException | ArrayIndexOutOfBoundsException ex) {
            key = 1;
        }
        return key;
    }
}
