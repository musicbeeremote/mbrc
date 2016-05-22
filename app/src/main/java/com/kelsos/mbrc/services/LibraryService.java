package com.kelsos.mbrc.services;

import com.kelsos.mbrc.data.Page;
import com.kelsos.mbrc.data.library.Album;
import com.kelsos.mbrc.data.library.Artist;
import com.kelsos.mbrc.data.library.Genre;
import com.kelsos.mbrc.data.library.Track;
import rx.Observable;

public interface LibraryService {
  Observable<Page<Genre>> getGenres(int offset, int limit);

  Observable<Page<Artist>> getArtists(int offset, int limit);

  Observable<Page<Album>> getAlbums(int offset, int limit);

  Observable<Page<Track>> getTracks(int offset, int limit);
}
