package com.kelsos.mbrc.mappers

class ListMapper<From, To>(val mapper: IMapper<From, To>) {
  fun map(items: List<From>): List<To> {
    return items.map { mapper.map(it) }.toList()
  }
}
