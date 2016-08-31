package com.kelsos.mbrc.presenters;

import com.kelsos.mbrc.model.MainDataModel;
import com.kelsos.mbrc.views.MiniControlView;
import javax.inject.Inject;

public class MiniControlPresenter extends BasePresenter<MiniControlView> {

  @Inject MainDataModel model;

  public MiniControlPresenter() {

  }

  public void load() {
    if (!isAttached()) {
      return;
    }

    getView().updateCover(model.getCover());
    getView().updateState(model.getPlayState());
    getView().updateTrackInfo(model.getTrackInfo());
  }

}
