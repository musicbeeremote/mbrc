package com.kelsos.mbrc.rx

import rx.Observable
import rx.Observable.Transformer

/*
  Picked from https://github.com/davidmoten/rxjava-extras
 */
class MapWithIndex<T> : Transformer<T, MapWithIndex.Indexed<T>> {

  override fun call(source: Observable<T>): Observable<Indexed<T>> {

    return source.zipWith(NaturalNumbers.instance()) { t, n -> Indexed(t, n!!) }
  }

  private object Holder {
    internal val INSTANCE: MapWithIndex<*> = MapWithIndex<Any>()
  }

  class Indexed<T>(private val value: T, private val index: Long) {

    override fun toString(): String {
      return index + "->" + value
    }

    fun index(): Long {
      return index
    }

    fun value(): T {
      return value
    }
  }

  private class NaturalNumbers : Iterable<Long> {

    override fun iterator(): Iterator<Long> {
      return object : Iterator<Long> {

        private var n: Long = 0

        override fun hasNext(): Boolean {
          return true
        }

        override fun next(): Long? {
          return n++
        }

        override fun remove() {
          throw RuntimeException("not supported")
        }
      }
    }

    private object Holder {
      internal val INSTANCE = NaturalNumbers()
    }

    companion object {

      internal fun instance(): NaturalNumbers {
        return Holder.INSTANCE
      }
    }
  }

  companion object {

    @SuppressWarnings("unchecked") fun <T> instance(): MapWithIndex<T> {
      return Holder.INSTANCE as MapWithIndex<T>
    }
  }
}

