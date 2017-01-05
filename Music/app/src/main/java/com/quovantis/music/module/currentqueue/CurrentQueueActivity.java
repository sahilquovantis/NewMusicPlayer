package com.quovantis.music.module.currentqueue;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.format.DateUtils;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.quovantis.music.R;
import com.quovantis.music.application.MusicApplication;
import com.quovantis.music.constants.IntentKeys;
import com.quovantis.music.helper.MusicHelper;
import com.quovantis.music.helper.QueueItemTouchHelper;
import com.quovantis.music.models.SongsModel;
import com.quovantis.music.module.base.activity.BaseActivity;

import java.util.Collections;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CurrentQueueActivity extends BaseActivity implements SeekBar.OnSeekBarChangeListener, CurrentQueueAdapter.ICurrentQueueClickListener {

    @BindView(R.id.rv_current_playlist)
    RecyclerView mCurrentSongsRV;
    @BindView(R.id.seekbar)
    SeekBar mSongDurationSB;
    @BindView(R.id.tv_current_duration)
    TextView mCurrentTimeTV;
    @BindView(R.id.tv_final_duration)
    TextView mFinalTimeTV;
    private CurrentQueueAdapter mAdapter;

    @Override
    protected void onCreatingBase(Bundle savedInstanceState) {
        ButterKnife.bind(this);
    }

    @Override
    protected void initBundle(Intent intent) {

    }

    @Override
    protected void setToolbarTitle() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(" ");
        }
    }

    @Override
    protected int provideContentView() {
        return R.layout.activity_current_queue;
    }

    @Override
    protected void initViews() {
        mSongDurationSB.setOnSeekBarChangeListener(this);
        mCurrentSongsRV.setLayoutManager(new LinearLayoutManager(this));
        mCurrentSongsRV.setAdapter(mAdapter);
        ItemTouchHelper.Callback callback = new QueueItemTouchHelper(mAdapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(mCurrentSongsRV);
    }

    @Override
    protected void initVariables() {
        mAdapter = new CurrentQueueAdapter(MusicHelper.getInstance().getCurrentQueue(), this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mProgressReceiver, new IntentFilter(IntentKeys.UPDATE_PROGRESS));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mProgressReceiver);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        mCurrentTimeTV.setText(DateUtils.formatElapsedTime(i / 1000));
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        int progress = seekBar.getProgress();
        if (mPresenter != null) {
            mPresenter.seekTo(progress);
            mCurrentTimeTV.setText(DateUtils.formatElapsedTime(progress / 1000));
        }
    }

    private BroadcastReceiver mProgressReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                long current = intent.getLongExtra(IntentKeys.CURRENT_PROGRESS, 0);
                long total = intent.getLongExtra(IntentKeys.TOTAL_PROGRESS, 0);
                mSongDurationSB.setProgress((int) current);
                mSongDurationSB.setMax((int) total);
                mCurrentTimeTV.setText(DateUtils.formatElapsedTime(current / 1000));
                mFinalTimeTV.setText(DateUtils.formatElapsedTime(total / 1000));
            }
        }
    };

    @Override
    public void onClick(int pos) {
        MusicHelper.getInstance().setCurrentPosition(pos);
        mPresenter.playSong();
    }

    @Override
    public void onSongRemove(int pos) {
        MusicHelper.getInstance().getCurrentQueue().remove(pos);
        mAdapter.notifyItemRemoved(pos);
        int curPos = MusicHelper.getInstance().getCurrentPosition();
        if (pos < curPos) {
            MusicHelper.getInstance().setCurrentPosition(curPos - 1);
        } else if (curPos == pos) {
            if (curPos == MusicHelper.getInstance().getCurrentQueue().size()) {
                MusicHelper.getInstance().setCurrentPosition(curPos - 1);
            }
            mPresenter.playSong();
        }
    }

    @Override
    public void onSongsMoved(int from, int to) {
        int pos = MusicHelper.getInstance().getCurrentPosition();
        Collections.swap(MusicHelper.getInstance().getCurrentQueue(), from, to);
        if (pos == to)
            pos = from;
        else if (pos == from)
            pos = to;
        MusicHelper.getInstance().setCurrentPosition(pos);
        mAdapter.notifyItemMoved(from, to);
    }

    @Override
    protected void updateEqualizerState(int state) {
        if (state == PlaybackStateCompat.STATE_PLAYING) {
            mAdapter.setIsPlaying(true);
        } else {
            mAdapter.setIsPlaying(false);
        }
        mAdapter.notifyItemChanged(MusicHelper.getInstance().getCurrentPosition());
        mAdapter.notifyItemChanged(MusicHelper.getInstance().getPreviousPosition());
    }

    @Override
    public void onOptionsIconClick(SongsModel model, int songPosition) {

    }
}