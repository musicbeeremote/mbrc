package com.kelsos.mbrc.features.settings.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.outlined.NewReleases
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.kelsos.mbrc.R

private const val PLUGIN_RELEASES_URL = "https://github.com/musicbeeremote/plugin/releases"

@Composable
fun UpdateRequiredScreen(version: String, onDismiss: () -> Unit, modifier: Modifier = Modifier) {
  val uriHandler = LocalUriHandler.current

  Surface(modifier = modifier.fillMaxSize()) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .statusBarsPadding()
        .navigationBarsPadding(),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      // Header with collapse button
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        IconButton(onClick = onDismiss) {
          Icon(
            imageVector = Icons.Default.KeyboardArrowDown,
            contentDescription = stringResource(R.string.navigate_back),
            tint = MaterialTheme.colorScheme.onSurface
          )
        }
      }

      Spacer(modifier = Modifier.weight(1f))

      Box(
        modifier = Modifier
          .size(88.dp)
          .clip(CircleShape)
          .background(MaterialTheme.colorScheme.errorContainer),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Outlined.NewReleases,
          contentDescription = stringResource(R.string.update_required__content_description),
          modifier = Modifier.size(44.dp),
          tint = MaterialTheme.colorScheme.onErrorContainer
        )
      }

      Spacer(modifier = Modifier.height(32.dp))

      Text(
        text = stringResource(R.string.plugin_update__title),
        style = MaterialTheme.typography.headlineMedium,
        textAlign = TextAlign.Center,
        color = MaterialTheme.colorScheme.onSurface
      )

      Spacer(modifier = Modifier.height(16.dp))

      Text(
        text = stringResource(R.string.plugin_update__description, version),
        style = MaterialTheme.typography.bodyLarge,
        textAlign = TextAlign.Center,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        lineHeight = MaterialTheme.typography.bodyLarge.lineHeight * 1.2,
        modifier = Modifier.padding(horizontal = 24.dp)
      )

      Spacer(modifier = Modifier.weight(1f))

      Button(
        onClick = { uriHandler.openUri(PLUGIN_RELEASES_URL) },
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 24.dp)
          .padding(bottom = 16.dp)
      ) {
        Text(
          text = stringResource(R.string.action_learn_more),
          style = MaterialTheme.typography.labelLarge
        )
      }
    }
  }
}
