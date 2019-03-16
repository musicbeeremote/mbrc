package com.kelsos.mbrc.networking.connections

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Resources
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.kelsos.mbrc.content.activestatus.livedata.DefaultSettingsLiveDataProvider
import com.kelsos.mbrc.content.activestatus.livedata.DefaultSettingsLiveDataProviderImpl
import com.kelsos.mbrc.data.Database
import com.kelsos.mbrc.networking.discovery.RemoteServiceDiscovery
import com.kelsos.mbrc.utils.observeOnce
import com.kelsos.mbrc.utils.testDispatcherModule
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.experimental.builder.singleBy
import org.koin.test.KoinTest
import org.koin.test.inject
import java.util.ArrayList

@RunWith(AndroidJUnit4::class)
class ConnectionRepositoryTest : KoinTest {

  private val repository: ConnectionRepository by inject()

  private lateinit var db: Database
  private lateinit var connectionDao: ConnectionDao

  @get:Rule
  val rule = InstantTaskExecutorRule()

  @Before
  fun setUp() {
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
  }

  @Test
  fun addNewSettings() = runBlockingTest {
    val settings = createSettings("192.167.90.10")

    repository.save(settings)

    assertThat(repository.getDefault().orNull()).isEqualTo(settings)
    assertThat(settings.id).isEqualTo(1)
  }

  @Test
  fun addMultipleNewSettings() = runBlockingTest {

    val settings = createSettings("192.167.90.10")
    val settings1 = createSettings("192.167.90.11")
    val settings2 = createSettings("192.167.90.12")
    val settings3 = createSettings("192.167.90.12")

    repository.save(settings)
    repository.save(settings1)
    repository.save(settings2)
    repository.save(settings3)

    assertThat(repository.getDefault().orNull()).isEqualTo(settings)
    assertThat(settings.id).isEqualTo(1)
    assertThat(repository.count()).isEqualTo(3)
  }

  @Test
  fun addMultipleNewSettingsRemoveOne() = runBlockingTest {
    val settings = createSettings("192.167.90.10")
    val settings1 = createSettings("192.167.90.11")
    val settings2 = createSettings("192.167.90.12")
    val settings3 = createSettings("192.167.90.13")

    repository.save(settings)
    repository.save(settings1)
    repository.save(settings2)
    repository.save(settings3)

    assertThat(repository.getDefault().orNull()).isEqualTo(settings)
    assertThat(settings.id).isEqualTo(1)
    assertThat(repository.count()).isEqualTo(4)

    repository.delete(settings2)

    val settingsList = ArrayList<ConnectionSettingsEntity>()
    settingsList.add(settings)
    settingsList.add(settings1)
    settingsList.add(settings3)

    assertThat(repository.count()).isEqualTo(3)

    repository.getAll().observeOnce {
      assertThat(it).containsExactlyElementsIn(settingsList)
    }
  }

  @Test
  fun changeDefault() = runBlockingTest {
    val settings = createSettings("192.167.90.10")
    val settings1 = createSettings("192.167.90.11")

    repository.save(settings)
    repository.save(settings1)

    assertThat(repository.getDefault().orNull()).isEqualTo(settings)
    repository.setDefault(settings1)
    assertThat(repository.getDefault().orNull()).isEqualTo(settings1)
  }

  @Test
  fun deleteSingleDefault() = runBlockingTest {
    val settings = createSettings("192.167.90.10")

    repository.save(settings)

    assertThat(settings.id).isEqualTo(1)
    assertThat(repository.getDefault().orNull()).isEqualTo(settings)

    repository.delete(settings)

    assertThat(repository.count()).isEqualTo(0)
    assertThat(repository.getDefault().orNull()).isNull()
  }

  @Test
  fun deleteFromMultipleDefaultFirst() = runBlockingTest {
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
    assertThat(repository.getDefault().orNull()).isEqualTo(settings)

    repository.delete(settings)

    assertThat(repository.count()).isEqualTo(3)
    assertThat(repository.getDefault().orNull()).isEqualTo(settings1)
  }

  @Test
  fun deleteFromMultipleDefaultSecond() = runBlockingTest {
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
    assertThat(repository.getDefault().orNull()).isEqualTo(settings)

    repository.setDefault(settings1)
    assertThat(repository.getDefault().orNull()).isEqualTo(settings1)

    repository.delete(settings1)

    assertThat(repository.count()).isEqualTo(3)
    assertThat(repository.getDefault().orNull()).isEqualTo(settings)
  }

  @Test
  fun deleteFromMultipleDefaultLast() = runBlockingTest {
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
    assertThat(repository.getDefault().orNull()).isEqualTo(settings)

    repository.setDefault(settings3)
    assertThat(repository.getDefault().orNull()).isEqualTo(settings3)

    repository.delete(settings3)

    assertThat(repository.count()).isEqualTo(3)
    assertThat(repository.getDefault().orNull()).isEqualTo(settings2)
  }

  @Test
  fun deleteFromMultipleNonDefault() = runBlockingTest {
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
    assertThat(repository.getDefault().orNull()).isEqualTo(settings)

    repository.setDefault(settings3)
    assertThat(repository.getDefault().orNull()).isEqualTo(settings3)

    repository.delete(settings1)

    assertThat(repository.count()).isEqualTo(3)
    assertThat(repository.getDefault().orNull()).isEqualTo(settings3)
  }

  @Test
  fun updateSettings() = runBlockingTest {
    val newPort = 6060
    val address = "192.167.90.10"
    val newAddress = "192.167.90.11"

    val settings = createSettings(address)

    repository.save(settings)

    assertThat(settings.id).isEqualTo(1)
    val defaultSettings = repository.getDefault().orNull()

    assertThat(defaultSettings).isEqualTo(settings)
    assertThat(defaultSettings!!.port).isEqualTo(3000)
    assertThat(defaultSettings.address).isEqualTo(address)

    settings.port = newPort

    repository.save(settings)

    assertThat(repository.getDefault().orNull()!!.port).isEqualTo(newPort)

    settings.address = newAddress

    repository.save(settings)

    assertThat(repository.getDefault().orNull()!!.address).isEqualTo(newAddress)
  }

  private fun createSettings(address: String): ConnectionSettingsEntity {
    val settings = ConnectionSettingsEntity()
    settings.name = "Desktop PC"
    settings.address = address
    settings.port = 3000
    return settings
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

    singleBy<ConnectionRepository, ConnectionRepositoryImpl>()
    single {
      val resources = mockk<Resources>()
      every { resources.getString(any()) } returns "preferences_key"
      resources
    }
    single { connectionDao }
    singleBy<DefaultSettingsLiveDataProvider, DefaultSettingsLiveDataProviderImpl>()
    factory<DefaultSettingsModel> { DefaultSettingsModelImpl }
  }
}
