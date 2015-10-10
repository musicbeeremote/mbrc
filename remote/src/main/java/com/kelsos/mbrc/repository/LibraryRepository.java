package com.kelsos.mbrc.repository;

import com.kelsos.mbrc.dao.Album;
import com.kelsos.mbrc.dao.Artist;
import com.kelsos.mbrc.dao.Genre;
import com.kelsos.mbrc.dao.Track;

import rx.Observable;

public interface LibraryRepository {
  Observable<Album> getAlbums();
  Observable<Genre> getGenres();
  Observable<Track> getTracks();
  Observable<Artist> getArtists();
}
