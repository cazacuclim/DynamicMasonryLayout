package com.project.climent.dynamicmasonrylayout.container;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;

import com.project.climent.dynamicmasonrylayout.R;
import com.project.climent.dynamicmasonrylayout.Singleton;
import com.project.climent.dynamicmasonrylayout.module.ModuleView;


public class MasonryLayout extends RelativeLayout implements View.OnDragListener {

    private int mStartX = -1;
    private int mStartY = -1;

    private int cellSize = 0;
    private int cellNumber = 0;
    private int gapSize = 0;
    private int paddingSize = 0;

    private boolean isEnabledEditMode = false;

    private int matriceX;
    private int matriceY;

    private int lastValidPositionX = 0;
    private int lastValidPositionY = 0;

    private int[][] matrice;

    private Paint mPaint = new Paint();


    public MasonryLayout(Context context) {
        super(context);
    }

    public MasonryLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        SharedPreferences prefs = context.getSharedPreferences(
                "com.example.climent.draganddrop", Context.MODE_PRIVATE);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MasonryLayout);
        if (a != null) {
            gapSize = a.getDimensionPixelSize(R.styleable.MasonryLayout_ssv_gap, 0);
            paddingSize = a.getDimensionPixelSize(R.styleable.MasonryLayout_ssv_padding, 0);
            cellNumber = a.getInteger(R.styleable.MasonryLayout_ssv_cell_number, 8);

            a.recycle();
        }

        this.setWillNotDraw(false);
        this.setOnDragListener(this);

        mPaint.setColor(Color.GRAY);
        mPaint.setStrokeWidth(5);
        mPaint.setStyle(Paint.Style.STROKE);


        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                getViewTreeObserver().removeOnGlobalLayoutListener(this);
                cellSize = (getMeasuredWidth() - ((cellNumber - 1) * gapSize) - 2 * paddingSize) / cellNumber;
                matrice = new int[cellNumber][cellNumber];
                initMatrix();
                Singleton.getInstance().setCellSize(cellSize);
                Singleton.getInstance().setGapSize(gapSize);
            }
        });


    }


    public void setCellNumber(int cellNumber) {
        this.cellNumber = cellNumber;
//        matrice = new int[cellNumber][cellNumber];
//        initMatrix();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (isEnabledEditMode) {
            canvas.drawRect(mStartX,
                    mStartY,
                    mStartX + cellSize * Singleton.getInstance().getSelectedView().getCellNumberHorizontal() + gapSize * (Singleton.getInstance().getSelectedView().getCellNumberHorizontal() - 1),
                    mStartY + cellSize * Singleton.getInstance().getSelectedView().getCellNumberVertical() + gapSize * (Singleton.getInstance().getSelectedView().getCellNumberVertical() - 1),
                    mPaint);
        }
    }

    @Override
    public boolean onDrag(View v, DragEvent event) {
        int action = event.getAction();

        int positionXForLongTouch = Singleton.getInstance().getPositionXForLongTouch();
        int positionYForLongTouch = Singleton.getInstance().getPositionYForLongTouch();

        ModuleView selectedView = Singleton.getInstance().getSelectedView();

        switch (action) {
            case DragEvent.ACTION_DRAG_LOCATION:
                if (v instanceof MasonryLayout) {
                    Point touchPosition = getTouchPositionFromDragEvent(v, event);

                    int viewX = touchPosition.x - positionXForLongTouch;
                    int viewY = touchPosition.y - positionYForLongTouch;

                    if (matriceX != (viewX + (cellSize + gapSize) / 2 - paddingSize) / (cellSize + gapSize) ||
                            matriceY != (viewY + (cellSize + gapSize) / 2 - paddingSize) / (cellSize + gapSize)) { //nu sunt pe aceeasi celula (scade utilizarea resorselor)

                        matriceX = (viewX + (cellSize + gapSize) / 2 - paddingSize) / (cellSize + gapSize);
                        matriceY = (viewY + (cellSize + gapSize) / 2 - paddingSize) / (cellSize + gapSize);

                        //Log.e("LOCATION", matriceX + " - " + matriceY);

                        Point topLeft = new Point((viewX + (cellSize + gapSize) / 2 - paddingSize) / (cellSize + gapSize),
                                (viewY + (cellSize + gapSize) / 2 - paddingSize) / (cellSize + gapSize));
                        Point topRight = new Point((viewX + (cellSize + gapSize) / 2 - paddingSize) / (cellSize + gapSize) + selectedView.getCellNumberHorizontal() - 1,
                                (viewY + (cellSize + gapSize) / 2 - paddingSize) / (cellSize + gapSize));
                        Point bottomLeft = new Point((viewX + (cellSize + gapSize) / 2 - paddingSize) / (cellSize + gapSize),
                                (viewY + (cellSize + gapSize) / 2 - paddingSize) / (cellSize + gapSize) + selectedView.getCellNumberVertical() - 1);


                        if (topRight.x < cellNumber &&bottomLeft.y < cellNumber && topLeft.x >= 0 && topLeft.y >= 0) {
                            int count = 0;
                            for (int i = topLeft.x; i <= topRight.x; i++) {
                                for (int j = topLeft.y; j <= bottomLeft.y; j++) {
                                    if (i >= 0 && j >= 0) {
                                        if (getMatrixValue(i, j) != 0) {
                                            Log.e("MATRIX ", i + "," + j + " = " + getMatrixValue(i, j));
                                            count++;
                                        }
                                    }
                                }
                            }

                            if (count == 0) {
                                mStartX = matriceX * (cellSize + gapSize) + paddingSize;
                                mStartY = matriceY * (cellSize + gapSize) + paddingSize;
                                invalidate();
                                //Log.e("VALID ",  mStartX+","+mStartY);
                                lastValidPositionX = matriceX;
                                lastValidPositionY = matriceY;
                            }
                        }
                    }
                }
                break;
            case DragEvent.ACTION_DRAG_STARTED:

                if (selectedView.getParent() instanceof MasonryLayout) {

                    int viewX = (int) selectedView.getTranslationX();
                    int viewY = (int) selectedView.getTranslationY();

                    int matriceX = ((viewX + (cellSize + gapSize) / 2 - paddingSize) / (cellSize + gapSize));
                    int matriceY = ((viewY + (cellSize + gapSize) / 2 - paddingSize) / (cellSize + gapSize));

                    mStartX = matriceX * (cellSize + gapSize) + paddingSize;
                    mStartY = matriceY * (cellSize + gapSize) + paddingSize;
                    invalidate();

                    lastValidPositionX = matriceX;
                    lastValidPositionY = matriceY;

                    removeFromMatrix(matriceX, matriceY, selectedView.getCellNumberHorizontal(), selectedView.getCellNumberVertical());
                }

                isEnabledEditMode = true;
                break;
            case DragEvent.ACTION_DRAG_ENTERED:
//                    v.setBackground(enterShape);
                if (mStartX == -1 || mStartY == -1) {
                    Point validLocation = findFirstValidPosition(Singleton.getInstance().getSelectedView());
                    mStartX = validLocation.x * (cellSize + gapSize) + paddingSize;
                    mStartY = validLocation.y * (cellSize + gapSize) + paddingSize;
                    //Log.e("MATRIX ",  mStartX+","+mStartY);
                }
                isEnabledEditMode = true;
                invalidate();
                break;
            case DragEvent.ACTION_DRAG_EXITED:
//                    v.setBackground(normalShape);
                isEnabledEditMode = false;
                invalidate();

//                View view2 = (View) event.getLocalState();
//                view2.cancelDragAndDrop();
//                ViewGroup owner2 = (ViewGroup) view2.getParent();
//                owner2.removeView(view2);
//
//                this.addView(view2);
//
//
//                    view2.setTranslationX(lastValidPositionX * (cellSize + gapSize) + paddingSize);
//                    view2.setTranslationY(lastValidPositionY * (cellSize + gapSize) + paddingSize);
//                    addOnMatrix(lastValidPositionX, lastValidPositionY, selectedView.getCellNumberHorizontal(), selectedView.getCellNumberVertical());
//
//
//
//                view2.setVisibility(View.VISIBLE);
                break;
            case DragEvent.ACTION_DROP:
                // Dropped, reassign View to ViewGroup
                View view = (View) event.getLocalState();
                ViewGroup owner = (ViewGroup) view.getParent();
                owner.removeView(view);

                this.addView(view);

                int viewX = getTouchPositionFromDragEvent(v, event).x - positionXForLongTouch;
                int viewY = getTouchPositionFromDragEvent(v, event).y - positionYForLongTouch;

                int matriceX = ((viewX + (cellSize + gapSize) / 2 - paddingSize) / (cellSize + gapSize));
                int matriceY = ((viewY + (cellSize + gapSize) / 2 - paddingSize) / (cellSize + gapSize));

                Point topLeft = new Point((viewX + (cellSize + gapSize) / 2 - paddingSize) / (cellSize + gapSize),
                        (viewY + (cellSize + gapSize) / 2 - paddingSize) / (cellSize + gapSize));
                Point topRight = new Point((viewX + (cellSize + gapSize) / 2 - paddingSize) / (cellSize + gapSize) + selectedView.getCellNumberHorizontal() - 1,
                        (viewY + (cellSize + gapSize) / 2 - paddingSize) / (cellSize + gapSize));
                Point bottomLeft = new Point((viewX + (cellSize + gapSize) / 2 - paddingSize) / (cellSize + gapSize),
                        (viewY + (cellSize + gapSize) / 2 - paddingSize) / (cellSize + gapSize) + selectedView.getCellNumberVertical() - 1);


                    int count = 0;
                    for (int i = topLeft.x; i <= topRight.x; i++) {
                        for (int j = topLeft.y; j <= bottomLeft.y; j++) {
                            if (i >= 0 && i < cellNumber && j >= 0 && j < cellNumber) {
                                if (getMatrixValue(i, j) == 1) {
                                    count++;
                                }
                            }
                        }
                    }

                    if (count > 0) {
                        if (lastValidPositionX == -1 || lastValidPositionY == -1) {
                            Point validPoint = findFirstValidPosition(Singleton.getInstance().getSelectedView());
                            view.setTranslationX(validPoint.x * (cellSize + gapSize) + paddingSize);
                            view.setTranslationY(validPoint.y * (cellSize + gapSize) + paddingSize);
                            addOnMatrix(validPoint.x, validPoint.y, selectedView.getCellNumberHorizontal(), selectedView.getCellNumberVertical());
                        } else {
                            view.setTranslationX(lastValidPositionX * (cellSize + gapSize) + paddingSize);
                            view.setTranslationY(lastValidPositionY * (cellSize + gapSize) + paddingSize);
                            addOnMatrix(lastValidPositionX, lastValidPositionY, selectedView.getCellNumberHorizontal(), selectedView.getCellNumberVertical());
                        }
                    } else {
                        if (matriceX >= 0 && matriceY >= 0 && topRight.x < cellNumber && bottomLeft.y < cellNumber) {
                            view.setTranslationX(matriceX * (cellSize + gapSize) + paddingSize);
                            view.setTranslationY(matriceY * (cellSize + gapSize) + paddingSize);
                            addOnMatrix(matriceX, matriceY, selectedView.getCellNumberHorizontal(), selectedView.getCellNumberVertical());
                        } else {
//                            Point validPoint = findFirstValidPosition(Singleton.getInstance().getSelectedView());
//                            view.setTranslationX(validPoint.x * (cellSize + gapSize) + paddingSize);
//                            view.setTranslationY(validPoint.y * (cellSize + gapSize) + paddingSize);
//                            addOnMatrix(validPoint.x, validPoint.y, selectedView.getCellNumberHorizontal(), selectedView.getCellNumberVertical());
                            view.setTranslationX(lastValidPositionX * (cellSize + gapSize) + paddingSize);
                            view.setTranslationY(lastValidPositionY * (cellSize + gapSize) + paddingSize);
                            addOnMatrix(lastValidPositionX, lastValidPositionY, selectedView.getCellNumberHorizontal(), selectedView.getCellNumberVertical());
                        }
                    }


                view.setVisibility(View.VISIBLE);

                lastValidPositionX = -1;
                lastValidPositionY = -1;
                mStartX = -1;
                mStartY = -1;
                isEnabledEditMode = false;
                break;
            case DragEvent.ACTION_DRAG_ENDED:
//                    v.setBackground(normalShape);
            default:
                break;
        }
        return true;
    }


    private Point getTouchPositionFromDragEvent(View item, DragEvent event) {
        Rect rItem = new Rect();
        item.getLocalVisibleRect(rItem);
        return new Point(rItem.left + Math.round(event.getX()), rItem.top + Math.round(event.getY()));
    }

    public void setCellSize(int size) {
        cellSize = size;
    }

    public int getCellSize() {
        return cellSize;
    }


    private void initMatrix() {
        for (int i = 0; i < cellNumber; i++)
            for (int j = 0; j < cellNumber; j++) {
                matrice[i][j] = 0;
            }
    }

    private void addOnMatrix(int x, int y, int horizontalNumber, int verticalNumber) {
        for (int i = x; i < x + horizontalNumber; i++)
            for (int j = y; j < y + verticalNumber; j++) {
                matrice[i][j] = 1;
            }
    }

    private void removeFromMatrix(int x, int y, int horizontalNumber, int verticalNumber) {
        for (int i = x; i < x + horizontalNumber; i++)
            for (int j = y; j < y + verticalNumber; j++) {
                matrice[i][j] = 0;
            }
    }

    private int getMatrixValue(int x, int y) {
        return matrice[x][y];
    }

    private Point findFirstValidPosition(ModuleView selectedModule) {
        for (int j = 0; j < cellNumber - selectedModule.getCellNumberVertical() + 1; j++) {
            for (int i = 0; i < cellNumber - selectedModule.getCellNumberHorizontal() + 1; i++) {
                Log.e("iiiiiiiiii", i + " - " + j);
                boolean agree = true;
                for (int k = i; k < i + selectedModule.getCellNumberHorizontal(); k++) {
                    for (int l = j; l < j + selectedModule.getCellNumberVertical(); l++) {
                        Log.e("kkkkkk", k + " - " + l);
                        if (matrice[k][l] != 0) {
                            agree = false;
                        }
                    }
                }
                if (agree) {
                    return new Point(i, j);
                }
            }
        }
        return null;
    }






}
