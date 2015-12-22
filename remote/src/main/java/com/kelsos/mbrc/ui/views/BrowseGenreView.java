package com.kelsos.mbrc.ui.views;

import com.kelsos.mbrc.domain.Genre;
import java.util.List;

public interface BrowseGenreView {
  void update(List<Genre> data);

  void showEnqueueFailure();

  void showEnqueueSuccess();

  void clear();
}
