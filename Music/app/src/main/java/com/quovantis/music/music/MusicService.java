package com.quovantis.music.music;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaDescription;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.widget.CheckBox;

import com.quovantis.music.R;
import com.quovantis.music.constants.AppConstants;
import com.quovantis.music.constants.IntentKeys;
import com.quovantis.music.helper.LoggerHelper;
import com.quovantis.music.helper.NotificationHelper;
import com.quovantis.music.models.NotificationModel;

/**
 * Music Service class which runs in background even when app is closed.
 */

public class MusicService extends Service implements PlayBackManager.PlaybackServiceCallback {
    private static final int NOTIFICATION_ID = 1;
    private Binder mBinder;
    private PlayBackManager mPlaybackManager;
    private MediaSessionCompat mMediaSession;
    private NotificationHelper mNotificationHelper;
    private Notification mNotification;
    private NotificationManager mNotificationManager;
    public static boolean sIsServiceDestroying;
    public static boolean sIsNotificationCreated;

    @Override
    public void onCreate() {
        super.onCreate();
        LoggerHelper.d("Music Service on Create called");
        sIsServiceDestroying = false;
        sIsNotificationCreated = false;
        mBinder = new ServiceBinder();
        LocalPlayback playback = new LocalPlayback(this);
        mPlaybackManager = new PlayBackManager(this, this, playback);
        mMediaSession = new MediaSessionCompat(this, AppConstants.MEDIA_SESSION_TAG);
        mMediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS
                | MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
        mMediaSession.setCallback(mPlaybackManager.getMediaSessionCallback());
        mPlaybackManager.updatePlaybackStateChanged();
        mNotificationHelper = new NotificationHelper(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LoggerHelper.d("Music Service OnStartCommand");
        if (intent != null && intent.getAction() != null) {
            String action = intent.getAction();
            switch (action) {
                case IntentKeys.INTENT_ACTION_PAUSE:
                    mPlaybackManager.handlePauseRequest();
                    break;
                case IntentKeys.INTENT_ACTION_STOP:
                    sIsServiceDestroying = true;
                    mPlaybackManager.handleStopRequest();
                    break;
                case IntentKeys.INTENT_ACTION_NEXT:
                    mPlaybackManager.handleSkipToNextRequest();
                    break;
                case IntentKeys.INTENT_ACTION_PREVIOUS:
                    mPlaybackManager.handleSkipToPreviousRequest();
                    break;
                case IntentKeys.INTENT_ACTION_PLAY:
                    mPlaybackManager.handlePlayRequest();
                    break;
            }
        }
        return START_STICKY;
    }

    /**
     * @return Returns the Media Session Token
     */
    public MediaSessionCompat.Token getMediaSessionToken() {
        return mMediaSession == null ? null : mMediaSession.getSessionToken();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mMediaSession != null) {
            mMediaSession.release();
            mMediaSession = null;
        }
        removeNotifications();
        sIsServiceDestroying = false;
        sIsNotificationCreated = false;
        mNotificationManager = null;
        mNotification = null;
        mNotificationHelper = null;
        mPlaybackManager = null;
        LoggerHelper.d("Music Service Destroyed");
    }

    @Override
    public void onPlaybackStart() {
        mMediaSession.setActive(true);
    }

    @Override
    public void onNotificationRequired(int state) {
        sIsNotificationCreated = true;
        NotificationModel model = new NotificationModel();
        MediaMetadataCompat metadata = mPlaybackManager.getCurrentMetaData();
        if (metadata == null) {
            return;
        }
        MediaDescriptionCompat description = metadata.getDescription();
        if (description == null) {
            return;
        }
        CharSequence title = description.getTitle();
        CharSequence artist = description.getSubtitle();
        if (title == null) {
            title = " ";
        }
        if (artist == null) {
            artist = " ";
        }
        model.setTitle(title.toString());
        model.setArtist(artist.toString());
        model.setBitmap(description.getIconBitmap());

        switch (state) {
            case PlaybackStateCompat.STATE_PLAYING:
                mNotification = mNotificationHelper.createNotification(model, getMediaSessionToken(), R.drawable.ic_action_pause, "Pause", IntentKeys.INTENT_ACTION_PAUSE);
                if (mNotification != null) {
                    startForeground(NOTIFICATION_ID, mNotification);
                }
                sIsNotificationCreated = true;
                break;
            case PlaybackStateCompat.STATE_PAUSED:
                stopForeground(true);
                mNotification = mNotificationHelper.createNotification(model, getMediaSessionToken(), R.drawable.ic_action_play, "Play", IntentKeys.INTENT_ACTION_PLAY);
                mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                mNotificationManager.notify(1, mNotification);
                sIsNotificationCreated = true;
                break;
        }
    }

    @Override
    public void onNotificationHide() {
        removeNotifications();
        sIsNotificationCreated = false;
    }

    @Override
    public void onPlaybackStop() {
        mMediaSession.setActive(false);
    }

    @Override
    public void onPlaybackStateUpdated(PlaybackStateCompat newState) {
        if (mMediaSession != null)
            mMediaSession.setPlaybackState(newState);
        LoggerHelper.d("New State : " + newState.toString());
        if (newState.getState() == PlaybackStateCompat.STATE_STOPPED) {
            removeNotifications();
            Intent intent = new Intent();
            intent.setAction(IntentKeys.CLOSE_MUSIC_ACTION);
            sendBroadcast(intent);
            mMediaSession.setActive(false);
            stopSelf();
        }
    }

    private void removeNotifications() {
        if (mNotificationManager != null) {
            mNotificationManager.cancelAll();
        }
        stopForeground(true);
    }

    @Override
    public void updateMetaData(MediaMetadataCompat metadata) {
        mMediaSession.setMetadata(metadata);
    }

    public class ServiceBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        LoggerHelper.d("On Task Removed Called");
        mPlaybackManager.handleStopRequest();
    }
}