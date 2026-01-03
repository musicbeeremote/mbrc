package com.kelsos.mbrc.feature.settings

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.kelsos.mbrc.core.common.test.testDispatcher
import com.kelsos.mbrc.core.common.test.testDispatcherModule
import com.kelsos.mbrc.core.common.test.testDispatchers
import com.mikepenz.aboutlibraries.entity.Developer
import com.mikepenz.aboutlibraries.entity.Library
import com.mikepenz.aboutlibraries.entity.License
import io.mockk.every
import io.mockk.mockk
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class LicensesViewModelTest : KoinTest {

  private lateinit var viewModel: LicensesViewModel

  @Before
  fun setUp() {
    startKoin { modules(listOf(testDispatcherModule)) }
    // Create ViewModel with injected test dispatchers
    viewModel = LicensesViewModel(RuntimeEnvironment.getApplication(), testDispatchers)
  }

  @After
  fun tearDown() {
    stopKoin()
  }

  @Test
  fun `search query should start empty`() = runTest(testDispatcher) {
    viewModel.searchQuery.test {
      assertThat(awaitItem()).isEmpty()
    }
  }

  @Test
  fun `selected library should start as null`() = runTest(testDispatcher) {
    viewModel.selectedLibrary.test {
      assertThat(awaitItem()).isNull()
    }
  }

  @Test
  fun `onSearchQueryChange should update search query`() = runTest(testDispatcher) {
    viewModel.searchQuery.test {
      assertThat(awaitItem()).isEmpty()

      viewModel.onSearchQueryChange("kotlin")
      assertThat(awaitItem()).isEqualTo("kotlin")
    }
  }

  @Test
  fun `clearSearch should reset search query to empty`() = runTest(testDispatcher) {
    viewModel.searchQuery.test {
      assertThat(awaitItem()).isEmpty()

      viewModel.onSearchQueryChange("test")
      assertThat(awaitItem()).isEqualTo("test")

      viewModel.clearSearch()
      assertThat(awaitItem()).isEmpty()
    }
  }

  @Test
  fun `selectLibrary should update selected library`() = runTest(testDispatcher) {
    val mockLibrary = createMockLibrary("Test Library")

    viewModel.selectedLibrary.test {
      assertThat(awaitItem()).isNull()

      viewModel.selectLibrary(mockLibrary)
      assertThat(awaitItem()).isEqualTo(mockLibrary)
    }
  }

  @Test
  fun `clearSelection should reset selected library to null`() = runTest(testDispatcher) {
    val mockLibrary = createMockLibrary("Test Library")

    viewModel.selectedLibrary.test {
      assertThat(awaitItem()).isNull()

      viewModel.selectLibrary(mockLibrary)
      assertThat(awaitItem()).isEqualTo(mockLibrary)

      viewModel.clearSelection()
      assertThat(awaitItem()).isNull()
    }
  }

  @Test
  fun `after loading completes state should not be loading`() = runTest(testDispatcher) {
    // Create ViewModel inside test context for proper dispatcher handling
    val vm = LicensesViewModel(RuntimeEnvironment.getApplication(), testDispatchers)
    advanceUntilIdle()

    vm.uiState.test {
      val state = awaitItem()
      // With test dispatchers, loading completes synchronously
      // In test environment, AboutLibraries may find no libraries (Success with empty list)
      // or encounter an error, but should not remain in Loading state
      assertThat(state).isNotEqualTo(LicensesUiState.Loading)
    }
  }

  @Test
  fun `multiple search queries should emit correctly`() = runTest(testDispatcher) {
    viewModel.searchQuery.test {
      assertThat(awaitItem()).isEmpty()

      viewModel.onSearchQueryChange("a")
      assertThat(awaitItem()).isEqualTo("a")

      viewModel.onSearchQueryChange("ab")
      assertThat(awaitItem()).isEqualTo("ab")

      viewModel.onSearchQueryChange("abc")
      assertThat(awaitItem()).isEqualTo("abc")
    }
  }

  private fun createMockLibrary(name: String): Library {
    val license = mockk<License>(relaxed = true) {
      every { this@mockk.name } returns "Apache-2.0"
      every { licenseContent } returns "Apache License content"
    }
    val developer = mockk<Developer>(relaxed = true) {
      every { this@mockk.name } returns "Test Developer"
    }
    return mockk(relaxed = true) {
      every { this@mockk.name } returns name
      every { uniqueId } returns "com.test:$name:1.0.0"
      every { artifactVersion } returns "1.0.0"
      every { licenses } returns persistentSetOf(license)
      every { developers } returns persistentListOf(developer)
      every { organization } returns null
      every { website } returns "https://example.com"
    }
  }
}
