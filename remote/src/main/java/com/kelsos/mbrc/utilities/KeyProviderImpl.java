package com.kelsos.mbrc.utilities;

import android.content.Context;
import android.support.annotation.NonNull;
import com.google.inject.Inject;

import com.kelsos.mbrc.R;

public class KeyProviderImpl implements KeyProvider {
    private Context context;

    @Inject
    public KeyProviderImpl(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public String getHostKey() {
        return context.getString(R.string.settings_key_hostname);
    }

    @NonNull
    @Override
    public String getPortKey() {
        return context.getString(R.string.settings_key_port);
    }

    @NonNull
    @Override
    public String getReduceVolumeKey() {
        return context.getString(R.string.settings_key_reduce_volume);
    }

    @NonNull
    @Override
    public String getNotificationKey() {
        return context.getString(R.string.settings_key_notification_control);
    }

    @NonNull
    @Override
    public String getPluginUpdateCheckKey() {
        return context.getString(R.string.settings_key_plugin_check);
    }

    @NonNull
    @Override
    public String getLastUpdateKey() {
        return context.getString(R.string.settings_key_last_update_check);
    }

    @NonNull
    @Override
    public String getSearchActionKey() {
        return context.getString(R.string.settings_search_default_key);
    }

    @NonNull
    @Override
    public String getSearchActionValueKey() {
        return context.getString(R.string.search_click_default_value);
    }

    @NonNull
    @Override
    public String getLastVersionKey() {
        return context.getString(R.string.settings_key_last_version_run);
    }
}
