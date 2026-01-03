package com.kelsos.mbrc.core.ui.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.kelsos.mbrc.core.ui.R

/**
 * Renders the appropriate FAB based on [FabState].
 *
 * @param state The current FAB state
 */
@Composable
fun ScaffoldFab(state: FabState) {
  when (state) {
    FabState.Hidden -> {
      // Render nothing
    }

    is FabState.Single -> SingleFab(
      icon = state.icon,
      contentDescription = state.contentDescription,
      onClick = state.onClick
    )

    is FabState.Expandable -> ExpandableFab(
      isExpanded = state.isExpanded,
      onToggle = state.onToggle,
      items = state.items
    )
  }
}

@Composable
private fun SingleFab(
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  contentDescription: String,
  onClick: () -> Unit
) {
  FloatingActionButton(
    onClick = onClick,
    containerColor = MaterialTheme.colorScheme.primaryContainer,
    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
  ) {
    Icon(
      imageVector = icon,
      contentDescription = contentDescription
    )
  }
}

/**
 * Expandable FAB (speed dial) with multiple action items.
 */
@Composable
private fun ExpandableFab(isExpanded: Boolean, onToggle: () -> Unit, items: List<FabItem>) {
  Column(
    horizontalAlignment = Alignment.End,
    verticalArrangement = Arrangement.spacedBy(12.dp)
  ) {
    // Speed dial items (shown when expanded)
    if (isExpanded) {
      items.forEach { item ->
        ExpandableFabMenuItem(
          label = item.label,
          icon = item.icon,
          onClick = item.onClick
        )
      }
    }

    // Main FAB
    FloatingActionButton(
      onClick = onToggle,
      containerColor = MaterialTheme.colorScheme.primaryContainer,
      contentColor = MaterialTheme.colorScheme.onPrimaryContainer
    ) {
      Icon(
        imageVector = if (isExpanded) Icons.Filled.Check else Icons.Filled.Add,
        contentDescription = if (isExpanded) {
          stringResource(R.string.fab_close_menu)
        } else {
          stringResource(R.string.fab_open_menu)
        }
      )
    }
  }
}

/**
 * Individual FAB menu item with label and small FAB.
 */
@Composable
private fun ExpandableFabMenuItem(
  label: String,
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  onClick: () -> Unit
) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.End
  ) {
    Surface(
      shape = RoundedCornerShape(8.dp),
      color = MaterialTheme.colorScheme.surfaceContainerHigh,
      shadowElevation = 2.dp
    ) {
      Text(
        text = label,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
      )
    }

    Spacer(modifier = Modifier.width(12.dp))

    SmallFloatingActionButton(
      onClick = onClick,
      containerColor = MaterialTheme.colorScheme.primaryContainer,
      contentColor = MaterialTheme.colorScheme.onPrimaryContainer
    ) {
      Icon(
        imageVector = icon,
        contentDescription = label
      )
    }
  }
}
