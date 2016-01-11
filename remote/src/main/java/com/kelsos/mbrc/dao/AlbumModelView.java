package com.kelsos.mbrc.dao;

import com.kelsos.mbrc.RemoteDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ModelView;
import com.raizlabs.android.dbflow.annotation.ModelViewQuery;
import com.raizlabs.android.dbflow.sql.Query;
import com.raizlabs.android.dbflow.sql.language.Join;
import com.raizlabs.android.dbflow.sql.language.NameAlias;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModelView;

@ModelView(database = RemoteDatabase.class, name = "album_view") public class AlbumModelView extends BaseModelView<AlbumDao> {

  public static final String ALBUM = "album";
  public static final String ARTIST = "artist";

  public static final String COVER = "cover";
  @ModelViewQuery public static final Query QUERY = SQLite.select(AlbumDao_Table.id.withTable(new NameAlias(ALBUM)),
      AlbumDao_Table.album_name.withTable(new NameAlias(ALBUM).as("name")),
      ArtistDao_Table.name.withTable(new NameAlias(ARTIST).as(ARTIST)),
      CoverDao_Table.hash.withTable(new NameAlias(COVER)).as(COVER))
      .from(AlbumDao.class)
      .join(ArtistDao.class, Join.JoinType.INNER)
      .on(AlbumDao_Table.artist_id.withTable(new NameAlias(ALBUM))
          .is(ArtistDao_Table.id.withTable(new NameAlias(ARTIST))))
      .join(CoverDao.class, Join.JoinType.INNER)
      .on(AlbumDao_Table.cover_id.withTable(new NameAlias(ALBUM))
          .is(CoverDao_Table.id.withTable(new NameAlias(COVER))));

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
