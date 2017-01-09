package com.quovantis.music.module.playlist;


import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.quovantis.music.R;
import com.quovantis.music.appcontroller.AppActionController;
import com.quovantis.music.constants.IntentKeys;
import com.quovantis.music.dialogs.PlaylistOptionsDialog;
import com.quovantis.music.models.FolderAndPlaylistClickedModel;
import com.quovantis.music.models.UserPlaylistModel;
import com.quovantis.music.module.base.fragment.BaseFragment;
import com.quovantis.music.module.songs.SongsActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link BaseFragment} subclass.
 */
public class PlaylistFragment extends BaseFragment implements IPlaylist.View, PlaylistAdapter.IOnUserPlaylistClickedListener {

    @BindView(R.id.rv_playlist)
    RecyclerView mPlaylistRV;
    @BindView(R.id.progress_bar)
    ProgressBar mProgressBar;
    private PlaylistAdapter mAdapter;
    private List<UserPlaylistModel> mPlaylistList;
    private IPlaylist.Presenter mPresenter;

    public PlaylistFragment() {
    }

    @Override
    protected void initBundle(Bundle bundle) {

    }

    @Override
    protected void initVariables() {
        mPlaylistList = new ArrayList<>();
        mAdapter = new PlaylistAdapter(getActivity(), mPlaylistList, this);
        mPresenter = new PlaylistPresenter(this);
        mPlaylistRV.setAdapter(mAdapter);
        mPresenter.getUserPlaylist();
        getActivity().registerReceiver(mUpdatePlaylistListReceiver, new IntentFilter(IntentKeys.PLAYLIST_UPDATED));
    }

    @Override
    protected void initViews(View view) {
        ButterKnife.bind(this, view);
        mPlaylistRV.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    @Override
    protected int getContentView() {
        return R.layout.fragment_playlist;
    }

    @Override
    public void showProgress() {
        mProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        mProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void onGettingPlaylist(List<UserPlaylistModel> list) {
        mPlaylistList.clear();
        mPlaylistList.addAll(list);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(mUpdatePlaylistListReceiver);
        if (mPresenter != null)
            mPresenter.destroy();
    }

    private BroadcastReceiver mUpdatePlaylistListReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (mPresenter != null) {
                mPresenter.getUserPlaylist();
            }
        }
    };

    @Override
    public void onUserPlaylistClicked(UserPlaylistModel model) {
        FolderAndPlaylistClickedModel clickedModel = new FolderAndPlaylistClickedModel();
        clickedModel.setTitle(model.getPlaylistName());
        clickedModel.setSongsList(model.getPlaylist());
        Bundle bundle = new Bundle();
        bundle.putParcelable(IntentKeys.FOLDER_AND_PLAYLIST_CLICKED_MODEL_KEY, clickedModel);
        new AppActionController.Builder()
                .from(getActivity())
                .setTargetActivity(SongsActivity.class)
                .setBundle(bundle)
                .build()
                .execute();
    }

    @Override
    public void showPlaylistOptionsDialog(UserPlaylistModel model) {
        IPlaylistOptionsListener listener = (IPlaylistOptionsListener) getActivity();
        listener.onShowPlaylistOptionsDialog(model);
    }

    public interface IPlaylistOptionsListener {
        void onShowPlaylistOptionsDialog(UserPlaylistModel model);
    }
}