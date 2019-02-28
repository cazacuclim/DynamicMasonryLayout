package com.project.climent.dynamicmasonrylayout.module;

import android.content.ClipData;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.project.climent.dynamicmasonrylayout.R;
import com.project.climent.dynamicmasonrylayout.Singleton;


public class ModuleView extends FrameLayout implements View.OnTouchListener, View.OnLongClickListener{

    private int cellNumberHorizontal = 1;
    private int cellNumberVertical = 1;

    int positionXForLongTouch;
    int positionYForLongTouch;

    public ModuleView(Context context) {
        super(context);
        init();
    }

    public ModuleView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ModuleView);
        if (a != null) {
            cellNumberHorizontal = a.getInt(R.styleable.ModuleView_ssv_cell_number_horizontal, 1);
            cellNumberVertical = a.getInt(R.styleable.ModuleView_ssv_cell_number_vertical, 1);
            a.recycle();
        }

        init();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setCellNumberHorizontal(cellNumberHorizontal);
        setCellNumberVertical(cellNumberVertical);
    }

    private void init(){
        setOnTouchListener(this);
        setOnLongClickListener(this);
    }


    public void setCellNumberHorizontal(int number){
        cellNumberHorizontal = number;
        getLayoutParams().width = Singleton.getInstance().getCellSize() * cellNumberHorizontal + (Singleton.getInstance().getGapSize() * (cellNumberHorizontal - 1));
        requestLayout();
    }

    public void setCellNumberVertical(int number){
        cellNumberVertical = number;
        getLayoutParams().height = Singleton.getInstance().getCellSize() * cellNumberVertical + (Singleton.getInstance().getGapSize() * (cellNumberVertical - 1));
        requestLayout();
    }

    public int getCellNumberHorizontal(){
        return cellNumberHorizontal;
    }

    public int getCellNumberVertical(){
        return cellNumberVertical;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        Log.e("TOUCH", motionEvent.getAction()+"");
        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {

            positionXForLongTouch = (int) motionEvent.getX();
            positionYForLongTouch = (int) motionEvent.getY();

        }

        return false;
    }

    @Override
    public boolean onLongClick(View view) {

        ClipData data = ClipData.newPlainText("", "");

        ModuleShadow shadowBuilder = new ModuleShadow(view);
        shadowBuilder.setCellSize(Singleton.getInstance().getCellSize());
        shadowBuilder.setTouchLocation(positionXForLongTouch,positionYForLongTouch);

        Singleton.getInstance().setSelectedView((ModuleView)view, positionXForLongTouch, positionYForLongTouch);

        //view.startDragAndDrop(data, shadowBuilder, view, View.DRAG_FLAG_OPAQUE);
        view.startDrag(data, shadowBuilder, view, 1);
        view.setVisibility(View.INVISIBLE);

        return false;
    }

}
