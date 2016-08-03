package com.kelsos.mbrc.presenters;

import com.kelsos.mbrc.model.MainDataModel;
import com.kelsos.mbrc.views.MiniControlView;
import javax.inject.Inject;

public class MiniControlPresenter implements BasePresenter<MiniControlView> {

  @Inject MainDataModel model;
  private MiniControlView view;

  public MiniControlPresenter() {

  }

  public void load() {
    if (!isAttached()) {
      return;
    }
    view.updateCover(model.getCover());
    view.updateState(model.getPlayState());
    view.updateTrackInfo(model.getTrackInfo());
  }

  @Override
  public void attach(MiniControlView view) {
    this.view = view;
  }

  @Override
  public void detach() {
    this.view = null;
  }

  @Override
  public boolean isAttached() {
    return view != null;
  }
}
