package com.kelsos.mbrc.interactors;

import com.google.inject.Inject;
import com.kelsos.mbrc.dao.views.AlbumModelView;
import com.kelsos.mbrc.domain.Album;
import com.kelsos.mbrc.domain.AlbumTrackModel;
import com.kelsos.mbrc.domain.Track;
import com.kelsos.mbrc.mappers.AlbumMapper;
import com.kelsos.mbrc.mappers.TrackMapper;
import com.kelsos.mbrc.repository.library.AlbumRepository;
import com.kelsos.mbrc.repository.library.TrackRepository;
import java.util.List;
import rx.Observable;
import rx.schedulers.Schedulers;

public class AlbumTrackInteractor {
  @Inject private AlbumRepository repository;
  @Inject private TrackRepository trackRepository;

  public Observable<AlbumTrackModel> execute(long id) {
    final Observable<List<Track>> tracks = trackRepository.getTracksByAlbumId(id)
        .flatMap(data -> Observable.defer(() -> Observable.just(TrackMapper.map(data))));
    return Observable.zip(tracks, getAlbum(id), AlbumTrackModel::new).subscribeOn(Schedulers.io());
  }

  private Observable<Album> getAlbum(long id) {
    return Observable.create(subscriber -> {
      final AlbumModelView albumView = repository.getAlbumViewById((int) id);
      Album album = AlbumMapper.map(albumView, repository.getAlbumYear(id));
      subscriber.onNext(album);
      subscriber.onCompleted();
    });
  }
}
