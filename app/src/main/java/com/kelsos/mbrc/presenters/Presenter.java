package com.kelsos.mbrc.presenters;

import com.kelsos.mbrc.views.BaseView;

public interface Presenter<T extends BaseView> {
  void attach(T view);

  void detach();
}
