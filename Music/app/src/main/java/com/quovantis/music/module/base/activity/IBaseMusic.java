package com.quovantis.music.module.base.activity;

import android.graphics.Bitmap;

import com.quovantis.music.models.SongsModel;
import com.quovantis.music.models.UserPlaylistModel;

import java.util.List;

/**
 * Interface Base Activity (MVP)
 */
interface IBaseMusic {
    interface View {
        void updateUi(String title, String artist, Bitmap bitmap);

        void updateState(int state);

        void showMusicLayout();

        void hideMusicLayout();

        void showProgressDialog(String message);

        void hideProgressDialog();
    }

    interface Presenter {
        void bindService();

        void unbindService();

        void playSong();

        void pauseSong();

        void destroy();

        void previousSong();

        void nextSong();

        void seekTo(int pos);

        void toggleRequest();

        void showOptionsDialog(List<SongsModel> songsList, String title);

        void showPlaylistOptionsDialog(UserPlaylistModel model);
    }

    interface Interactor {
        void createNewPlaylist(List<SongsModel> songsList, String name, IBaseMusic.Interactor.Listener listener);

        void addSongsToExistingPlaylist(List<SongsModel> songsList, UserPlaylistModel model, IBaseMusic.Interactor.Listener listener);

        void deletePlaylist(UserPlaylistModel model, IBaseMusic.Interactor.Listener listener);

        void renamePlaylist(final UserPlaylistModel model, final String newName, final IBaseMusic.Interactor.Listener listener);

        interface Listener {
            void onSuccess(boolean isSuccess, String msg);
        }
    }
}