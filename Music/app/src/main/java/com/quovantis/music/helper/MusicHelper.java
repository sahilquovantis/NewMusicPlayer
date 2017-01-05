package com.quovantis.music.helper;

import android.content.ContentUris;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.support.v4.media.MediaMetadataCompat;

import com.quovantis.music.R;
import com.quovantis.music.models.SongsModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Music Helper class maintains currently playing songs
 * and returns current position song and its metadata
 */

public class MusicHelper {

    private static MusicHelper sInstance;
    private final static Object mMutex = new Object();
    private MediaMetadataCompat mCurrentMusic;
    private List<SongsModel> mCurrentPlaylist;
    private CurrentPositionHelper mCurrentPositionHelper;

    public static MusicHelper getInstance() {
        if (sInstance == null) {
            synchronized (mMutex) {
                if (sInstance == null) {
                    sInstance = new MusicHelper();
                }
            }
        }
        return sInstance;
    }

    private MusicHelper() {
        mCurrentPositionHelper = new CurrentPositionHelper();
        mCurrentPlaylist = new ArrayList<>();
    }

    /**
     * Set Next Song Position
     */
    public void skipToNext() {
        mCurrentPositionHelper.getNextSong(mCurrentPlaylist.size());
    }

    /**
     * Set Previous Song Position
     */
    public void skipToPrevious() {
        mCurrentPositionHelper.getPreviousSong(mCurrentPlaylist.size());
    }

    /**
     * This method is used for getting Uri of current song
     *
     * @param id Current Song id for which Uri is needed
     * @return Uri of current song which is used for setting media player {@link com.quovantis.music.music.LocalPlayback}
     */
    public Uri getSongURI(String id) {
        return ContentUris.withAppendedId(android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, Long.parseLong(id));
    }

    /**
     * This method is used for getting Metadata of the song.
     *
     * @param context Context
     * @return Metadata of the current song {@link com.quovantis.music.music.PlayBackManager}
     */
    public MediaMetadataCompat getCurrentMusic(Context context) {
        int pos = mCurrentPositionHelper.getCurrentPosition();
        if (isValidIndex(pos)) {
            SongsModel songDetailsModel = mCurrentPlaylist.get(pos);
            LoggerHelper.d("Current Position Song : " + pos + " , " + songDetailsModel.getSongTitle());
            try {
                MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
                mediaMetadataRetriever.setDataSource(songDetailsModel.getSongPath());
                String duration = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                if (duration != null) {
                    MediaMetadataCompat.Builder builder = new MediaMetadataCompat.Builder();
                    builder.putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, songDetailsModel.getSongID());
                    builder.putString(MediaMetadataCompat.METADATA_KEY_ARTIST, songDetailsModel.getSongArtist());
                    builder.putString(MediaMetadataCompat.METADATA_KEY_TITLE, songDetailsModel.getSongTitle());
                    builder.putLong(MediaMetadataCompat.METADATA_KEY_DURATION, Long.parseLong(duration));
                    builder.putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, getAlbumBitmap(context,
                            mediaMetadataRetriever.getEmbeddedPicture()));
                    return builder.build();
                }
            } catch (IllegalArgumentException e) {
                LoggerHelper.d("MetaData Retriever Exception : " + e.getMessage());
                return null;
            }
        }
        return null;
    }

    /**
     * This method is used for getting thumbnail of song
     *
     * @param context Context
     * @param data    Byte Data
     * @return Thumbnail of current song
     */
    private Bitmap getAlbumBitmap(Context context, byte[] data) {
        Bitmap bitmap;
        if (data == null) {
            bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.music);
        } else {
            bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
        }
        return bitmap;
    }

    /**
     * Check whether current playing song position is valid or not
     *
     * @param index Position of current song
     * @return true if position is valid
     */
    private boolean isValidIndex(int index) {
        return mCurrentPlaylist.size() > 0 && (index == 0 || (0 < index && index < mCurrentPlaylist.size()));
    }

    /**
     * This method clear the current songs list and replace with the new list.
     *
     * @param songsList      New current songs list
     * @param currentSongPos Song position which is selected by user
     */
    public void setCurrentSongsQueue(List<SongsModel> songsList, int currentSongPos) {
        mCurrentPlaylist.clear();
        mCurrentPlaylist.addAll(songsList);
        mCurrentPositionHelper.setCurrentPosition(currentSongPos);
    }

    /**
     * This method add new songs list to the current queue
     *
     * @param songsList               New songs list
     * @param shouldClearCurrentQueue if true then it will clear the current queue
     *                                and add the songs at the beginning. If not, then it will add the songs at the end.
     * @param shouldPlayThisList      if true then it will change the current song position to the new list first song
     */
    public void addSongsToQueue(List<SongsModel> songsList,
                                boolean shouldClearCurrentQueue, boolean shouldPlayThisList) {
        int pos = -1;
        if (mCurrentPlaylist == null) {
            mCurrentPlaylist = new ArrayList<>();
        }
        if (shouldClearCurrentQueue) {
            mCurrentPlaylist.clear();
        }
        if (shouldPlayThisList) {
            pos = mCurrentPlaylist.size();
        }
        mCurrentPlaylist.addAll(songsList);
        if (isValidIndex(pos)) {
            mCurrentPositionHelper.setCurrentPosition(pos);
        }
    }

    /**
     * Method used for getting current queue music list
     *
     * @return Current songs list
     */
    public List<SongsModel> getCurrentQueue() {
        return mCurrentPlaylist;
    }

    /**
     * Method used for getting current position
     *
     * @return Current Position
     */
    public int getCurrentPosition() {
        return mCurrentPositionHelper.getCurrentPosition();
    }

    /**
     * Method used for getting previous played position
     *
     * @return Previous Position
     */
    public int getPreviousPosition() {
        return mCurrentPositionHelper.getPreviousPosition();
    }

    public void setCurrentPosition(int pos) {
        mCurrentPositionHelper.setCurrentPosition(pos);
    }

    /**
     * This method is used for checking if last song is completed playing.
     * If true, then we will update the music list with first song but in pause state.
     *
     * @return true or false
     */
    public boolean shouldBeInPauseState() {
        return mCurrentPositionHelper.shouldBeInPauseState();
    }
}