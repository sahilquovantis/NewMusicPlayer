package com.quovantis.music.music;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.PowerManager;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.text.TextUtils;
import android.util.Log;

import com.quovantis.music.helper.LoggerHelper;
import com.quovantis.music.helper.MusicHelper;

import java.io.IOException;

/**
 * This class controls the media player.
 */

public class LocalPlayback implements Playback, AudioManager.OnAudioFocusChangeListener,
        MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnSeekCompleteListener {

    private static final float VOLUME_DUCK = 0.2f;
    private static final float VOLUME_NORMAL = 1.0f;
    private static final int AUDIO_NO_FOCUS_NO_DUCK = 0;
    private static final int AUDIO_NO_FOCUS_CAN_DUCK = 1;
    private static final int AUDIO_FOCUSED = 2;
    private static final long TIME_IN_MILLIS = 100;
    private MediaPlayer mMediaPlayer;
    private AudioManager mAudioManager;
    private Handler mHandler;
    private String mCurrentMediaID;
    private Playback.Callback mCallback;
    private int mState;
    private volatile int mCurrentPosition;
    private Context mContext;
    private int mAudioFocus = AUDIO_FOCUSED;
    private boolean mPlayOnFocusGain;

    LocalPlayback(Context context) {
        mContext = context;
        mState = PlaybackStateCompat.STATE_NONE;
        mHandler = new Handler();
    }

    /**
     * Used for releasing resources
     */
    private void releasePlayer() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        if (mAudioManager != null) {
            mAudioManager.abandonAudioFocus(this);
            mAudioManager = null;
        }
        if (mHandler != null)
            mHandler.removeCallbacks(mProgressTimer);
        mHandler = null;
        mCurrentMediaID = null;
    }

    @Override
    public void setNullMetaDataAndHideMusicLayout(MediaMetadataCompat metaData) {
        mCurrentMediaID = null;
        mCurrentPosition = 0;
        mMediaPlayer.stop();
        if (mHandler != null) {
            mHandler.removeCallbacks(mProgressTimer);
        }
        mState = PlaybackStateCompat.STATE_NONE;
        if(mCallback != null){
            mCallback.onPlaybackStateChanged(mState);
        }
    }

    /**
     * Used for stopping media player and release all the resources.
     * And update current state to STOPPED
     */
    @Override
    public void stop() {
        LoggerHelper.d("On Stop Called");
        mState = PlaybackStateCompat.STATE_STOPPED;
        releasePlayer();
        if (mCallback != null) {
            mCallback.onPlaybackStateChanged(mState);
        }
    }

    @Override
    public void setMediaInPauseState(MediaMetadataCompat mediaMetaData) {
        try {
            createMediaPlayerIfNeeded();
            mCurrentPosition = 0;
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setDataSource(mContext, MusicHelper.getInstance().getSongURI(mediaMetaData.getDescription().getMediaId()));
            mState = PlaybackStateCompat.STATE_PAUSED;
            mCurrentMediaID = null;
            if (mHandler != null) {
                mHandler.removeCallbacks(mProgressTimer);
            }
            if (mCallback != null) {
                mCallback.onProgress(0, 0);
                mCallback.onPlaybackStateChanged(mState);
            }
        } catch (IOException e) {
            LoggerHelper.d("Error in Setting Data Source : " + e.getMessage());
        }
    }

    //Todo: If data source is not found, play next song
    @Override
    public void play(MediaMetadataCompat mediaMetaData) {
        LoggerHelper.d("On Play Called");
        mPlayOnFocusGain = true;
        if (tryToGetAudioFocus()) {
            mPlayOnFocusGain = false;
        }
        String mediaId = mediaMetaData.getDescription().getMediaId();
        boolean mediaHasChanged = !TextUtils.equals(mediaId, mCurrentMediaID);
        if (mediaHasChanged) {
            mCurrentMediaID = mediaId;
            mCurrentPosition = 0;
        }
        if (mState == PlaybackStateCompat.STATE_PAUSED && !mediaHasChanged && mMediaPlayer != null) {
            LoggerHelper.d("Resuming");
            configMediaPlayerState();
        } else {
            try {
                createMediaPlayerIfNeeded();
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mMediaPlayer.setDataSource(mContext, MusicHelper.getInstance().getSongURI(mediaMetaData.getDescription().getMediaId()));
                mMediaPlayer.prepareAsync();
            } catch (IOException e) {
                LoggerHelper.d("Error in Setting Data Source : " + e.getMessage());
            }
        }
    }

    /**
     * Used for pausing media player and update the current playback state to PAUSED.
     */
    @Override
    public void pause() {
        LoggerHelper.d("On Paused Called");
        if (mState == PlaybackStateCompat.STATE_PLAYING) {
            if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
                mMediaPlayer.pause();
                if (mHandler != null) {
                    mHandler.removeCallbacks(mProgressTimer);
                }
                mCurrentPosition = mMediaPlayer.getCurrentPosition();
            }
        }
        mState = PlaybackStateCompat.STATE_PAUSED;
        if (mCallback != null) {
            mCallback.onPlaybackStateChanged(mState);
        }
    }

    /**
     * Used for getting current playback state
     *
     * @return Current Playback state
     */
    @Override
    public int getState() {
        return mState;
    }

    /**
     * Check if the media player is playing or not
     *
     * @return true if it is playing
     */
    @Override
    public boolean isPlaying() {
        return mPlayOnFocusGain || (mMediaPlayer != null && mMediaPlayer.isPlaying());
    }

    /**
     * Try to get the system audio focus.
     */
    private boolean tryToGetAudioFocus() {
        if (mAudioManager == null)
            mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        int result = mAudioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN);
        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            mAudioFocus = AUDIO_FOCUSED;
        } else {
            mAudioFocus = AUDIO_NO_FOCUS_NO_DUCK;
        }
        LoggerHelper.d("Try to get audio focus : " + mAudioFocus);
        return result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
    }

    //ToDo : On Seek to
    @Override
    public void seekTo(int position) {
        LoggerHelper.d("On Seek To Called");
        if (mMediaPlayer == null) {
            mCurrentPosition = position;
        } else {
            if (mMediaPlayer.isPlaying()) {
                mState = PlaybackStateCompat.STATE_BUFFERING;
            }
            mMediaPlayer.seekTo(position);
            if (mCallback != null) {
                mCallback.onPlaybackStateChanged(mState);
            }
        }
    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        LoggerHelper.d("On Audio Focus Change : " + focusChange);
        if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
            mAudioFocus = AUDIO_FOCUSED;
        } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS ||
                focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT ||
                focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {
            boolean canDuck = focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK;
            mAudioFocus = canDuck ? AUDIO_NO_FOCUS_CAN_DUCK : AUDIO_NO_FOCUS_NO_DUCK;
            if (mState == PlaybackStateCompat.STATE_PLAYING && !canDuck) {
                mPlayOnFocusGain = true;
            }
        }
        if (mPlayOnFocusGain)
            configMediaPlayerState();
    }

    /**
     * This method is called when song is completed
     *
     * @param mediaPlayer Media Player instance
     */
    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        LoggerHelper.d("On Song Completion");
        mState = PlaybackStateCompat.STATE_BUFFERING;
        if (mCallback != null) {
            mCallback.onPlaybackStateChanged(mState);
        }
        if (mCallback != null) {
            mCallback.onCompletion();
        }
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        LoggerHelper.d("Error in Media Player : " + i + " , " + i1);
        return true;
    }

    /**
     * This method called when media player is done preparing and starts the song
     *
     * @param mediaPlayer Media Player instance
     */
    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        LoggerHelper.d("Media Player is Prepared");
        if (mediaPlayer != null) {
            mMediaPlayer = mediaPlayer;
            mMediaPlayer.setOnCompletionListener(this);
            configMediaPlayerState();
        }
    }

    /**
     * This method is called when the media player completes seeking
     *
     * @param mediaPlayer Media Player instance
     */
    @Override
    public void onSeekComplete(MediaPlayer mediaPlayer) {
        LoggerHelper.d("OnSeekComplete from MediaPlayer : " + mediaPlayer.getCurrentPosition());
        mCurrentPosition = mediaPlayer.getCurrentPosition();
        if (mState == PlaybackStateCompat.STATE_BUFFERING) {
            mMediaPlayer.start();
            mState = PlaybackStateCompat.STATE_PLAYING;
        }
        if (mCallback != null) {
            mCallback.onPlaybackStateChanged(mState);
        }
    }

    /**
     * This method checks the media player instance and create new instance if we don't have any.
     * If the instance is already exists, then it will reset the media player.
     */
    private void createMediaPlayerIfNeeded() {
        if (mMediaPlayer == null) {
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setWakeMode(mContext.getApplicationContext(),
                    PowerManager.PARTIAL_WAKE_LOCK);
            mMediaPlayer.setOnPreparedListener(this);
            mMediaPlayer.setOnErrorListener(this);
            mMediaPlayer.setOnSeekCompleteListener(this);
            LoggerHelper.d("CreateMediaPlayerIfNeeded needed");
        } else {
            mMediaPlayer.setOnCompletionListener(null);
            mMediaPlayer.reset();
            LoggerHelper.d("Media Player Reset");
        }
    }

    /**
     * This method play the song if we have full audio focus and it can also pause the song if
     * we lost the audio focus. Depending upon the audio focus, it plays or pause the song.
     */
    private void configMediaPlayerState() {
        if (mAudioFocus == AUDIO_NO_FOCUS_NO_DUCK) {
            // If we don't have audio focus and can't duck, we have to pause,
            if (mState == PlaybackStateCompat.STATE_PLAYING) {
                pause();
            }
        } else {
            if (mAudioFocus == AUDIO_NO_FOCUS_CAN_DUCK) {
                mMediaPlayer.setVolume(VOLUME_DUCK, VOLUME_DUCK); // we'll be relatively quiet
            } else {
                if (mMediaPlayer != null) {
                    mMediaPlayer.setVolume(VOLUME_NORMAL, VOLUME_NORMAL); // we can be loud again
                }
            }
            // If we were playing when we lost focus, we need to resume playing.
            //if (mPlayOnFocusGain) {
            if (mMediaPlayer != null && !mMediaPlayer.isPlaying()) {
                if (mCurrentPosition == mMediaPlayer.getCurrentPosition()) {
                    mMediaPlayer.start();
                    mState = PlaybackStateCompat.STATE_PLAYING;
                    LoggerHelper.d("Started Playing");
                    if (mHandler != null) {
                        mHandler.postDelayed(mProgressTimer, TIME_IN_MILLIS);
                    }
                } else {
                    mMediaPlayer.seekTo(mCurrentPosition);
                    mState = PlaybackStateCompat.STATE_BUFFERING;
                    LoggerHelper.d("Buffering");
                }
            }
            mPlayOnFocusGain = false;
            //}
        }
        if (mCallback != null) {
            mCallback.onPlaybackStateChanged(mState);
        }
    }

    /**
     * This method is used for getting current stream position
     *
     * @return Current stream position
     */
    @Override
    public int getCurrentStreamPosition() {
        return mMediaPlayer != null ?
                mMediaPlayer.getCurrentPosition() : mCurrentPosition;
    }

    private Runnable mProgressTimer = new Runnable() {
        @Override
        public void run() {
            if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
                int total = mMediaPlayer.getDuration();
                int current = mMediaPlayer.getCurrentPosition();
                mCallback.onProgress(current, total);
                if (mHandler != null)
                    mHandler.postDelayed(mProgressTimer, TIME_IN_MILLIS);
            }
        }
    };

    @Override
    public void setCallback(Callback callback) {
        mCallback = callback;
    }
}