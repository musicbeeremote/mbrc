package com.kelsos.mbrc.repository

import android.graphics.Bitmap
import com.kelsos.mbrc.domain.TrackInfo
import com.kelsos.mbrc.domain.TrackPosition
import com.kelsos.mbrc.domain.TrackRating
import rx.Observable

interface TrackRepository {
    fun getTrackInfo(reload: Boolean): Observable<TrackInfo>

    fun getTrackLyrics(reload: Boolean): Observable<List<String>>

    fun getTrackCover(reload: Boolean): Observable<Bitmap?>

    fun getPosition(): Observable<TrackPosition>

    fun setPosition(position: TrackPosition)

    fun getRating(): Observable<TrackRating>

    fun setRating(rating: TrackRating)


    fun setTrackInfo(trackInfo: TrackInfo)

    fun setLyrics(lyrics: List<String>)

    fun setTrackCover(cover: Bitmap)
}
