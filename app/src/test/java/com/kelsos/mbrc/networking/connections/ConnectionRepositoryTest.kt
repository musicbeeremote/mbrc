package com.kelsos.mbrc.networking.connections

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Resources
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.LoadState
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.kelsos.mbrc.data.Database
import com.kelsos.mbrc.networking.discovery.RemoteServiceDiscovery
import com.kelsos.mbrc.ui.connectionmanager.ConnectionAdapter
import com.kelsos.mbrc.utils.noopListUpdateCallback
import com.kelsos.mbrc.utils.testDispatcher
import com.kelsos.mbrc.utils.testDispatcherModule
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.bind
import org.koin.dsl.module
import org.koin.dsl.single
import org.koin.test.KoinTest
import org.koin.test.inject
import java.util.concurrent.CountDownLatch

@RunWith(AndroidJUnit4::class)
class ConnectionRepositoryTest : KoinTest {

  private val repository: ConnectionRepository by inject()

  private lateinit var db: Database
  private lateinit var connectionDao: ConnectionDao

  @get:Rule
  val rule = InstantTaskExecutorRule()

  @Before
  fun setUp() {
    Dispatchers.setMain(testDispatcher)
    val context = ApplicationProvider.getApplicationContext<Context>()
    db = Room.inMemoryDatabaseBuilder(context, Database::class.java)
      .allowMainThreadQueries()
      .build()
    connectionDao = db.connectionDao()

    startKoin {
      modules(listOf(getTestModule(), testDispatcherModule))
    }
  }

  @After
  fun tearDown() {
    stopKoin()
    db.close()
    Dispatchers.resetMain()
    testDispatcher.cleanupTestCoroutines()
  }

  @Test
  fun addNewSettings() = runBlockingTest(testDispatcher) {
    val settings = createSettings("192.167.90.10")

    repository.save(settings)

    assertThat(repository.getDefault()).isEqualTo(settings)
    assertThat(settings.id).isEqualTo(1)
  }

  @Test
  fun addMultipleNewSettings() = runBlockingTest(testDispatcher) {

    val settings = createSettings("192.167.90.10")
    val settings1 = createSettings("192.167.90.11")
    val settings2 = createSettings("192.167.90.12")
    val settings3 = createSettings("192.167.90.12")

    repository.save(settings)
    repository.save(settings1)
    repository.save(settings2)
    repository.save(settings3)

    assertThat(repository.getDefault()).isEqualTo(settings)
    assertThat(settings.id).isEqualTo(1)
    assertThat(repository.count()).isEqualTo(3)
  }

  @Test
  fun addMultipleNewSettingsRemoveOne() = runBlockingTest(testDispatcher) {
    val settings = createSettings("192.167.90.10")
    val settings1 = createSettings("192.167.90.11")
    val settings2 = createSettings("192.167.90.12")
    val settings3 = createSettings("192.167.90.13")

    repository.save(settings)
    repository.save(settings1)
    repository.save(settings2)
    repository.save(settings3)

    assertThat(repository.getDefault()).isEqualTo(settings)
    assertThat(settings.id).isEqualTo(1)
    assertThat(repository.count()).isEqualTo(4)

    repository.delete(settings2)

    val settingsList = ArrayList<ConnectionSettings>()
    settingsList.add(settings)
    settingsList.add(settings1)
    settingsList.add(settings3)

    assertThat(repository.count()).isEqualTo(3)

    val differ = AsyncPagingDataDiffer(
      diffCallback = ConnectionAdapter.CONNECTION_COMPARATOR,
      updateCallback = noopListUpdateCallback,
      mainDispatcher = testDispatcher,
      workerDispatcher = testDispatcher
    )
    val latch = CountDownLatch(1)
    differ.addLoadStateListener {
      if (it.prepend == LoadState.NotLoading(endOfPaginationReached = true)) {
        latch.countDown()
      }
    }

    val job = launch {
      repository.getAll().collectLatest {
        differ.submitData(it)
      }
    }

    advanceUntilIdle()
    @Suppress("BlockingMethodInNonBlockingContext")
    latch.await()

    val snapshot = differ.snapshot()
    assertThat(snapshot.items).containsExactlyElementsIn(settingsList)

    job.cancel()
  }

  @Test
  fun changeDefault() = runBlockingTest(testDispatcher) {
    val settings = createSettings("192.167.90.10")
    val settings1 = createSettings("192.167.90.11")

    repository.save(settings)
    repository.save(settings1)

    assertThat(repository.getDefault()).isEqualTo(settings)
    repository.setDefault(settings1)
    assertThat(repository.getDefault()).isEqualTo(settings1)
  }

  @Test
  fun deleteSingleDefault() = runBlockingTest(testDispatcher) {
    val settings = createSettings("192.167.90.10")

    repository.save(settings)

    assertThat(settings.id).isEqualTo(1)
    assertThat(repository.getDefault()).isEqualTo(settings)

    repository.delete(settings)

    assertThat(repository.count()).isEqualTo(0)
    assertThat(repository.getDefault()).isNull()
  }

