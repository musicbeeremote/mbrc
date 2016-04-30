package com.kelsos.mbrc.ui.views

import com.kelsos.mbrc.domain.Genre

interface BrowseGenreView {
  fun update(data: List<Genre>)

  fun showEnqueueFailure()

  fun showEnqueueSuccess()

  fun clear()
}
