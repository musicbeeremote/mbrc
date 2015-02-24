package com.kelsos.mbrc.ui.layout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Checkable;
import android.widget.CheckedTextView;
import android.widget.LinearLayout;
import com.kelsos.mbrc.R;

public class CheckableLinearLayout extends LinearLayout implements Checkable {

    private CheckedTextView mCheckedTextView;

    public CheckableLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override protected void onFinishInflate() {
        super.onFinishInflate();
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View view = getChildAt(i);
            if (view instanceof CheckedTextView) {
                mCheckedTextView = (CheckedTextView) view;
            }
        }
    }

    /**
     * Change the checked state of the view
     *
     * @param checked The new checked state
     */
    @Override public void setChecked(boolean checked) {
        if (mCheckedTextView != null) {
            mCheckedTextView.setChecked(checked);
            changeColor(checked);
        }
    }

    /**
     * @return The current checked state of the view
     */
    @Override public boolean isChecked() {
        return mCheckedTextView != null && mCheckedTextView.isChecked();
    }

    /**
     * Change the checked state of the view to the inverse of its current state
     */
    @Override public void toggle() {
        if (mCheckedTextView != null) {
            mCheckedTextView.toggle();
            changeColor(mCheckedTextView.isChecked());
        }

    }

    private void changeColor(boolean isChecked) {
        if (isChecked) {
            setBackgroundColor(getResources().getColor(R.color.mbrc_selected_item));
        } else {
            setBackgroundColor(getResources().getColor(android.R.color.transparent));
        }
    }
}
