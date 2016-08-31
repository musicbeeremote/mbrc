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

public class MainViewPresenter extends BasePresenter<MainView> {

  @Inject RxBus bus;
  @Inject MainDataModel model;
  @Inject ConnectionModel connectionModel;

  public void load() {
    checkIfAttached();
    getView().updateLfmStatus(model.getLfmStatus());
    getView().updateScrobbleStatus(model.isScrobblingEnabled());
    getView().updateCover(model.getCover());
    getView().updateRepeat(model.getRepeat());
    getView().updateShuffleState(model.getShuffle());
    getView().updateVolume(model.getVolume(), model.isMute());
    getView().updatePlayState(model.getPlayState());
    getView().updateTrackInfo(model.getTrackInfo());
    getView().updateConnection(connectionModel.getConnection());
  }

  public void requestNowPlayingPosition() {
    final UserAction action = UserAction.create(Protocol.NowPlayingPosition);
    bus.post(new MessageEvent(ProtocolEventType.UserAction, action));
  }

  public void toggleScrobbling() {
    bus.post(new MessageEvent(ProtocolEventType.UserAction, new UserAction(Protocol.PlayerScrobble, Const.TOGGLE)));
  }
}
