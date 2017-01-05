package com.quovantis.music.module.home;

import android.app.Activity;

import com.quovantis.music.models.FoldersModel;
import com.quovantis.music.models.SongsModel;

import java.util.List;

/**
 * Home Presenter
 */
class HomePresenter implements IHomeMVP.Presenter, IHomeMVP.Model.Listener {
    private IHomeMVP.View mView;
    private IHomeMVP.Model mInteractor;

    HomePresenter(IHomeMVP.View mView) {
        this.mView = mView;
        mInteractor = new HomeInteractor(this);
    }

    @Override
    public void onGettingAllSongs(List<SongsModel> songsList) {
        if (mView != null) {
            mView.onGettingAllSongs(songsList);
        }
    }

    @Override
    public void onGettingAllFolders(List<FoldersModel> foldersList) {
        if (mView != null) {
            mView.onGettingAllFolders(foldersList);
        }
    }

    @Override
    public void getAllSongsAndFolders(Activity activity) {
        mInteractor.getAllSongsAndFolders(activity);
    }

    @Override
    public void destroy() {
        mView = null;
    }
}