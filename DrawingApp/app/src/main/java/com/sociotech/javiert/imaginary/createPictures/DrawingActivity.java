package com.sociotech.javiert.imaginary.createPictures;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.sociotech.javiert.imaginary.Constants;
import com.sociotech.javiert.imaginary.R;

import java.util.UUID;

/**
 * Drawing App created by Javier Tresaco on 8/04/15.
 * com.sociotech.javiert.imaginary
 * Source code on:  https://github.com/JavierT/SocioDraw
 */
public class DrawingActivity extends Activity {

    //custom drawing view
    private CustomDrawingView drawView;
    //buttons
    private ImageButton currPaint;
    private int mStrokeSize;

    private ImageButton mPaintButton;
    private ImageButton mHandButton;
    private ImageButton mEraserButton;
    private ImageButton mStrokeButton;
    private ImageButton mSaveButton;
    private ImageButton mNewButton;


    private int paint;

    private int mSizeWidth;
    private int mSizeHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawing);

        //get drawing view
        drawView = (CustomDrawingView)findViewById(R.id.drawing);

        //get the palette and first color button
        LinearLayout paintLayout = (LinearLayout)findViewById(R.id.paint_colors);
        currPaint = (ImageButton)paintLayout.getChildAt(0);
        currPaint.setImageDrawable(getResources().getDrawable(R.drawable.paint_pressed));

        mSizeWidth = Constants.WIDTH;
        mSizeHeight = Constants.HEIGHT;
        //int paint = 0xFF660000;

        drawView = (CustomDrawingView)findViewById(R.id.drawing);
        drawView.setSize(mSizeWidth,mSizeHeight);

        mStrokeSize = Constants.STROKE_SIZE_MEDIUM;

        mPaintButton = (ImageButton) findViewById(R.id.draw_btn);
        mHandButton = (ImageButton) findViewById(R.id.hand_btn);
        mEraserButton = (ImageButton) findViewById(R.id.erase_btn);
        mStrokeButton = (ImageButton) findViewById(R.id.stroke_btn);
        mSaveButton = (ImageButton) findViewById(R.id.save_btn);
        mNewButton = (ImageButton) findViewById(R.id.new_btn);

        mPaintButton.setBackgroundColor(paint);
        mPaintButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawView.setPaintMode();
                mPaintButton.setBackgroundColor(Color.parseColor(currPaint.getTag().toString()));
                mHandButton.setBackgroundResource(R.mipmap.ic_hand);
                mEraserButton.setBackgroundResource(R.mipmap.ic_eraser);
            }
        });


        mHandButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawView.setMovementMode();

                v.setBackgroundResource(R.mipmap.ic_hand_pressed);
                mPaintButton.setBackgroundColor(Color.TRANSPARENT);
                mEraserButton.setBackgroundResource(R.mipmap.ic_eraser);
            }
        });


        //mEraserButton.setImageDrawable(getResources().getDrawable(R.drawable.icons_status));
        mEraserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawView.setEraseMode();
                v.setBackgroundResource(R.mipmap.ic_eraser_pressed);
                mPaintButton.setBackgroundColor(Color.TRANSPARENT);
                mHandButton.setBackgroundResource(R.mipmap.ic_hand);
            }
        });


        //mEraserButton.setImageDrawable(getResources().getDrawable(R.drawable.icons_status));
        mStrokeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (mStrokeSize) {
                    case Constants.STROKE_SIZE_BIG:
                        mStrokeSize = Constants.STROKE_SIZE_MEDIUM;
                        mStrokeButton.setImageResource(R.mipmap.ic_stroke2);
                        break;
                    case Constants.STROKE_SIZE_MEDIUM:
                        mStrokeSize = Constants.STROKE_SIZE_SMALL;
                        mStrokeButton.setImageResource(R.mipmap.ic_stroke3);
                        break;
                    case Constants.STROKE_SIZE_SMALL:
                        mStrokeSize = Constants.STROKE_SIZE_BIG;
                        mStrokeButton.setImageResource(R.mipmap.ic_stroke1);
                        break;
                    default: break;
                }
                drawView.setStrokeSize(mStrokeSize);
                mStrokeButton.setBackgroundColor(Color.TRANSPARENT);
            }
        });

        mNewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newPicture();
            }
        });

        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savePicture();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    //user clicked paint
    public void paintClicked(View view){

        if(view!=currPaint){
            ImageButton imgView = (ImageButton)view;
            String color = view.getTag().toString();
            drawView.setColor(color);
            //update ui
            imgView.setImageDrawable(getResources().getDrawable(R.drawable.paint_pressed));
            currPaint.setImageDrawable(getResources().getDrawable(R.drawable.paint));
            currPaint=(ImageButton)view;
        }
    }

    private void newPicture() {
        AlertDialog.Builder newDialog = new AlertDialog.Builder(this);
        newDialog.setTitle("New drawing");
        newDialog.setMessage("Start new drawing (you will lose the current drawing)?");
        newDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int which){
                drawView.startNew();
                dialog.dismiss();
            }
        });
        newDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int which){
                dialog.cancel();
            }
        });
        newDialog.show();
    }

    private void savePicture() {
        AlertDialog.Builder saveDialog = new AlertDialog.Builder(this);
        saveDialog.setTitle("Save drawing");
        saveDialog.setMessage("Save drawing to device Gallery?");
        saveDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int which){
                //save drawing
                drawView.setDrawingCacheEnabled(true);
                //attempt to save
                String imgSaved = MediaStore.Images.Media.insertImage(
                        getContentResolver(), drawView.getDrawingCache(),
                        UUID.randomUUID().toString()+".png", "drawing");
                //feedback
                if(imgSaved!=null){
                    Toast savedToast = Toast.makeText(getApplicationContext(),
                            "Drawing saved to Gallery!", Toast.LENGTH_SHORT);
                    savedToast.show();
                }
                else{
                    Toast unsavedToast = Toast.makeText(getApplicationContext(),
                            "Oops! Image could not be saved.", Toast.LENGTH_SHORT);
                    unsavedToast.show();
                }
                drawView.destroyDrawingCache();
            }
        });
        saveDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int which){
                dialog.cancel();
            }
        });
        saveDialog.show();
    }


}
