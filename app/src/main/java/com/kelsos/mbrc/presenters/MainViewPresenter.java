package com.kelsos.mbrc.presenters;

import com.kelsos.mbrc.constants.Const;
import com.kelsos.mbrc.constants.Protocol;
import com.kelsos.mbrc.constants.ProtocolEventType;
import com.kelsos.mbrc.data.UserAction;
import com.kelsos.mbrc.events.MessageEvent;
import com.kelsos.mbrc.events.bus.RxBus;
import com.kelsos.mbrc.model.ConnectionModel;
import com.kelsos.mbrc.model.MainDataModel;
import com.kelsos.mbrc.views.MainView;
import javax.inject.Inject;
import timber.log.Timber;

public class MainViewPresenter implements BasePresenter<MainView> {
  private MainView view;
  @Inject RxBus bus;
  @Inject MainDataModel model;
  @Inject ConnectionModel connectionModel;

  public void attach(MainView view) {
    this.view = view;
  }

  public boolean isAttached() {
    return view != null;
  }

  public void load() {
    if (!isAttached()) {
      Timber.v("View was not attached");
      return;
    }

    view.updateLfmStatus(model.getLfmStatus());
    view.updateScrobbleStatus(model.isScrobblingEnabled());
    view.updateCover(model.getCover());
    view.updateRepeat(model.getRepeat());
    view.updateShuffleState(model.getShuffle());
    view.updateVolume(model.getVolume(), model.isMute());
    view.updatePlayState(model.getPlayState());
    view.updateTrackInfo(model.getTrackInfo());
    view.updateConnection(connectionModel.getConnection());
  }

  public void detach() {
    this.view = null;
  }

  public void requestNowPlayingPosition() {
    final UserAction action = UserAction.create(Protocol.NowPlayingPosition);
    bus.post(new MessageEvent(ProtocolEventType.UserAction, action));
  }

  public void toggleScrobbling() {
    bus.post(new MessageEvent(ProtocolEventType.UserAction, new UserAction(Protocol.PlayerScrobble, Const.TOGGLE)));
  }
}
