package com.kelsos.mbrc.common.ui

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun SingleLineRow(
  text: String?,
  clicked: () -> Unit = {},
  menuContent: @Composable ColumnScope.() -> Unit,
) = Row(
  modifier =
    Modifier
      .fillMaxWidth()
      .height(48.dp)
      .clickable { clicked() },
  verticalAlignment = Alignment.CenterVertically,
  horizontalArrangement = Arrangement.SpaceBetween,
) {
  Column(modifier = Modifier.weight(1f)) {
    Text(
      text = text ?: "",
      style = MaterialTheme.typography.body1,
      modifier =
        Modifier
          .padding(start = 16.dp),
      maxLines = 1,
      overflow = TextOverflow.Ellipsis,
    )
  }
  PopupMenu(menuContent)
}

@Composable
fun DoubleLineRow(
  lineOne: String?,
  lineTwo: String?,
  coverUrl: String?,
  clicked: () -> Unit = {},
  menuContent: @Composable ColumnScope.() -> Unit,
) = Row(
  modifier =
    Modifier
      .fillMaxWidth()
      .height(72.dp)
      .clickable { clicked() }
      .padding(vertical = 8.dp),
  verticalAlignment = Alignment.CenterVertically,
) {
  if (coverUrl != null) {
    Column(
      modifier =
        Modifier
          .padding(start = 16.dp)
          .width(48.dp)
          .height(48.dp),
    ) {
      TrackCover(
        coverUrl = coverUrl,
        modifier =
          Modifier
            .size(48.dp),
        cornerRadius = 2.dp,
      )
    }
  }
  Column(
    modifier =
      Modifier
        .weight(1f)
        .padding(start = 16.dp),
    Arrangement.SpaceAround,
  ) {
    Text(
      text = lineOne ?: "",
      style = MaterialTheme.typography.body1,
      maxLines = 1,
      overflow = TextOverflow.Ellipsis,
    )
    Text(
      text = lineTwo ?: "",
      style = MaterialTheme.typography.subtitle2,
      color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f),
      maxLines = 1,
      overflow = TextOverflow.Ellipsis,
    )
  }
  PopupMenu(menuContent)
}

@Preview(
  uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
fun SingleLineRowPreview() {
  SingleLineRow(text = "Playlist") {}
}

@Preview(
  uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
fun DoubleLineRowPreview() {
  DoubleLineRow(lineOne = "Album", lineTwo = "Artist", coverUrl = "") {}
}
