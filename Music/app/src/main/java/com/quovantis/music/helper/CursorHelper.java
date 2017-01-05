package com.quovantis.music.helper;

import android.app.Activity;

import com.quovantis.music.models.CursorModel;
import com.quovantis.music.models.SongsModel;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Class for managing the CursorLoader requests.
 * This class save the requests in the queue and works on the requests one by one.
 */

public class CursorHelper implements LoaderHelper.ICursorAndLoaderHelperBridge {
    private static final Object mMutex = new Object();
    private static CursorHelper sInstance;
    private Queue<CursorModel> mRequestQueue;
    private LoaderHelper mLoader;

    private CursorHelper() {
        mRequestQueue = new LinkedList<>();
        mLoader = LoaderHelper.getInstance();
    }

    public static CursorHelper getInstance() {
        if (sInstance == null) {
            synchronized (mMutex) {
                if (sInstance == null) {
                    sInstance = new CursorHelper();
                }
            }
        }
        return sInstance;
    }

    /**
     * This method is called for getting songs from storage.
     * It saves the request in the queue, if the queue is empty it further send the request to the
     * {@link LoaderHelper} class. If not, then it saves to the queue.
     *
     * @param model This model contains all the details of the cursorLoader
     */
    public void getSongs(CursorModel model) {
        synchronized (mMutex) {
            LoggerHelper.d("Get songs function called in cursor helper");
            mRequestQueue.add(model);
            if (mRequestQueue.size() == 1) {
                if (model.getActivityWeakReference().get() != null) {
                    mLoader.getSongs(mRequestQueue.element(), this);
                } else {
                    mRequestQueue.remove(model);
                }
            }
        }
    }

    /**
     * This method is called from {@link LoaderHelper} class when songs are fetched
     * from file storage. And send the results back to the class which called getSongs() method.
     *
     * @param songsList List of songs which are fetched from storage.
     */
    @Override
    public void onGettingSongs(List<SongsModel> songsList) {
        synchronized (mMutex) {
            LoggerHelper.d("On getting songs called in cursor helper");
            if (!mRequestQueue.isEmpty()) {
                CursorModel model = mRequestQueue.remove();
                Activity activity = model.getActivityWeakReference().get();
                if (activity != null) {
                    model.getListener().onGettingAllSongs(songsList);
                }
                nextRequest();
            }
        }
    }

    /**
     * This method is used to check if the queue is empty or not.
     * If the queue is not empty, it fetches the top most request and works on it.
     */
    private void nextRequest() {
        if (!mRequestQueue.isEmpty()) {
            LoggerHelper.d("Next request called in cursor helper");
            CursorModel model = mRequestQueue.element();
            if (model.getActivityWeakReference().get() != null) {
                mLoader.getSongs(model, this);
            } else {
                mRequestQueue.remove(model);
                nextRequest();
            }
        } else {
            LoggerHelper.d("All requests are implemented");
        }
    }
}