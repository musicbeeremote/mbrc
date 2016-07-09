package com.kelsos.mbrc.presenters;

import com.kelsos.mbrc.views.BaseView;

public interface BasePresenter<T extends BaseView> {
  void attach(T view);

  void detach();

  boolean isAttached();
}
