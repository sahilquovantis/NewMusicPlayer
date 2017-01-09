package com.quovantis.music.dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.quovantis.music.R;
import com.quovantis.music.models.SongsModel;
import com.quovantis.music.models.UserPlaylistModel;
import com.quovantis.music.utility.Utils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Custom Create Playlist Dialog
 */
class PlaylistNameDialog extends AlertDialog implements View.OnClickListener {

    @BindView(R.id.et_playlist_name)
    EditText mPlaylistNameET;
    @BindView(R.id.tv_cancel)
    TextView mCancelTV;
    @BindView(R.id.tv_create_playlist)
    TextView mCreatePlaylistTV;
    private List<SongsModel> mSongsList;
    private IOnCreatePlaylistDialogListener iOnCreatePlaylistListener;

    PlaylistNameDialog(Context context, List<SongsModel> songsList, IOnCreatePlaylistDialogListener playlistDialog) {
        super(context);
        mSongsList = songsList;
        iOnCreatePlaylistListener = playlistDialog;
        setDialogTitle();
        setCancelable(true);
    }

    private void setDialogTitle() {
        Window window = getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            requestWindowFeature(Window.FEATURE_NO_TITLE);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_playlist_name_dialog);
        ButterKnife.bind(this);
        mCancelTV.setOnClickListener(this);
        mCreatePlaylistTV.setOnClickListener(this);
        mPlaylistNameET.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == R.id.done || i == EditorInfo.IME_ACTION_DONE) {
                    Utils.hideKeyboard(mPlaylistNameET);
                    mCreatePlaylistTV.performClick();
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.tv_cancel:
                dismiss();
                break;
            case R.id.tv_create_playlist:
                String playlistName = mPlaylistNameET.getText().toString();
                if (playlistName.trim().length() > 0) {
                    iOnCreatePlaylistListener.onCreatePlaylist(mSongsList, playlistName.trim());
                    dismiss();
                } else
                    mPlaylistNameET.setError("Can not be blanked");
                break;
        }
    }

    interface IOnCreatePlaylistDialogListener {
        void onCreatePlaylist(List<SongsModel> songsList, String playlistName);
    }
}