package com.kelsos.mbrc.dao.views;

import com.kelsos.mbrc.RemoteDatabase;
import com.kelsos.mbrc.dao.AlbumDao;
import com.kelsos.mbrc.dao.AlbumDao_Table;
import com.kelsos.mbrc.dao.ArtistDao;
import com.kelsos.mbrc.dao.ArtistDao_Table;
import com.kelsos.mbrc.dao.CoverDao;
import com.kelsos.mbrc.dao.CoverDao_Table;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ModelView;
import com.raizlabs.android.dbflow.annotation.ModelViewQuery;
import com.raizlabs.android.dbflow.sql.Query;
import com.raizlabs.android.dbflow.sql.language.Join;
import com.raizlabs.android.dbflow.sql.language.NameAlias;
import com.raizlabs.android.dbflow.sql.language.OrderBy;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModelView;

@ModelView(database = RemoteDatabase.class, name = "album_view") public class AlbumModelView
    extends BaseModelView<AlbumDao> {

  @ModelViewQuery public static final Query QUERY = SQLite.select(AlbumDao_Table.id.as("id").withTable(),
      AlbumDao_Table.album_name.as("name").withTable(),
      ArtistDao_Table.name.as("artist").withTable(),
      CoverDao_Table.hash.as("cover").withTable())
      .from(AlbumDao.class)
      .join(ArtistDao.class, Join.JoinType.INNER)
      .on(AlbumDao_Table.artist_id.withTable().is(ArtistDao_Table.id.withTable()))
      .join(CoverDao.class, Join.JoinType.INNER)
      .on(AlbumDao_Table.cover_id.withTable().is(CoverDao_Table.id.withTable()))
      .orderBy(OrderBy.fromNameAlias(new NameAlias("artist")).ascending())
      .orderBy(OrderBy.fromNameAlias(new NameAlias("name")).ascending());

  @Column private long id;
  @Column private String name;
  @Column private String artist;
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

  public String getCover() {
    return cover;
  }

  public void setCover(String cover) {
    this.cover = cover;
  }
}
