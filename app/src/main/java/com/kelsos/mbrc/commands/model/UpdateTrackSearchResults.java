package com.kelsos.mbrc.commands.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.google.inject.Inject;
import com.kelsos.mbrc.data.library.Track;
import com.kelsos.mbrc.interfaces.ICommand;
import com.kelsos.mbrc.interfaces.IEvent;
import com.kelsos.mbrc.model.MainDataModel;
import java.util.ArrayList;
import roboguice.util.Ln;
import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;

public class UpdateTrackSearchResults implements ICommand {
  private MainDataModel model;

  @Inject public UpdateTrackSearchResults(MainDataModel model) {
    this.model = model;
  }

  @Override public void execute(IEvent e) {
    Observable.create((Subscriber<? super ArrayList<Track>> subscriber) -> {
      ArrayList<Track> tracks = new ArrayList<>();
      ArrayNode node = (ArrayNode) e.getData();
      for (int i = 0; i < node.size(); i++) {
        JsonNode jNode = node.get(i);
        Track entry = new Track(jNode);
        tracks.add(entry);
      }

      subscriber.onNext(tracks);
      subscriber.onCompleted();
    }).subscribeOn(Schedulers.io()).subscribe(model::setSearchTracks, Ln::d);
  }
}
