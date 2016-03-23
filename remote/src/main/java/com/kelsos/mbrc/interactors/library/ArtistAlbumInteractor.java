package com.kelsos.mbrc.interactors.library;

import com.kelsos.mbrc.domain.Album;
import java.util.List;
import rx.Observable;

public interface ArtistAlbumInteractor {
  Observable<List<Album>> getArtistAlbums(long artistId);
}
