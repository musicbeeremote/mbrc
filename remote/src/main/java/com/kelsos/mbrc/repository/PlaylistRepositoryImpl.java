package com.kelsos.mbrc.repository;

import com.annimon.stream.Stream;
import com.google.inject.Inject;
import com.kelsos.mbrc.RemoteDatabase;
import com.kelsos.mbrc.dao.PlaylistDao;
import com.kelsos.mbrc.domain.Playlist;
import com.kelsos.mbrc.dto.playlist.PlaylistTrack;
import com.kelsos.mbrc.dto.playlist.PlaylistTrackInfo;
import com.kelsos.mbrc.mappers.PlaylistMapper;
import com.kelsos.mbrc.services.api.PlaylistService;
import com.raizlabs.android.dbflow.runtime.TransactionManager;
import com.raizlabs.android.dbflow.sql.language.Select;
import java.util.List;
import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;

public class PlaylistRepositoryImpl implements PlaylistRepository {
  @Inject private PlaylistService service;

  @Override
  public Observable<List<Playlist>> getPlaylists() {
    return Observable.create((Subscriber<? super List<Playlist>> subscriber) -> {
      final List<PlaylistDao> playlistDaos = new Select().from(PlaylistDao.class).queryList();
      subscriber.onNext(PlaylistMapper.mapData(playlistDaos));
      subscriber.onCompleted();
    }).subscribeOn(Schedulers.io());
  }


  @Override
  public void savePlaylists(List<PlaylistDao> playlists) {
    TransactionManager.transact(RemoteDatabase.NAME, () -> {
      Stream.of(playlists).forEach(value -> {
        if (value.getDateDeleted() > 0) {
          value.delete();
          return;
        }
        if (value.getDateUpdated() > 0) {
          value.update();
        } else {
          value.save();
        }
      });
    });
  }

  @Override
  public Observable<List<PlaylistTrack>> getPlaylistTracks(long playlistId) {
    return null;
  }

  @Override
  public Observable<List<PlaylistTrackInfo>> getTrackInfo() {
    return null;
  }
}
