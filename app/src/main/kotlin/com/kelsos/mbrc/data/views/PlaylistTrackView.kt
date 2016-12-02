package com.kelsos.mbrc.data.views

import com.kelsos.mbrc.data.dao.PlaylistTrack
import com.kelsos.mbrc.data.dao.PlaylistTrack_Table
import com.kelsos.mbrc.data.dao.PlaylistTrackInfo
import com.kelsos.mbrc.data.dao.PlaylistTrackInfo_Table
import com.kelsos.mbrc.data.db.RemoteDatabase
import com.raizlabs.android.dbflow.annotation.Column
import com.raizlabs.android.dbflow.annotation.ModelView
import com.raizlabs.android.dbflow.annotation.ModelViewQuery
import com.raizlabs.android.dbflow.sql.Query
import com.raizlabs.android.dbflow.sql.language.Join
import com.raizlabs.android.dbflow.sql.language.OrderBy
import com.raizlabs.android.dbflow.sql.language.SQLite
import com.raizlabs.android.dbflow.structure.BaseModelView

@ModelView(database = RemoteDatabase::class,
    name = "playlist_track_view") class PlaylistTrackView : BaseModelView() {

  @Column var id: Long = 0
  @Column(name = "playlist_id") var playlistId: Long = 0
  @Column var position: Long = 0
  @Column var path: String? = null
  @Column var artist: String? = null
  @Column var title: String? = null

  companion object {
    @JvmField @ModelViewQuery val QUERY: Query = SQLite.select(PlaylistTrack_Table.id.withTable(),
        PlaylistTrack_Table.playlist_id.withTable(),
        PlaylistTrack_Table.position.withTable(),
        PlaylistTrackInfo_Table.path.withTable(),
        PlaylistTrackInfo_Table.artist.withTable(),
        PlaylistTrackInfo_Table.title.withTable())
        .from(PlaylistTrack::class.java)
        .join(PlaylistTrackInfo::class.java, Join.JoinType.INNER)
        .on(PlaylistTrackInfo_Table.id.withTable().`is`(PlaylistTrack_Table.track_info_id))
        .orderBy(OrderBy.fromProperty(PlaylistTrack_Table.playlist_id).ascending())
        .orderBy(OrderBy.fromProperty(PlaylistTrack_Table.position).ascending())
  }
}
