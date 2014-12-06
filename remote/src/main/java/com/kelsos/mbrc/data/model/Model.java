package com.kelsos.mbrc.data.model;

import android.content.Context;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.kelsos.mbrc.rest.RemoteApi;
import roboguice.util.Ln;

@Singleton
public class Model {
    public static final String EMPTY = "";

    private String pluginVersion;

    @Inject
    private TrackStateModel trackStateModel;

    @Inject
    private PlayerState playerState;

    @Inject
    private RemoteApi api;

    @Inject
    public Model(Context context, RemoteApi api) {
        pluginVersion = EMPTY;
        Ln.d("Model instantiated");

    }

    public String getPluginVersion() {
        return pluginVersion;
    }

    public void setPluginVersion(String pluginVersion) {
        this.pluginVersion = pluginVersion.substring(0, pluginVersion.lastIndexOf('.'));
    }
}

