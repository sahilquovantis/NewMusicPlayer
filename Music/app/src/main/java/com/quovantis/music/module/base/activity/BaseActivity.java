package com.quovantis.music.module.base.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.quovantis.music.R;
import com.quovantis.music.appcontroller.AppActionController;
import com.quovantis.music.constants.IntentKeys;
import com.quovantis.music.helper.LoggerHelper;
import com.quovantis.music.module.currentqueue.CurrentQueueActivity;
import com.quovantis.music.music.MusicService;
import com.quovantis.music.utility.Utils;

import butterknife.BindView;
import butterknife.OnClick;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Base Activity for all other activities
 */

public abstract class BaseActivity extends AppCompatActivity implements IBaseMusic.View {
    @BindView(R.id.ll_main_layout)
    LinearLayout mMainLayoutLL;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.rl_music_layout)
    RelativeLayout mMusicLayoutRL;
    @BindView(R.id.tv_song_name)
    TextView mSongNameTV;
    @BindView(R.id.tv_song_artist)
    TextView mSongArtistTV;
    @BindView(R.id.iv_song_thumbnail)
    ImageView mThumbnailIV;
    @BindView(R.id.iv_play_pause)
    ImageView mPlayPauseIV;
    protected IBaseMusic.Presenter mPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(provideContentView());
        initPresenter();
        onCreatingBase(savedInstanceState);
        initVariables();
        initViews();
        initBundle(getIntent());
        initToolbar();
    }

    private void initPresenter() {
        mPresenter = new BaseMusicPresenter(this, this);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!MusicService.sIsServiceDestroying) {
            if (mPresenter != null) {
                mPresenter.bindService();
            }
        } else {
            onBackPressed();
        }
        registerReceiver(mAppCloseReceiver, new IntentFilter(IntentKeys.CLOSE_MUSIC_ACTION));
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mPresenter != null) {
            mPresenter.unbindService();
        }
        unregisterReceiver(mAppCloseReceiver);
    }

    private BroadcastReceiver mAppCloseReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            LoggerHelper.d("Close App Receiver called");
            if (action != null && action.equalsIgnoreCase(IntentKeys.CLOSE_MUSIC_ACTION)) {
                onBackPressed();
            }
        }
    };

    @OnClick({R.id.iv_next_song, R.id.iv_previous_song, R.id.iv_play_pause})
    void onClick(ImageView imageView) {
        switch (imageView.getId()) {
            case R.id.iv_next_song:
                mPresenter.nextSong();
                break;
            case R.id.iv_previous_song:
                mPresenter.previousSong();
                break;
            case R.id.iv_play_pause:
                mPresenter.toggleRequest();
                break;
        }
    }

    @OnClick(R.id.rl_music_layout)
    public void onClickMusic() {
        if (this instanceof CurrentQueueActivity) {
            return;
        }
        new AppActionController.Builder()
                .from(this)
                .setTargetActivity(CurrentQueueActivity.class)
                .build()
                .execute();
    }

    @Override
    public void updateUi(String title, String artist, Bitmap bitmap) {
        mSongNameTV.setText(title);
        mSongArtistTV.setText(artist);
        mThumbnailIV.setImageBitmap(bitmap);
    }

    @Override
    public void updateState(int state) {
        if (state == PlaybackStateCompat.STATE_PLAYING) {
            mPlayPauseIV.setImageResource(R.drawable.ic_action_pause);
        } else {
            mPlayPauseIV.setImageResource(R.drawable.ic_action_play);
        }
        updateEqualizerState(state);
    }

    @Override
    public void showMusicLayout() {
        mMusicLayoutRL.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideMusicLayout() {
        mMusicLayoutRL.setVisibility(View.GONE);
        if (this instanceof CurrentQueueActivity) {
            Toast.makeText(this, getString(R.string.empty_current_queue), Toast.LENGTH_LONG).show();
            finish();
        }
    }

    /**
     * This method updates the equalizer state {@link CurrentQueueActivity}
     * If state is PAUSED, then it will pause the equalizer else it will animate the equalizer.
     *
     * @param state Current Media Player state
     */
    protected abstract void updateEqualizerState(int state);

    /**
     * Initialise toolbar in all activities
     */
    protected void initToolbar() {
        setSupportActionBar(mToolbar);
        setToolbarTitle();
    }

    /**
     * First Method to be called in Child Activities
     *
     * @param savedInstanceState Previously saved instance state
     */
    protected abstract void onCreatingBase(Bundle savedInstanceState);

    /**
     * Get Bundle in this method
     *
     * @param intent Intent received from other activities
     */
    protected abstract void initBundle(Intent intent);

    /**
     * Used for setting toolbar title in child activities
     */
    protected abstract void setToolbarTitle();

    /**
     * Provide Content View of activity
     *
     * @return Content View
     */
    protected abstract int provideContentView();


    /**
     * Initialize All Views Here.
     */
    protected abstract void initViews();

    /**
     * Initialize All Variables Here.
     */
    protected abstract void initVariables();
}
