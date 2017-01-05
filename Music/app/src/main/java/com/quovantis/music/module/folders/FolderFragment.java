package com.quovantis.music.module.folders;


import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import com.quovantis.music.R;
import com.quovantis.music.appcontroller.AppActionController;
import com.quovantis.music.constants.IntentKeys;
import com.quovantis.music.models.FoldersModel;
import com.quovantis.music.models.SongsModel;
import com.quovantis.music.module.base.fragment.BaseFragment;
import com.quovantis.music.module.songs.SongsActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link BaseFragment} subclass.
 */
public class FolderFragment extends BaseFragment implements IFolders.View, FolderAdapter.IOnFolderClickedListener {

    @BindView(R.id.rv_folders)
    RecyclerView mFoldersRV;
    @BindView(R.id.progress_bar)
    ProgressBar mProgressBar;
    private FolderAdapter mAdapter;
    private List<FoldersModel> mFoldersList;

    public FolderFragment() {
    }

    @Override
    protected void initBundle(Bundle bundle) {

    }

    @Override
    protected void initVariables() {
        mFoldersList = new ArrayList<>();
        mAdapter = new FolderAdapter(getActivity(), mFoldersList, this);
        mFoldersRV.setAdapter(mAdapter);
        showProgress();
    }

    @Override
    protected void initViews(View view) {
        ButterKnife.bind(this, view);
        mFoldersRV.setLayoutManager(new GridLayoutManager(getActivity(), 2));
    }

    @Override
    protected int getContentView() {
        return R.layout.fragment_folder;
    }

    @Override
    public void onGettingFolders(List<FoldersModel> foldersList) {
        hideProgress();
        mFoldersList.clear();
        mFoldersList.addAll(foldersList);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void showProgress() {
        mProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        mProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void onFolderClicked(FoldersModel folder) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(IntentKeys.FOLDER_MODEL_KEY, folder);
        new AppActionController.Builder()
                .from(getActivity())
                .setTargetActivity(SongsActivity.class)
                .setBundle(bundle)
                .build()
                .execute();
    }

    @Override
    public void onOptionsClicked(List<SongsModel> list, String title) {
        IFolderClickedListener listener = (IFolderClickedListener) getActivity();
        listener.showOptionsDialog(list, title);
    }

    public interface IFolderClickedListener {
        void showOptionsDialog(List<SongsModel> list, String title);
    }
}