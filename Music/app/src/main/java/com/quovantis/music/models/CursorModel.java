package com.quovantis.music.models;

import android.app.Activity;
import android.os.Bundle;

import com.quovantis.music.interfaces.IHomeAndCursorBridge;

import java.lang.ref.WeakReference;

/**
 * Cursor Model for requesting songs
 */

public class CursorModel {
    private int mLoaderId;
    private Bundle mBundle;
    private IHomeAndCursorBridge mListener;
    private WeakReference<Activity> mActivityWeakReference;

    public int getLoaderId() {
        return mLoaderId;
    }

    public void setLoaderId(int mLoaderId) {
        this.mLoaderId = mLoaderId;
    }

    public Bundle getBundle() {
        return mBundle;
    }

    public void setBundle(Bundle mBundle) {
        this.mBundle = mBundle;
    }

    public IHomeAndCursorBridge getListener() {
        return mListener;
    }

    public void setListener(IHomeAndCursorBridge mListener) {
        this.mListener = mListener;
    }

    public WeakReference<Activity> getActivityWeakReference() {
        return mActivityWeakReference;
    }

    public void setActivityWeakReference(Activity activity) {
        mActivityWeakReference = new WeakReference<Activity>(activity);
    }
}
