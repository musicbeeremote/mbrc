package com.kelsos.mbrc.dao.views;

import com.kelsos.mbrc.RemoteDatabase;
import com.kelsos.mbrc.dao.ArtistDao;
import com.kelsos.mbrc.dao.ArtistDao_Table;
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

@ModelView(database = RemoteDatabase.class, name = "genre_artist_view") public class GenreArtistView
    extends BaseModelView<TrackDao> {

  @ModelViewQuery public static final Query QUERY = SQLite.select(ArtistDao_Table.id.as("id").withTable(),
      ArtistDao_Table.name.as("name").withTable(),
      TrackDao_Table.genre_id.as("genre_id").withTable())
      .distinct()
      .from(TrackDao.class)
      .join(ArtistDao.class, Join.JoinType.INNER)
      .on(TrackDao_Table.artist_id.withTable().is(ArtistDao_Table.id.withTable()))
      .orderBy(OrderBy.fromNameAlias(new NameAlias("name")).ascending());

  @Column private long id;
  @Column private String name;
  @Column(name = "genre_id") private long genreId;

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

  public long getGenreId() {
    return genreId;
  }

  public void setGenreId(long genreId) {
    this.genreId = genreId;
  }
}
