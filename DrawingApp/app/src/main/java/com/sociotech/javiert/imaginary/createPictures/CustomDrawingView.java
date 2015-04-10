package com.sociotech.javiert.imaginary.createPictures;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import com.sociotech.javiert.imaginary.Constants;

/**
 * Drawing App created by Javier Tresaco on 8/04/15.
 * com.sociotech.javiert.imaginary.CreatePictures
 * Source code on:  https://github.com/JavierT/SocioDraw
 */
public class CustomDrawingView extends View {

    //drawing path
    private Path drawPath;
    //drawing and canvas paint
    private Paint drawPaint;//, canvasPaint;
    //initial color
    private int paintColor = 0xFF660000;
    //canvas
    private Canvas drawCanvas;
    //canvas bitmap
    private Bitmap canvasBitmap;

    private float mDriftingX;
    private float mDriftingY;
    private float mOriginalDriftingY;


    private float mLastMovementTouchX;
    private float mLastMovementTouchY;

    private float mMiddleScaleTouchX;
    private float mMiddleScaleTouchY;

    private ScaleGestureDetector mScaleDetector;
    private float mScaleFactor = 1.f;
    private float mOriginalScaleFactor = 0.0f;

    private boolean mPaintMode = true;

    private Rect clipBounds;
    private int mActivePointerId;
    private int mStrokeSize = Constants.STROKE_SIZE_MEDIUM;
    private int mOldStrokeSize = Constants.STROKE_SIZE_MEDIUM;
    private boolean mPaintAllowed = true;
    private Rect rect;
    private RectF rectF ;
    Paint paintStroke;


    public CustomDrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);
