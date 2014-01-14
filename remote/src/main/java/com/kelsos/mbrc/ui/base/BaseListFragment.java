package com.kelsos.mbrc.ui.base;

import com.google.inject.Inject;
import roboguice.RoboGuice;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;

public abstract class BaseListFragment extends ListFragment {
    @Inject private ScopedBus scopedBus;
    protected ScopedBus getBus() {
        return scopedBus;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RoboGuice.getInjector(getActivity()).injectMembersWithoutViews(this);
        scopedBus.register(this);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RoboGuice.getInjector(getActivity()).injectViewMembers(this);
    }

    @Override public void onPause() {
        super.onPause();
        scopedBus.paused();
    }

    @Override public void onResume() {
        super.onResume();
        scopedBus.resumed();
    }

    @Override public void onDestroy() {
        scopedBus.unregister(this);
        super.onDestroy();
    }
}
