package com.kelsos.mbrc.commands.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.google.inject.Inject;
import com.kelsos.mbrc.data.ArtistEntry;
import com.kelsos.mbrc.interfaces.ICommand;
import com.kelsos.mbrc.interfaces.IEvent;
import com.kelsos.mbrc.model.MainDataModel;
import java.util.ArrayList;
import roboguice.util.Ln;
import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;

public class UpdateArtistSearchResults implements ICommand {
  private MainDataModel model;

  @Inject public UpdateArtistSearchResults(MainDataModel model) {
    this.model = model;
  }

  @Override public void execute(IEvent e) {

    Observable.create((Subscriber<? super ArrayList<ArtistEntry>> subscriber) -> {
      ArrayList<ArtistEntry> artists = new ArrayList<>();
      ArrayNode node = (ArrayNode) e.getData();
      for (int i = 0; i < node.size(); i++) {
        JsonNode jNode = node.get(i);
        ArtistEntry entry = new ArtistEntry(jNode);
        artists.add(entry);
      }
      subscriber.onNext(artists);
      subscriber.onCompleted();
    }).subscribeOn(Schedulers.io()).subscribe(model::setSearchArtists, Ln::d);
  }
}
