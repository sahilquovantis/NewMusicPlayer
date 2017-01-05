package com.quovantis.music.module.base.activity;

import android.graphics.Bitmap;

import com.quovantis.music.models.SongsModel;

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

        void showOptionsDialog(List<SongsModel> songsList,String title);
    }
}
