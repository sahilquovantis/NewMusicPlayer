package com.quovantis.music.module.home;

import android.app.Activity;

import com.quovantis.music.helper.CursorHelper;
import com.quovantis.music.interfaces.IHomeAndCursorBridge;
import com.quovantis.music.models.CursorModel;
import com.quovantis.music.models.FoldersModel;
import com.quovantis.music.models.SongsModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Class fetches all songs with the help of cursor helper
 * <p/>{@link com.quovantis.music.helper.CursorHelper}
 */
class HomeInteractor implements IHomeMVP.Model, IHomeAndCursorBridge {
    private IHomeMVP.Model.Listener mListener;
    private final int ALL_SONGS = 1;
    private final int PARTICULAR_FOLDER_SONGS = 2;

    HomeInteractor(Listener mListener) {
        this.mListener = mListener;
    }

    @Override
    public void getAllSongsAndFolders(Activity activity) {
        CursorModel model = new CursorModel();
        model.setBundle(null);
        model.setLoaderId(ALL_SONGS);
        model.setListener(this);
        model.setActivityWeakReference(activity);
        CursorHelper.getInstance().getSongs(model);
    }

    @Override
    public void onGettingAllSongs(List<SongsModel> songsList) {
        mListener.onGettingAllSongs(songsList);
        getAllFolders(songsList);
    }

    private void getAllFolders(List<SongsModel> songsList) {
        List<FoldersModel> foldersList = null;
        if (songsList != null && songsList.size() > 0) {
            foldersList = new ArrayList<>();
            for (SongsModel song : songsList) {
                String songPath = song.getSongPath();
                String path = songPath.substring(0, songPath.lastIndexOf("/"));
                FoldersModel model = new FoldersModel();
                model.setAlbumId(song.getAlbumId());
                model.setPath(path);
                model.setDirectory(path.substring(path.lastIndexOf("/") + 1));
                int index = foldersList.indexOf(model);
                if (index == -1) {
                    model.addSong(song);
                    foldersList.add(model);
                } else {
                    foldersList.get(index).addSong(song);
                }
            }
        }
        mListener.onGettingAllFolders(foldersList);
    }
}