package com.quovantis.music.module.base.activity;

import android.app.Dialog;
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
import android.view.WindowManager;
import android.widget.Toast;

import com.quovantis.music.R;
import com.quovantis.music.constants.IntentKeys;
import com.quovantis.music.dialogs.CreatePlaylistDialog;
import com.quovantis.music.dialogs.RenamePlaylistDialog;
import com.quovantis.music.dialogs.SongsAndFolderOptionsDialog;
import com.quovantis.music.dialogs.PlaylistOptionsDialog;
import com.quovantis.music.helper.LoggerHelper;
import com.quovantis.music.helper.MusicHelper;
import com.quovantis.music.interfaces.IOptionsDialogListener;
import com.quovantis.music.models.SongsModel;
import com.quovantis.music.models.UserPlaylistModel;
import com.quovantis.music.music.MusicService;

import java.util.List;

/**
 * Base Activity Presenter, here binding and unbinding of service takes place.
 */

class BaseMusicPresenter implements IBaseMusic.Presenter, ServiceConnection, IOptionsDialogListener, CreatePlaylistDialog.IOnCreatingPlaylist, IBaseMusic.Interactor.Listener, PlaylistOptionsDialog.IPlaylistOptionsDialog, RenamePlaylistDialog.IOnRenamePlaylistDialogListener {

    private IBaseMusic.View mView;
    private Context mContext;
    private boolean mIsBounded;
    private MediaControllerCompat mMediaController;
    private IBaseMusic.Interactor mInteractor;

    BaseMusicPresenter(IBaseMusic.View mView, Context context) {
        this.mView = mView;
        mContext = context;
        mInteractor = new BaseMusicInteractor(mContext);
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

    /**
     * This method is called when user toggles the play or pause button
     */
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

    /**
     * This method is called for showing Options for Songs or Folders {@link com.quovantis.music.module.home.HomeActivity}
     * @param songsList SongsList
     * @param title Title of the dialog
     */
    @Override
    public void showOptionsDialog(List<SongsModel> songsList, String title) {
        SongsAndFolderOptionsDialog mDialog = new SongsAndFolderOptionsDialog(mContext, songsList, this);
        mDialog.show();
        mDialog.setMessage(title);
    }

    /**
     * This method is called for showing Playlist Options {@link com.quovantis.music.module.playlist.PlaylistFragment}
     * @param model Playlist
     */
    @Override
    public void showPlaylistOptionsDialog(UserPlaylistModel model) {
        PlaylistOptionsDialog dialog = new PlaylistOptionsDialog(mContext, model, this);
        dialog.show();
        dialog.setMessage(model.getPlaylistName());
    }

    /**
     * This method will called when user selects add to playlist options{@link IOptionsDialogListener}
     * After this it will show new dialog with two options. Either it can create new playlist
     * or can add songs to existing playlist {@link CreatePlaylistDialog}
     *
     * @param songsList Songs List which will be added to the playlist
     */
    @Override
    public void onAddToPlaylist(List<SongsModel> songsList) {
        Dialog createPlaylistDialog = new CreatePlaylistDialog(songsList, mContext, this);
        createPlaylistDialog.show();
    }

    /**
     * This method is called when user selects add to queue or clear queue option in dialog {@link IOptionsDialogListener}
     * @param songsList          Songs List which will be added to the queue
     * @param shouldClearQueue   If true, this will clear current queue
     * @param shouldPlayingSongs If true, this will play this songs list
     */
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

    /**
     * This method will called when user selects create new playlist option in dialog {@link CreatePlaylistDialog}
     *
     * @param songsList    Songs List which user wants to add in the playlist.
     * @param playlistName Playlist Name
     */
    @Override
    public void createNewPlaylist(List<SongsModel> songsList, String playlistName) {
        if (songsList == null || songsList.isEmpty()) {
            onSuccess(false, mContext.getString(R.string.something_went_wrong));
            return;
        }
        if (mView != null) {
            mView.showProgressDialog(mContext.getString(R.string.creating_playlist));
            mInteractor.createNewPlaylist(songsList, playlistName, this);
        }
    }

    /**
     * This method will called when user selects one playlist from all the playlist in dialog {@link CreatePlaylistDialog}
     *
     * @param songsList New SongsList
     * @param model     Existing Playlist in which songs will added
     */
    @Override
    public void addSongsToExistingPlaylist(List<SongsModel> songsList, UserPlaylistModel model) {
        if (model == null || model.getPlaylist() == null) {
            onSuccess(false, mContext.getString(R.string.something_went_wrong));
            return;
        }
        if (mView != null) {
            mView.showProgressDialog(mContext.getString(R.string.updating_playlist));
            mInteractor.addSongsToExistingPlaylist(songsList, model, this);
        }
    }

    /**
     * Called when user selects Rename Playlist option in Dialog {@link PlaylistOptionsDialog}
     * After selecting that, it will display new dialog where user will enter new playlist name {@link RenamePlaylistDialog}
     *
     * @param model User Playlist Model which user wants to rename
     */
    @Override
    public void onRenamePlaylist(UserPlaylistModel model) {
        if (model == null) {
            onSuccess(false, mContext.getString(R.string.something_went_wrong));
            return;
        }
        RenamePlaylistDialog dialog = new RenamePlaylistDialog(mContext, model, this);
        dialog.show();
        if (dialog.getWindow() != null)
            dialog.getWindow().clearFlags(
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                            | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
    }

    /**
     * Called when user enter new playlist name {@link RenamePlaylistDialog}
     *
     * @param model        User Playlist Model which user wants to rename
     * @param playlistName New playlist name
     */
    @Override
    public void onRenamePlaylist(UserPlaylistModel model, String playlistName) {
        if (mView != null) {
            mView.showProgressDialog(mContext.getString(R.string.renaming_playlist));
            mInteractor.renamePlaylist(model, playlistName, this);
        }
    }

    /**
     * Called when user selects Delete Playlist option in Dialog {@link PlaylistOptionsDialog}
     *
     * @param model User Playlist Model which user wants to delete
     */
    @Override
    public void onDeletePlaylist(UserPlaylistModel model) {
        if (model == null) {
            onSuccess(false, mContext.getString(R.string.something_went_wrong));
            return;
        }
        if (mView != null && mInteractor != null) {
            mView.showProgressDialog(mContext.getString(R.string.processing));
            mInteractor.deletePlaylist(model, this);
        }
    }

    /**
     * Called when playlist is created, deleted or renamed
     *
     * @param isSuccess Indicates that whether operation is success or not
     * @param msg       Message will display in toast
     */
    @Override
    public void onSuccess(boolean isSuccess, String msg) {
        if (mView != null) {
            mView.hideProgressDialog();
            if (isSuccess) {
                Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show();
                Intent intent = new Intent();
                intent.setAction(IntentKeys.PLAYLIST_UPDATED);
                mContext.sendBroadcast(intent);
            } else {
                Toast.makeText(mContext, mContext.getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show();
            }
        }
    }
}