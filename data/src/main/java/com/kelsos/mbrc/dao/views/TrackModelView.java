package com.kelsos.mbrc.dao.views;

import com.kelsos.mbrc.RemoteDatabase;
import com.kelsos.mbrc.dao.AlbumDao;
import com.kelsos.mbrc.dao.AlbumDao_Table;
import com.kelsos.mbrc.dao.ArtistDao;
import com.kelsos.mbrc.dao.ArtistDao_Table;
import com.kelsos.mbrc.dao.CoverDao;
import com.kelsos.mbrc.dao.CoverDao_Table;
import com.kelsos.mbrc.dao.TrackDao;
import com.kelsos.mbrc.dao.TrackDao_Table;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ModelView;
import com.raizlabs.android.dbflow.annotation.ModelViewQuery;
import com.raizlabs.android.dbflow.sql.Query;
import com.raizlabs.android.dbflow.sql.language.Join;
import com.raizlabs.android.dbflow.sql.language.NameAlias;
import com.raizlabs.android.dbflow.sql.language.OrderBy;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModelView;

@ModelView(database = RemoteDatabase.class, name = "track_view") public class TrackModelView
    extends BaseModelView<TrackDao> {
  @ModelViewQuery public static final Query QUERY = SQLite.select(TrackDao_Table.id.as("id").withTable(),
      TrackDao_Table.disc.as("disc").withTable(),
      TrackDao_Table.position.as("position").withTable(),
      TrackDao_Table.title.as("title").withTable(),
      AlbumDao_Table.name.as("album").withTable(),
      AlbumDao_Table.id.as("album_id").withTable(),
      ArtistDao_Table.name.as("artist").withTable(),
      CoverDao_Table.hash.as("cover").withTable())
      .from(TrackDao.class)
      .join(AlbumDao.class, Join.JoinType.INNER)
      .on(TrackDao_Table.album_id.withTable().is(AlbumDao_Table.id.withTable()))
      .join(ArtistDao.class, Join.JoinType.INNER)
      .on(TrackDao_Table.artist_id.withTable().is(ArtistDao_Table.id.withTable()))
      .join(ArtistDao.class, Join.JoinType.INNER)
      .as("album_artist")
      .on(TrackDao_Table.album_artist_id.withTable().is(ArtistDao_Table.id.withTable(new NameAlias("album_artist"))))
      .join(CoverDao.class, Join.JoinType.LEFT_OUTER)
      .on(AlbumDao_Table.cover_id.withTable().is(CoverDao_Table.id.withTable()))
      .orderBy(OrderBy.fromNameAlias(new NameAlias("name").withTable("album_artist")).ascending())
      .orderBy(OrderBy.fromNameAlias(new NameAlias("album")).ascending())
      .orderBy(OrderBy.fromNameAlias(new NameAlias("disc")).ascending())
      .orderBy(OrderBy.fromNameAlias(new NameAlias("position")).ascending());

  @Column(name = "id") private long id;
  @Column(name = "disc") private int disc;
  @Column(name = "position") private int position;
  @Column(name = "title") private String title;
  @Column(name = "album") private String album;
  @Column(name = "album_id") private long albumId;
  @Column(name = "artist") private String artist;
  @Column(name = "cover") private String cover;

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public int getDisc() {
    return disc;
  }

  public void setDisc(int disc) {
    this.disc = disc;
  }

  public int getPosition() {
    return position;
  }

  public void setPosition(int position) {
    this.position = position;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getAlbum() {
    return album;
  }

  public void setAlbum(String album) {
    this.album = album;
  }

  public String getArtist() {
    return artist;
  }

  public void setArtist(String artist) {
    this.artist = artist;
  }

  public String getCover() {
    return cover;
  }

  public void setCover(String cover) {
    this.cover = cover;
  }

  public long getAlbumId() {
    return albumId;
  }

  public void setAlbumId(long albumId) {
    this.albumId = albumId;
  }
}
