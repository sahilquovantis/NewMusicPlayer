package com.quovantis.music.application;

import android.app.Application;

import com.quovantis.music.R;
import com.wonderkiln.blurkit.BlurKit;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Custom Application class
 */

public class MusicApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
        BlurKit.init(this);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/robotolight.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
    }
}
