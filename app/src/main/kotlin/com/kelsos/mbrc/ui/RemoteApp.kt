package com.kelsos.mbrc.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.AwaitPointerEventScope
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerId
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.kelsos.mbrc.core.networking.client.UiMessageQueue
import com.kelsos.mbrc.core.ui.theme.RemoteTheme
import com.kelsos.mbrc.feature.misc.whatsnew.WhatsNewScreen
import com.kelsos.mbrc.feature.misc.whatsnew.WhatsNewViewModel
import com.kelsos.mbrc.feature.settings.compose.UpdateRequiredScreen
import com.kelsos.mbrc.feature.settings.domain.SettingsManager
import com.kelsos.mbrc.feature.settings.theme.Theme
import kotlin.math.abs
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

private val DRAWER_EDGE_WIDTH = 20.dp
private const val EDGE_HORIZONTAL_DOMINANCE = 2f

/**
 * Main composable container for the MusicBee Remote app.
 * Sets up the theme, navigation, and drawer.
 * Each screen handles its own Scaffold configuration.
 */
@Composable
fun RemoteApp() {
  val settingsManager: SettingsManager = koinInject()
  // Initial value matches DataStore default ("dark") to avoid flash on first load
  val themeState by settingsManager.themeFlow.collectAsStateWithLifecycle(initialValue = Theme.Dark)

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
    val showWhatsNew by whatsNewViewModel.showWhatsNew.collectAsStateWithLifecycle()
    val whatsNewEntries by whatsNewViewModel.entries.collectAsStateWithLifecycle()
    val whatsNewLoading by whatsNewViewModel.isLoading.collectAsStateWithLifecycle()

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
        // Only enable Material's full-area drag while the drawer is open (so
        // swipe-to-close still works). When closed, the drawer's horizontal
        // AnchoredDraggable competes with per-row SwipeToDismissBox on the
        // now playing queue. Opening from the closed state is handled by a
        // narrow left-edge detector below, plus the toolbar menu button.
        gesturesEnabled = drawerState.isOpen,
        drawerContent = {
          AppDrawer(
            drawerState = drawerState,
            navController = navController,
            drawerViewModel = drawerViewModel
          )
        }
      ) {
        Box(
          modifier = Modifier
            .fillMaxSize()
            .leftEdgeDrawerSwipe(drawerState) { scope.launch { drawerState.open() } }
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
 * Left-edge swipe-to-open detector for the navigation drawer. Only arms on
 * pointer-downs within [DRAWER_EDGE_WIDTH] of the left edge, and only commits
 * ([onOpen]) once the gesture crosses touch slop while clearly rightward and
 * horizontal, so content swipes elsewhere on the screen don't open the drawer.
 *
 * Slop is tracked here on the Initial pass so children (raised-slop swipeable
 * rows in now-playing) can't consume the move events before we claim the gesture.
 */
private fun Modifier.leftEdgeDrawerSwipe(drawerState: DrawerState, onOpen: () -> Unit): Modifier =
  pointerInput(drawerState) {
    awaitPointerEventScope {
      val edgePx = DRAWER_EDGE_WIDTH.toPx()
      val slop = viewConfiguration.touchSlop
      while (true) {
        val down = awaitFirstDown(pass = PointerEventPass.Initial)
        if (drawerState.isOpen || down.position.x > edgePx) continue
        if (awaitLeftEdgeArm(down.id, slop)) onOpen()
      }
    }
  }

/**
 * Waits for the in-progress gesture started by [downId] to either arm as a
 * rightward, horizontal-dominant drawer swipe (returns `true`, consuming the
 * move) or resolve as vertical / released (returns `false`), letting children
 * handle it.
 */
private suspend fun AwaitPointerEventScope.awaitLeftEdgeArm(
  downId: PointerId,
  slop: Float
): Boolean {
  var totalX = 0f
  var totalY = 0f
  while (true) {
    val event = awaitPointerEvent(PointerEventPass.Initial)
    val change = event.changes.firstOrNull { it.id == downId } ?: return false
    if (!change.pressed) return false
    val delta = change.positionChange()
    totalX += delta.x
    totalY += delta.y
    if (totalX > slop && totalX > abs(totalY) * EDGE_HORIZONTAL_DOMINANCE) {
      change.consume()
      return true
    }
    if (abs(totalY) > slop && abs(totalY) > abs(totalX)) return false
  }
}