  @Test
  fun deleteFromMultipleDefaultFirst() = runBlockingTest(testDispatcher) {
    val settings = createSettings("192.167.90.10")
    val settings1 = createSettings("192.167.90.11")
    val settings2 = createSettings("192.167.90.12")
    val settings3 = createSettings("192.167.90.14")

    repository.save(settings)
    repository.save(settings1)
    repository.save(settings2)
    repository.save(settings3)

    assertThat(repository.count()).isEqualTo(4)

    assertThat(settings.id).isEqualTo(1)
    assertThat(repository.getDefault()).isEqualTo(settings)

    repository.delete(settings)

    assertThat(repository.count()).isEqualTo(3)
    assertThat(repository.getDefault()).isEqualTo(settings1)
  }

  @Test
  fun deleteFromMultipleDefaultSecond() = runBlockingTest(testDispatcher) {
    val settings = createSettings("192.167.90.10")
    val settings1 = createSettings("192.167.90.11")
    val settings2 = createSettings("192.167.90.12")
    val settings3 = createSettings("192.167.90.14")

    repository.save(settings)
    repository.save(settings1)
    repository.save(settings2)
    repository.save(settings3)

    assertThat(repository.count()).isEqualTo(4)

    assertThat(settings.id).isEqualTo(1)
    assertThat(repository.getDefault()).isEqualTo(settings)

    repository.setDefault(settings1)
    assertThat(repository.getDefault()).isEqualTo(settings1)

    repository.delete(settings1)

    assertThat(repository.count()).isEqualTo(3)
    assertThat(repository.getDefault()).isEqualTo(settings)
  }

  @Test
  fun deleteFromMultipleDefaultLast() = runBlockingTest(testDispatcher) {
    val settings = createSettings("192.167.90.10")
    val settings1 = createSettings("192.167.90.11")
    val settings2 = createSettings("192.167.90.12")
    val settings3 = createSettings("192.167.90.14")

    repository.save(settings)
    repository.save(settings1)
    repository.save(settings2)
    repository.save(settings3)

    assertThat(repository.count()).isEqualTo(4)

    assertThat(settings.id).isEqualTo(1)
    assertThat(repository.getDefault()).isEqualTo(settings)

    repository.setDefault(settings3)
    assertThat(repository.getDefault()).isEqualTo(settings3)

    repository.delete(settings3)

    assertThat(repository.count()).isEqualTo(3)
    assertThat(repository.getDefault()).isEqualTo(settings2)
  }

  @Test
  fun deleteFromMultipleNonDefault() = runBlockingTest(testDispatcher) {
    val settings = createSettings("192.167.90.10")
    val settings1 = createSettings("192.167.90.11")
    val settings2 = createSettings("192.167.90.12")
    val settings3 = createSettings("192.167.90.14")

    repository.save(settings)
    repository.save(settings1)
    repository.save(settings2)
    repository.save(settings3)

    assertThat(repository.count()).isEqualTo(4)

    assertThat(settings.id).isEqualTo(1)
    assertThat(repository.getDefault()).isEqualTo(settings)

    repository.setDefault(settings3)
    assertThat(repository.getDefault()).isEqualTo(settings3)

    repository.delete(settings1)

    assertThat(repository.count()).isEqualTo(3)
    assertThat(repository.getDefault()).isEqualTo(settings3)
  }

  @Test
  fun updateSettings() = runBlockingTest(testDispatcher) {
    val newPort = 6060
    val address = "192.167.90.10"
    val newAddress = "192.167.90.11"

    val settings = createSettings(address)

    repository.save(settings)

    assertThat(settings.id).isEqualTo(1)
    val defaultSettings = repository.getDefault()

    assertThat(defaultSettings).isEqualTo(settings)
    assertThat(defaultSettings!!.port).isEqualTo(3000)
    assertThat(defaultSettings.address).isEqualTo(address)

    settings.port = newPort

    repository.save(settings)

    assertThat(repository.getDefault()!!.port).isEqualTo(newPort)

    settings.address = newAddress

    repository.save(settings)

    assertThat(repository.getDefault()!!.address).isEqualTo(newAddress)
  }

  private fun createSettings(address: String): ConnectionSettings {
    return ConnectionSettings(
      name = "Desktop PC",
      address = address,
      port = 3000,
      isDefault = true,
      id = 0
    )
  }

  private fun getTestModule() = module {

    single {
      val slot = slot<Long>()
      val preferences = mockk<SharedPreferences>()
      val editor = mockk<SharedPreferences.Editor>()
      every { preferences.edit() } returns editor
      every { preferences.getLong(any(), any()) } answers { slot.captured }
      every { editor.putLong(any(), capture(slot)) } returns editor
      preferences
    }

    single { mockk<RemoteServiceDiscovery>() }

    single<ConnectionRepositoryImpl>() bind ConnectionRepository::class
    single {
      val resources = mockk<Resources>()
      every { resources.getString(any()) } returns "preferences_key"
      resources
    }
    single { connectionDao }
  }
}
