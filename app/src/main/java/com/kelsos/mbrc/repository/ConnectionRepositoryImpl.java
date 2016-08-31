package com.kelsos.mbrc.repository;

import android.content.SharedPreferences;
import android.content.res.Resources;

import com.kelsos.mbrc.R;
import com.kelsos.mbrc.data.ConnectionSettings;
import com.kelsos.mbrc.data.ConnectionSettings_Table;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.List;

import javax.inject.Inject;

public class ConnectionRepositoryImpl implements ConnectionRepository {

  private SharedPreferences preferences;
  private Resources resources;

  @Inject
  public ConnectionRepositoryImpl(SharedPreferences preferences, Resources resources) {
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
    long oldId = settings.getId();

    settings.delete();

    if (oldId != getDefaultId()) {
      return;
    }

    long count = count();
    if (count == 0) {
      setDefaultId(-1);
    } else {
      ConnectionSettings before = getItemBefore(oldId);
      if (before != null) {
        setDefault(before);
      } else {
        setDefault(getFirst());
      }
    }
  }

  private ConnectionSettings getItemBefore(long id) {
    return SQLite.select()
        .from(ConnectionSettings.class)
        .where(ConnectionSettings_Table.id.lessThan(id))
        .orderBy(ConnectionSettings_Table.id, false)
        .querySingle();
  }

  private ConnectionSettings getFirst() {
    return SQLite.select()
        .from(ConnectionSettings.class)
        .orderBy(ConnectionSettings_Table.id, true)
        .querySingle();
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

  @Override
  public long getDefaultId() {
    String key = resources.getString(R.string.settings_key_default_index);
    return this.preferences.getLong(key, 0);
  }

  @Override
  public void setDefault(ConnectionSettings settings) {
    if (settings == null) {
      return;
    }

    setDefaultId(settings.getId());
  }

  private void setDefaultId(long id) {
    String key = resources.getString(R.string.settings_key_default_index);
    this.preferences.edit().putLong(key, id).apply();
  }

  @Override
  public List<ConnectionSettings> getAll() {
   return SQLite.select().from(ConnectionSettings.class).queryList();
  }

  @Override
  public long count() {
    return SQLite.selectCountOf().from(ConnectionSettings.class).count();
  }

}
