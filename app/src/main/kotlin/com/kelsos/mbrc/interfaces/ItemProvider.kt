package com.kelsos.mbrc.interfaces

interface ItemProvider<T> {
    fun getById(id: Long): T
}
