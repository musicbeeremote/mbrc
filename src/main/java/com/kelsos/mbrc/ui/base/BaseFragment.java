package com.kelsos.mbrc.ui.base;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import com.google.inject.Inject;
import roboguice.RoboGuice;

public abstract class BaseFragment extends Fragment {
    @Inject private ScopedBus scopedBus;
    protected ScopedBus getBus() {
        return scopedBus;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RoboGuice.getInjector(getActivity()).injectMembersWithoutViews(this);
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
}
