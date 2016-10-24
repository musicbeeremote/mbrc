package com.kelsos.mbrc.data.views

import com.kelsos.mbrc.data.RemoteDatabase
import com.kelsos.mbrc.data.dao.PlaylistTrackDao
import com.kelsos.mbrc.data.dao.PlaylistTrackDao_Table
import com.kelsos.mbrc.data.dao.PlaylistTrackInfoDao
import com.kelsos.mbrc.data.dao.PlaylistTrackInfoDao_Table
import com.raizlabs.android.dbflow.annotation.Column
import com.raizlabs.android.dbflow.annotation.ModelView
import com.raizlabs.android.dbflow.annotation.ModelViewQuery
import com.raizlabs.android.dbflow.sql.Query
import com.raizlabs.android.dbflow.sql.language.Join
import com.raizlabs.android.dbflow.sql.language.OrderBy
import com.raizlabs.android.dbflow.sql.language.SQLite
import com.raizlabs.android.dbflow.structure.BaseModelView

@ModelView(database = RemoteDatabase::class,
    name = "playlist_track_view") class PlaylistTrackView : BaseModelView<PlaylistTrackDao>() {

  @Column var id: Long = 0
  @Column(name = "playlist_id") var playlistId: Long = 0
  @Column var position: Long = 0
  @Column var path: String? = null
  @Column var artist: String? = null
  @Column var title: String? = null

  companion object {
    @JvmField @ModelViewQuery val QUERY: Query = SQLite.select(PlaylistTrackDao_Table.id.withTable(),
        PlaylistTrackDao_Table.playlist_id.withTable(),
        PlaylistTrackDao_Table.position.withTable(),
        PlaylistTrackInfoDao_Table.path.withTable(),
        PlaylistTrackInfoDao_Table.artist.withTable(),
        PlaylistTrackInfoDao_Table.title.withTable())
        .from(PlaylistTrackDao::class.java)
        .join(PlaylistTrackInfoDao::class.java, Join.JoinType.INNER)
        .on(PlaylistTrackInfoDao_Table.id.withTable().`is`(PlaylistTrackDao_Table.track_info_id))
        .orderBy(OrderBy.fromProperty(PlaylistTrackDao_Table.playlist_id).ascending())
        .orderBy(OrderBy.fromProperty(PlaylistTrackDao_Table.position).ascending())
  }
}
