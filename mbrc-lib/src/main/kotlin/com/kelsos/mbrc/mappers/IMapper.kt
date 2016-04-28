package com.kelsos.mbrc.mappers

interface IMapper<From, To> {
    fun map(from: From): To
}
