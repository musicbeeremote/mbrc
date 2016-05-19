package com.kelsos.mbrc.commands.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.google.inject.Inject;
import com.kelsos.mbrc.data.Playlist;
import com.kelsos.mbrc.interfaces.ICommand;
import com.kelsos.mbrc.interfaces.IEvent;
import com.kelsos.mbrc.model.MainDataModel;
import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class UpdatePlaylistList implements ICommand {

  private final MainDataModel model;
  private ObjectMapper mapper;

  @Inject public UpdatePlaylistList(MainDataModel model, ObjectMapper mapper) {
    this.model = model;
    this.mapper = mapper;
  }

  @Override public void execute(IEvent e) {
    ArrayNode nodes = (ArrayNode) e.getData();
    Observable.from(nodes).flatMap(node -> Observable.create((Subscriber<? super Playlist> subscriber) -> {
      try {
        Playlist playlist = mapper.treeToValue(node, Playlist.class);
        subscriber.onNext(playlist);
        subscriber.onCompleted();
      } catch (JsonProcessingException e1) {
        subscriber.onError(e1);
      }
    })).subscribeOn(Schedulers.io()).toList().subscribe(model::setPlaylists, t -> {
      Timber.v(t, "failed to parse the playlists");
    });
  }
}
