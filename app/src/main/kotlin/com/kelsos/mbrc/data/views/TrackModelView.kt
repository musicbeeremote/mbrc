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
import com.raizlabs.android.dbflow.sql.language.Join
import com.raizlabs.android.dbflow.sql.language.NameAlias
import com.raizlabs.android.dbflow.sql.language.OrderBy
import com.raizlabs.android.dbflow.sql.language.SQLite
import com.raizlabs.android.dbflow.structure.BaseModelView

@ModelView(database = RemoteDatabase::class,
    name = "track_view") class TrackModelView : BaseModelView() {

  @Column(name = "id") var id: Long = 0
  @Column(name = "disc") var disc: Int = 0
  @Column(name = "position") var position: Int = 0
  @Column(name = "title") var title: String? = null
  @Column(name = "album") var album: String? = null
  @Column(name = "album_id") var albumId: Long = 0
  @Column(name = "artist") var artist: String? = null
  @Column(name = "cover") var cover: String? = null

  companion object {
    @JvmField
    @ModelViewQuery val QUERY: Query = SQLite.select(Track_Table.id.`as`("id").withTable(),
        Track_Table.disc.`as`("disc").withTable(),
        Track_Table.position.`as`("position").withTable(),
        Track_Table.title.`as`("title").withTable(),
        Album_Table.name.`as`("album").withTable(),
        Album_Table.id.`as`("album_id").withTable(),
        Artist_Table.name.`as`("artist").withTable(),
        Cover_Table.hash.`as`("cover").withTable())
        .from(Track::class.java)
        .join(Album::class.java, Join.JoinType.INNER)
        .on(Track_Table.album_id.withTable().`is`(Album_Table.id.withTable()))
        .join(Artist::class.java, Join.JoinType.INNER)
        .on(Track_Table.artist_id.withTable().`is`(Artist_Table.id.withTable()))
        .join(Artist::class.java, Join.JoinType.INNER)
        .`as`("album_artist")
        .on(Track_Table.album_artist_id.withTable().`is`(Artist_Table.id.withTable(NameAlias.builder(
            "album_artist").build())))
        .join(Cover::class.java, Join.JoinType.LEFT_OUTER)
        .on(Album_Table.cover_id.withTable().`is`(Cover_Table.id.withTable()))
        .orderBy(OrderBy.fromNameAlias(NameAlias.builder("name").withTable("album_artist").build()).ascending())
        .orderBy(OrderBy.fromNameAlias(NameAlias.builder("album").build()).ascending())
        .orderBy(OrderBy.fromNameAlias(NameAlias.builder("disc").build()).ascending())
        .orderBy(OrderBy.fromNameAlias(NameAlias.builder("position").build()).ascending())
  }
}
