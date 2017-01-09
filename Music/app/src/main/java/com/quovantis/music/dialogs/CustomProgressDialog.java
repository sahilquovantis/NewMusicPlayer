package com.quovantis.music.dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;

import com.quovantis.music.R;

/**
 * Show Progress Dialog
 */

public class CustomProgressDialog extends AlertDialog {
    public CustomProgressDialog(Context context) {
        super(context);
        setDialogTitle();
        setCancelable(false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_progress_dialog);
    }

    public void setMessage(String message) {
        TextView messageTV = (TextView) findViewById(R.id.tv_progress_dialog_message);
        messageTV.setText(message);
    }

    private void setDialogTitle() {
        Window window = getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            requestWindowFeature(Window.FEATURE_NO_TITLE);
        }
    }
}
