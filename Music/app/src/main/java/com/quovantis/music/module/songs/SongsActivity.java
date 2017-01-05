package com.quovantis.music.module.songs;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.quovantis.music.R;
import com.quovantis.music.constants.IntentKeys;
import com.quovantis.music.helper.MusicHelper;
import com.quovantis.music.models.FoldersModel;
import com.quovantis.music.models.SongsModel;
import com.quovantis.music.module.allsongs.ISongs;
import com.quovantis.music.module.allsongs.SongsAdapter;
import com.quovantis.music.module.base.activity.BaseActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SongsActivity extends BaseActivity implements ISongs.View, SongsAdapter.IOnSongClickedListener {

    @BindView(R.id.rv_songs)
    RecyclerView mSongsRV;
    @BindView(R.id.progress_bar)
    ProgressBar mProgressBar;
    private SongsAdapter mAdapter;
    private List<SongsModel> mSongsList;
    private String mTitle;

    @Override
    protected int provideContentView() {
        return R.layout.activity_songs;
    }

    @Override
    protected void onCreatingBase(Bundle savedInstanceState) {
        ButterKnife.bind(this);
    }

    @Override
    protected void initViews() {
        mSongsRV.setLayoutManager(new LinearLayoutManager(this));
        mSongsRV.setAdapter(mAdapter);
    }

    @Override
    protected void initVariables() {
        mTitle = "";
        mSongsList = new ArrayList<>();
        mAdapter = new SongsAdapter(mSongsList, this, this);
    }

    @Override
    protected void initBundle(Intent intent) {
        if (intent != null) {
            Bundle extras = intent.getExtras();
            if (extras != null) {
                FoldersModel folder = (FoldersModel) extras.getSerializable(IntentKeys.FOLDER_MODEL_KEY);
                if (folder != null) {
                    mTitle = folder.getDirectory();
                    onGettingSongs(folder.getSongs());
                }
            }
        }
    }

    @Override
    protected void setToolbarTitle() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(mTitle);
        }
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
        mProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        mProgressBar.setVisibility(View.GONE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        overridePendingTransition(0, R.anim.exit_out);
        super.onBackPressed();
    }

    @Override
    public void onSongClicked(List<SongsModel> songsList, int currentSongPos) {
        MusicHelper.getInstance().setCurrentSongsQueue(songsList, currentSongPos);
        mPresenter.playSong();
    }

    @Override
    protected void updateEqualizerState(int state) {

    }
}