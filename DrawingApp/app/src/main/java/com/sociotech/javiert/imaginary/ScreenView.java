package com.sociotech.javiert.imaginary;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Drawing App created by Javier Tresaco on 2/03/15.
 * drawing.training.javi.drawingapp
 * Source code on:  https://github.com/JavierT/SocioDraw
 */
public class ScreenView extends View {
    private float totalWidth = (float) Constants.WIDTH;
    private float totalHeight = (float) Constants.HEIGHT;
    //drawing path
    private Path drawPath;
    //drawing and canvas paint
    private Paint drawPaint, canvasPaint;
    //initial color
    private int paintColor = 0xFF660000;
    //canvas bitmap
    private Bitmap canvasBitmap;
    //canvas
    private Canvas drawCanvas;
    private Rect clipBounds;

    private Matrix mat;
    private Matrix auxMat;
    private boolean mDrawingAllowed;


    public ScreenView(Context context, AttributeSet attrs) {
        super(context, attrs);
        drawPath = new Path();
        drawPaint = new Paint();

        drawPaint.setColor(paintColor);
        drawPaint.setAntiAlias(true);
        drawPaint.setStrokeWidth(20);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);

        canvasPaint = new Paint(Paint.DITHER_FLAG);
        mDrawingAllowed = false;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        //view given size
        super.onSizeChanged(w,h,oldw,oldh);
        if(drawCanvas == null || !mDrawingAllowed) {
            Log.d("DrawingApp", "On size: " + w + "," + h);
            canvasBitmap = Bitmap.createBitmap(Constants.WIDTH, Constants.HEIGHT, Bitmap.Config.RGB_565);
            drawCanvas = new Canvas(canvasBitmap);
            drawCanvas.drawColor(Color.WHITE);
            clipBounds = drawCanvas.getClipBounds();

            // We calculate the matrix needed to scale the canvas into the screen
            // dimensions.
            mat = new Matrix();
            mat.setTranslate(clipBounds.left, clipBounds.top);
            mat.setScale(w / totalWidth, h / totalHeight);
        }
//        Log.d("DrawingApp","Old size : " + oldw + "," + oldh);
//        Log.d("DrawingApp","Canvas size: " + w + "," + h);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //draw view
        canvas.drawBitmap(canvasBitmap, mat, canvasPaint);
        canvas.drawPath(drawPath, drawPaint);
        Log.d("DrawingApp", "onDraw");
    }

    public void paintPoints(DrawingPath points) {

        drawPath.moveTo((float)points.fromX, (float)points.fromY);
        drawPath.lineTo((float)points.toX, (float)points.toY);
        drawPaint.setStrokeWidth(points.stroke);
        drawPaint.setColor(points.color);
        drawCanvas.drawPath(drawPath,drawPaint);
        drawPath.reset();
        invalidate(); //invalidate view to repaint
    }

    public void prepareToSave() {
        Log.d("DrawingApp", "prepareToSave canvas!!! ");
        auxMat = new Matrix(mat);
        mat.setTranslate(0,0);
        mat.setScale(1.0f,1.0f);
        invalidate();
    }

    public void restoreAfterSave() {
        Log.d("DrawingApp", "restoreAfterSave canvas!!! ");
        mat.set(auxMat);
        invalidate();
    }

    public void clearCanvas() {
        Log.d("DrawingApp", "Cleaning canvas!!! ");
        drawPath.reset();
        canvasBitmap.eraseColor(android.graphics.Color.TRANSPARENT);
        canvasBitmap.prepareToDraw();
        drawCanvas.drawColor(Color.WHITE);
        invalidate();
    }

    public void allowShowingDrawing(boolean status) {
        mDrawingAllowed = status;
    }
}
