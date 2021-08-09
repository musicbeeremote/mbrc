package com.kelsos.mbrc.common.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.contentColorFor
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import com.kelsos.mbrc.R

@Composable
fun RemoteTopAppBar(
  openDrawer: () -> Unit,
  content: @Composable ColumnScope.() -> Unit
) = TopAppBar(
  backgroundColor = MaterialTheme.colors.primary,
  contentColor = contentColorFor(
    backgroundColor = MaterialTheme.colors.primary
  ),
) {
  Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
    IconButton(onClick = { openDrawer() }) {
      Icon(
        imageVector = Icons.Filled.Menu,
        contentDescription = stringResource(id = R.string.navigation_menu_description)
      )
    }
    Column(content = content)
  }
}

@Composable
fun EmptyScreen(
  modifier: Modifier = Modifier,
  text: String,
  imageVector: ImageVector,
  contentDescription: String,
  content: @Composable() (ColumnScope.() -> Unit)? = null
) = Row(
  modifier = modifier,
  verticalAlignment = Alignment.CenterVertically,
  horizontalArrangement = Arrangement.Center
) {
  Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
    Text(text = text, style = MaterialTheme.typography.h5)
    Icon(
      imageVector = imageVector,
      contentDescription = contentDescription,
      modifier = Modifier.fillMaxSize(0.2f)
    )
    content?.invoke(this)
  }
}
