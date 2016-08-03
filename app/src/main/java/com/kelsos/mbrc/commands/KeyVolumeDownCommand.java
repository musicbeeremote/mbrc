package com.kelsos.mbrc.commands;

import javax.inject.Inject;
import com.kelsos.mbrc.constants.Protocol;
import com.kelsos.mbrc.constants.ProtocolEventType;
import com.kelsos.mbrc.data.UserAction;
import com.kelsos.mbrc.events.MessageEvent;
import com.kelsos.mbrc.events.bus.RxBus;
import com.kelsos.mbrc.interfaces.ICommand;
import com.kelsos.mbrc.interfaces.IEvent;
import com.kelsos.mbrc.model.MainDataModel;

public class KeyVolumeDownCommand implements ICommand {
  private MainDataModel model;
  private RxBus bus;

  @Inject public KeyVolumeDownCommand(MainDataModel model, RxBus bus) {
    this.model = model;
    this.bus = bus;
  }

  @Override public void execute(IEvent e) {
    if (model.getVolume() >= 10) {
      int mod = model.getVolume() % 10;
      int volume;

      if (mod == 0) {
        volume = model.getVolume() - 10;
      } else if (mod < 5) {
        volume = model.getVolume() - (10 + mod);
      } else {
        volume = model.getVolume() - mod;
      }
      bus.post(new MessageEvent(ProtocolEventType.UserAction,
          new UserAction(Protocol.PlayerVolume, volume)));
    }
  }
}
