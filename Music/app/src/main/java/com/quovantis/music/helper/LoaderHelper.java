package com.quovantis.music.helper;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.database.MergeCursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import com.quovantis.music.models.CursorModel;
import com.quovantis.music.models.SongsModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Class for querying file storage for audio files.
 * It uses LoaderManager for querying the storage
 * and provides result in the loader callbacks.
 */
class LoaderHelper implements LoaderManager.LoaderCallbacks<Cursor> {

    private ICursorAndLoaderHelperBridge mListener;
    private Context mContext;
    private final static Object mMutex = new Object();
    private static LoaderHelper sInstance;

    private LoaderHelper() {

    }

    static LoaderHelper getInstance() {
        if (sInstance == null) {
            synchronized (mMutex) {
                if (sInstance == null) {
                    sInstance = new LoaderHelper();
                }
            }
        }
        return sInstance;
    }

    void getSongs(CursorModel cursorModel, ICursorAndLoaderHelperBridge mListener) {
        this.mListener = mListener;
        Activity activity = cursorModel.getActivityWeakReference().get();
        if (activity != null) {
            mContext = activity;
            LoggerHelper.d("Loader Initialised");
            activity.getLoaderManager().restartLoader(cursorModel.getLoaderId(), cursorModel.getBundle(), this);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        LoggerHelper.d("Loader created");
        String sortOrder = MediaStore.Audio.Media.TITLE + " ASC";
        return new CursorLoader(mContext, getMediaUri(), getColumns(), null, null, sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        LoggerHelper.d("Loader Finished : " + loader.getId());
        ArrayList<SongsModel> songsList = new ArrayList<>();
        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                SongsModel model = new SongsModel();
                model.setSongTitle(cursor.getString(2));
                model.setSongArtist(cursor.getString(3));
                model.setAlbumId(cursor.getLong(4));
                model.setSongPath(cursor.getString(1));
                model.setSongID(cursor.getString(0));
                songsList.add(model);
                cursor.moveToNext();
            }
            cursor.close();
        }
        LoggerHelper.d("On finished");
        mContext = null;
        mListener.onGettingSongs(songsList);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private Uri getMediaUri() {
        return MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
    }

    private String[] getColumns() {
        return new String[]{MediaStore.Audio.Media._ID, MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.ALBUM_ID};
    }

    /**
     * This act as a bridge between this and {@link CursorHelper} class.
     * After fetching songs it sends back results to the {@link CursorHelper} class.
     */
    interface ICursorAndLoaderHelperBridge {
        void onGettingSongs(List<SongsModel> songsList);
    }
}