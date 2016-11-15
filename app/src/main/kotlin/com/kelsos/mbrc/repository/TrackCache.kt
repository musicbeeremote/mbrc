package com.kelsos.mbrc.repository

import android.app.Application
import com.fasterxml.jackson.databind.ObjectMapper
import com.kelsos.mbrc.domain.TrackInfo
import rx.Completable
import rx.Single
import java.io.File
import java.io.FileNotFoundException
import javax.inject.Inject

class TrackCache
@Inject constructor(private val mapper: ObjectMapper,
                    private val context: Application) {

    fun persist(trackInfo: TrackInfo): Completable {
        return Completable.fromCallable {
            val infoFile = File(context.filesDir, TRACK_INFO)
            if (infoFile.exists()) {
                infoFile.delete()
            }
            mapper.writeValue(infoFile, trackInfo)
        }
    }

    fun restore(): Single<TrackInfo> {
        return Single.fromCallable {
            val infoFile = File(context.filesDir, TRACK_INFO)
            if (infoFile.exists()) {
                return@fromCallable mapper.readValue(infoFile, TrackInfo::class.java)
            }
            throw FileNotFoundException()
        }
    }

    companion object {
        const val TRACK_INFO = "track.json"
    }
}
