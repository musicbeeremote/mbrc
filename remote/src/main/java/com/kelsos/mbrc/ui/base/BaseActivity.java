package com.kelsos.mbrc.ui.base;

import android.os.Bundle;
import com.google.inject.Inject;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import roboguice.activity.RoboActionBarActivity;

public abstract class BaseActivity extends RoboActionBarActivity {

    @Inject
    private ScopedBus scopedBus;
    protected ScopedBus getBus() {
        return scopedBus;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        scopedBus.register(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        scopedBus.resumed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        scopedBus.paused();
    }

    @Override
    protected void onDestroy() {
        try {
            scopedBus.unregister(this);
            Crouton.cancelAllCroutons();
        } finally {
            super.onDestroy();
        }
    }

}
