package com.kelsos.mbrc.dao.views;

import com.kelsos.mbrc.RemoteDatabase;
import com.kelsos.mbrc.dao.PlaylistTrackDao;
import com.kelsos.mbrc.dao.PlaylistTrackDao_Table;
import com.kelsos.mbrc.dao.PlaylistTrackInfoDao;
import com.kelsos.mbrc.dao.PlaylistTrackInfoDao_Table;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ModelView;
import com.raizlabs.android.dbflow.annotation.ModelViewQuery;
import com.raizlabs.android.dbflow.sql.Query;
import com.raizlabs.android.dbflow.sql.language.Join;
import com.raizlabs.android.dbflow.sql.language.OrderBy;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModelView;

@ModelView(database = RemoteDatabase.class, name = "playlist_track_view") public class PlaylistTrackView
    extends BaseModelView<PlaylistTrackDao> {
  @ModelViewQuery public static final Query QUERY = SQLite.select(PlaylistTrackDao_Table.id.withTable(),
      PlaylistTrackDao_Table.playlist_id.withTable(),
      PlaylistTrackDao_Table.position.withTable(),
      PlaylistTrackInfoDao_Table.path.withTable(),
      PlaylistTrackInfoDao_Table.artist.withTable(),
      PlaylistTrackInfoDao_Table.title.withTable())
      .from(PlaylistTrackDao.class)
      .join(PlaylistTrackInfoDao.class, Join.JoinType.INNER)
      .on(PlaylistTrackInfoDao_Table.id.withTable().is(PlaylistTrackDao_Table.track_info_id))
      .orderBy(OrderBy.fromProperty(PlaylistTrackDao_Table.playlist_id).ascending())
      .orderBy(OrderBy.fromProperty(PlaylistTrackDao_Table.position).ascending());

  @Column private long id;
  @Column(name = "playlist_id") private long playlistId;
  @Column private long position;
  @Column private String path;
  @Column private String artist;
  @Column private String title;

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getArtist() {
    return artist;
  }

  public void setArtist(String artist) {
    this.artist = artist;
  }

  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public long getPosition() {
    return position;
  }

  public void setPosition(long position) {
    this.position = position;
  }

  public long getPlaylistId() {
    return playlistId;
  }

  public void setPlaylistId(long playlistId) {
    this.playlistId = playlistId;
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }
}
