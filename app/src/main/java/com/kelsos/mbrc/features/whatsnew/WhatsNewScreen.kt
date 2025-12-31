package com.kelsos.mbrc.features.whatsnew

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kelsos.mbrc.R

@Composable
fun WhatsNewScreen(
  entries: List<ChangelogEntry>,
  onDismiss: () -> Unit,
  modifier: Modifier = Modifier,
  isLoading: Boolean = false
) {
  Surface(
    modifier = modifier.fillMaxSize(),
    color = MaterialTheme.colorScheme.background
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .windowInsetsPadding(WindowInsets.statusBars)
    ) {
      WhatsNewHeader(onDismiss = onDismiss)

      when {
        isLoading -> LoadingState(modifier = Modifier.weight(1f))

        entries.isEmpty() -> EmptyState(modifier = Modifier.weight(1f))

        else -> ChangelogContent(
          entries = entries,
          modifier = Modifier.weight(1f)
        )
      }

      // Bottom action button
      Surface(
        modifier = Modifier
          .fillMaxWidth()
          .windowInsetsPadding(WindowInsets.navigationBars),
        tonalElevation = 2.dp
      ) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
          contentAlignment = Alignment.Center
        ) {
          FilledTonalButton(
            onClick = onDismiss,
            modifier = Modifier.fillMaxWidth()
          ) {
            Text(
              text = stringResource(R.string.whats_new_got_it),
              style = MaterialTheme.typography.labelLarge
            )
          }
        }
      }
    }
  }
}

@Composable
private fun LoadingState(modifier: Modifier = Modifier) {
  Box(
    modifier = modifier.fillMaxWidth(),
    contentAlignment = Alignment.Center
  ) {
    if (LocalInspectionMode.current) {
      CircularProgressIndicator(progress = { 0.7f })
    } else {
      CircularProgressIndicator()
    }
  }
}

@Composable
private fun EmptyState(modifier: Modifier = Modifier) {
  Box(
    modifier = modifier.fillMaxWidth(),
    contentAlignment = Alignment.Center
  ) {
    Text(
      text = stringResource(R.string.whats_new_empty),
      style = MaterialTheme.typography.bodyLarge,
      color = MaterialTheme.colorScheme.onSurfaceVariant
    )
  }
}

@Composable
private fun ChangelogContent(entries: List<ChangelogEntry>, modifier: Modifier = Modifier) {
  val firstVersion = entries.firstOrNull { it is ChangelogEntry.Version } as? ChangelogEntry.Version

  LazyColumn(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp),
    verticalArrangement = Arrangement.spacedBy(8.dp)
  ) {
    item { Spacer(modifier = Modifier.height(8.dp)) }

    itemsIndexed(
      items = entries,
      key = { index, entry ->
        when (entry) {
          is ChangelogEntry.Version -> "version_${entry.version}"
          is ChangelogEntry.Entry -> "entry_${index}_${entry.hashCode()}"
        }
      }
    ) { index, entry ->
      when (entry) {
        is ChangelogEntry.Version -> {
          // Add divider before version headers (except the first one)
          val isFirstVersion = entry == firstVersion
          if (!isFirstVersion) {
            HorizontalDivider(
              modifier = Modifier.padding(vertical = 8.dp),
              color = MaterialTheme.colorScheme.outlineVariant
            )
          }
          VersionHeader(
            version = entry,
            isLatest = isFirstVersion
          )
        }

        is ChangelogEntry.Entry -> EntryItem(entry)
      }
    }

    item { Spacer(modifier = Modifier.height(16.dp)) }
  }
}

@Composable
private fun WhatsNewHeader(onDismiss: () -> Unit, modifier: Modifier = Modifier) {
  Column(modifier = modifier.fillMaxWidth()) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(start = 16.dp, end = 8.dp, top = 8.dp, bottom = 8.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column {
        Text(
          text = stringResource(R.string.whats_new_title),
          style = MaterialTheme.typography.headlineSmall,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onSurface
        )
        Text(
          text = stringResource(R.string.whats_new_subtitle),
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }

      IconButton(
        onClick = onDismiss,
        colors = IconButtonDefaults.iconButtonColors(
          containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        modifier = Modifier.size(40.dp)
      ) {
        Icon(
          imageVector = Icons.Default.Close,
          contentDescription = stringResource(R.string.description_close),
          tint = MaterialTheme.colorScheme.onSurfaceVariant,
          modifier = Modifier.size(20.dp)
        )
      }
    }

    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
  }
}

@Composable
private fun VersionHeader(
  version: ChangelogEntry.Version,
  modifier: Modifier = Modifier,
  isLatest: Boolean = false
) {
  Row(
    modifier = modifier
      .fillMaxWidth()
      .padding(top = 16.dp, bottom = 8.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      Text(
        text = "Version ${version.version}",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurface
      )
      if (isLatest) {
        Text(
          text = stringResource(R.string.whats_new_latest_badge),
          style = MaterialTheme.typography.labelSmall,
          fontWeight = FontWeight.Medium,
          color = MaterialTheme.colorScheme.primary,
          modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f))
            .padding(horizontal = 6.dp, vertical = 2.dp)
        )
      }
    }
    Text(
      text = version.release,
      style = MaterialTheme.typography.bodySmall,
      color = MaterialTheme.colorScheme.onSurfaceVariant
    )
  }
}

@Composable
private fun EntryItem(entry: ChangelogEntry.Entry, modifier: Modifier = Modifier) {
  Row(
    modifier = modifier
      .fillMaxWidth()
      .padding(vertical = 4.dp, horizontal = 4.dp),
    verticalAlignment = Alignment.Top
  ) {
    EntryTypeIndicator(type = entry.type)

    Spacer(modifier = Modifier.width(12.dp))

    Text(
      text = entry.text,
      style = MaterialTheme.typography.bodyMedium,
      color = MaterialTheme.colorScheme.onSurface,
      modifier = Modifier.weight(1f)
    )
  }
}

private data class EntryTypeStyle(val icon: ImageVector, val contentDescription: Int)

@Composable
private fun EntryTypeIndicator(type: EntryType, modifier: Modifier = Modifier) {
  val color = when (type) {
    EntryType.FEATURE -> MaterialTheme.colorScheme.tertiary
    EntryType.BUG -> MaterialTheme.colorScheme.primary
    EntryType.REMOVED -> MaterialTheme.colorScheme.error
  }

  val style = when (type) {
    EntryType.FEATURE -> EntryTypeStyle(
      icon = Icons.Default.AutoAwesome,
      contentDescription = R.string.entry_type_feature
    )

    EntryType.BUG -> EntryTypeStyle(
      icon = Icons.Default.Build,
      contentDescription = R.string.entry_type_bug
    )

    EntryType.REMOVED -> EntryTypeStyle(
      icon = Icons.Default.DeleteOutline,
      contentDescription = R.string.entry_type_removed
    )
  }

  Box(
    modifier = modifier
      .size(32.dp)
      .clip(RoundedCornerShape(8.dp))
      .background(color.copy(alpha = 0.12f)),
    contentAlignment = Alignment.Center
  ) {
    Icon(
      imageVector = style.icon,
      contentDescription = stringResource(style.contentDescription),
      tint = color,
      modifier = Modifier.size(18.dp)
    )
  }
}
