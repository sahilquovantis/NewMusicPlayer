package com.quovantis.music.module.base.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.quovantis.music.R;

import butterknife.ButterKnife;

/**
 * Base fragment for all other fragments
 */

public abstract class BaseFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(getContentView(), container, false);
        initViews(view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle bundle = getArguments();
        initBundle(bundle);
        initVariables();
    }

    protected abstract void initBundle(Bundle bundle);

    protected abstract void initVariables();

    protected abstract void initViews(View view);

    protected abstract int getContentView();
}
