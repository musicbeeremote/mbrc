package com.kelsos.mbrc.core.common.utilities

/**
 * A discriminated union representing either success or failure.
 * Unlike Kotlin's Result<T>, this uses a sealed hierarchy for
 * exhaustive when expressions and typed errors.
 */
sealed class Outcome<out T> {
  data class Success<T>(val data: T) : Outcome<T>()
  data class Failure(val error: AppError) : Outcome<Nothing>()

  val isSuccess: Boolean get() = this is Success
  val isFailure: Boolean get() = this is Failure

  fun getOrNull(): T? = (this as? Success)?.data
  fun errorOrNull(): AppError? = (this as? Failure)?.error
}

/**
 * Common application errors.
 */
sealed class AppError {
  // Network errors
  data object NotConnected : AppError()
  data object NetworkTimeout : AppError()
  data object ConnectionRefused : AppError()
  data object NetworkUnavailable : AppError()

  // Operation errors
  data object OperationFailed : AppError()
  data object NoOp : AppError()

  // Generic
  data class Message(val message: String) : AppError()
  data class Unknown(val cause: Throwable? = null) : AppError()
}

inline fun <T, R> Outcome<T>.map(transform: (T) -> R): Outcome<R> = when (this) {
  is Outcome.Success -> Outcome.Success(transform(data))
  is Outcome.Failure -> this
}

inline fun <T, R> Outcome<T>.flatMap(transform: (T) -> Outcome<R>): Outcome<R> = when (this) {
  is Outcome.Success -> transform(data)
  is Outcome.Failure -> this
}

inline fun <T> Outcome<T>.onSuccess(action: (T) -> Unit): Outcome<T> = apply {
  if (this is Outcome.Success) action(data)
}

inline fun <T> Outcome<T>.onFailure(action: (AppError) -> Unit): Outcome<T> = apply {
  if (this is Outcome.Failure) action(error)
}

inline fun <T> Outcome<T>.getOrElse(default: (AppError) -> T): T = when (this) {
  is Outcome.Success -> data
  is Outcome.Failure -> default(error)
}

fun <T> Outcome<T>.getOrDefault(default: T): T = when (this) {
  is Outcome.Success -> data
  is Outcome.Failure -> default
}

fun <T> T.asSuccess(): Outcome<T> = Outcome.Success(this)

fun AppError.asFailure(): Outcome<Nothing> = Outcome.Failure(this)

inline fun <T> Result<T>.toOutcome(
  errorMapper: (Throwable) -> AppError = { AppError.Unknown(it) }
): Outcome<T> = fold(
  onSuccess = { Outcome.Success(it) },
  onFailure = { Outcome.Failure(errorMapper(it)) }
)

inline fun <T> runOutcome(
  errorMapper: (Throwable) -> AppError = { AppError.Unknown(it) },
  block: () -> T
): Outcome<T> = runCatching(block).toOutcome(errorMapper)
