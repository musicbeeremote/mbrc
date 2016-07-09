package com.kelsos.mbrc.commands;

import com.google.inject.Inject;
import com.kelsos.mbrc.constants.Protocol;
import com.kelsos.mbrc.constants.ProtocolEventType;
import com.kelsos.mbrc.data.UserAction;
import com.kelsos.mbrc.events.MessageEvent;
import com.kelsos.mbrc.events.bus.RxBus;
import com.kelsos.mbrc.interfaces.ICommand;
import com.kelsos.mbrc.interfaces.IEvent;
import com.kelsos.mbrc.model.MainDataModel;

public class KeyVolumeUpCommand implements ICommand {
  private MainDataModel model;
  private RxBus bus;

  @Inject public KeyVolumeUpCommand(final MainDataModel model, final RxBus bus) {
    this.model = model;
    this.bus = bus;
  }

  @Override public void execute(final IEvent e) {
    if (model.getVolume() <= 90) {
      int mod = model.getVolume() % 10;
      int volume;

      if (mod == 0) {
        volume = model.getVolume() + 10;
      } else if (mod < 5) {
        volume = model.getVolume() + (10 - mod);
      } else {
        volume = model.getVolume() + (20 - mod);
      }

      bus.post(new MessageEvent(ProtocolEventType.UserAction,
          new UserAction(Protocol.PlayerVolume, volume)));
    }
  }
}
