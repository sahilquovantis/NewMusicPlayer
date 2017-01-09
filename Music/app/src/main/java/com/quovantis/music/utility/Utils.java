package com.quovantis.music.utility;

import android.app.Activity;
import android.content.Context;
import android.support.design.widget.Snackbar;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * Utility class
 */

public class Utils {
    public static void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static int getHeight(Context context) {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        return displaymetrics.heightPixels;
    }

    public static void showKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(view, 0);
    }

    public static void showSnackBar(View view, String msg) {
        Snackbar.make(view, msg, Snackbar.LENGTH_LONG).show();
    }
}
