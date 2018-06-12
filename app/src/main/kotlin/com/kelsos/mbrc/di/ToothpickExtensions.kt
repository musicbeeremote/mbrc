package com.kelsos.mbrc.di

/**
 *
 *    Copyright [yyyy] [name of copyright owner]
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 *    Original on https://github.com/sporttotal-tv/toothpick-kotlin-extensions
 *    v0.2.2
 *
 */

import toothpick.Scope
import toothpick.Toothpick
import toothpick.config.Binding
import toothpick.config.Module
import javax.inject.Provider
import kotlin.reflect.KClass

inline fun <reified T> Module.bind(): Binding<T> = bind(T::class.java)
inline fun <reified T> Module.bindClass(target: () -> KClass<out Any>): Binding<T> =
  bind<T>().apply { to(target().java as Class<T>) }

inline fun <reified T> Module.bindInstance(target: () -> T): Binding<T> =
  bind<T>().apply { toInstance(target()) }

fun module(bindings: Module.() -> Binding<*>?): Module = Module().apply { bindings() }

fun scope(scopeName: Any, vararg bindings: Scope.() -> Module?): Scope =
  Toothpick.openScope(scopeName).apply { bindings.forEach { installModules(it()) } }

// Extra additions
inline fun <reified T> Module.bindSingletonProvider(target: KClass<out Provider<T>>): Binding<T> =
  bind<T>().apply { toProvider(target.java).providesSingletonInScope() }

inline fun <reified T> Module.bindSingletonClass(target: () -> KClass<out Any>): Binding<T> =
  bind<T>().apply { to(target().java as Class<T>).singletonInScope() }

fun scopes(vararg scopes: Any): Scope = Toothpick.openScopes(*scopes)

fun Scope.modules(vararg modules: Module): Scope {
  modules.forEach { installModules(it) }
  return this
}

fun Scope.close() {
  Toothpick.closeScope(this)
}

fun Scope.inject(obj: Any) {
  Toothpick.inject(obj, this)
}
