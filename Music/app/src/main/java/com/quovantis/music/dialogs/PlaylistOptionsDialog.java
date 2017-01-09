package com.quovantis.music.dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.quovantis.music.R;
import com.quovantis.music.interfaces.IOptionsDialogListener;
import com.quovantis.music.models.UserPlaylistModel;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Options for playlist like rename, delete etc.
 */

public class PlaylistOptionsDialog extends AlertDialog {
    private IPlaylistOptionsDialog mListener;
    private UserPlaylistModel mPlaylistModel;

    public PlaylistOptionsDialog(Context context, UserPlaylistModel model, IPlaylistOptionsDialog listener) {
        super(context);
        mPlaylistModel = model;
        mListener = listener;
        setDialogTitle();
        setCancelable(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_playlist_options_dialog);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.ll_add_to_playlist, R.id.ll_add_to_queue, R.id.ll_play_and_clear, R.id.ll_rename_playlist, R.id.ll_delete_playlist})
    void onClick(LinearLayout layout) {
        switch (layout.getId()) {
            case R.id.ll_add_to_playlist:
                mListener.onAddToPlaylist(mPlaylistModel.getPlaylist());
                dismiss();
                break;
            case R.id.ll_add_to_queue:
                mListener.onAddToQueue(mPlaylistModel.getPlaylist(), false, false);
                dismiss();
                break;
            case R.id.ll_play_and_clear:
                mListener.onAddToQueue(mPlaylistModel.getPlaylist(), true, true);
                dismiss();
                break;
            case R.id.ll_rename_playlist:
                mListener.onRenamePlaylist(mPlaylistModel);
                dismiss();
                break;
            case R.id.ll_delete_playlist:
                mListener.onDeletePlaylist(mPlaylistModel);
                dismiss();
                break;
        }
    }

    public void setMessage(String msg) {
        TextView titleTV = (TextView) findViewById(R.id.tv_dialog_title);
        titleTV.setText(msg);
    }

    private void setDialogTitle() {
        Window window = getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            requestWindowFeature(Window.FEATURE_NO_TITLE);
        }
    }

    public interface IPlaylistOptionsDialog extends IOptionsDialogListener {
        void onRenamePlaylist(UserPlaylistModel model);

        void onDeletePlaylist(UserPlaylistModel model);
    }
}