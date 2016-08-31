package com.kelsos.mbrc.connection_manager;

import com.kelsos.mbrc.views.BaseView;

public interface ConnectionManagerView extends BaseView {
  void updateModel(ConnectionModel connectionModel);
}
