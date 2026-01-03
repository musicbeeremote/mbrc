package com.kelsos.mbrc.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.kelsos.mbrc.core.networking.client.UiMessageQueue
import com.kelsos.mbrc.core.ui.theme.RemoteTheme
import com.kelsos.mbrc.feature.misc.whatsnew.WhatsNewScreen
import com.kelsos.mbrc.feature.misc.whatsnew.WhatsNewViewModel
import com.kelsos.mbrc.feature.settings.compose.UpdateRequiredScreen
import com.kelsos.mbrc.feature.settings.domain.SettingsManager
import com.kelsos.mbrc.feature.settings.theme.Theme
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

/**
 * Main composable container for the MusicBee Remote app.
 * Sets up the theme, navigation, and drawer.
 * Each screen handles its own Scaffold configuration.
 */
@Composable
fun RemoteApp() {
  val settingsManager: SettingsManager = koinInject()
  // Initial value matches DataStore default ("dark") to avoid flash on first load
  val themeState by settingsManager.themeFlow.collectAsState(initial = Theme.Dark)

  val darkTheme = when (themeState) {
    Theme.Light -> false
    Theme.Dark -> true
    Theme.System -> isSystemInDarkTheme()
  }

  RemoteTheme(darkTheme = darkTheme) {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val drawerViewModel: DrawerViewModel = koinViewModel()
    val uiMessageQueue: UiMessageQueue = koinInject()
    val whatsNewViewModel: WhatsNewViewModel = koinViewModel()

    // Update required overlay state
    var showUpdateRequired by remember { mutableStateOf(false) }
    var updateRequiredVersion by remember { mutableStateOf("") }

    // What's New overlay state
    val showWhatsNew by whatsNewViewModel.showWhatsNew.collectAsState()
    val whatsNewEntries by whatsNewViewModel.entries.collectAsState()
    val whatsNewLoading by whatsNewViewModel.isLoading.collectAsState()

    // Handle back press to close overlays
    BackHandler(enabled = showUpdateRequired || showWhatsNew) {
      when {
        showUpdateRequired -> showUpdateRequired = false
        showWhatsNew -> whatsNewViewModel.dismiss()
      }
    }

    // Handle global UI messages (connection errors, etc.)
    GlobalUiMessageHandler(
      uiMessageQueue = uiMessageQueue,
      snackbarHostState = snackbarHostState,
      onPluginUpdateRequired = { version ->
        updateRequiredVersion = version
        showUpdateRequired = true
      }
    )

    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.background)
    ) {
      ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
          AppDrawer(
            drawerState = drawerState,
            navController = navController,
            drawerViewModel = drawerViewModel
          )
        }
      ) {
        // Each screen handles its own Scaffold - no shared Scaffold here
        AppNavGraph(
          navController = navController,
          snackbarHostState = snackbarHostState,
          startDestination = Screen.Home.route,
          onOpenDrawer = {
            scope.launch { drawerState.open() }
          }
        )
      }

      // Update required overlay with slide animation from bottom
      AnimatedVisibility(
        visible = showUpdateRequired,
        enter = slideInVertically(
          initialOffsetY = { fullHeight -> fullHeight },
          animationSpec = tween(durationMillis = 300)
        ),
        exit = slideOutVertically(
          targetOffsetY = { fullHeight -> fullHeight },
          animationSpec = tween(durationMillis = 300)
        )
      ) {
        UpdateRequiredScreen(
          version = updateRequiredVersion,
          onDismiss = { showUpdateRequired = false },
          modifier = Modifier.fillMaxSize()
        )
      }

      // What's New overlay with slide animation from bottom
      AnimatedVisibility(
        visible = showWhatsNew,
        enter = slideInVertically(
          initialOffsetY = { fullHeight -> fullHeight },
          animationSpec = tween(durationMillis = 300)
        ),
        exit = slideOutVertically(
          targetOffsetY = { fullHeight -> fullHeight },
          animationSpec = tween(durationMillis = 300)
        )
      ) {
        WhatsNewScreen(
          entries = whatsNewEntries,
          onDismiss = { whatsNewViewModel.dismiss() },
          modifier = Modifier.fillMaxSize(),
          isLoading = whatsNewLoading
        )
      }
    }
  }
}
