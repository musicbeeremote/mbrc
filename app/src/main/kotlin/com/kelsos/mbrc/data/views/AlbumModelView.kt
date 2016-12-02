package com.kelsos.mbrc.data.views

import com.kelsos.mbrc.data.dao.Album
import com.kelsos.mbrc.data.dao.Album_Table
import com.kelsos.mbrc.data.dao.Artist
import com.kelsos.mbrc.data.dao.Artist_Table
import com.kelsos.mbrc.data.dao.Cover
import com.kelsos.mbrc.data.dao.Cover_Table
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

@ModelView(database = RemoteDatabase::class, name = "album_view")
class AlbumModelView : BaseModelView() {

  @Column var id: Long = 0
  @Column(name = "artist_id") var artistId: Long = 0
  @Column var name: String? = null
  @Column var artist: String? = null
  @Column var cover: String? = null

  companion object {

    @JvmField
    @ModelViewQuery
    val QUERY: Query = SQLite.select(Album_Table.id.`as`("id").withTable(),
        Album_Table.name.`as`("name").withTable(),
        Artist_Table.name.`as`("artist").withTable(),
        Artist_Table.id.`as`("artist_id").withTable(),
        Cover_Table.hash.`as`("cover").withTable())
        .from(Album::class.java)
        .join(Artist::class.java, Join.JoinType.INNER)
        .on(Album_Table.artist_id.withTable().`is`(Artist_Table.id.withTable()))
        .join(Cover::class.java, Join.JoinType.LEFT_OUTER)
        .on(Album_Table.cover_id.withTable().`is`(Cover_Table.id.withTable()))
        .orderBy(OrderBy.fromNameAlias(NameAlias.builder("artist").build()).ascending())
        .orderBy(OrderBy.fromNameAlias(NameAlias.builder("name").build()).ascending())
  }
}
