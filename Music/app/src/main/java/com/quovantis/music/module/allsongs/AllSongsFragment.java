package com.quovantis.music.module.allsongs;


import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import com.quovantis.music.R;
import com.quovantis.music.helper.MusicHelper;
import com.quovantis.music.models.SongsModel;
import com.quovantis.music.module.base.fragment.BaseFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link BaseFragment} subclass.
 */
public class AllSongsFragment extends BaseFragment implements ISongs.View, SongsAdapter.IOnSongClickedListener {

    @BindView(R.id.rv_songs)
    RecyclerView mSongsRV;
    @BindView(R.id.progress_bar)
    ProgressBar mProgressBar;
    private SongsAdapter mAdapter;
    private List<SongsModel> mSongsList;
    private IGetSongsListener mListener;

    public AllSongsFragment() {
    }

    @Override
    protected void initBundle(Bundle bundle) {

    }

    @Override
    protected void initVariables() {
        mListener = (IGetSongsListener) getActivity();
        showProgress();
        mSongsList = new ArrayList<>();
        mAdapter = new SongsAdapter(mSongsList, getActivity(), this);
        mSongsRV.setAdapter(mAdapter);
        mListener.getSongs();
    }

    @Override
    protected void initViews(View view) {
        ButterKnife.bind(this, view);
        mSongsRV.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    @Override
    protected int getContentView() {
        return R.layout.fragment_all_songs;
    }

    @Override
    public void onGettingSongs(List<SongsModel> songsList) {
        hideProgress();
        mSongsList.clear();
        mSongsList.addAll(songsList);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void showProgress() {
        if (mProgressBar != null) {
            mProgressBar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void hideProgress() {
        if (mProgressBar != null) {
            mProgressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void onSongClicked(List<SongsModel> songsList, int currentSongPos) {
        MusicHelper.getInstance().setCurrentSongsQueue(songsList, currentSongPos);
        mListener.playSong();
    }

    @Override
    public void onSongOptionClick(SongsModel model) {
        List<SongsModel> list = new ArrayList<>(1);
        list.add(model);
        mListener.showOptionsDialog(list, model.getSongTitle());
    }


    public interface IGetSongsListener {
        void getSongs();

        void playSong();

        void showOptionsDialog(List<SongsModel> list, String title);
    }
}