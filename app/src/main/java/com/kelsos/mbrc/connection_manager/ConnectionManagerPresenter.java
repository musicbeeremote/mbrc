package com.kelsos.mbrc.connection_manager;

import com.kelsos.mbrc.data.ConnectionSettings;
import com.kelsos.mbrc.presenters.Presenter;

public interface ConnectionManagerPresenter extends Presenter<ConnectionManagerView> {
  void load();

  void setDefault(ConnectionSettings settings);

  void update(ConnectionSettings settings);

  void delete(ConnectionSettings settings);
}
