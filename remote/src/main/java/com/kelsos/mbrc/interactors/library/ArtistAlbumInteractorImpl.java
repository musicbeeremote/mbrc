package com.kelsos.mbrc.interactors.library;

import com.google.inject.Inject;
import com.kelsos.mbrc.domain.Album;
import com.kelsos.mbrc.mappers.AlbumMapper;
import com.kelsos.mbrc.repository.library.AlbumRepository;
import java.util.List;
import rx.Observable;

public class ArtistAlbumInteractorImpl implements ArtistAlbumInteractor {
  @Inject private AlbumRepository repository;

  @Override public Observable<List<Album>> getArtistAlbums(long artistId) {
    return repository.getAlbumsByArtist(artistId)
        .flatMap(albums -> Observable.defer(() -> Observable.just(AlbumMapper.mapArtistAlbums(albums))));
  }
}
