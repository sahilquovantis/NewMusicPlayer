package com.quovantis.music.module.allsongs;

import android.content.ContentUris;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.quovantis.music.R;
import com.quovantis.music.constants.AppConstants;
import com.quovantis.music.models.SongsModel;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Adapter for displaying songs
 */
public class SongsAdapter extends RecyclerView.Adapter<SongsAdapter.ViewHolder> {
    private List<SongsModel> mSongsList;
    private Context mContext;
    private IOnSongClickedListener mListener;

    public SongsAdapter(List<SongsModel> mSongsList, Context mContext, IOnSongClickedListener listener) {
        this.mSongsList = mSongsList;
        this.mContext = mContext;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_songs_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final int pos = position;
        SongsModel model = mSongsList.get(position);
        holder.mSongNameTV.setText(model.getSongTitle());
        holder.mSongSingerTV.setText(model.getSongArtist());
        Glide.with(mContext)
                .load(ContentUris.withAppendedId(AppConstants.ALBUM_URI, model.getAlbumId()))
                .asBitmap()
                .placeholder(R.drawable.music)
                .into(holder.mSongThumbnailIV);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onSongClicked(mSongsList, pos);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mSongsList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_song_name)
        TextView mSongNameTV;
        @BindView(R.id.tv_song_singer)
        TextView mSongSingerTV;
        @BindView(R.id.iv_song_thumbnail)
        ImageView mSongThumbnailIV;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface IOnSongClickedListener {
        void onSongClicked(List<SongsModel> songsList, int currentSongPos);
    }
}
