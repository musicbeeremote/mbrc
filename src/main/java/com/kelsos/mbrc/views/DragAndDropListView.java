package com.kelsos.mbrc.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ListView;

public class DragAndDropListView extends ListView {

    private boolean mDragMode;
    private int mStartPosition;
    private int mEndPosition;
    private int mDragPointOffset;

    private ImageView mDragView;
    private GestureDetector mGestureDetector;





    public DragAndDropListView(Context context) {
        super(context);
    }


    @Override
    public boolean onTouchEvent(MotionEvent motionEvent){
        final int action = motionEvent.getAction();
        final int x = (int) motionEvent.getX();
        final int y = (int) motionEvent.getY();

        if(action == MotionEvent.ACTION_DOWN && x < this.getWidth()/4) {
            mDragMode = true;
        }

        if(!mDragMode){
            return super.onTouchEvent(motionEvent);
        }

        switch (action){
            case MotionEvent.ACTION_DOWN:
                mStartPosition = pointToPosition(x,y);
                if(mStartPosition != INVALID_POSITION) {
                    int mItemPosition = mStartPosition - getFirstVisiblePosition();
                    mDragPointOffset =y - getChildAt(mItemPosition).getTop();
                    mDragPointOffset = ((int)motionEvent.getRawY()) - y;
                    startDrag(mItemPosition, y);
                    drag(0,y);
                }
        }
    }

    private void drag(int x, int y){
        if(mDragView != null) {
            WindowManager.LayoutParams layoutParams = (WindowManager.LayoutParams) mDragView.getLayoutParams();
            layoutParams.x = x;
            layoutParams.y = y - mDragPointOffset;
            WindowManager mWindowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
            mWindowManager.updateViewLayout(mDragView,layoutParams);

            if(mdra)
        }
    }

}
