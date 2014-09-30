package com.kelsos.mbrc.ui.base;

import android.os.Bundle;
import android.view.View;
import com.google.inject.Inject;
import roboguice.fragment.provided.RoboListFragment;

public abstract class BaseListFragment extends RoboListFragment {
    @Inject private ScopedBus scopedBus;
    protected ScopedBus getBus() {
        return scopedBus;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        scopedBus.register(this);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
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
