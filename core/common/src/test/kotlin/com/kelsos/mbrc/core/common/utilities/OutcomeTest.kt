package com.kelsos.mbrc.core.common.utilities

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class OutcomeTest {

  // region Success tests

  @Test
  fun `Success should hold data correctly`() {
    val outcome = Outcome.Success("test data")
    assertThat(outcome.data).isEqualTo("test data")
  }

  @Test
  fun `Success isSuccess should return true`() {
    val outcome: Outcome<String> = Outcome.Success("test")
    assertThat(outcome.isSuccess).isTrue()
  }

  @Test
  fun `Success isFailure should return false`() {
    val outcome: Outcome<String> = Outcome.Success("test")
    assertThat(outcome.isFailure).isFalse()
  }

  @Test
  fun `Success getOrNull should return data`() {
    val outcome: Outcome<String> = Outcome.Success("test")
    assertThat(outcome.getOrNull()).isEqualTo("test")
  }

  @Test
  fun `Success errorOrNull should return null`() {
    val outcome: Outcome<String> = Outcome.Success("test")
    assertThat(outcome.errorOrNull()).isNull()
  }

  // endregion

  // region Failure tests

  @Test
  fun `Failure should hold error correctly`() {
    val outcome = Outcome.Failure(AppError.NotConnected)
    assertThat(outcome.error).isEqualTo(AppError.NotConnected)
  }

  @Test
  fun `Failure isSuccess should return false`() {
    val outcome: Outcome<String> = Outcome.Failure(AppError.NotConnected)
    assertThat(outcome.isSuccess).isFalse()
  }

  @Test
  fun `Failure isFailure should return true`() {
    val outcome: Outcome<String> = Outcome.Failure(AppError.NotConnected)
    assertThat(outcome.isFailure).isTrue()
  }

  @Test
  fun `Failure getOrNull should return null`() {
    val outcome: Outcome<String> = Outcome.Failure(AppError.NotConnected)
    assertThat(outcome.getOrNull()).isNull()
  }

  @Test
  fun `Failure errorOrNull should return error`() {
    val outcome: Outcome<String> = Outcome.Failure(AppError.NetworkTimeout)
    assertThat(outcome.errorOrNull()).isEqualTo(AppError.NetworkTimeout)
  }

  // endregion

  // region map tests

  @Test
  fun `map should transform Success data`() {
    val outcome: Outcome<Int> = Outcome.Success(5)
    val mapped = outcome.map { it * 2 }
    assertThat(mapped).isInstanceOf(Outcome.Success::class.java)
    assertThat((mapped as Outcome.Success).data).isEqualTo(10)
  }

  @Test
  fun `map should pass through Failure`() {
    val outcome: Outcome<Int> = Outcome.Failure(AppError.OperationFailed)
    val mapped = outcome.map { it * 2 }
    assertThat(mapped).isInstanceOf(Outcome.Failure::class.java)
    assertThat((mapped as Outcome.Failure).error).isEqualTo(AppError.OperationFailed)
  }

  // endregion

  // region flatMap tests

  @Test
  fun `flatMap should chain Success outcomes`() {
    val outcome: Outcome<Int> = Outcome.Success(5)
    val flatMapped = outcome.flatMap { Outcome.Success(it.toString()) }
    assertThat(flatMapped).isInstanceOf(Outcome.Success::class.java)
    assertThat((flatMapped as Outcome.Success).data).isEqualTo("5")
  }

  @Test
  fun `flatMap should short-circuit on Failure`() {
    val outcome: Outcome<Int> = Outcome.Failure(AppError.NotConnected)
    val flatMapped = outcome.flatMap { Outcome.Success(it.toString()) }
    assertThat(flatMapped).isInstanceOf(Outcome.Failure::class.java)
    assertThat((flatMapped as Outcome.Failure).error).isEqualTo(AppError.NotConnected)
  }

  @Test
  fun `flatMap should return inner Failure`() {
    val outcome: Outcome<Int> = Outcome.Success(5)
    val flatMapped = outcome.flatMap<Int, String> { Outcome.Failure(AppError.NetworkTimeout) }
    assertThat(flatMapped).isInstanceOf(Outcome.Failure::class.java)
    assertThat((flatMapped as Outcome.Failure).error).isEqualTo(AppError.NetworkTimeout)
  }

  // endregion

  // region onSuccess tests

  @Test
  fun `onSuccess should execute action for Success`() {
    var executed = false
    val outcome: Outcome<String> = Outcome.Success("test")
    outcome.onSuccess { executed = true }
    assertThat(executed).isTrue()
  }

  @Test
  fun `onSuccess should not execute action for Failure`() {
    var executed = false
    val outcome: Outcome<String> = Outcome.Failure(AppError.NotConnected)
    outcome.onSuccess { executed = true }
    assertThat(executed).isFalse()
  }

  @Test
  fun `onSuccess should return same outcome`() {
    val outcome: Outcome<String> = Outcome.Success("test")
    val returned = outcome.onSuccess { }
    assertThat(returned).isSameInstanceAs(outcome)
  }

  // endregion

  // region onFailure tests

  @Test
  fun `onFailure should execute action for Failure`() {
    var capturedError: AppError? = null
    val outcome: Outcome<String> = Outcome.Failure(AppError.NetworkTimeout)
    outcome.onFailure { capturedError = it }
    assertThat(capturedError).isEqualTo(AppError.NetworkTimeout)
  }

  @Test
  fun `onFailure should not execute action for Success`() {
    var executed = false
    val outcome: Outcome<String> = Outcome.Success("test")
    outcome.onFailure { executed = true }
    assertThat(executed).isFalse()
  }

  @Test
  fun `onFailure should return same outcome`() {
    val outcome: Outcome<String> = Outcome.Failure(AppError.NotConnected)
    val returned = outcome.onFailure { }
    assertThat(returned).isSameInstanceAs(outcome)
  }

  // endregion

  // region getOrElse tests

  @Test
  fun `getOrElse should return data for Success`() {
    val outcome: Outcome<String> = Outcome.Success("test")
    val result = outcome.getOrElse { "default" }
    assertThat(result).isEqualTo("test")
  }

  @Test
  fun `getOrElse should return default for Failure`() {
    val outcome: Outcome<String> = Outcome.Failure(AppError.NotConnected)
    val result = outcome.getOrElse { "default" }
    assertThat(result).isEqualTo("default")
  }

  @Test
  fun `getOrElse should pass error to default function`() {
    val outcome: Outcome<String> = Outcome.Failure(AppError.NetworkTimeout)
    val result = outcome.getOrElse { error ->
      when (error) {
        AppError.NetworkTimeout -> "timeout"
        else -> "other"
      }
    }
    assertThat(result).isEqualTo("timeout")
  }

  // endregion

  // region getOrDefault tests

  @Test
  fun `getOrDefault should return data for Success`() {
    val outcome: Outcome<String> = Outcome.Success("test")
    val result = outcome.getOrDefault("default")
    assertThat(result).isEqualTo("test")
  }

  @Test
  fun `getOrDefault should return default for Failure`() {
    val outcome: Outcome<String> = Outcome.Failure(AppError.NotConnected)
    val result = outcome.getOrDefault("default")
    assertThat(result).isEqualTo("default")
  }

  // endregion

  // region Extension function tests

  @Test
  fun `asSuccess should wrap value in Success`() {
    val outcome = "test".asSuccess()
    assertThat(outcome).isInstanceOf(Outcome.Success::class.java)
    assertThat((outcome as Outcome.Success).data).isEqualTo("test")
  }

  @Test
  fun `asFailure should wrap error in Failure`() {
    val outcome = AppError.ConnectionRefused.asFailure()
    assertThat(outcome).isInstanceOf(Outcome.Failure::class.java)
    assertThat((outcome as Outcome.Failure).error).isEqualTo(AppError.ConnectionRefused)
  }

  @Test
  fun `toOutcome should convert successful Result to Success`() {
    val result = Result.success("test")
    val outcome = result.toOutcome()
    assertThat(outcome).isInstanceOf(Outcome.Success::class.java)
    assertThat((outcome as Outcome.Success).data).isEqualTo("test")
  }

  @Test
  fun `toOutcome should convert failed Result to Failure with Unknown error`() {
    val exception = RuntimeException("test error")
    val result = Result.failure<String>(exception)
    val outcome = result.toOutcome()
    assertThat(outcome).isInstanceOf(Outcome.Failure::class.java)
    val error = (outcome as Outcome.Failure).error
    assertThat(error).isInstanceOf(AppError.Unknown::class.java)
    assertThat((error as AppError.Unknown).cause).isEqualTo(exception)
  }

  @Test
  fun `toOutcome should use custom error mapper`() {
    val exception = RuntimeException("timeout")
    val result = Result.failure<String>(exception)
    val outcome = result.toOutcome { AppError.NetworkTimeout }
    assertThat(outcome).isInstanceOf(Outcome.Failure::class.java)
    assertThat((outcome as Outcome.Failure).error).isEqualTo(AppError.NetworkTimeout)
  }

  @Test
  fun `runOutcome should return Success for successful block`() {
    val outcome = runOutcome { "result" }
    assertThat(outcome).isInstanceOf(Outcome.Success::class.java)
    assertThat((outcome as Outcome.Success).data).isEqualTo("result")
  }

  @Test
  fun `runOutcome should return Failure for throwing block`() {
    val outcome = runOutcome<String> { throw RuntimeException("error") }
    assertThat(outcome).isInstanceOf(Outcome.Failure::class.java)
    assertThat((outcome as Outcome.Failure).error).isInstanceOf(AppError.Unknown::class.java)
  }

  @Test
  fun `runOutcome should use custom error mapper for exceptions`() {
    val outcome = runOutcome<String>(errorMapper = { AppError.OperationFailed }) {
      throw RuntimeException("error")
    }
    assertThat(outcome).isInstanceOf(Outcome.Failure::class.java)
    assertThat((outcome as Outcome.Failure).error).isEqualTo(AppError.OperationFailed)
  }

  // endregion

  // region AppError tests

  @Test
  fun `AppError Message should hold message`() {
    val error = AppError.Message("custom error")
    assertThat(error.message).isEqualTo("custom error")
  }

  @Test
  fun `AppError Unknown should hold cause`() {
    val cause = RuntimeException("root cause")
    val error = AppError.Unknown(cause)
    assertThat(error.cause).isEqualTo(cause)
  }

  @Test
  fun `AppError Unknown should allow null cause`() {
    val error = AppError.Unknown()
    assertThat(error.cause).isNull()
  }

  // endregion
}
