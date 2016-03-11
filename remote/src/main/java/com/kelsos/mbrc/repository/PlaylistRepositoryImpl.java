package com.kelsos.mbrc.repository;

import com.kelsos.mbrc.RemoteDatabase;
import com.kelsos.mbrc.dao.PlaylistDao;
import com.kelsos.mbrc.dao.PlaylistDao_Table;
import com.kelsos.mbrc.dao.PlaylistTrackDao;
import com.kelsos.mbrc.dao.PlaylistTrackInfoDao;
import com.kelsos.mbrc.dao.PlaylistTrackInfoDao_Table;
import com.kelsos.mbrc.dao.views.PlaylistTrackView;
import com.kelsos.mbrc.dao.views.PlaylistTrackView_ViewTable;
import com.kelsos.mbrc.domain.Playlist;
import com.kelsos.mbrc.dto.playlist.PlaylistTrackInfo;
import com.kelsos.mbrc.mappers.PlaylistMapper;
import com.raizlabs.android.dbflow.runtime.TransactionManager;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.sql.language.Select;
import java.util.List;
import roboguice.util.Ln;
import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;

public class PlaylistRepositoryImpl implements PlaylistRepository {

  @Override public Observable<List<Playlist>> getPlaylists() {
    return Observable.create((Subscriber<? super List<Playlist>> subscriber) -> {
      final List<PlaylistDao> playlistDaos = new Select().from(PlaylistDao.class).queryList();
      subscriber.onNext(PlaylistMapper.mapData(playlistDaos));
      subscriber.onCompleted();
    }).subscribeOn(Schedulers.io());
  }

  @Override public void savePlaylists(List<PlaylistDao> playlists) {
    TransactionManager.transact(RemoteDatabase.NAME, () -> {
      Observable.from(playlists).forEach(value -> {
        if (value.getDateDeleted() > 0) {
          value.delete();
          return;
        }

        value.save();
      });
    });
  }

  @Override public Observable<List<PlaylistTrackView>> getPlaylistTracks(long playlistId) {
    List<PlaylistTrackView> list = SQLite.select()
        .from(PlaylistTrackView.class)
        .where(PlaylistTrackView_ViewTable.playlist_id.eq(playlistId))
        .queryList();

    return Observable.just(list);
  }

  @Override public Observable<List<PlaylistTrackInfo>> getTrackInfo() {
    return null;
  }

  @Override public void savePlaylistTrackInfo(List<PlaylistTrackInfoDao> data) {
    TransactionManager.transact(RemoteDatabase.NAME, () -> {
      Observable.from(data).subscribeOn(Schedulers.immediate()).observeOn(Schedulers.immediate()).subscribe(info -> {
        if (info.getDateDeleted() > 0) {
          info.delete();
          return;
        }

        info.save();
      }, throwable -> {

      });
    });
  }

  @Override public void savePlaylistTracks(List<PlaylistTrackDao> data) {
    TransactionManager.transact(RemoteDatabase.NAME, () -> {
      Observable.from(data).subscribeOn(Schedulers.immediate()).observeOn(Schedulers.immediate()).subscribe(info -> {
        if (info.getDateDeleted() > 0) {
          info.delete();
          return;
        }

        info.save();
      }, throwable -> {
        Ln.v(throwable);
      });
    });
  }

  @Override public PlaylistDao getPlaylistById(long id) {
    return SQLite.select().from(PlaylistDao.class).where(PlaylistDao_Table.id.is(id)).querySingle();
  }

  @Override public PlaylistTrackInfoDao getTrackInfoById(long id) {
    return SQLite.select().from(PlaylistTrackInfoDao.class).where(PlaylistTrackInfoDao_Table.id.is(id)).querySingle();
  }
}
