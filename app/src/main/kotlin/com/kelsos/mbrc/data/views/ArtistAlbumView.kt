package com.kelsos.mbrc.data.views

import com.kelsos.mbrc.data.dao.Album
import com.kelsos.mbrc.data.dao.Album_Table
import com.kelsos.mbrc.data.dao.Artist
import com.kelsos.mbrc.data.dao.Artist_Table
import com.kelsos.mbrc.data.dao.Cover
import com.kelsos.mbrc.data.dao.Cover_Table
import com.kelsos.mbrc.data.dao.Track
import com.kelsos.mbrc.data.dao.Track_Table
import com.kelsos.mbrc.data.db.RemoteDatabase
import com.raizlabs.android.dbflow.annotation.Column
import com.raizlabs.android.dbflow.annotation.ModelView
import com.raizlabs.android.dbflow.annotation.ModelViewQuery
import com.raizlabs.android.dbflow.sql.Query
import com.raizlabs.android.dbflow.sql.language.NameAlias
import com.raizlabs.android.dbflow.sql.language.OrderBy
import com.raizlabs.android.dbflow.sql.language.SQLite
import com.raizlabs.android.dbflow.structure.BaseModelView

@ModelView(database = RemoteDatabase::class,
    name = "artist_album_view") class ArtistAlbumView : BaseModelView() {

  @Column var id: Long = 0
  @Column var name: String? = null
  @Column var artist: String? = null
  @Column(name = "artist_id") var artistId: Long = 0
  @Column var cover: String? = null

  companion object {
    @JvmField
    @ModelViewQuery val QUERY: Query = SQLite.select(Album_Table.id.`as`("id").withTable(),
        Album_Table.name.`as`("name").withTable(),
        Artist_Table.name.`as`("artist").withTable(),
        Artist_Table.id.`as`("artist_id").withTable(),
        Cover_Table.hash.`as`("cover").withTable())
        .distinct()
        .from(Track::class.java)
        .innerJoin(Album::class.java)
        .on(Track_Table.album_id.withTable().`is`(Album_Table.id.withTable()))
        .innerJoin(Artist::class.java)
        .on(Track_Table.artist_id.withTable().`is`(Artist_Table.id.withTable()))
        .leftOuterJoin(Cover::class.java)
        .on(Album_Table.cover_id.withTable().`is`(Cover_Table.id.withTable()))
        .orderBy(OrderBy.fromNameAlias(NameAlias.builder("name").build()).ascending())
  }
}
