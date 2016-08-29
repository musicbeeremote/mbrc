package com.kelsos.mbrc;

import android.content.SharedPreferences;
import android.content.res.Resources;

import com.kelsos.mbrc.data.ConnectionSettings;
import com.kelsos.mbrc.data.ConnectionSettings_Table;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.Collection;

import javax.inject.Inject;

public class SettingsRepositoryImpl implements SettingsRepository {

  private SharedPreferences preferences;
  private Resources resources;

  @Inject
  public SettingsRepositoryImpl(SharedPreferences preferences, Resources resources) {
    this.preferences = preferences;
    this.resources = resources;
  }

  @Override
  public void save(ConnectionSettings settings) {
    settings.save();
    if (count() == 1) {
      setDefault(settings);
    }
  }

  @Override
  public void delete(ConnectionSettings settings) {
    if (settings.getId() == getDefaultId()) {
      // TODO: 8/29/2016 check previous or next value to set as default
    }
    settings.delete();
  }

  @Override
  public void update(ConnectionSettings settings) {
    settings.update();
  }

  @Override
  public ConnectionSettings getDefault() {
    long defaultId = getDefaultId();
    if (defaultId < 0) {
      return null;
    }

    return SQLite.select()
        .from(ConnectionSettings.class)
        .where(ConnectionSettings_Table.id.is(defaultId))
        .querySingle();
  }

  private long getDefaultId() {
    String key = resources.getString(R.string.settings_key_default_index);
    return this.preferences.getLong(key, 0);
  }

  @Override
  public void setDefault(ConnectionSettings settings) {
    if (settings == null) {
      return;
    }

    String key = resources.getString(R.string.settings_key_default_index);
    this.preferences.edit().putLong(key, settings.getId()).apply();
  }

  @Override
  public Collection<ConnectionSettings> getAll() {
   return SQLite.select().from(ConnectionSettings.class).queryList();
  }

  @Override
  public long count() {
    return SQLite.selectCountOf().from(ConnectionSettings.class).count();
  }

}
