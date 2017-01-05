package com.quovantis.music.utility;

import android.support.design.widget.Snackbar;
import android.view.View;

/**
 * Utility class
 */

public class Utils {
    public static void showSnackBar(View view, String msg) {
        Snackbar.make(view, msg, Snackbar.LENGTH_LONG).show();
    }
}
