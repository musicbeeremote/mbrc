package com.kelsos.mbrc.repository

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.content.res.Resources
import android.os.Build
import com.google.common.truth.Truth.assertThat
import com.kelsos.mbrc.BuildConfig
import com.kelsos.mbrc.TestApplication
import com.kelsos.mbrc.networking.connections.ConnectionRepository
import com.kelsos.mbrc.networking.connections.ConnectionRepositoryImpl
import com.kelsos.mbrc.networking.connections.ConnectionSettingsEntity
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.Mockito.given
import org.mockito.Mockito.anyInt
import org.mockito.Mockito.anyLong
import org.mockito.Mockito.anyString
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import toothpick.config.Module
import toothpick.testing.ToothPickRule
import java.util.*

@RunWith(RobolectricTestRunner::class)
@Config(constants = BuildConfig::class,
    application = TestApplication::class,
    sdk = [(Build.VERSION_CODES.N_MR1)])
class ConnectionRepositoryTest {
  private val toothPickRule = ToothPickRule(this, "test")
  @Rule
  @JvmField
  val ruleChain: TestRule = RuleChain.outerRule(toothPickRule)

  private lateinit var repository: ConnectionRepository

  @Before
  @Throws(Exception::class)
  fun setUp() {
    toothPickRule.scope.installModules(TestModule())
    repository = toothPickRule.getInstance(ConnectionRepository::class.java)
  }

  @Test
  fun addNewSettings() {

    val settings = createSettings("192.167.90.10")
    repository.save(settings)

    assertThat(repository.default).isEqualTo(settings)
    assertThat(settings.id).isEqualTo(1)
  }

  @Test
  fun addMultipleNewSettings() {
    val settings = createSettings("192.167.90.10")
    val settings1 = createSettings("192.167.90.11")
    val settings2 = createSettings("192.167.90.12")
    val settings3 = createSettings("192.167.90.12")
    repository.save(settings)
    repository.save(settings1)
    repository.save(settings2)
    repository.save(settings3)

    assertThat(repository.default).isEqualTo(settings)
    assertThat(settings.id).isEqualTo(1)
    assertThat(repository.count()).isEqualTo(3)
  }

  @Test
  fun addMultipleNewSettingsRemoveOne() {
    val settings = createSettings("192.167.90.10")
    val settings1 = createSettings("192.167.90.11")
    val settings2 = createSettings("192.167.90.12")
    val settings3 = createSettings("192.167.90.13")

    repository.save(settings)
    repository.save(settings1)
    repository.save(settings2)
    repository.save(settings3)

    assertThat(repository.default).isEqualTo(settings)
    assertThat(settings.id).isEqualTo(1)
    assertThat(repository.count()).isEqualTo(4)

    repository.delete(settings2)

    val settingsList = ArrayList<ConnectionSettingsEntity>()
    settingsList.add(settings)
    settingsList.add(settings1)
    settingsList.add(settings3)

    assertThat(repository.count()).isEqualTo(3)
    assertThat(repository.getAll().value).containsAllIn(settingsList)
  }

  @Test
  fun changeDefault() {

    val settings = createSettings("192.167.90.10")
    val settings1 = createSettings("192.167.90.11")

    repository.save(settings)
    repository.save(settings1)

    assertThat(repository.default).isEqualTo(settings)

    repository.default = settings1

    assertThat(repository.default).isEqualTo(settings1)
  }

  @Test
  fun deleteSingleDefault() {

    val settings = createSettings("192.167.90.10")
    repository.save(settings)

    assertThat(settings.id).isEqualTo(1)
    assertThat(repository.default).isEqualTo(settings)

    repository.delete(settings)

    assertThat(repository.count()).isEqualTo(0)
    assertThat(repository.default).isNull()
    assertThat(repository.defaultId).isEqualTo(-1)
  }

