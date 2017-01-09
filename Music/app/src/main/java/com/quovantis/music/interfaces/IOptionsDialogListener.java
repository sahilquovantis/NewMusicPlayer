package com.quovantis.music.interfaces;

import com.quovantis.music.models.SongsModel;

import java.util.List;

/**
 * Listener for clicking options items
 */
public interface IOptionsDialogListener {
    /**
     * This method will add the songs to the user playlist
     *
     * @param songsList Songs List which will be added to the playlist
     */
    void onAddToPlaylist(List<SongsModel> songsList);

    /**
     * This method will add songs to the current queue
     *
     * @param songsList          Songs List which will be added to the queue
     * @param shouldClearQueue   If true, this will clear current queue
     * @param shouldPlayingSongs If true, this will play this songs list
     */
    void onAddToQueue(List<SongsModel> songsList, boolean shouldClearQueue, boolean shouldPlayingSongs);
}
