package com.quovantis.music.module.folders;

import com.quovantis.music.models.FoldersModel;

import java.util.List;

/**
 * Interface Folders Fragment (MVP)
 */

public interface IFolders {
    interface View {
        void onGettingFolders(List<FoldersModel> foldersList);

        void showProgress();

        void hideProgress();
    }
}
