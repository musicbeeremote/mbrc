package com.kelsos.mbrc.app

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.kelsos.mbrc.R
import com.kelsos.mbrc.common.ui.compose.DrawerNavigationIcon
import com.kelsos.mbrc.common.ui.compose.RemoteTopAppBar
import com.kelsos.mbrc.features.settings.SettingsManager
import com.kelsos.mbrc.features.settings.compose.UpdateRequiredScreen
import com.kelsos.mbrc.features.theme.Theme
import com.kelsos.mbrc.features.whatsnew.WhatsNewScreen
import com.kelsos.mbrc.features.whatsnew.WhatsNewViewModel
import com.kelsos.mbrc.networking.client.UiMessageQueue
import com.kelsos.mbrc.theme.RemoteTheme
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

/**
 * Main composable container for the MusicBee Remote app.
 * Sets up the theme, navigation, drawer, and scaffold structure.
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

    // Screen configuration state
    var currentScreenConfig by remember { mutableStateOf(ScreenConfig.Empty) }

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

    // Handle screen-level snackbar messages
    LaunchedEffect(currentScreenConfig.snackbarMessages) {
      currentScreenConfig.snackbarMessages?.collect { message ->
        snackbarHostState.showSnackbar(message)
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
        Scaffold(
          topBar = {
            val customTopBar = currentScreenConfig.topBar
            if (customTopBar != null) {
              // Screen provides a completely custom topBar
              customTopBar()
            } else {
              // Use default RemoteTopAppBar with optional actions
              RemoteTopAppBar(
                title = getCurrentScreenTitle(navController),
                navigationIcon = {
                  DrawerNavigationIcon(
                    onClick = {
                      scope.launch {
                        drawerState.open()
                      }
                    }
                  )
                },
                actions = currentScreenConfig.topBarActions ?: {},
                transparent = currentScreenConfig.transparentTopBar
              )
            }
          },
          floatingActionButton = {
            currentScreenConfig.floatingActionButton?.invoke()
          },
          snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
          },
          containerColor = if (currentScreenConfig.transparentTopBar) {
            Color.Transparent
          } else {
            MaterialTheme.colorScheme.background
          }
        ) { paddingValues ->
          Box(
            modifier = Modifier
              .fillMaxSize()
              .then(
                if (currentScreenConfig.transparentTopBar) {
                  Modifier.padding(bottom = paddingValues.calculateBottomPadding())
                } else {
                  Modifier.padding(paddingValues)
                }
              )
          ) {
            AppNavGraph(
              navController = navController,
              snackbarHostState = snackbarHostState,
              startDestination = Screen.Home.route,
              onScreenConfigChange = { config ->
                currentScreenConfig = config
              },
              onOpenDrawer = {
                scope.launch { drawerState.open() }
              }
            )
          }
        }
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

/**
 * Gets the title for the current screen based on the navigation route.
 * TODO: This should eventually be managed by each screen's ViewModel
 */
@Composable
private fun getCurrentScreenTitle(navController: androidx.navigation.NavController): String {
  // Collect the current back stack entry as state to trigger recomposition on navigation
  val currentBackStackEntry by navController.currentBackStackEntryAsState()
  val currentRoute = currentBackStackEntry?.destination?.route

  return currentRoute?.let { route ->
    getScreenTitleForRoute(route)
  } ?: stringResource(R.string.application_name)
}

@Composable
private fun getScreenTitleForRoute(route: String): String = when (route) {
  Screen.Home.route -> stringResource(R.string.nav_now_playing)
  Screen.Library.route -> stringResource(R.string.common_library)
  Screen.Playlists.route -> stringResource(R.string.nav_playlists)
  Screen.Radio.route -> stringResource(R.string.nav_radio)
  Screen.Settings.route -> stringResource(R.string.common_settings)
  Screen.Help.route -> stringResource(R.string.nav_help)
  Screen.ConnectionManager.route -> stringResource(R.string.connection_manager_title)
  Screen.NowPlayingList.route -> stringResource(R.string.menu_queue_next)
  else -> getDetailScreenTitle(route)
}

@Composable
private fun getDetailScreenTitle(route: String): String = when {
  route.startsWith("album_tracks") -> ""
  route.startsWith("artist_albums") -> ""
  route.startsWith("genre_artists") -> ""
  else -> stringResource(R.string.application_name)
}
