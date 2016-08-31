package com.kelsos.mbrc.repository;

import com.kelsos.mbrc.data.ConnectionSettings;

import java.util.List;

public interface ConnectionRepository {
  void save(ConnectionSettings settings);

  void delete(ConnectionSettings settings);

  void update(ConnectionSettings settings);

  ConnectionSettings getDefault();

  void setDefault(ConnectionSettings settings);

  List<ConnectionSettings> getAll();

  long count();

  long getDefaultId();
}
