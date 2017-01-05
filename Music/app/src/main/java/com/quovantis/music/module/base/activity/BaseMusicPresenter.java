package com.quovantis.music.module.base.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import com.quovantis.music.dialogs.OptionsDialog;
import com.quovantis.music.helper.LoggerHelper;
import com.quovantis.music.helper.MusicHelper;
import com.quovantis.music.models.SongsModel;
import com.quovantis.music.music.MusicService;

import java.util.List;

/**
 * Base Activity Presenter, here binding and unbinding of service takes place.
 */

class BaseMusicPresenter implements IBaseMusic.Presenter, ServiceConnection, OptionsDialog.IOptionsDialogListener {

    private IBaseMusic.View mView;
    private Context mContext;
    private boolean mIsBounded;
    private MediaControllerCompat mMediaController;
    private OptionsDialog mDialog;

    BaseMusicPresenter(IBaseMusic.View mView, Context context) {
        this.mView = mView;
        mContext = context;
    }

    @Override
    public void bindService() {
        Intent intent = new Intent(mContext, MusicService.class);
        mContext.startService(intent);
        mContext.bindService(intent, this, Context.BIND_AUTO_CREATE);
        mIsBounded = true;
        LoggerHelper.d("Service starts bounding : " + mContext);
    }

    @Override
    public void unbindService() {
        if (mIsBounded) {
            mContext.unbindService(this);
            LoggerHelper.d("Service is unbounded : " + mContext);
        }
        mIsBounded = false;
    }

    @Override
    public void destroy() {
        mView = null;
    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        if (iBinder instanceof MusicService.ServiceBinder) {
            LoggerHelper.d("Service is bounded");
            try {
                mMediaController = new MediaControllerCompat(mContext, ((MusicService.ServiceBinder) iBinder).getService().getMediaSessionToken());
                mMediaController.registerCallback(mMediaCallback);
                updateUi(mMediaController.getMetadata());
                updateState(mMediaController.getPlaybackState());
            } catch (RemoteException e) {
                LoggerHelper.d("Error in Media Controller in base presenter");
            }
        }
    }

    private MediaControllerCompat.Callback mMediaCallback = new MediaControllerCompat.Callback() {
        @Override
        public void onMetadataChanged(MediaMetadataCompat metadata) {
            super.onMetadataChanged(metadata);
            updateUi(metadata);
        }

        @Override
        public void onPlaybackStateChanged(PlaybackStateCompat state) {
            super.onPlaybackStateChanged(state);
            updateState(state);
        }
    };

    private void updateUi(MediaMetadataCompat metaData) {
        if (!shouldShowControls()) {
            mView.hideMusicLayout();
            return;
        } else {
            mView.showMusicLayout();
        }
        LoggerHelper.d("Update MetaData called in base presenter");
        if (mView == null || metaData == null) {
            return;
        }
        MediaDescriptionCompat mediaDescription = metaData.getDescription();
        String title = (String) mediaDescription.getTitle();
        String artist = (String) mediaDescription.getSubtitle();
        if (title == null || artist == null)
            return;
        Bitmap bitmap = mediaDescription.getIconBitmap();
        mView.updateUi(title, artist, bitmap);
    }

    private void updateState(PlaybackStateCompat playbackState) {
        if (!shouldShowControls()) {
            mView.hideMusicLayout();
            return;
        } else {
            mView.showMusicLayout();
        }
        LoggerHelper.d("Update state called in base presenter");
        if (mView == null || playbackState == null) {
            return;
        }
        if (playbackState.getState() == PlaybackStateCompat.STATE_NONE) {
            mView.hideMusicLayout();
            return;
        }
        mView.updateState(playbackState.getState());
    }

    private boolean shouldShowControls() {
        return !(mMediaController == null || mMediaController.getMetadata() == null
                || mMediaController.getPlaybackState() == null);
    }

    @Override
    public void playSong() {
        if (mMediaController != null) {
            mMediaController.getTransportControls().play();
        }
    }

    @Override
    public void pauseSong() {
        if (mMediaController != null) {
            mMediaController.getTransportControls().pause();
        }
    }

    @Override
    public void nextSong() {
        if (mMediaController != null) {
            mMediaController.getTransportControls().skipToNext();
        }
    }

    @Override
    public void previousSong() {
        if (mMediaController != null) {
            mMediaController.getTransportControls().skipToPrevious();
        }
    }

    @Override
    public void seekTo(int pos) {
        if (mMediaController != null) {
            mMediaController.getTransportControls().seekTo(pos);
        }
    }

    @Override
    public void toggleRequest() {
        if (mMediaController != null) {
            PlaybackStateCompat playbackState = mMediaController.getPlaybackState();
            int state = playbackState == null ? PlaybackStateCompat.STATE_NONE : playbackState.getState();
            if (state == PlaybackStateCompat.STATE_PAUSED ||
                    state == PlaybackStateCompat.STATE_STOPPED ||
                    state == PlaybackStateCompat.STATE_NONE) {
                playSong();
            } else if (state == PlaybackStateCompat.STATE_PLAYING ||
                    state == PlaybackStateCompat.STATE_BUFFERING) {
                pauseSong();
            }
        }
    }

    @Override
    public void showOptionsDialog(List<SongsModel> songsList, String title) {
        mDialog = new OptionsDialog(mContext, songsList, this);
        mDialog.show();
        mDialog.setMessage(title);
    }

    @Override
    public void onAddToPlaylist(List<SongsModel> songsList) {

    }

    @Override
    public void onAddToQueue(List<SongsModel> songsList, boolean shouldClearQueue, boolean shouldPlayingSongs) {
        MusicHelper.getInstance().addSongsToQueue(songsList, shouldClearQueue, shouldPlayingSongs);
        if (shouldPlayingSongs) {
            playSong();
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {

    }
}