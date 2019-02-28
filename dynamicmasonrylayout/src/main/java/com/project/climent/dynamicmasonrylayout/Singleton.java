package com.project.climent.dynamicmasonrylayout;

import android.annotation.SuppressLint;

import com.project.climent.dynamicmasonrylayout.module.ModuleView;


public class Singleton {
    @SuppressLint("StaticFieldLeak")
    private static final Singleton ourInstance = new Singleton();

    public static Singleton getInstance() {
        return ourInstance;
    }

    private ModuleView selectedView = null;
    private int positionXForLongTouch = -1;
    private int positionYForLongTouch = -1;
    private int cellSize;
    private int gapSize;

    private Singleton() {
    }

    public ModuleView getSelectedView() {
        return selectedView;
    }

    public void setSelectedView(ModuleView selectedView, int positionXForLongTouch, int positionYForLongTouch) {
        this.selectedView = selectedView;
        this.positionXForLongTouch = positionXForLongTouch;
        this.positionYForLongTouch = positionYForLongTouch;
    }

    public int getPositionXForLongTouch() {
        return positionXForLongTouch;
    }

    public int getPositionYForLongTouch() {
        return positionYForLongTouch;
    }

    public int getCellSize() {
        return cellSize;
    }

    public void setCellSize(int cellSize) {
        this.cellSize = cellSize;
    }

    public int getGapSize() {
        return gapSize;
    }

    public void setGapSize(int gapSize) {
        this.gapSize = gapSize;
    }
}
