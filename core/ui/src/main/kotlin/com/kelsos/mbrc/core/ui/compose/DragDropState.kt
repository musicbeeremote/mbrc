package com.kelsos.mbrc.core.ui.compose

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.lazy.LazyListItemInfo
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch

/**
 * State holder for drag and drop functionality in a LazyColumn.
 *
 * Based on the official AndroidX LazyColumnDragAndDropDemo.kt from the Compose Foundation library.
 * Source: https://cs.android.com/androidx/platform/frameworks/support/+/androidx-main:
 * compose/foundation/foundation/integration-tests/foundation-demos/src/main/java/
 * androidx/compose/foundation/demos/LazyColumnDragAndDropDemo.kt
 *
 * @param state The LazyListState of the LazyColumn
 * @param scope CoroutineScope for animations and scroll operations
 * @param onMove Callback invoked when items are swapped during drag
 * @param onDragEnd Callback invoked when drag operation completes (for syncing to server)
 */
class DragDropState(
  private val state: LazyListState,
  private val scope: CoroutineScope,
  private val onMove: (Int, Int) -> Unit,
  private val onDragEnd: () -> Unit
) {
  var draggingItemIndex by mutableStateOf<Int?>(null)
    private set

  internal val scrollChannel = Channel<Float>()

  private var draggingItemDraggedDelta by mutableFloatStateOf(0f)
  private var draggingItemInitialOffset by mutableIntStateOf(0)

  val draggingItemOffset: Float
    get() = draggingItemLayoutInfo?.let { item ->
      draggingItemInitialOffset + draggingItemDraggedDelta - item.offset
    } ?: 0f

  private val draggingItemLayoutInfo: LazyListItemInfo?
    get() = state.layoutInfo.visibleItemsInfo.firstOrNull { it.index == draggingItemIndex }

  var previousIndexOfDraggedItem by mutableStateOf<Int?>(null)
    private set

  var previousItemOffset = Animatable(0f)
    private set

  internal fun onDragStart(offset: Offset) {
    state.layoutInfo.visibleItemsInfo
      .firstOrNull { item -> offset.y.toInt() in item.offset..item.offset + item.size }
      ?.also {
        draggingItemIndex = it.index
        draggingItemInitialOffset = it.offset
      }
  }

  internal fun onDragInterrupted() {
    if (draggingItemIndex != null) {
      previousIndexOfDraggedItem = draggingItemIndex
      val startOffset = draggingItemOffset
      scope.launch {
        previousItemOffset.snapTo(startOffset)
        previousItemOffset.animateTo(
          0f,
          spring(stiffness = Spring.StiffnessMediumLow, visibilityThreshold = 1f)
        )
        previousIndexOfDraggedItem = null
      }
      // Notify that drag has ended - this triggers server sync
      onDragEnd()
    }
    draggingItemDraggedDelta = 0f
    draggingItemIndex = null
    draggingItemInitialOffset = 0
  }

  internal fun onDrag(offset: Offset) {
    draggingItemDraggedDelta += offset.y

    val draggingItem = draggingItemLayoutInfo ?: return
    val startOffset = draggingItem.offset + draggingItemOffset
    val endOffset = startOffset + draggingItem.size
    val middleOffset = startOffset + (endOffset - startOffset) / 2f

    val targetItem = state.layoutInfo.visibleItemsInfo.find { item ->
      middleOffset.toInt() in item.offset..item.offsetEnd &&
        draggingItem.index != item.index
    }

    if (targetItem != null) {
      if (
        draggingItem.index == state.firstVisibleItemIndex ||
        targetItem.index == state.firstVisibleItemIndex
      ) {
        state.requestScrollToItem(
          state.firstVisibleItemIndex,
          state.firstVisibleItemScrollOffset
        )
      }
      onMove.invoke(draggingItem.index, targetItem.index)
      draggingItemIndex = targetItem.index
    } else {
      val overscroll = when {
        draggingItemDraggedDelta > 0 ->
          (endOffset - state.layoutInfo.viewportEndOffset).coerceAtLeast(0f)

        draggingItemDraggedDelta < 0 ->
          (startOffset - state.layoutInfo.viewportStartOffset).coerceAtMost(0f)

        else -> 0f
      }
      if (overscroll != 0f) {
        scrollChannel.trySend(overscroll)
      }
    }
  }

  private val LazyListItemInfo.offsetEnd: Int
    get() = this.offset + this.size
}

/**
 * Creates and remembers a [DragDropState] for managing drag and drop in a LazyColumn.
 *
 * @param lazyListState The LazyListState of the LazyColumn
 * @param onMove Callback invoked when items are swapped during drag
 * @param onDragEnd Callback invoked when drag operation completes
 */
@Composable
fun rememberDragDropState(
  lazyListState: LazyListState,
  onMove: (Int, Int) -> Unit,
  onDragEnd: () -> Unit
): DragDropState {
  val scope = rememberCoroutineScope()
  val state = remember(lazyListState) {
    DragDropState(
      state = lazyListState,
      onMove = onMove,
      scope = scope,
      onDragEnd = onDragEnd
    )
  }

  LaunchedEffect(state) {
    while (true) {
      val diff = state.scrollChannel.receive()
      lazyListState.scrollBy(diff)
    }
  }

  return state
}

/**
 * Modifier that enables drag detection on a container (typically applied to LazyColumn).
 * Detects long press followed by drag gestures.
 */
fun Modifier.dragContainer(dragDropState: DragDropState): Modifier = pointerInput(dragDropState) {
  detectDragGesturesAfterLongPress(
    onDrag = { change, offset ->
      change.consume()
      dragDropState.onDrag(offset = offset)
    },
    onDragStart = { offset -> dragDropState.onDragStart(offset) },
    onDragEnd = { dragDropState.onDragInterrupted() },
    onDragCancel = { dragDropState.onDragInterrupted() }
  )
}
