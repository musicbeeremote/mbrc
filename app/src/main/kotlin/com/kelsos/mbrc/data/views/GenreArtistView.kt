package com.kelsos.mbrc.data.views

import com.kelsos.mbrc.data.dao.Artist
import com.kelsos.mbrc.data.dao.Artist_Table
import com.kelsos.mbrc.data.dao.Track
import com.kelsos.mbrc.data.dao.Track_Table
import com.kelsos.mbrc.data.db.RemoteDatabase
import com.raizlabs.android.dbflow.annotation.Column
import com.raizlabs.android.dbflow.annotation.ModelView
import com.raizlabs.android.dbflow.annotation.ModelViewQuery
import com.raizlabs.android.dbflow.sql.Query
import com.raizlabs.android.dbflow.sql.language.Join
import com.raizlabs.android.dbflow.sql.language.NameAlias
import com.raizlabs.android.dbflow.sql.language.OrderBy
import com.raizlabs.android.dbflow.sql.language.SQLite
import com.raizlabs.android.dbflow.structure.BaseModelView

@ModelView(database = RemoteDatabase::class,
    name = "genre_artist_view") class GenreArtistView : BaseModelView() {

  @Column var id: Long = 0
  @Column var name: String? = null
  @Column(name = "genre_id") var genreId: Long = 0

  companion object {

    @JvmField
    @ModelViewQuery val QUERY: Query = SQLite.select(Artist_Table.id.`as`("id").withTable(),
        Artist_Table.name.`as`("name").withTable(),
        Track_Table.genre_id.`as`("genre_id").withTable())
        .distinct()
        .from(Track::class.java)
        .join(Artist::class.java, Join.JoinType.INNER)
        .on(Track_Table.artist_id.withTable().`is`(Artist_Table.id.withTable()))
        .orderBy(OrderBy.fromNameAlias(NameAlias.builder("name").build()).ascending())
  }
}
