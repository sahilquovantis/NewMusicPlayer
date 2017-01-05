package com.quovantis.music.music;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import com.quovantis.music.constants.IntentKeys;
import com.quovantis.music.helper.MusicHelper;

/**
 * Manges the interaction with service and music queue
 */
class PlayBackManager implements Playback.Callback {

    private MediaSessionCompat.Callback mMediaCallback;
    private PlaybackServiceCallback mServiceCallback;
    private Playback mPlayback;
    private Context mContext;
    private MediaMetadataCompat mCurrentMetaData;

    PlayBackManager(Context context, PlaybackServiceCallback serviceCallback, Playback playback) {
        mServiceCallback = serviceCallback;
        mPlayback = playback;
        mContext = context;
        mMediaCallback = new MediaSessionCallback();
        mPlayback.setCallback(this);
    }

    MediaSessionCompat.Callback getMediaSessionCallback() {
        return mMediaCallback;
    }

    MediaMetadataCompat getCurrentMetaData() {
        return mCurrentMetaData;
    }

    //ToDo : If metadata is null, play next song
    void handlePlayRequest() {
        MediaMetadataCompat metadata = MusicHelper.getInstance().getCurrentMusic(mContext);
        mCurrentMetaData = metadata;
        if (metadata != null) {
            mServiceCallback.onPlaybackStart();
            mServiceCallback.updateMetaData(metadata);
            onProgress(0, 0);
            mPlayback.play(metadata);
        } else {
            mPlayback.setNullMetaDataAndHideMusicLayout(null);
        }
    }

    void handlePauseRequest() {
        if (mPlayback.isPlaying()) {
            mServiceCallback.onPlaybackStop();
            mPlayback.pause();
        }
    }

    void handleSkipToNextRequest() {
        MusicHelper.getInstance().skipToNext();
        handlePlayRequest();
    }

    void handleSkipToPreviousRequest() {
        MusicHelper.getInstance().skipToPrevious();
        handlePlayRequest();
    }

    void handleStopRequest() {
        mPlayback.stop();
    }

    @Override
    public void onCompletion() {
        MusicHelper.getInstance().skipToNext();
        if (MusicHelper.getInstance().shouldBeInPauseState()) {
            MediaMetadataCompat metadata = MusicHelper.getInstance().getCurrentMusic(mContext);
            mCurrentMetaData = metadata;
            if (metadata != null) {
                mServiceCallback.onPlaybackStop();
                mServiceCallback.updateMetaData(metadata);
                mPlayback.setMediaInPauseState(metadata);
            } else {
                mPlayback.setNullMetaDataAndHideMusicLayout(null);
            }
        } else {
            handlePlayRequest();
        }
    }

    @Override
    public void onPlaybackStateChanged(int s) {
        updatePlaybackStateChanged();
    }

    void updatePlaybackStateChanged() {
        int state = mPlayback.getState();
        long position = mPlayback.getCurrentStreamPosition();
        PlaybackStateCompat.Builder playbackState = new PlaybackStateCompat.Builder()
                .setState(state, position, 1.0f, SystemClock.elapsedRealtime());
        mServiceCallback.onPlaybackStateUpdated(playbackState.build());
        if (state == PlaybackStateCompat.STATE_PLAYING ||
                state == PlaybackStateCompat.STATE_PAUSED) {
            mServiceCallback.onNotificationRequired(state);
        } else if (state == PlaybackStateCompat.STATE_NONE) {
            mServiceCallback.onNotificationHide();
            mServiceCallback.onPlaybackStop();
        }
    }

    private class MediaSessionCallback extends MediaSessionCompat.Callback {
        @Override
        public void onPause() {
            super.onPause();
            handlePauseRequest();
        }

        @Override
        public void onPlay() {
            super.onPlay();
            handlePlayRequest();
        }

        @Override
        public void onPlayFromMediaId(String mediaId, Bundle extras) {
            super.onPlayFromMediaId(mediaId, extras);
            handlePlayRequest();
        }

        @Override
        public void onSkipToNext() {
            super.onSkipToNext();
            handleSkipToNextRequest();
        }

        @Override
        public void onSkipToPrevious() {
            super.onSkipToPrevious();
            handleSkipToPreviousRequest();
        }

        @Override
        public void onCustomAction(String action, Bundle extras) {
            super.onCustomAction(action, extras);
        }

        @Override
        public boolean onMediaButtonEvent(Intent mediaButtonEvent) {
            return super.onMediaButtonEvent(mediaButtonEvent);
        }

        @Override
        public void onSeekTo(long pos) {
            super.onSeekTo(pos);
            mPlayback.seekTo((int) pos);
        }
    }

    @Override
    public void onProgress(long currentProgress, long totalProgress) {
        Intent intent = new Intent();
        intent.putExtra(IntentKeys.CURRENT_PROGRESS, currentProgress);
        intent.putExtra(IntentKeys.TOTAL_PROGRESS, totalProgress);
        intent.setAction(IntentKeys.UPDATE_PROGRESS);
        mContext.sendBroadcast(intent);
    }

    interface PlaybackServiceCallback {
        void onPlaybackStart();

        void onNotificationRequired(int state);

        void onPlaybackStop();

        void onNotificationHide();

        void onPlaybackStateUpdated(PlaybackStateCompat newState);

        void updateMetaData(MediaMetadataCompat metadata);
    }
}