package com.quovantis.music.module.currentqueue;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.quovantis.music.R;
import com.quovantis.music.helper.MusicHelper;
import com.quovantis.music.interfaces.IItemTouchHelperBridge;
import com.quovantis.music.models.SongsModel;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.claucookie.miniequalizerlibrary.EqualizerView;

/**
 * Adapter for displaying current queue songs
 */
class CurrentQueueAdapter extends RecyclerView.Adapter<CurrentQueueAdapter.ViewHolder> implements IItemTouchHelperBridge {

    private List<SongsModel> mSongsList;
    private ICurrentQueueClickListener mListener;
    private boolean mIsPlaying;

    CurrentQueueAdapter(List<SongsModel> mSongsList, ICurrentQueueClickListener listener) {
        this.mSongsList = mSongsList;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_current_queue_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        SongsModel model = mSongsList.get(position);
        holder.mSongTV.setText(model.getSongTitle());
        holder.mArtistTV.setText(model.getSongArtist());
        holder.mEqualizer.setVisibility(View.GONE);
        if (position == MusicHelper.getInstance().getCurrentPosition()) {
            holder.mEqualizer.setVisibility(View.VISIBLE);
            if (mIsPlaying) {
                holder.mEqualizer.animateBars();
            } else {
                holder.mEqualizer.stopBars();
            }
        }
    }

    @Override
    public int getItemCount() {
        return mSongsList.size();
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        mListener.onSongsMoved(fromPosition, toPosition);
    }

    @Override
    public void onItemDismiss(int position) {
        mListener.onSongRemove(position);
    }

    @Override
    public void onItemMoveCompleted() {

    }

    void setIsPlaying(boolean isPlaying) {
        this.mIsPlaying = isPlaying;
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.tv_song_name)
        TextView mSongTV;
        @BindView(R.id.tv_song_artist)
        TextView mArtistTV;
        @BindView(R.id.iv_song_options)
        ImageView mSongOptionsIV;
        @BindView(R.id.equalizer_view)
        EqualizerView mEqualizer;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mListener.onClick(getAdapterPosition());
        }
    }

    interface ICurrentQueueClickListener {
        void onClick(int pos);

        void onSongRemove(int pos);

        void onSongsMoved(int from, int to);

        void onOptionsIconClick(SongsModel model, int songPosition);
    }
}
