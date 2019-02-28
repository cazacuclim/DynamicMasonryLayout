package com.project.climent.dynamicmasonrylayout.module;

import android.graphics.Canvas;
import android.graphics.Point;
import android.view.View;

public class ModuleShadow extends View.DragShadowBuilder {

    private Point mScaleFactor;
    private int xTouch;
    private int yTouch;
    private int cellSize;

    public ModuleShadow(View v) {
        super(v);

    }

    public void setTouchLocation(int x, int y){
        xTouch = x;
        yTouch = y;
    }

    public void setCellSize(int cellSize){
        this.cellSize = cellSize;
    }


    @Override
    public void onProvideShadowMetrics (Point size, Point touch) {
        int width = getView().getMeasuredWidth() ;
        int height = getView().getMeasuredHeight() ;
        int xSizeDifference = cellSize / 8;
        int ySizeDifference = cellSize / 8;

        size.set(width + cellSize / 8, height + cellSize / 8);

        mScaleFactor = size;

        //touch.set(width / 2, height / 2);
        touch.set(xTouch + xSizeDifference/2, yTouch + ySizeDifference/2);
    }

    @Override
    public void onDrawShadow(Canvas canvas) {
        canvas.scale(mScaleFactor.x/(float)getView().getWidth(), mScaleFactor.y/(float)getView().getHeight());
        getView().draw(canvas);
    }

}
