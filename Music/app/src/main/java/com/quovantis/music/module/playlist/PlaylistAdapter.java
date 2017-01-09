package com.quovantis.music.module.playlist;

import android.content.ContentUris;
import android.content.Context;
import android.net.Uri;
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
import com.quovantis.music.models.UserPlaylistModel;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Used for displaying user playlist
 */

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.ViewHolder> {
    private List<UserPlaylistModel> mPlaylistList;
    private Context mContext;
    private IOnUserPlaylistClickedListener mListener;

    public PlaylistAdapter(Context context, List<UserPlaylistModel> mPlaylistList, IOnUserPlaylistClickedListener listener) {
        mContext = context;
        this.mPlaylistList = mPlaylistList;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_user_playlist_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        UserPlaylistModel model = mPlaylistList.get(position);
        List<SongsModel> songList = model.getPlaylist();
        holder.mDivider.setVisibility(View.VISIBLE);
        holder.mPlaylistNameTV.setText(model.getPlaylistName());
        holder.mTracksTV.setVisibility(View.GONE);
        int tracks = songList == null ? 0 : songList.size();
        if (tracks > 0) {
            Uri uri = ContentUris.withAppendedId(AppConstants.ALBUM_URI, songList.get(0).getAlbumId());
            Glide.with(mContext)
                    .load(uri)
                    .asBitmap()
                    .error(R.drawable.music)
                    .into(holder.mThumbnailIV);
            String trackDetails = tracks + " Tracks";
            if (tracks == 1)
                trackDetails = tracks + " Track";
            holder.mTracksTV.setText(trackDetails);
            holder.mTracksTV.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return mPlaylistList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        @BindView(R.id.tv_user_playlist_name)
        TextView mPlaylistNameTV;
        @BindView(R.id.tv_playlist_tracks)
        TextView mTracksTV;
        @BindView(R.id.iv_song_thumbnail)
        ImageView mThumbnailIV;
        @BindView(R.id.divider)
        View mDivider;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mListener.onUserPlaylistClicked(mPlaylistList.get(getAdapterPosition()));
        }

        @Override
        public boolean onLongClick(View view) {
            mListener.showPlaylistOptionsDialog(mPlaylistList.get(getAdapterPosition()));
            return true;
        }
    }

    public interface IOnUserPlaylistClickedListener {
        void onUserPlaylistClicked(UserPlaylistModel model);

        void showPlaylistOptionsDialog(UserPlaylistModel model);
    }
}