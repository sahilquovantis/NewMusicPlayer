package com.quovantis.music.module.playlist;

import com.quovantis.music.models.UserPlaylistModel;

import java.util.List;

/**
 * Interface Playlist {MVP}
 */

public interface IPlaylist {
    interface View {
        void showProgress();

        void hideProgress();

        void onGettingPlaylist(List<UserPlaylistModel> list);
    }

    interface Presenter {
        void getUserPlaylist();

        void destroy();
    }

    interface Interactor {
        void getUserPlaylist(IPlaylist.Interactor.Listener listener);

        interface Listener {
            void onGettingPlaylist(List<UserPlaylistModel> list);

            void onFailure();
        }
    }
}
