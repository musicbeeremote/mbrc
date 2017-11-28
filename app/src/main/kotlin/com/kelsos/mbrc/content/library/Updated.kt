package com.kelsos.mbrc.content.library

import com.kelsos.mbrc.RemoteDatabase
import com.raizlabs.android.dbflow.annotation.Column
import com.raizlabs.android.dbflow.annotation.PrimaryKey
import com.raizlabs.android.dbflow.annotation.Table

@Table(name = "updated", database = RemoteDatabase::class)
data class Updated(
    @PrimaryKey(autoincrement = true)
    var id: Long = 0,
    @Column(name = "path")
    var path: String = "",
    @Column(name = "date_inserted")
    var dateInserted: Long = 0
)