  @Test
  fun deleteFromMultipleDefaultFirst() {

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
    assertThat(repository.default).isEqualTo(settings)

    repository.delete(settings)

    assertThat(repository.count()).isEqualTo(3)
    assertThat(repository.default).isEqualTo(settings1)
    assertThat(repository.defaultId).isEqualTo(2)
  }

  @Test
  fun deleteFromMultipleDefaultSecond() {

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
    assertThat(repository.default).isEqualTo(settings)

    repository.default = settings1
    assertThat(repository.default).isEqualTo(settings1)

    repository.delete(settings1)

    assertThat(repository.count()).isEqualTo(3)
    assertThat(repository.default).isEqualTo(settings)
    assertThat(repository.defaultId).isEqualTo(1)
  }

  @Test
  fun deleteFromMultipleDefaultLast() {

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
    assertThat(repository.default).isEqualTo(settings)

    repository.default = settings3
    assertThat(repository.default).isEqualTo(settings3)

    repository.delete(settings3)

    assertThat(repository.count()).isEqualTo(3)
    assertThat(repository.default).isEqualTo(settings2)
    assertThat(repository.defaultId).isEqualTo(3)
  }

  @Test
  fun deleteFromMultipleNonDefault() {

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
    assertThat(repository.default).isEqualTo(settings)

    repository.default = settings3
    assertThat(repository.default).isEqualTo(settings3)

    repository.delete(settings1)

    assertThat(repository.count()).isEqualTo(3)
    assertThat(repository.default).isEqualTo(settings3)
    assertThat(repository.defaultId).isEqualTo(4)
  }

  @Test
  fun updateSettings() {
    val newPort = 6060
    val address = "192.167.90.10"
    val newAddress = "192.167.90.11"

    val settings = createSettings(address)
    repository.save(settings)

    assertThat(settings.id).isEqualTo(1)
    val defaultSettings = repository.default

    assertThat(defaultSettings).isEqualTo(settings)
    assertThat(defaultSettings!!.port).isEqualTo(3000)
    assertThat(defaultSettings.address).isEqualTo(address)

    settings.port = newPort

    repository.save(settings)

    assertThat(repository.default!!.port).isEqualTo(newPort)

    settings.address = newAddress
    repository.save(settings)

    assertThat(repository.default!!.address).isEqualTo(newAddress)
  }

  @Test
  fun setDefaultNull() {

    val settings = createSettings("192.167.90.10")
    repository.save(settings)

    assertThat(settings.id).isEqualTo(1)
    assertThat(repository.default).isEqualTo(settings)

    repository.default = null

    assertThat(repository.count()).isEqualTo(1)
    assertThat(repository.default!!).isEqualTo(settings)
    assertThat(repository.defaultId).isEqualTo(1)
  }

  private fun createSettings(address: String): ConnectionSettingsEntity {
    val settings = ConnectionSettingsEntity()
    settings.name = "Desktop PC"
    settings.address = address
    settings.port = 3000
    return settings
  }

  private inner class TestModule @SuppressLint("CommitPrefEdits")
  internal constructor() : Module() {
    init {
      bind(SharedPreferences::class.java).toProviderInstance {
        val defaultId = longArrayOf(-1)
        val preferences = Mockito.mock(SharedPreferences::class.java)
        val editor = Mockito.mock(SharedPreferences.Editor::class.java)
        given(preferences.edit()).willReturn(editor)
        given(preferences.getLong(anyString(), anyLong())).thenAnswer { defaultId[0] }
        given(editor.putLong(anyString(), anyLong())).then {
          val o = it.arguments[1]
          defaultId[0] = java.lang.Long.parseLong(o.toString())
          editor
        }
        preferences
      }
      bind(ConnectionRepository::class.java).to(ConnectionRepositoryImpl::class.java)
      bind(Resources::class.java).toProviderInstance {
        val resources = Mockito.mock(Resources::class.java)
        given(resources.getString(anyInt())).willReturn("preferences_key")
        resources
      }
    }
  }
}