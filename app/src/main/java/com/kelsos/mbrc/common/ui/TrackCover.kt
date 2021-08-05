package com.kelsos.mbrc.common.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.kelsos.mbrc.R

@Composable
fun TrackCover(
  modifier: Modifier = Modifier,
  coverUrl: String = "",
  contentScale: ContentScale = ContentScale.Fit,
  cornerRadius: Dp = 8.dp
) = Box(modifier = Modifier.aspectRatio(1f)) {
  Image(
    painter = rememberImagePainter(
      data = coverUrl,
      builder = {
        placeholder(R.drawable.ic_image_no_cover)
        error(R.drawable.ic_image_no_cover)
      }
    ),
    modifier = modifier.clip(RoundedCornerShape(cornerRadius)),
    contentScale = contentScale,
    contentDescription = null
  )
}

@Preview
@Composable
fun TrackCoverPreview() {
  TrackCover(modifier = Modifier.fillMaxSize())
}
