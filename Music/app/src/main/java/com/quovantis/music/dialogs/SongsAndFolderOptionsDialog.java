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
import com.quovantis.music.models.SongsModel;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * This class creates custom dialog which is used for menu options for folders and songs.
 */

public class SongsAndFolderOptionsDialog extends AlertDialog {

    private List<SongsModel> mSongsList;
    private IOptionsDialogListener mListener;

    public SongsAndFolderOptionsDialog(Context context, List<SongsModel> songsList, IOptionsDialogListener listener) {
        super(context);
        mSongsList = songsList;
        mListener = listener;
        setDialogTitle();
        setCancelable(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_options_dialog);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.ll_add_to_playlist, R.id.ll_add_to_queue, R.id.ll_play_and_clear})
    void onClick(LinearLayout layout) {
        switch (layout.getId()) {
            case R.id.ll_add_to_playlist:
                mListener.onAddToPlaylist(mSongsList);
                dismiss();
                break;
            case R.id.ll_add_to_queue:
                mListener.onAddToQueue(mSongsList, false, false);
                dismiss();
                break;
            case R.id.ll_play_and_clear:
                mListener.onAddToQueue(mSongsList, true, true);
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
}