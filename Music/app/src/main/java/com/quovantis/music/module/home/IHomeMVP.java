package com.quovantis.music.module.home;

import android.app.Activity;

import com.quovantis.music.models.FoldersModel;
import com.quovantis.music.models.SongsModel;

import java.util.List;

/**
 * Interface for Home Activity (MVP)
 */
interface IHomeMVP {
    interface View {
        void onGettingAllSongs(List<SongsModel> songsList);

        void onGettingAllFolders(List<FoldersModel> foldersList);
    }

    interface Presenter {
        void getAllSongsAndFolders(Activity activity);

        void destroy();
    }

    interface Model {
        void getAllSongsAndFolders(Activity activity);

        interface Listener {
            void onGettingAllSongs(List<SongsModel> songsList);

            void onGettingAllFolders(List<FoldersModel> foldersList);
        }
    }
}
