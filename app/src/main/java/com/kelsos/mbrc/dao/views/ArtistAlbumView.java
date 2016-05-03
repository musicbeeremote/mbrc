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
import com.raizlabs.android.dbflow.sql.language.NameAlias;
import com.raizlabs.android.dbflow.sql.language.OrderBy;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModelView;

@ModelView(database = RemoteDatabase.class, name = "artist_album_view") public class ArtistAlbumView
    extends BaseModelView<TrackDao> {
  @ModelViewQuery public static final Query QUERY = SQLite.select(AlbumDao_Table.id.as("id").withTable(),
      AlbumDao_Table.name.as("name").withTable(),
      ArtistDao_Table.name.as("artist").withTable(),
      ArtistDao_Table.id.as("artist_id").withTable(),
      CoverDao_Table.hash.as("cover").withTable())
      .distinct()
      .from(TrackDao.class)
      .innerJoin(AlbumDao.class)
      .on(TrackDao_Table.album_id.withTable().is(AlbumDao_Table.id.withTable()))
      .innerJoin(ArtistDao.class)
      .on(TrackDao_Table.artist_id.withTable().is(ArtistDao_Table.id.withTable()))
      .leftOuterJoin(CoverDao.class)
      .on(AlbumDao_Table.cover_id.withTable().is(CoverDao_Table.id.withTable()))
      .orderBy(OrderBy.fromNameAlias(new NameAlias("name")).ascending());

  @Column private long id;
  @Column private String name;
  @Column private String artist;
  @Column(name = "artist_id") private long artistId;
  @Column private String cover;

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getArtist() {
    return artist;
  }

  public void setArtist(String artist) {
    this.artist = artist;
  }

  public long getArtistId() {
    return artistId;
  }

  public void setArtistId(long artistId) {
    this.artistId = artistId;
  }

  public String getCover() {
    return cover;
  }

  public void setCover(String cover) {
    this.cover = cover;
  }
}
