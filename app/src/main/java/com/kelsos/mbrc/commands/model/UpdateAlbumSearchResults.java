package com.kelsos.mbrc.commands.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.google.inject.Inject;
import com.kelsos.mbrc.data.Album;
import com.kelsos.mbrc.interfaces.ICommand;
import com.kelsos.mbrc.interfaces.IEvent;
import com.kelsos.mbrc.model.MainDataModel;
import java.util.ArrayList;
import roboguice.util.Ln;
import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;

public class UpdateAlbumSearchResults implements ICommand {
  private MainDataModel model;

  @Inject public UpdateAlbumSearchResults(MainDataModel model) {
    this.model = model;
  }

  @Override public void execute(IEvent e) {

    Observable.create((Subscriber<? super ArrayList<Album>> subscriber) -> {
      ArrayList<Album> albums = new ArrayList<>();
      ArrayNode node = (ArrayNode) e.getData();
      for (int i = 0; i < node.size(); i++) {
        JsonNode jNode = node.get(i);
        Album entry = new Album(jNode);
        albums.add(entry);
      }
      subscriber.onNext(albums);
      subscriber.onCompleted();
    }).subscribeOn(Schedulers.io()).subscribe(model::setSearchAlbums, Ln::d);
  }
}
