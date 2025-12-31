package com.kelsos.mbrc.features.widgets.glance

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.glance.state.GlanceStateDefinition
import coil3.imageLoader
import coil3.request.ImageRequest
import coil3.request.allowHardware
import coil3.toBitmap
import com.kelsos.mbrc.R
import com.kelsos.mbrc.features.widgets.WidgetState
import java.io.File
import java.io.IOException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

private val Context.widgetDataStore: DataStore<Preferences> by preferencesDataStore(
  name = "widget_state"
)

/**
 * GlanceStateDefinition for widget state using Preferences DataStore.
 * Handles persistence of widget state with proper Glance integration.
 */
object WidgetGlanceStateDefinition : GlanceStateDefinition<Preferences> {

  private val KEY_TITLE = stringPreferencesKey("title")
  private val KEY_ARTIST = stringPreferencesKey("artist")
  private val KEY_ALBUM = stringPreferencesKey("album")
  private val KEY_IS_PLAYING = booleanPreferencesKey("is_playing")
  private val KEY_COVER_URL = stringPreferencesKey("cover_url")

  private const val COVER_CACHE_FILE = "widget_cover_cache.png"
  private const val PNG_QUALITY = 90

  override suspend fun getDataStore(context: Context, fileKey: String): DataStore<Preferences> =
    context.widgetDataStore

  override fun getLocation(context: Context, fileKey: String): File =
    context.filesDir.resolve("widget_state")

  /**
   * Converts DataStore Preferences to WidgetState.
   * Loads the cached bitmap if available.
   */
  fun preferencesToWidgetState(context: Context, prefs: Preferences): WidgetState {
    val coverBitmap = loadCachedBitmap(context)

    return WidgetState(
      title = prefs[KEY_TITLE] ?: "",
      artist = prefs[KEY_ARTIST] ?: "",
      album = prefs[KEY_ALBUM] ?: "",
      isPlaying = prefs[KEY_IS_PLAYING] ?: false,
      coverBitmap = coverBitmap
    )
  }

  /**
   * Creates preferences update for track info.
   */
  fun trackInfoUpdate(
    title: String,
    artist: String,
    album: String,
    coverUrl: String
  ): suspend (Preferences) -> Preferences = { prefs ->
    prefs.toMutablePreferences().apply {
      this[KEY_TITLE] = title
      this[KEY_ARTIST] = artist
      this[KEY_ALBUM] = album
      this[KEY_COVER_URL] = coverUrl
    }
  }

  /**
   * Creates preferences update for play state.
   */
  fun playStateUpdate(isPlaying: Boolean): suspend (Preferences) -> Preferences = { prefs ->
    prefs.toMutablePreferences().apply {
      this[KEY_IS_PLAYING] = isPlaying
    }
  }

  /**
   * Loads and caches cover image from URL.
   */
  @Suppress("InjectDispatcher") // Using IO dispatcher directly in state definition object
  suspend fun loadAndCacheCover(context: Context, url: String): Bitmap? {
    if (url.isBlank()) {
      deleteCachedBitmap(context)
      return null
    }

    return withContext(Dispatchers.IO) {
      try {
        val request = ImageRequest.Builder(context)
          .data(url)
          .size(context.resources.getDimensionPixelSize(R.dimen.widget_normal_height))
          .allowHardware(false)
          .build()

        val result = context.imageLoader.execute(request)
        result.image?.toBitmap()?.also { bitmap ->
          saveBitmapToCache(context, bitmap)
        }
      } catch (e: IOException) {
        Timber.e(e, "Failed to load cover bitmap")
        null
      }
    }
  }

  private fun loadCachedBitmap(context: Context): Bitmap? = try {
    val cacheFile = File(context.filesDir, COVER_CACHE_FILE)
    if (cacheFile.exists()) {
      BitmapFactory.decodeFile(cacheFile.absolutePath)
    } else {
      null
    }
  } catch (e: IOException) {
    Timber.e(e, "Failed to load cached bitmap")
    null
  }

  private fun saveBitmapToCache(context: Context, bitmap: Bitmap) {
    try {
      val cacheFile = File(context.filesDir, COVER_CACHE_FILE)
      cacheFile.outputStream().use { out ->
        bitmap.compress(Bitmap.CompressFormat.PNG, PNG_QUALITY, out)
      }
    } catch (e: IOException) {
      Timber.e(e, "Failed to save bitmap to cache")
    }
  }

  private fun deleteCachedBitmap(context: Context) {
    try {
      val cacheFile = File(context.filesDir, COVER_CACHE_FILE)
      if (cacheFile.exists()) {
        cacheFile.delete()
      }
    } catch (e: SecurityException) {
      Timber.e(e, "Failed to delete cached bitmap")
    }
  }
}
