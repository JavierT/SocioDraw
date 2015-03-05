package drawing.training.javi.drawingapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

/**
 * Drawing App created by Javier Tresaco on 22/01/15.
 * ${PACKAGE_NAME}
 * Source code on:  https://github.com/JavierT/SocioDraw
 */
public class DrawingView extends View{

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

    private float mLastPaintTouchX;
    private float mLastPaintTouchY;

    private float mLastMovementTouchX;
    private float mLastMovementTouchY;

    private float mMiddleScaleTouchX;
    private float mMiddleScaleTouchY;

    private ScaleGestureDetector mScaleDetector;
    private float mScaleFactor = 1.f;

    private boolean mPaintMode = true;



    private sendPlayerPaint mCallbackPaint;
    private Rect clipBounds;
    private int mActivePointerId;


    public DrawingView(Context context, AttributeSet attrs) {
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
        drawPaint.setStrokeWidth(20);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);

        canvasBitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
        drawCanvas = new Canvas(canvasBitmap);
        clipBounds = drawCanvas.getClipBounds();


    }

    public void setPaint(int paint) {
        this.paintColor = paint;
        drawPaint.setColor(paint);
    }

    public void setSize(int w, int h) {
        canvasBitmap.recycle();
        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        drawCanvas = new Canvas(canvasBitmap);
        drawCanvas.drawColor(Color.WHITE);
        clipBounds = drawCanvas.getClipBounds();
    }

//    @Override
//    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
//        //view given size
//        super.onSizeChanged(w,h,oldw,oldh);
//
//        canvasBitmap = Bitmap.createBitmap(Constants.WIDTH, Constants.HEIGHT, Bitmap.Config.ARGB_8888);
//        drawCanvas = new Canvas(canvasBitmap);
//        drawCanvas.drawColor(Color.WHITE);
//        clipBounds = drawCanvas.getClipBounds();
//        //Log.d("DrawingApp. OnSize", "CanvasSize: L:" + clipBounds.left + " R:" + clipBounds.right
//        //        + " Top:" + clipBounds.top + " Bottom:" + clipBounds.bottom);
//        //Log.d("DrawingApp. OnSize", "Drawingspace size: H:" + this.getWidth() + " H: " + this.getHeight()) ;
//    }

    @Override
    protected void onDraw(Canvas canvas) {
        //draw view
        super.onDraw(canvas);
        canvas.save();
        drawPaint.setStrokeWidth(12/mScaleFactor);
        canvas.translate(mDriftingX, mDriftingY);
        canvas.scale(mScaleFactor, mScaleFactor, mMiddleScaleTouchX, mMiddleScaleTouchY);
        canvas.drawBitmap(canvasBitmap, 0, 0, drawPaint);
        canvas.drawPath(drawPath, drawPaint);
        clipBounds = canvas.getClipBounds();
        canvas.restore();
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {

        if (mPaintMode) {
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
        //Log.d("DrawingApp. OnSize", "Touch: X:" + event.getX() + " Y: " + event.getY() + "ScaleFactor= " + mScaleFactor) ;
        //Log.d("DrawingApp. OnSize", "CanvasSize: L:" + clipBounds.left + " R:" + clipBounds.right
        //        + " Top:" + clipBounds.top + " Bottom:" + clipBounds.bottom);
        //Log.d("DrawingApp. OnSize", "Drifting: X:" + mDriftingX + " Y: " + mDriftingY) ;


//        float touchX = (event.getX() + clipBounds.left - mDriftingX) / mScaleFactor;
//        float touchY = (event.getY() + clipBounds.top - mDriftingY) / mScaleFactor;
        float touchX = (event.getX() / mScaleFactor) + clipBounds.left;
        float touchY = (event.getY()/ mScaleFactor) + clipBounds.top;
//        if(touchX <0.0f || touchX > (clipBounds.right-clipBounds.left))
//            return;
//        if(touchY<0.0f || touchY>(clipBounds.bottom-clipBounds.top))
//            return;
        //Log.d("DrawingApp. OnSize", "Final touch: X:" + touchX + " Y: " + touchY) ;
        //Log.d("DrawingApp. OnSize", "-------------------------------------------------") ;
        if (event.getPointerCount() > 1) {
            return;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                drawPath.moveTo(touchX, touchY);
                mLastPaintTouchX = touchX;
                mLastPaintTouchY = touchY;
                break;
            case MotionEvent.ACTION_MOVE:
                drawPath.lineTo(touchX, touchY);
                mCallbackPaint.sendPaint(mLastPaintTouchX, mLastPaintTouchY, touchX, touchY);
                mLastPaintTouchX = touchX;
                mLastPaintTouchY = touchY;
                break;
            case MotionEvent.ACTION_UP:
                drawCanvas.drawPath(drawPath, drawPaint);
                drawPath.reset();
                break;
            default:
                return;
        }
        return;
    }


    public void setCallback(FragmentActivity activity) {
        try {
            mCallbackPaint = (sendPlayerPaint) activity ;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement sendPlayerPaint");
        }
    }

    public interface sendPlayerPaint {
        public void sendPaint(float fromX, float fromY, float toX, float toY);
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

    public void setPaintMode(boolean status) {
        mPaintMode = status;
    }

    public boolean getPaintMode() {
        return mPaintMode;
    }

}
