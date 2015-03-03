package drawing.training.javi.drawingapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.support.annotation.NonNull;
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
    private Paint drawPaint, canvasPaint;
    //initial color
    private int paintColor = 0xFF660000;
    //canvas
    private Canvas drawCanvas;
    //canvas bitmap
    private Bitmap canvasBitmap;

    private float mPosX;
    private float mPosY;

    private float mLastTouchX;
    private float mLastTouchY;

    private ScaleGestureDetector mScaleDetector;
    private float mScaleFactor = 1.f;

    private boolean mPaintMode = true;



    private sendPlayerPaint mCallbackPaint;
    private Rect clipBounds;
    private int mActivePointerId;


    public DrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paintColor = attrs.getAttributeIntValue(0,paintColor);
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

        canvasPaint = new Paint(Paint.DITHER_FLAG);
    }

    public void setPaint(int paint) {
        this.paintColor = paint;
        drawPaint.setColor(paint);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        //view given size
        super.onSizeChanged(w,h,oldw,oldh);

        canvasBitmap = Bitmap.createBitmap(1950, 2419, Bitmap.Config.ARGB_8888);
        drawCanvas = new Canvas(canvasBitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //draw view
        super.onDraw(canvas);
        clipBounds = canvas.getClipBounds();
        canvas.save();
        drawPaint.setStrokeWidth(8/mScaleFactor);
        canvas.translate(mPosX, mPosY);
        canvas.scale(mScaleFactor, mScaleFactor, mLastTouchX, mLastTouchY);
        canvas.drawPath(drawPath, drawPaint);
        canvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);
        canvas.restore();
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {

        //detect user touch
//        float touchX = event.getX();
//        float touchY = event.getY();

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
        float touchX = (event.getX() + clipBounds.left) / mScaleFactor;
        float touchY = (event.getY() + clipBounds.top) / mScaleFactor;
        final int action = event.getAction();
        switch (action & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN: {
                mLastTouchX = touchX;
                mLastTouchY = touchY;
                mActivePointerId = event.getPointerId(0);
                break;
            }

            case MotionEvent.ACTION_MOVE: {
                final int pointerIndex = event.findPointerIndex(mActivePointerId);
                touchX = (event.getX(pointerIndex) + clipBounds.left) / mScaleFactor;
                touchY = (event.getY(pointerIndex) + clipBounds.top) / mScaleFactor;

                // Only move if the ScaleGestureDetector isn't processing a gesture.
                if (!mScaleDetector.isInProgress()) {
                    final float dx = touchX - mLastTouchX;
                    final float dy = touchY - mLastTouchY;
                    mPosX += dx;
                    mPosY += dy;
                    invalidate();
                }
                mLastTouchX = touchX;
                mLastTouchY = touchY;
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
                    mLastTouchX = event.getX(newPointerIndex);
                    mLastTouchY = event.getY(newPointerIndex);
                    mActivePointerId = event.getPointerId(newPointerIndex);
                }
                break;
            }
        }

    }

    private void detectPainting(MotionEvent event) {
        float touchX = (event.getX() + clipBounds.left) / mScaleFactor;
        float touchY = (event.getY() + clipBounds.top) / mScaleFactor;
        if (event.getPointerCount() > 1) {
            return;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                drawPath.moveTo(touchX, touchY);
                mLastTouchX = touchX;
                mLastTouchY = touchY;
                break;
            case MotionEvent.ACTION_MOVE:
                drawPath.lineTo(touchX, touchY);
//                    mCallbackPaint.sendPaint(mLastTouchX, mLastTouchY, mPosX, mPosY);
                mLastTouchX = touchX;
                mLastTouchY = touchY;
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


//    public void setCallback(FragmentActivity activity) {
//        try {
//            mCallbackPaint = (sendPlayerPaint) activity ;
//        } catch (ClassCastException e) {
//            throw new ClassCastException(activity.toString()
//                    + " must implement sendPlayerPaint");
//        }
//    }

    public interface sendPlayerPaint {
        public void sendPaint(float fromX, float fromY, float toX, float toY);
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            mScaleFactor *= detector.getScaleFactor();

            // Don't let the object get too small or too large.
            mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 5.0f));


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
