package com.quovantis.music.module.playlist;

import com.quovantis.music.models.UserPlaylistModel;

import java.util.List;

/**
 * Act as a intermediate between View and Model
 */

public class PlaylistPresenter implements IPlaylist.Presenter, IPlaylist.Interactor.Listener {
    private IPlaylist.View mView;
    private IPlaylist.Interactor mInteractor;

    public PlaylistPresenter(IPlaylist.View mView) {
        this.mView = mView;
        mInteractor = new PlaylistInteractor();
    }

    @Override
    public void onGettingPlaylist(List<UserPlaylistModel> list) {
        if (mView != null) {
            mView.hideProgress();
            mView.onGettingPlaylist(list);
        }
    }

    @Override
    public void onFailure() {
        if (mView != null) {
            mView.hideProgress();
        }
    }

    @Override
    public void getUserPlaylist() {
        if (mView != null) {
            mView.showProgress();
            mInteractor.getUserPlaylist(this);
        }
    }

    @Override
    public void destroy() {
        mView = null;
    }
}