package com.quovantis.music.module.base.activity;

import android.graphics.Bitmap;

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
    }
}
