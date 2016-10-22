package com.kelsos.mbrc.repository

import android.graphics.Bitmap
import android.text.TextUtils
import javax.inject.Inject
import com.kelsos.mbrc.cache.TrackCache
import com.kelsos.mbrc.domain.TrackInfo
import com.kelsos.mbrc.domain.TrackPosition
import com.kelsos.mbrc.domain.TrackRating
import com.kelsos.mbrc.extensions.io
import com.kelsos.mbrc.interactors.TrackPositionInteractor
import com.kelsos.mbrc.interactors.TrackRatingInteractor
import com.kelsos.mbrc.mappers.TrackInfoMapper
import com.kelsos.mbrc.services.api.TrackService
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.functions.Func1
import rx.lang.kotlin.toSingletonObservable
import rx.schedulers.Schedulers
import java.util.*

class TrackRepositoryImpl : TrackRepository {
  @Inject
  private lateinit var trackRatingUserCase: TrackRatingInteractor
  @Inject
  private lateinit var trackPositionInteractor: TrackPositionInteractor
  @Inject
  private lateinit var cache: TrackCache
  @Inject
  private lateinit var trackService: TrackService

  override fun getTrackInfo(reload: Boolean): Observable<TrackInfo> {
    val infoObservable = trackService.getTrackInfo()
        .io()
        .flatMap {
          val info = TrackInfoMapper.map(it)
          cache.trackinfo = info
          info.toSingletonObservable()
        }

    return if (reload) infoObservable
    else Observable.concat(cache.trackinfo.toSingletonObservable(), infoObservable)
        .filter { it != null && !it.isEmpty() }
        .first()
  }

  override fun setTrackInfo(trackInfo: TrackInfo) {
    cache.trackinfo = trackInfo
  }

  override fun getTrackLyrics(reload: Boolean): Observable<List<String>> {

    val remote = trackService.getTrackLyrics()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .flatMap<List<String>>(Func1 {

          var trackLyrics = it.lyrics

          trackLyrics = trackLyrics.replace("<p>", "\r\n")
              .replace("<br>", "\n")
              .replace("&lt;", "<")
              .replace("&gt;", ">")
              .replace("&quot;", "\"")
              .replace("&apos;", "'")
              .replace("&amp;", "&")
              .replace("<p>", "\r\n")
              .replace("<br>", "\n")
              .trim({ it <= ' ' })

          val lyricsList = if (TextUtils.isEmpty(trackLyrics)) emptyList<String>()
          else ArrayList(Arrays.asList<String>(*trackLyrics.split("\r\n".toRegex())
              .dropLastWhile({ it.isEmpty() })
              .toTypedArray()))

          cache.lyrics = lyricsList
          Observable.just(lyricsList)
        })

    return if (reload) remote
    else Observable.concat(Observable.just(cache.lyrics),
        remote).filter { strings -> strings != null }.first()
  }

  override fun getTrackCover(reload: Boolean): Observable<Bitmap?> {
    val remote = trackService.getTrackCover()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .flatMap<Bitmap>(Func1 {
          cache.cover = it
          Observable.just<Bitmap>(it)
        })

    return if (reload) remote
    else Observable.concat(Observable.just(cache.cover), remote)
        .filter { bitmap ->
          bitmap != null
        }.first()
  }

  override fun setTrackCover(cover: Bitmap) {
    cache.cover = cover
  }

  override fun getPosition(): Observable<TrackPosition> {
    return trackPositionInteractor.getPosition()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .flatMap<TrackPosition>(Func1 {
          cache.position = it
          Observable.just<TrackPosition>(it)
        })
  }

  override fun setPosition(position: TrackPosition) {
    cache.position = position
  }

  override fun getRating(): Observable<TrackRating> {
    return trackRatingUserCase.getRating()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .flatMap<TrackRating>(Func1 {
          var rating = TrackRating()
          rating.value = it.rating
          cache.rating = rating
          Observable.just<TrackRating>(rating)
        })
  }

  override fun setRating(rating: TrackRating) {
    cache.rating = rating
  }

  override fun setLyrics(lyrics: List<String>) {
    cache.lyrics = lyrics
  }
}
