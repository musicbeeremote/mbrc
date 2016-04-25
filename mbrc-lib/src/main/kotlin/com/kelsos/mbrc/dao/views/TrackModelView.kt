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

@ModelView(database = RemoteDatabase::class, name = "track_view") class TrackModelView : BaseModelView<TrackDao>() {

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
    @ModelViewQuery
    val QUERY: Query = SQLite.select(TrackDao_Table.id.`as`("id").withTable(),
        TrackDao_Table.disc.`as`("disc").withTable(),
        TrackDao_Table.position.`as`("position").withTable(),
        TrackDao_Table.title.`as`("title").withTable(),
        AlbumDao_Table.name.`as`("album").withTable(),
        AlbumDao_Table.id.`as`("album_id").withTable(),
        ArtistDao_Table.name.`as`("artist").withTable(),
        CoverDao_Table.hash.`as`("cover").withTable()).from(TrackDao::class.java).join(AlbumDao::class.java,
        Join.JoinType.INNER).on(TrackDao_Table.album_id.withTable().`is`(AlbumDao_Table.id.withTable())).join(ArtistDao::class.java,
        Join.JoinType.INNER).on(TrackDao_Table.artist_id.withTable().`is`(ArtistDao_Table.id.withTable())).join(
        ArtistDao::class.java,
        Join.JoinType.INNER).`as`("album_artist").on(TrackDao_Table.album_artist_id.withTable().`is`(ArtistDao_Table.id.withTable(
        NameAlias("album_artist")))).join(CoverDao::class.java,
        Join.JoinType.LEFT_OUTER).on(AlbumDao_Table.cover_id.withTable().`is`(CoverDao_Table.id.withTable())).orderBy(
        OrderBy.fromNameAlias(NameAlias("name").withTable("album_artist")).ascending()).orderBy(OrderBy.fromNameAlias(
        NameAlias("album")).ascending()).orderBy(OrderBy.fromNameAlias(NameAlias("disc")).ascending()).orderBy(OrderBy.fromNameAlias(
        NameAlias("position")).ascending())
  }
}
