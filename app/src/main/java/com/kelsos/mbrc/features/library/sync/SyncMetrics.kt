package com.kelsos.mbrc.features.library.sync

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.kelsos.mbrc.R
import com.kelsos.mbrc.metrics.SyncedData

@Composable
fun SyncMetricsDialog(metrics: SyncedData, showDialog: Boolean, dismiss: () -> Unit) {
  if (showDialog) {
    Dialog(
      onDismissRequest = { dismiss() },
    ) {
      Surface(
        modifier = Modifier.clip(RoundedCornerShape(8.dp))
      ) {
        SyncMetricsContent(metrics, dismiss)
      }
    }
  }
}

@Composable
fun SyncMetricsContent(metrics: SyncedData, dismiss: () -> Unit) =
  Column(modifier = Modifier.padding(16.dp)) {
    Row(modifier = Modifier.fillMaxWidth()) {
      Text(
        text = stringResource(id = R.string.library_stats__title),
        style = MaterialTheme.typography.h6
      )
    }
    Row(
      modifier = Modifier
        .padding(vertical = 16.dp)
        .fillMaxWidth()
    ) {
      Text(
        text = stringResource(id = R.string.library_stats__description),
        style = MaterialTheme.typography.caption,
        color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f)
      )
    }

    MetricsRow(R.string.media__genres, metrics.genres)
    MetricsRow(R.string.media__artists, metrics.artists)
    MetricsRow(R.string.media__albums, metrics.albums)
    MetricsRow(R.string.media__tracks, metrics.tracks)
    MetricsRow(R.string.media__playlists, metrics.playlists)
    Row(modifier = Modifier.padding(top = 16.dp)) {
      Spacer(modifier = Modifier.weight(1f))
      TextButton(onClick = dismiss) {
        Text(text = stringResource(id = android.R.string.ok))
      }
    }
  }

@Composable
private fun MetricsRow(@StringRes resId: Int, items: Long) {
  Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
    Column {
      Text(text = stringResource(id = resId), color = MaterialTheme.colors.primary)
    }
    Column {
      Text(text = items.toString())
    }
  }
}

@Preview
@Composable
fun SyncMetricsContentPreview() {
  SyncMetricsContent(metrics = SyncedData(10, 40, 100, 1000, 4)) {}
}
