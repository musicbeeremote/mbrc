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

public class VisualUpdateHandshakeComplete implements ICommand {
  public static final String EMPTY = "";
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
    ArrayList<SocketMessage> messages = new ArrayList<>();
    messages.add(new SocketMessage(Protocol.NowPlayingCover, Protocol.Request, EMPTY));
    messages.add(new SocketMessage(Protocol.PlayerStatus, Protocol.Request, EMPTY));
    messages.add(new SocketMessage(Protocol.NowPlayingTrack, Protocol.Request, EMPTY));
    messages.add(new SocketMessage(Protocol.NowPlayingLyrics, Protocol.Request, EMPTY));
    messages.add(new SocketMessage(Protocol.NowPlayingPosition, Protocol.Request, EMPTY));
    messages.add(new SocketMessage(Protocol.PluginVersion, Protocol.Request, EMPTY));

    int totalMessages = messages.size();
    Observable.interval(150, TimeUnit.MILLISECONDS)
        .take(totalMessages)
        .subscribe(tick -> service.sendData(messages.remove(0)));
  }
}