//        paintColor = attrs.getAttributeIntValue(0,paintColor);
//        sizeWidth = attrs.getAttributeIntValue(1,Constants.WIDTH);
//        sizeHeight = attrs.getAttributeIntValue(2,Constants.HEIGHT);
        setupDrawing();
        // Create our ScaleGestureDetector
        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
    }

    private void setupDrawing() {
        //Get drawing area setup for interaction
        drawPath = new Path();
        drawPaint = new Paint();

        drawPaint.setColor(paintColor);
        drawPaint.setAntiAlias(true);
        drawPaint.setStrokeWidth(Constants.STROKE_SIZE_MEDIUM);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);

        canvasBitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.RGB_565 );
        drawCanvas = new Canvas(canvasBitmap);
        clipBounds = drawCanvas.getClipBounds();

        paintStroke = new Paint();

        paintStroke.setStrokeWidth(2);
        paintStroke.setStyle(Paint.Style.STROKE);
        paintStroke.setColor(Color.RED);
        paintStroke.setAntiAlias(true);

    }

    public void setPaint(int paint) {
        this.paintColor = paint;
        drawPaint.setColor(paint);
    }

    public void setSize(int w, int h) {
        canvasBitmap.recycle();

        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.RGB_565 );


        rect = new Rect(0, 0, w, h);
        rectF = new RectF(rect);


        drawCanvas = new Canvas(canvasBitmap);
        drawCanvas.drawColor(Color.WHITE);
        clipBounds = drawCanvas.getClipBounds();

        drawCanvas.drawRect(rectF, paintStroke);

        Paint fgPaintSel = new Paint();
        fgPaintSel.setColor(Color.GRAY);
        fgPaintSel.setStyle(Paint.Style.STROKE);
        fgPaintSel.setPathEffect(new DashPathEffect(new float[] {10,20}, 0));
        drawCanvas.drawLine(w/3, 0, w/3, h, fgPaintSel);
        drawCanvas.drawLine(2*w/3, 0, 2*w/3, h, fgPaintSel);
        drawCanvas.drawLine(0, 2*h/3, w, 2*h/3, fgPaintSel);
        drawCanvas.drawLine(0, h/3, w, h/3, fgPaintSel);

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {

        super.onSizeChanged(w,h,oldw,oldh);

        mScaleFactor = (float)(w-5)/(float)Constants.WIDTH;
        mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 5.0f));
        mDriftingY = h - Constants.HEIGHT*mScaleFactor;
        mOriginalDriftingY = mDriftingY;
        // Save the scale factor for next rounds
        if(mOriginalScaleFactor==0.0f)
            mOriginalScaleFactor = mScaleFactor;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //draw view
        super.onDraw(canvas);
        canvas.save();
        drawPaint.setStrokeWidth(mStrokeSize);
        canvas.translate(mDriftingX, mDriftingY);
        canvas.scale(mScaleFactor, mScaleFactor, mMiddleScaleTouchX, mMiddleScaleTouchY);
        canvas.drawBitmap(canvasBitmap, rect, rect, drawPaint);

        canvas.drawRect(rectF, paintStroke);

        canvas.drawPath(drawPath, drawPaint);
        clipBounds = canvas.getClipBounds();
        canvas.restore();
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        // If the painting is not allowed, we don't allow painting

        //Otherwise, we detect depending on the mode.
        if (mPaintMode && mPaintAllowed) {
            detectPainting(event);
        }else {
            detectMovement(event);
            super.onTouchEvent(event);
        }
        invalidate(); //invalidate view to repaint

        return true;
    }

    private void detectMovement(MotionEvent event) {
        mScaleDetector.onTouchEvent(event);
//        float touchX = (event.getX() + clipBounds.left) / mScaleFactor;
//        float touchY = (event.getY() + clipBounds.top) / mScaleFactor;
        float touchX = event.getX();
        float touchY = event.getY();
        final int action = event.getAction();
        switch (action & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN: {
                mLastMovementTouchX = touchX;
                mLastMovementTouchY = touchY;
                mActivePointerId = event.getPointerId(0);
                break;
            }

            case MotionEvent.ACTION_MOVE: {
                final int pointerIndex = event.findPointerIndex(mActivePointerId);
                Log.d("Imaginary - Movement", "pointerIndex is:" + pointerIndex);
                if(pointerIndex == -1)
                    break;
                touchX = event.getX(pointerIndex);
                touchY = event.getY(pointerIndex);

                // Only move if the ScaleGestureDetector isn't processing a gesture.
                if (!mScaleDetector.isInProgress() && event.getPointerCount()<=1) {
                    final float dx = touchX - mLastMovementTouchX;
                    final float dy = touchY - mLastMovementTouchY;
                    mDriftingX += dx;
                    mDriftingY += dy;
//                    Log.d("DrawingApp. OnSize", "Drifting- current touch: X:" + touchX + " Y: " + touchY) ;
//                    Log.d("DrawingApp. OnSize", "Drifting- last touch: X:" + mLastMovementTouchX + " Y: " + mLastMovementTouchY) ;
//                    Log.d("DrawingApp. OnSize", "Drifting: X:" + mDriftingX + " Y: " + mDriftingY) ;
//                    Log.d("DrawingApp. OnSize", "-------------------------------------------------") ;
                    mLastMovementTouchX = touchX;
                    mLastMovementTouchY = touchY;
                }

                break;
            }
            case MotionEvent.ACTION_UP: {
                mActivePointerId = -1;
                break;
            }

            case MotionEvent.ACTION_CANCEL: {
                mActivePointerId = -1;
                break;
            }

            case MotionEvent.ACTION_POINTER_UP: {
                final int pointerIndex = (event.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
                final int pointerId = event.getPointerId(pointerIndex);
                if (pointerId == mActivePointerId) {
                    // This was our active pointer going up. Choose a new
                    // active pointer and adjust accordingly.
                    final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
                    mLastMovementTouchX = event.getX(newPointerIndex);
                    mLastMovementTouchY = event.getY(newPointerIndex);
                    mActivePointerId = event.getPointerId(newPointerIndex);
                }
                break;
            }
        }

    }

    private void detectPainting(MotionEvent event) {

        float touchX = (event.getX() / mScaleFactor) + clipBounds.left;
        float touchY = (event.getY()/ mScaleFactor) + clipBounds.top;

        if (event.getPointerCount() > 1) {
            return;
        }
        final int action = event.getAction();
        switch (action & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN: {
                drawPath.moveTo(touchX, touchY);
                mActivePointerId = event.getPointerId(0);
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                final int pointerIndex = event.findPointerIndex(mActivePointerId);
                Log.d("Imaginary - Painting", "pointerIndex is:" + pointerIndex);
                if (pointerIndex == -1 || pointerIndex>=event.getPointerCount())
                    break;
                touchX = (event.getX(pointerIndex) / mScaleFactor) + clipBounds.left;
                touchY = (event.getY(pointerIndex) / mScaleFactor) + clipBounds.top;
                drawPath.lineTo(touchX, touchY);
                break;
            }
            case MotionEvent.ACTION_UP: {
                drawCanvas.drawPath(drawPath, drawPaint);
                drawPath.reset();
                mActivePointerId = -1;
                break;
            }
            case MotionEvent.ACTION_CANCEL: {
                mActivePointerId = -1;
                break;
            }
            case MotionEvent.ACTION_POINTER_UP: {
                final int pointerIndex = (event.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
                final int pointerId = event.getPointerId(pointerIndex);
                if (pointerId == mActivePointerId) {
                    // This was our active pointer going up. Choose a new
                    // active pointer and adjust accordingly.
                    final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
                    mActivePointerId = event.getPointerId(newPointerIndex);
                }
                break;
            }
            default:
                break;
        }
    }

    //start new drawing
    public void startNew(){
        int w = Constants.WIDTH;
        int h = Constants.HEIGHT;
        drawPath.reset();
        mScaleFactor = mOriginalScaleFactor;
        mDriftingX = 0.0f;
        mDriftingY = mOriginalDriftingY;
        mMiddleScaleTouchX = 0.0f;
        mMiddleScaleTouchY = 0.0f;
        canvasBitmap.eraseColor(android.graphics.Color.TRANSPARENT);
        canvasBitmap.prepareToDraw();
        drawCanvas.drawColor(Color.WHITE);
        Paint fgPaintSel = new Paint();
        fgPaintSel.setColor(Color.GRAY);
        fgPaintSel.setStyle(Paint.Style.STROKE);
        fgPaintSel.setPathEffect(new DashPathEffect(new float[] {10,20}, 0));
        rect = new Rect(0, 0, w, h);
        rectF = new RectF(rect);
        drawCanvas.drawLine(w/3, 0, w/3, h, fgPaintSel);
        drawCanvas.drawLine(2*w/3, 0, 2*w/3, h, fgPaintSel);
        drawCanvas.drawLine(0, 2*h/3, w, 2*h/3, fgPaintSel);
        drawCanvas.drawLine(0, h/3, w, h/3, fgPaintSel);

        invalidate();
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            //Log.d("DrawingApp. OnSize", "OnScale gesture happening. "+"Final touch: X:" + mLastPaintTouchX + " Y: " + mLastPaintTouchY);
            mScaleFactor *= detector.getScaleFactor();

            // Don't let the object get too small or too large.
            mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 5.0f));
            mMiddleScaleTouchX = detector.getFocusX();
            mMiddleScaleTouchY = detector.getFocusY();

            invalidate();
            return true;
        }
    }

    public void setPaintMode() {
        mPaintMode = true;
        mStrokeSize = mOldStrokeSize;
        drawPaint.setColor(paintColor);
    }

    public void setEraseMode() {
        mPaintMode = true;
        if(mStrokeSize != Constants.STROKE_SIZE_ERASE)
            mOldStrokeSize = mStrokeSize;
        mStrokeSize = Constants.STROKE_SIZE_ERASE;
        drawPaint.setColor(Color.WHITE);
    }

    public void setMovementMode() {
        mPaintMode = false;
    }

    public void setStrokeSize(int strokeSize) {
        mOldStrokeSize = strokeSize;
        mStrokeSize = strokeSize;
    }

    public void prepareToSave() {
        Log.d("DrawingApp", "prepareToSave canvas!!! ");
        mScaleFactor = 1.0f;
        mDriftingX = 0.0f;
        mDriftingY = 0.0f;
        invalidate();
    }

}

