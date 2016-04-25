package com.kelsos.mbrc.dao.views

import com.kelsos.mbrc.RemoteDatabase
import com.kelsos.mbrc.dao.ArtistDao
import com.kelsos.mbrc.dao.ArtistDao_Table
import com.kelsos.mbrc.dao.TrackDao
import com.kelsos.mbrc.dao.TrackDao_Table
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
    name = "genre_artist_view") class GenreArtistView : BaseModelView<TrackDao>() {

  @Column var id: Long = 0
  @Column var name: String? = null
  @Column(name = "genre_id") var genreId: Long = 0

  companion object {
    @JvmField
    @ModelViewQuery val QUERY: Query = SQLite.select(ArtistDao_Table.id.`as`("id").withTable(),
        ArtistDao_Table.name.`as`("name").withTable(),
        TrackDao_Table.genre_id.`as`("genre_id").withTable()).distinct().from(TrackDao::class.java).join(ArtistDao::class.java,
        Join.JoinType.INNER).on(TrackDao_Table.artist_id.withTable().`is`(ArtistDao_Table.id.withTable())).orderBy(
        OrderBy.fromNameAlias(NameAlias("name")).ascending())
  }
}
