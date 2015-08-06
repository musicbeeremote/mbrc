package com.kelsos.mbrc.utilities;

import android.support.v4.view.ViewCompat;
import android.view.View;
import roboguice.util.Ln;

public class ViewUtils {
  public static boolean hitTest(View v, int x, int y) {
    final int tx = (int) (ViewCompat.getTranslationX(v) + 0.5f);
    final int ty = (int) (ViewCompat.getTranslationY(v) + 0.5f);
    final int left = v.getLeft() + tx;
    final int right = v.getRight() + tx;
    final int top = v.getTop() + ty;
    final int bottom = v.getBottom() + ty;

    Ln.v("left: %d, right: %d, top: %d, bottom: %d", left, right, top, bottom);
    Ln.v("x: %d, y: %d", x, y);

    return (x >= left) && (x <= right) && (y >= top) && (y <= bottom);
  }
}
