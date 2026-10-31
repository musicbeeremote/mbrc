package com.kelsos.mbrc.core.ui.compose

import android.content.Context
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.unit.dp
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.test.core.app.ApplicationProvider
import com.kelsos.mbrc.core.ui.R
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class PagedScreensTest {
  @get:Rule
  val composeRule = createComposeRule()

  private val context: Context = ApplicationProvider.getApplicationContext()
  private val loadFailed = context.getString(R.string.paging_load_failed)
  private val retry = context.getString(R.string.action_retry)

  @Test
  fun `a failed first load on a list shows an error whose retry loads the rows`() {
    composeRule.setContent {
      PagingListScreen(items = pagedRows(failingPage = 0), listId = "test") { RowText(it) }
    }

    composeRule.onNodeWithText(loadFailed).assertExists()
    composeRule.onNodeWithText(retry).performClick()

    composeRule.onNodeWithText("row 0").assertExists()
    composeRule.onNodeWithText(loadFailed).assertDoesNotExist()
  }

  @Test
  fun `a failed first load under pull to refresh shows an error whose retry loads the rows`() {
    composeRule.setContent {
      SwipeRefreshScreen(
        items = pagedRows(failingPage = 0),
        listId = "test",
        isRefreshing = false,
        onRefresh = {}
      ) { RowText(it) }
    }

    composeRule.onNodeWithText(loadFailed).assertExists()
    composeRule.onNodeWithText(retry).performClick()

    composeRule.onNodeWithText("row 0").assertExists()
  }

  @Test
  fun `a failed next page on a list shows a retry footer that loads the page`() {
    composeRule.setContent {
      PagingListScreen(
        items = pagedRows(failingPage = 1),
        listId = "test",
        modifier = Modifier.testTag(LIST)
      ) { RowText(it) }
    }

    composeRule.onNodeWithTag(LIST).performScrollToNode(hasText(retry))
    composeRule.onNodeWithText(retry).performClick()

    composeRule.onNodeWithTag(LIST).performScrollToNode(hasText("row $PAGE_SIZE"))
  }

  @Test
  fun `a failed next page on a grid shows a retry footer that loads the page`() {
    composeRule.setContent {
      PagingGridScreen(
        items = pagedRows(failingPage = 1),
        listId = "test",
        modifier = Modifier.testTag(LIST)
      ) { RowText(it) }
    }

    composeRule.onNodeWithTag(LIST).performScrollToNode(hasText(retry))
    composeRule.onNodeWithText(retry).performClick()

    composeRule.onNodeWithTag(LIST).performScrollToNode(hasText("row $PAGE_SIZE"))
  }

  @Composable
  private fun pagedRows(failingPage: Int): LazyPagingItems<Int> = remember {
    Pager(PagingConfig(pageSize = PAGE_SIZE, initialLoadSize = PAGE_SIZE, prefetchDistance = 1)) {
      FailsOnceSource(failingPage)
    }.flow
  }.collectAsLazyPagingItems()

  @Composable
  private fun RowText(index: Int) {
    Text(text = "row $index", modifier = Modifier.height(48.dp))
  }

  /** Serves [LAST_PAGE] + 1 pages of [PAGE_SIZE] rows, failing [failingPage] the first time it loads. */
  private class FailsOnceSource(private val failingPage: Int) : PagingSource<Int, Int>() {
    private var failed = false

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Int> {
      val page = params.key ?: 0
      if (page == failingPage && !failed) {
        failed = true
        return LoadResult.Error(IllegalStateException("page $page"))
      }
      val start = page * PAGE_SIZE
      return LoadResult.Page(
        data = (start until start + PAGE_SIZE).toList(),
        prevKey = null,
        nextKey = if (page < LAST_PAGE) page + 1 else null
      )
    }

    override fun getRefreshKey(state: PagingState<Int, Int>): Int? = null
  }

  private companion object {
    const val LIST = "list"
    const val PAGE_SIZE = 20
    const val LAST_PAGE = 2
  }
}
