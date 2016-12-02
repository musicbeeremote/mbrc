package com.kelsos.mbrc.extensions

import android.support.v7.app.ActionBar

fun ActionBar.enableHome(title: String?) {
  this.setDisplayHomeAsUpEnabled(true)
  this.setDisplayShowHomeEnabled(true)
  this.title = title ?: ""
}
