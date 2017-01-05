package com.quovantis.music.module.home;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.quovantis.music.R;
import com.quovantis.music.constants.IntentKeys;
import com.quovantis.music.models.FoldersModel;
import com.quovantis.music.models.SongsModel;
import com.quovantis.music.module.allsongs.AllSongsFragment;
import com.quovantis.music.module.base.activity.BaseActivity;
import com.quovantis.music.module.folders.FolderFragment;
import com.quovantis.music.module.playlist.PlaylistFragment;
import com.quovantis.music.music.MusicService;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HomeActivity extends BaseActivity implements IHomeMVP.View,
        AllSongsFragment.IGetSongsListener, FolderFragment.IFolderClickedListener,
        ViewPager.OnPageChangeListener {

    @BindView(R.id.view_pager)
    protected ViewPager mViewPager;
    @BindView(R.id.playlist_tab)
    protected View mPlaylistView;
    @BindView(R.id.folders_tab)
    protected View mFolderView;
    @BindView(R.id.tracks_tab)
    protected View mTracksView;
    private HomeAdapter mPagerAdapter;
    private IHomeMVP.Presenter mHomePresenter;
    private boolean mIsBackPressedCalled;

    @Override
    protected int provideContentView() {
        return R.layout.activity_home;
    }

    @Override
    protected void onCreatingBase(Bundle savedInstanceState) {
        ButterKnife.bind(this);
        mIsBackPressedCalled = false;
    }

    @Override
    protected void initBundle(Intent intent) {

    }

    @Override
    protected void setToolbarTitle() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.home_title));
        }
    }

    @Override
    protected void initViews() {
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.addOnPageChangeListener(this);
    }

    private void addAllFragments() {
        mPagerAdapter.addFragment(new FolderFragment(), getString(R.string.tab_folder_title));
        mPagerAdapter.addFragment(new AllSongsFragment(), getString(R.string.tab_tracks_title));
        mPagerAdapter.addFragment(new PlaylistFragment(), getString(R.string.tab_playlist_title));
    }

    @Override
    protected void initVariables() {
        mHomePresenter = new HomePresenter(this);
        mPagerAdapter = new HomeAdapter(getSupportFragmentManager());
        addAllFragments();
    }

    @Override
    public void onGettingAllSongs(List<SongsModel> songsList) {
        AllSongsFragment songsFragment = (AllSongsFragment) mPagerAdapter.getItem(1);
        songsFragment.onGettingSongs(songsList);
    }

    @Override
    public void onGettingAllFolders(List<FoldersModel> foldersList) {
        FolderFragment folderFragment = (FolderFragment) mPagerAdapter.getItem(0);
        folderFragment.onGettingFolders(foldersList);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHomePresenter.destroy();
        mHomePresenter = null;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        changeTabBackground(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @OnClick({R.id.rl_folder_tab, R.id.rl_tracks_tab, R.id.rl_playlist_tab})
    protected void onClickTabs(View view) {
        switch (view.getId()) {
            case R.id.rl_tracks_tab:
                mViewPager.setCurrentItem(1, true);
                break;
            case R.id.rl_folder_tab:
                mViewPager.setCurrentItem(0, true);
                break;
            case R.id.rl_playlist_tab:
                mViewPager.setCurrentItem(2, true);
                break;
        }
    }

    private void changeTabBackground(int pos) {
        mFolderView.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary));
        mTracksView.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary));
        mPlaylistView.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary));
        switch (pos) {
            case 0:
                mFolderView.setBackgroundColor(ContextCompat.getColor(this, R.color.colorTabLayout));
                break;
            case 1:
                mTracksView.setBackgroundColor(ContextCompat.getColor(this, R.color.colorTabLayout));
                break;
            case 2:
                mPlaylistView.setBackgroundColor(ContextCompat.getColor(this, R.color.colorTabLayout));
                break;
        }
    }

    @Override
    public void getSongs() {
        if (mHomePresenter != null) {
            mHomePresenter.getAllSongsAndFolders(this);
        }
    }

    @Override
    public void playSong() {
        mPresenter.playSong();
    }

    @Override
    public void showOptionsDialog(List<SongsModel> list, String title) {
        mPresenter.showOptionsDialog(list, title);
    }

    @Override
    public void onBackPressed() {
        if (!mIsBackPressedCalled && !MusicService.sIsNotificationCreated) {
            Intent intent = new Intent(this, MusicService.class);
            intent.setAction(IntentKeys.INTENT_ACTION_STOP);
            startService(intent);
        }
        mIsBackPressedCalled = true;
        super.onBackPressed();
    }

    @Override
    protected void updateEqualizerState(int state) {

    }
}