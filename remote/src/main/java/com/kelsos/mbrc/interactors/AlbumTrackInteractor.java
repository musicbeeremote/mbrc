package com.kelsos.mbrc.interactors;

import com.google.inject.Inject;
import com.kelsos.mbrc.dao.AlbumModelView;
import com.kelsos.mbrc.domain.AlbumTrackModel;
import com.kelsos.mbrc.domain.Track;
import com.kelsos.mbrc.mappers.AlbumMapper;
import com.kelsos.mbrc.mappers.TrackMapper;
import com.kelsos.mbrc.repository.LibraryRepository;
import java.util.List;
import rx.Observable;
import rx.schedulers.Schedulers;

public class AlbumTrackInteractor {
  @Inject private LibraryRepository repository;

  public Observable<AlbumTrackModel> execute(long id) {
    final Observable<List<Track>> tracks = repository.getTracksByAlbumId(id)
        .flatMap(data -> Observable.just(TrackMapper.map(data)));
    final AlbumModelView album = repository.getAlbumViewById((int) id);
    return Observable.zip(tracks, Observable.just(AlbumMapper.map(album)), AlbumTrackModel::new).subscribeOn(Schedulers.io());
  }
}
