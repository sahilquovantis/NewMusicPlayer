package com.quovantis.music.module.folders;

import android.content.ContentUris;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.quovantis.music.R;
import com.quovantis.music.constants.AppConstants;
import com.quovantis.music.models.FoldersModel;
import com.quovantis.music.models.SongsModel;
import com.wonderkiln.blurkit.BlurKit;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.ColorFilterTransformation;
import jp.wasabeef.glide.transformations.GrayscaleTransformation;

/**
 * Folder Adapter used for displaying folders {@link FolderFragment}
 */
class FolderAdapter extends RecyclerView.Adapter<FolderAdapter.ViewHolder> {

    private List<FoldersModel> mFoldersList;
    private Context mContext;
    private IOnFolderClickedListener mListener;

    FolderAdapter(Context context, List<FoldersModel> mFoldersList, IOnFolderClickedListener mListener) {
        mContext = context;
        this.mFoldersList = mFoldersList;
        this.mListener = mListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.custom_folder_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        FoldersModel model = mFoldersList.get(position);
        List<SongsModel> songsList = model.getSongs();
        holder.mDirectoryNameTV.setText(model.getDirectory());
        String tracks;
        if (songsList.size() <= 1)
            tracks = songsList.size() + " Track";
        else
            tracks = songsList.size() + " Tracks";
        holder.mTotalTracksTV.setText(tracks);
        Uri uri = ContentUris.withAppendedId(AppConstants.ALBUM_URI, model.getAlbumId());
        Glide.with(mContext)
                .load(uri)
                .asBitmap()
                .error(R.drawable.music)
                .into(holder.mThumbnailIV);
    }

    @Override
    public int getItemCount() {
        return mFoldersList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        @BindView(R.id.tv_directory_name)
        TextView mDirectoryNameTV;
        @BindView(R.id.iv_folder_image)
        ImageView mThumbnailIV;
        @BindView(R.id.tv_total_tracks)
        TextView mTotalTracksTV;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mListener.onFolderClicked(mFoldersList.get(getAdapterPosition()));
        }

        @Override
        public boolean onLongClick(View view) {
            FoldersModel model = mFoldersList.get(getAdapterPosition());
            mListener.onOptionsClicked(model.getSongs(), model.getDirectory());
            return true;
        }
    }

    /**
     * Folder listener used when folder is clicked.
     */
    interface IOnFolderClickedListener {
        void onFolderClicked(FoldersModel folder);

        void onOptionsClicked(List<SongsModel> list, String title);
    }
}