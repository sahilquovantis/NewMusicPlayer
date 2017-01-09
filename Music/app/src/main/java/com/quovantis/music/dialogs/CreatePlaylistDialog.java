package com.quovantis.music.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.quovantis.music.R;
import com.quovantis.music.helper.LoggerHelper;
import com.quovantis.music.models.SongsModel;
import com.quovantis.music.models.UserPlaylistModel;
import com.quovantis.music.module.playlist.IPlaylist;
import com.quovantis.music.module.playlist.PlaylistAdapter;
import com.quovantis.music.module.playlist.PlaylistPresenter;
import com.quovantis.music.utility.Utils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Custom dialog for creating new playlist or adding songs to existing playlist
 */

public class CreatePlaylistDialog extends AlertDialog implements IPlaylist.View, PlaylistAdapter.IOnUserPlaylistClickedListener, PlaylistNameDialog.IOnCreatePlaylistDialogListener {
    @BindView(R.id.rv_playlist)
    RecyclerView mPlaylistRV;
    @BindView(R.id.progress_bar)
    ProgressBar mProgressBar;
    @BindView(R.id.ll_main_layout_dialog)
    LinearLayout mDialogMainLayoutLL;
    private List<SongsModel> mSongsList;
    private Context mContext;
    private IPlaylist.Presenter mPresenter;
    private PlaylistAdapter mAdapter;
    private List<UserPlaylistModel> mPlaylistList;
    private IOnCreatingPlaylist mListener;

    public CreatePlaylistDialog(List<SongsModel> mSongsList, Context context, IOnCreatingPlaylist listener) {
        super(context);
        mListener = listener;
        this.mSongsList = mSongsList;
        mContext = context;
        setDialogTitle();
        setCancelable(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_creating_playlist_dialog);
        ButterKnife.bind(this);

        mPlaylistList = new ArrayList<>();
        mAdapter = new PlaylistAdapter(mContext, mPlaylistList, this);
        mPresenter = new PlaylistPresenter(this);
        mPlaylistRV.setLayoutManager(new LinearLayoutManager(mContext));
        mPlaylistRV.setAdapter(mAdapter);
        mPresenter.getUserPlaylist();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        setMaxHeight();
    }


    @OnClick(R.id.ll_create_new)
    void onClick(LinearLayout linearLayout) {
        switch (linearLayout.getId()) {
            case R.id.ll_create_new:
                Dialog dialog = new PlaylistNameDialog(mContext, mSongsList, this);
                dialog.show();
                if (dialog.getWindow() != null)
                    dialog.getWindow().clearFlags(
                            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                                    | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
                dismiss();
                break;
        }
    }

    private void setDialogTitle() {
        Window window = getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            requestWindowFeature(Window.FEATURE_NO_TITLE);
        }
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
        mPlaylistRV.setVisibility(View.VISIBLE);
        mPlaylistList.clear();
        mPlaylistList.addAll(list);
        mAdapter.notifyDataSetChanged();
        setMaxHeight();
    }

    private void setMaxHeight() {
        int height = Utils.getHeight(mContext);
        int layoutHeight = mDialogMainLayoutLL.getHeight();
        LoggerHelper.d("Device Height : " + height);
        LoggerHelper.d("Layout Height : " + layoutHeight);
        Window window = getWindow();
        if (layoutHeight == 0 || window == null) {
            return;
        }
        if (mDialogMainLayoutLL.getHeight() < height / 1.5) {
            window.setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        } else {
            window.setLayout(LinearLayout.LayoutParams.WRAP_CONTENT,  (int)(height / 1.5));
        }
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mPresenter != null)
            mPresenter.destroy();
    }

    @Override
    public void onUserPlaylistClicked(UserPlaylistModel model) {
        mListener.addSongsToExistingPlaylist(mSongsList, model);
        dismiss();
    }

    @Override
    public void showPlaylistOptionsDialog(UserPlaylistModel model) {

    }

    @Override
    public void onCreatePlaylist(List<SongsModel> songsList, String playlistName) {
        mListener.createNewPlaylist(songsList, playlistName);
    }

    public interface IOnCreatingPlaylist {
        void createNewPlaylist(List<SongsModel> songsList, String playlistName);

        void addSongsToExistingPlaylist(List<SongsModel> mSongsList, UserPlaylistModel model);
    }
}