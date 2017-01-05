package com.quovantis.music.interfaces;

import com.quovantis.music.models.SongsModel;

import java.util.List;

/**
 * Interface between HomeActivity and CursorHelper class.
 * Used for sending results back to the HomeActivity from CursorHelper class
 */
public interface IHomeAndCursorBridge {
    void onGettingAllSongs(List<SongsModel> songsList);
}
