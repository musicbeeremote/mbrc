package com.kelsos.mbrc.repository

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.content.res.Resources
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.common.truth.Truth.assertThat
import com.kelsos.mbrc.data.ConnectionSettings
import com.kelsos.mbrc.di.modules.AppDispatchers
import com.kelsos.mbrc.rules.DBFlowTestRule
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.Mockito.anyInt
import org.mockito.Mockito.anyLong
import org.mockito.Mockito.anyString
import toothpick.Toothpick
import toothpick.config.Module
import toothpick.testing.ToothPickRule
import java.util.ArrayList

@RunWith(AndroidJUnit4::class)
class ConnectionRepositoryTest {
  private val toothPickRule = ToothPickRule(this)
  private val testDispatcher = TestCoroutineDispatcher()

  @Rule
  fun chain(): TestRule = RuleChain.outerRule(toothPickRule).around(DBFlowTestRule.create())

  @Before
  @Throws(Exception::class)
  fun setUp() {
  }

  @Test
  fun addNewSettings() = runBlockingTest {
    val repository = repository
    val settings = createSettings("192.167.90.10")
    repository.save(settings)

    assertThat(repository.getDefault()).isEqualTo(settings)
    assertThat(settings.id).isEqualTo(1)
  }

  @Test
  fun addMultipleNewSettings() = runBlockingTest {
    val repository = repository
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

  private val repository: ConnectionRepository
    get() {
      val scope = Toothpick.openScope(InstrumentationRegistry.getInstrumentation().targetContext)
      scope.installModules(
        TestModule(),
        object : Module() {
          init {
            bind(AppDispatchers::class.java).toInstance(
              AppDispatchers(
                testDispatcher,
                testDispatcher,
                testDispatcher
              )
            )
          }
        }
      )
      return scope.getInstance(ConnectionRepository::class.java)
    }

  @Test
  fun addMultipleNewSettingsRemoveOne() = runBlockingTest {
    val repository = repository
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
    assertThat(repository.getAll()).containsExactlyElementsIn(settingsList)
  }

  @Test
  fun changeDefault() = runBlockingTest {
    val repository = repository

    val settings = createSettings("192.167.90.10")
    val settings1 = createSettings("192.167.90.11")

    repository.save(settings)
    repository.save(settings1)

    assertThat(repository.getDefault()).isEqualTo(settings)

    repository.setDefault(settings1)

    assertThat(repository.getDefault()).isEqualTo(settings1)
  }

  @Test
  fun deleteSingleDefault() = runBlockingTest {
    val repository = repository

    val settings = createSettings("192.167.90.10")
    repository.save(settings)

    assertThat(settings.id).isEqualTo(1)
    assertThat(repository.getDefault()).isEqualTo(settings)

    repository.delete(settings)

    assertThat(repository.count()).isEqualTo(0)
    assertThat(repository.getDefault()).isNull()
    assertThat(repository.defaultId).isEqualTo(-1)
  }

  @Test
  fun deleteFromMultipleDefaultFirst() = runBlockingTest {
    val repository = repository

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
    assertThat(repository.defaultId).isEqualTo(2)
  }

  @Test
  fun deleteFromMultipleDefaultSecond() = runBlockingTest {
    val repository = repository

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
    assertThat(repository.defaultId).isEqualTo(1)
  }

  @Test
  fun deleteFromMultipleDefaultLast() = runBlockingTest {
    val repository = repository

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
    assertThat(repository.defaultId).isEqualTo(3)
  }

  @Test
  fun deleteFromMultipleNonDefault() = runBlockingTest {
    val repository = repository

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
    assertThat(repository.defaultId).isEqualTo(4)
  }

  @Test
  fun updateSettings() = runBlockingTest {
    val newPort = 6060
    val address = "192.167.90.10"
    val newAddress = "192.167.90.11"

    val repository = repository

    val settings = createSettings(address)
    repository.save(settings)

    assertThat(settings.id).isEqualTo(1)
    val defaultSettings = repository.getDefault()

    assertThat(defaultSettings).isEqualTo(settings)
    assertThat(defaultSettings!!.port).isEqualTo(3000)
    assertThat(defaultSettings.address).isEqualTo(address)

    settings.port = newPort

    repository.update(settings)

    assertThat(repository.getDefault()!!.port).isEqualTo(newPort)

    settings.address = newAddress
    repository.update(settings)

    assertThat(repository.getDefault()!!.address).isEqualTo(newAddress)
  }

  private fun createSettings(address: String): ConnectionSettings {
    val settings = ConnectionSettings()
    settings.name = "Desktop PC"
    settings.address = address
    settings.port = 3000
    return settings
  }

  private inner class TestModule @SuppressLint("CommitPrefEdits") constructor() : Module() {
    init {
      bind(SharedPreferences::class.java).toProviderInstance {
        val defaultId = longArrayOf(-1)
        val preferences = Mockito.mock(SharedPreferences::class.java)
        val editor = Mockito.mock(SharedPreferences.Editor::class.java)
        `when`(preferences.edit()).thenReturn(editor)
        `when`(preferences.getLong(anyString(), anyLong())).thenAnswer { defaultId[0] }
        `when`(editor.putLong(anyString(), anyLong())).then {
          val o = it.arguments[1]
          defaultId[0] = java.lang.Long.parseLong(o.toString())
          editor
        }
        preferences
      }
      bind(ConnectionRepository::class.java).to(ConnectionRepositoryImpl::class.java)
      bind(Resources::class.java).toProviderInstance {
        val resources = Mockito.mock(Resources::class.java)
        `when`(resources.getString(anyInt())).thenReturn("preferences_key")
        resources
      }
    }
  }

  @After
  @Throws(Exception::class)
  fun tearDown() {
    Toothpick.reset()
  }
}
