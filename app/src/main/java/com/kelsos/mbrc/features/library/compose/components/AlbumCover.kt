package com.kelsos.mbrc.features.library.compose.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import coil3.size.Size
import com.kelsos.mbrc.R
import java.io.File
import okio.ByteString.Companion.encodeUtf8

/**
 * Displays an album cover image using a pre-computed cover hash/key.
 *
 * This is the preferred version when the cover hash is already available
 * (e.g., from [Album.cover]). It avoids computing the hash and checking
 * file existence on the main thread.
 *
 * @param cover The cover hash/key from the database, or null if no cover is cached
 * @param modifier Modifier for the composable
 * @param size The size of the cover image
 */
@Composable
fun AlbumCover(cover: String?, modifier: Modifier = Modifier, size: Dp = 48.dp) {
  val context = LocalContext.current
  val density = LocalDensity.current
  val sizePx = with(density) { size.roundToPx() }

  if (cover != null) {
    val coverFile = remember(cover) {
      File(File(context.cacheDir, "covers"), cover)
    }

    val imageRequest = remember(cover, sizePx) {
      ImageRequest.Builder(context)
        .data(coverFile)
        .size(Size(sizePx, sizePx))
        .memoryCacheKey("cover_${cover}_$sizePx")
        .diskCacheKey("cover_$cover")
        .memoryCachePolicy(CachePolicy.ENABLED)
        .diskCachePolicy(CachePolicy.ENABLED)
        .build()
    }

    AsyncImage(
      model = imageRequest,
      contentDescription = null,
      placeholder = painterResource(R.drawable.ic_image_no_cover),
      error = painterResource(R.drawable.ic_image_no_cover),
      contentScale = ContentScale.Crop,
      modifier = modifier.size(size)
    )
  } else {
    Image(
      painter = painterResource(R.drawable.ic_image_no_cover),
      contentDescription = null,
      contentScale = ContentScale.Crop,
      modifier = modifier.size(size)
    )
  }
}

/**
 * Displays an album cover by computing the cover key from artist and album names.
 *
 * Use this version when the cover hash is not available (e.g., for tracks).
 * The key is computed once and Coil handles missing files gracefully by
 * showing the error placeholder.
 *
 * Note: This version does NOT check if the file exists - it lets Coil
 * handle missing files, which is more performant than blocking I/O
 * during composition.
 *
 * @param artist The artist name
 * @param album The album name
 * @param modifier Modifier for the composable
 * @param size The size of the cover image
 */
@Composable
fun AlbumCoverByKey(
  artist: String,
  album: String,
  modifier: Modifier = Modifier,
  size: Dp = 48.dp
) {
  val context = LocalContext.current
  val density = LocalDensity.current
  val sizePx = with(density) { size.roundToPx() }

  val key = remember(artist, album) {
    "${artist}_$album".encodeUtf8().sha1().hex().uppercase()
  }

  val coverFile = remember(key) {
    File(File(context.cacheDir, "covers"), key)
  }

  val imageRequest = remember(key, sizePx) {
    ImageRequest.Builder(context)
      .data(coverFile)
      .size(Size(sizePx, sizePx))
      .memoryCacheKey("cover_${key}_$sizePx")
      .diskCacheKey("cover_$key")
      .memoryCachePolicy(CachePolicy.ENABLED)
      .diskCachePolicy(CachePolicy.ENABLED)
      .build()
  }

  AsyncImage(
    model = imageRequest,
    contentDescription = null,
    placeholder = painterResource(R.drawable.ic_image_no_cover),
    error = painterResource(R.drawable.ic_image_no_cover),
    contentScale = ContentScale.Crop,
    modifier = modifier.size(size)
  )
}
