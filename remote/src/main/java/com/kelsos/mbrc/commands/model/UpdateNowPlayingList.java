package com.kelsos.mbrc.commands.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.google.inject.Inject;
import com.kelsos.mbrc.data.MusicTrack;
import com.kelsos.mbrc.interfaces.ICommand;
import com.kelsos.mbrc.interfaces.IEvent;
import com.kelsos.mbrc.model.MainDataModel;
import java.util.ArrayList;
import roboguice.util.Ln;
import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;

public class UpdateNowPlayingList implements ICommand {
  private MainDataModel model;

  @Inject public UpdateNowPlayingList(MainDataModel model) {
    this.model = model;
  }

  @Override public void execute(final IEvent e) {

    Observable.create((Subscriber<? super ArrayList<MusicTrack>> subscriber) -> {
      ArrayNode node = (ArrayNode) e.getData();
      ArrayList<MusicTrack> playList = new ArrayList<>();
      for (int i = 0; i < node.size(); i++) {
        JsonNode jNode = node.get(i);
        playList.add(new MusicTrack(jNode));
      }
      subscriber.onNext(playList);
      subscriber.onCompleted();
    }).subscribeOn(Schedulers.io()).subscribe(model::setNowPlayingList, Ln::d);
  }
}
