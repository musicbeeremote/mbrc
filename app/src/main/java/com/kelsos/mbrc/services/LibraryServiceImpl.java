package com.kelsos.mbrc.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.kelsos.mbrc.constants.Protocol;
import com.kelsos.mbrc.data.Page;
import com.kelsos.mbrc.data.PageRange;
import com.kelsos.mbrc.data.library.Album;
import com.kelsos.mbrc.data.library.Artist;
import com.kelsos.mbrc.data.library.Genre;
import com.kelsos.mbrc.data.library.Track;

import java.io.IOException;

import rx.Observable;

public class LibraryServiceImpl extends ServiceBase implements LibraryService {

  @Override
  public Observable<Page<Genre>> getGenres(int offset, int limit) {
    PageRange range = getPageRange(offset, limit);

    return request(Protocol.LibraryBrowseGenres, range == null ? "" : range).flatMap(socketMessage -> Observable.create(subscriber -> {
      try {
        TypeReference<Page<Genre>> typeReference = new TypeReference<Page<Genre>>() {
        };
        Page<Genre> page = mapper.readValue((String) socketMessage.getData(), typeReference);
        subscriber.onNext(page);
        subscriber.onCompleted();
      } catch (IOException e) {
        subscriber.onError(e);
      }
    }));
  }

  @Override
  public Observable<Page<Artist>> getArtists(int offset, int limit) {
    PageRange range = getPageRange(offset, limit);

    return request(Protocol.LibraryBrowseArtists, range == null ? "" : range).flatMap(socketMessage -> Observable.create(subscriber -> {
      try {
        TypeReference<Page<Artist>> typeReference = new TypeReference<Page<Artist>>() {
        };
        Page<Artist> page = mapper.readValue((String) socketMessage.getData(), typeReference);
        subscriber.onNext(page);
        subscriber.onCompleted();
      } catch (IOException e) {
        subscriber.onError(e);
      }
    }));
  }

  @Override
  public Observable<Page<Album>> getAlbums(int offset, int limit) {
    PageRange range = getPageRange(offset, limit);

    return request(Protocol.LibraryBrowseAlbums, range == null ? "" : range).flatMap(socketMessage -> Observable.create(subscriber -> {
      try {
        TypeReference<Page<Album>> typeReference = new TypeReference<Page<Album>>() {
        };
        Page<Album> page = mapper.readValue((String) socketMessage.getData(), typeReference);
        subscriber.onNext(page);
        subscriber.onCompleted();
      } catch (IOException e) {
        subscriber.onError(e);
      }
    }));
  }

  @Override
  public Observable<Page<Track>> getTracks(int offset, int limit) {
    PageRange range = getPageRange(offset, limit);

    return request(Protocol.LibraryBrowseTracks, range == null ? "" : range).flatMap(socketMessage -> Observable.create(subscriber -> {
      try {
        TypeReference<Page<Track>> typeReference = new TypeReference<Page<Track>>() {
        };
        Page<Track> page = mapper.readValue((String) socketMessage.getData(), typeReference);
        subscriber.onNext(page);
        subscriber.onCompleted();
      } catch (IOException e) {
        subscriber.onError(e);
      }
    }));
  }

}
