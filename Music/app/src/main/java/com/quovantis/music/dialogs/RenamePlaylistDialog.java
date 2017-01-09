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
import com.quovantis.music.models.UserPlaylistModel;
import com.quovantis.music.utility.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Custom dialog for getting new playlist name
 */

public class RenamePlaylistDialog extends AlertDialog implements View.OnClickListener {

    @BindView(R.id.et_playlist_name)
    EditText mPlaylistNameET;
    @BindView(R.id.tv_cancel)
    TextView mCancelTV;
    @BindView(R.id.tv_rename_playlist)
    TextView mCreatePlaylistTV;
    private IOnRenamePlaylistDialogListener iOnCreatePlaylistListener;
    private UserPlaylistModel mUserPlaylistModel;

    public RenamePlaylistDialog(Context context, UserPlaylistModel model, IOnRenamePlaylistDialogListener playlistDialog) {
        super(context);
        mUserPlaylistModel = model;
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
        setContentView(R.layout.custom_rename_playlist_dialog);
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
        mPlaylistNameET.setText(mUserPlaylistModel.getPlaylistName());
        mPlaylistNameET.setSelection(mUserPlaylistModel.getPlaylistName().length());
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.tv_cancel:
                dismiss();
                break;
            case R.id.tv_rename_playlist:
                String playlistName = mPlaylistNameET.getText().toString();
                if (playlistName.trim().length() > 0) {
                    iOnCreatePlaylistListener.onRenamePlaylist(mUserPlaylistModel, playlistName.trim());
                    dismiss();
                } else
                    mPlaylistNameET.setError("Can not be blanked");
                break;
        }
    }

    public interface IOnRenamePlaylistDialogListener {
        void onRenamePlaylist(UserPlaylistModel model, String playlistName);
    }
}