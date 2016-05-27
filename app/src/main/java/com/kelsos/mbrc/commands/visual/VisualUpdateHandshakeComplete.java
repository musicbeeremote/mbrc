package com.kelsos.mbrc.commands.visual;

import com.google.inject.Inject;
import com.kelsos.mbrc.constants.Protocol;
import com.kelsos.mbrc.data.SocketMessage;
import com.kelsos.mbrc.interfaces.ICommand;
import com.kelsos.mbrc.interfaces.IEvent;
import com.kelsos.mbrc.model.MainDataModel;
import com.kelsos.mbrc.services.SocketService;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import rx.Observable;
import timber.log.Timber;

public class VisualUpdateHandshakeComplete implements ICommand {
  private SocketService service;
  private MainDataModel model;

  @Inject public VisualUpdateHandshakeComplete(SocketService service, MainDataModel model) {
    this.service = service;
    this.model = model;
  }

  public void execute(IEvent e) {
    boolean isComplete = (Boolean) e.getData();
    model.setHandShakeDone(isComplete);

    if (!isComplete) {
      return;
    }

    if (model.getPluginProtocol() < 2.1) {

      Timber.v("Preparing to send requests for state");

      ArrayList<SocketMessage> messages = new ArrayList<>();
      messages.add(SocketMessage.create(Protocol.NowPlayingCover));
      messages.add(SocketMessage.create(Protocol.PlayerStatus));
      messages.add(SocketMessage.create(Protocol.NowPlayingTrack));
      messages.add(SocketMessage.create(Protocol.NowPlayingLyrics));
      messages.add(SocketMessage.create(Protocol.NowPlayingPosition));
      messages.add(SocketMessage.create(Protocol.PluginVersion));

      int totalMessages = messages.size();
      Observable.interval(150, TimeUnit.MILLISECONDS)
          .take(totalMessages)
          .subscribe(tick -> service.sendData(messages.remove(0)));
    } else {
      Timber.v("Sending init request");
      service.sendData(SocketMessage.create(Protocol.INIT));
    }
  }
}


