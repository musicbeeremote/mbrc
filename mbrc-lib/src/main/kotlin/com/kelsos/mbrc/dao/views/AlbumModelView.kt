package com.kelsos.mbrc.dao.views

import com.kelsos.mbrc.RemoteDatabase
import com.kelsos.mbrc.dao.*
import com.raizlabs.android.dbflow.annotation.Column
import com.raizlabs.android.dbflow.annotation.ModelView
import com.raizlabs.android.dbflow.annotation.ModelViewQuery
import com.raizlabs.android.dbflow.sql.Query
import com.raizlabs.android.dbflow.sql.language.Join
import com.raizlabs.android.dbflow.sql.language.NameAlias
import com.raizlabs.android.dbflow.sql.language.OrderBy
import com.raizlabs.android.dbflow.sql.language.SQLite
import com.raizlabs.android.dbflow.structure.BaseModelView

@ModelView(database = RemoteDatabase::class, name = "album_view") class AlbumModelView : BaseModelView<AlbumDao>() {

  @Column var id: Long = 0
  @Column(name = "artist_id") var artistId: Long = 0
  @Column var name: String? = null
  @Column var artist: String? = null
  @Column var cover: String? = null

  companion object {
    @JvmField
    @ModelViewQuery val QUERY: Query = SQLite.select(AlbumDao_Table.id.`as`("id").withTable(),
        AlbumDao_Table.name.`as`("name").withTable(),
        ArtistDao_Table.name.`as`("artist").withTable(),
        ArtistDao_Table.id.`as`("artist_id").withTable(),
        CoverDao_Table.hash.`as`("cover").withTable()).from(AlbumDao::class.java).join(ArtistDao::class.java,
        Join.JoinType.INNER).on(AlbumDao_Table.artist_id.withTable().`is`(ArtistDao_Table.id.withTable())).join(CoverDao::class.java,
        Join.JoinType.LEFT_OUTER).on(AlbumDao_Table.cover_id.withTable().`is`(CoverDao_Table.id.withTable())).orderBy(
        OrderBy.fromNameAlias(NameAlias("artist")).ascending()).orderBy(OrderBy.fromNameAlias(NameAlias("name")).ascending())
  }
}
