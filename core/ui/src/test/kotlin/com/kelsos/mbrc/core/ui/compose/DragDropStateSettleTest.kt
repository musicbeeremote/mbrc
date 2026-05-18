package com.kelsos.mbrc.core.ui.compose

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.CoroutineScope
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [34])
class DragDropStateSettleTest {

  @get:Rule
  val composeTestRule = createComposeRule()

  @Test
  fun `settle window opens after drag release and clears after the timeout`() {
    val dragDrop = mountAndStartDrag(itemY = 50)

    assertThat(dragDrop.dragSourceIndex).isNotNull()
    assertThat(dragDrop.draggingItemIndex).isNotNull()
    assertThat(dragDrop.isSettling).isFalse()

    composeTestRule.runOnUiThread { dragDrop.onDragInterrupted() }
    composeTestRule.waitForIdle()

    assertThat(dragDrop.isSettling).isTrue()
    assertThat(dragDrop.dragSourceIndex).isNotNull()

    // Past the 100ms settle timeout the state must clear itself.
    composeTestRule.mainClock.advanceTimeBy(SETTLE_TIMEOUT_MS * 4)
    composeTestRule.waitForIdle()

    assertThat(dragDrop.isSettling).isFalse()
    assertThat(dragDrop.dragSourceIndex).isNull()
    assertThat(dragDrop.draggingItemIndex).isNull()
  }

  @Test
  fun `starting a new drag while a previous settle is pending invalidates the stale settle`() {
    val dragDrop = mountAndStartDrag(itemY = 50)

    composeTestRule.runOnUiThread { dragDrop.onDragInterrupted() }
    composeTestRule.waitForIdle()
    assertThat(dragDrop.isSettling).isTrue()

    // Begin a second drag before the first settle's coroutine completes.
    composeTestRule.runOnUiThread { dragDrop.onDragStart(Offset(0f, 50f)) }
    composeTestRule.waitForIdle()

    assertThat(dragDrop.isSettling).isFalse()
    assertThat(dragDrop.dragSourceIndex).isNotNull()

    // Let the original settle timeout fire. Because settleVersion bumped at
    // the second onDragStart, the stale completion must NOT clear our state.
    composeTestRule.mainClock.advanceTimeBy(SETTLE_TIMEOUT_MS * 4)
    composeTestRule.waitForIdle()

    assertThat(dragDrop.dragSourceIndex).isNotNull()
    assertThat(dragDrop.draggingItemIndex).isNotNull()
    assertThat(dragDrop.isSettling).isFalse()
  }

  @Test
  fun `onDragInterrupted without a prior onDragStart is a no-op`() {
    val dragDrop = mountWithoutDrag()

    composeTestRule.runOnUiThread { dragDrop.onDragInterrupted() }
    composeTestRule.waitForIdle()

    assertThat(dragDrop.isSettling).isFalse()
    assertThat(dragDrop.dragSourceIndex).isNull()
    assertThat(dragDrop.draggingItemIndex).isNull()
  }

  private fun mountWithoutDrag(): DragDropState {
    lateinit var listState: LazyListState
    lateinit var scope: CoroutineScope
    composeTestRule.setContent {
      listState = rememberLazyListState()
      scope = rememberCoroutineScope()
      DragDropTestHost(listState = listState)
    }
    composeTestRule.waitForIdle()
    return DragDropState(
      state = listState,
      scope = scope,
      onMove = { _, _ -> },
      onDragEnd = {}
    )
  }

  private fun mountAndStartDrag(itemY: Int): DragDropState {
    val dragDrop = mountWithoutDrag()
    composeTestRule.runOnUiThread {
      dragDrop.onDragStart(Offset(0f, itemY.toFloat()))
    }
    composeTestRule.waitForIdle()
    return dragDrop
  }

  private companion object {
    const val SETTLE_TIMEOUT_MS = 100L
  }
}

@Composable
private fun DragDropTestHost(listState: LazyListState) {
  LazyColumn(state = listState, modifier = Modifier.fillMaxSize()) {
    items(count = 20, key = { it }) { index ->
      Text(
        text = "Item $index",
        modifier = Modifier
          .fillMaxWidth()
          .height(40.dp)
      )
    }
  }
}
