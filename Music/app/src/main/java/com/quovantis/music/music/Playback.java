package com.quovantis.music.music;

import android.support.v4.media.MediaMetadataCompat;

/**
 * Playback interface which includes all the operations related to media player.
 */

public interface Playback {
    void stop();

    void setMediaInPauseState(MediaMetadataCompat mediaMetadata);

    void play(MediaMetadataCompat mediaMetaData);

    void setNullMetaDataAndHideMusicLayout(MediaMetadataCompat metaData);

    void pause();

    boolean isPlaying();

    int getState();

    void seekTo(int position);

    int getCurrentStreamPosition();

    void setCallback(Callback callback);

    interface Callback {
        void onCompletion();

        void onProgress(long currentProgress, long totalProgress);

        void onPlaybackStateChanged(int state);
    }
}