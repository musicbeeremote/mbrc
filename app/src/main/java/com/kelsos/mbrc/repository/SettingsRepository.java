package com.kelsos.mbrc.repository;

import com.kelsos.mbrc.data.ConnectionSettings;

import java.util.Collection;

public interface SettingsRepository {
  void save(ConnectionSettings settings);

  void delete(ConnectionSettings settings);

  void update(ConnectionSettings settings);

  ConnectionSettings getDefault();

  void setDefault(ConnectionSettings settings);

  Collection<ConnectionSettings> getAll();

  long count();

  long getDefaultId();
}
