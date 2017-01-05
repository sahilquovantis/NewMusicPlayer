package com.quovantis.music.module.allsongs;

import android.content.Context;

import com.quovantis.music.models.SongsModel;

import java.util.List;

/**
 * Interface for Songs Fragment (MVP)
 */

public interface ISongs {
    interface View {
        void onGettingSongs(List<SongsModel> songsList);

        void showProgress();

        void hideProgress();
    }
}
