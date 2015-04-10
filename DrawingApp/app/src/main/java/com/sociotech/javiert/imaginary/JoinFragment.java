package com.sociotech.javiert.imaginary;


import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


public class JoinFragment extends Fragment {

    private ImageButton mCurrPaint;
    private ArrayList<ImageButton> mColors;
    private boolean mReadyState = false;
    //private int paintColor = 0xFF660000;
    private Button mReadyButton;

    private setPlayerColor mCallbackColor;
    private setPlayerReady mCallbackReady;

    private boolean mSendingColor = false;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_join, container, false);

        TextView txt = (TextView) rootView.findViewById(R.id.txtLobbyColor);
        txt.setTypeface(MainActivity.handwritingFont);

        mReadyButton = (Button) rootView.findViewById(R.id.btnJoinReady);
        mReadyButton.setTypeface(MainActivity.handwritingFont);
        mReadyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mCurrPaint== null) {// If no color selected
                    Toast.makeText(getActivity().getApplicationContext(), "Please select a color first", Toast.LENGTH_LONG).show();
                    return;
                }
                mReadyState = !mReadyState;
                if(mReadyState) {
                    for(ImageButton ib: mColors) {
                        if(ib.getId()!=mCurrPaint.getId())
                             ib.getBackground().setAlpha(50);
                    }
                    mReadyButton.setText(getString(R.string.join_NotReady));
                    mReadyButton.setTextColor(Color.RED);

                }
                else {
                    setNotReady();
                }
                mCallbackReady.setReady(mReadyState);
            }
        });

        // Color picker
        //LinearLayout paintLayout = (LinearLayout)rootView.findViewById(R.id.paint_colors);
        //mCurrPaint = (ImageButton)paintLayout.getChildAt(0);
        //mCurrPaint.setImageDrawable(getResources().getDrawable(R.drawable.paint_pressed));
        mCurrPaint = null; // By default, no color selected.

        View.OnClickListener colorPickerListener = new View.OnClickListener(){
            public void onClick(View v){
                paintClicked(v);
            }
        };


        ArrayList<Integer> ids = new ArrayList<>();
        ids.add(R.id.ibColor1);ids.add(R.id.ibColor2);
        ids.add(R.id.ibColor3);ids.add(R.id.ibColor4);
        ids.add(R.id.ibColor5);ids.add(R.id.ibColor6);

        mColors = new ArrayList<>();
        for(Integer i : ids) {
            ImageButton ib =(ImageButton)rootView.findViewById(i);
            ib.setOnClickListener(colorPickerListener);
            mColors.add(ib);
            ib.getBackground().setAlpha(255);
            ib.setEnabled(true);
        }



        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallbackReady = (setPlayerReady) activity;
            mCallbackColor = (setPlayerColor) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement setReady or setColor");
        }
    }

    private void paintClicked(View view) {
        if(view!= mCurrPaint && !mReadyState && !mSendingColor) {
            mSendingColor = true;
            String color = view.getTag().toString();

            mCallbackColor.setColor(color);
        }
    }

    public void setSelectionCorrect(String color)
    {
        ImageButton imgBtn = findImageButton(color);
        if(imgBtn == null)
            return;
        imgBtn.setImageDrawable(getResources().getDrawable(R.drawable.paint_pressed));
        if(mCurrPaint!= null) //If there is one already selected
            mCurrPaint.setImageDrawable(getResources().getDrawable(R.drawable.paint));
        mCurrPaint =imgBtn;
    }

    private ImageButton findImageButton(String color) {
        for(ImageButton ib: mColors) {

            if(ib.getTag().toString().equals(color))
                    return ib;
        }
        return mCurrPaint;
    }

    public void setNotReady() {
        mReadyState = false;
        mReadyButton.setText(getString(R.string.join_Ready));
        mReadyButton.setTextColor(Color.GREEN);
        for(ImageButton ib: mColors) {
            if(ib.isEnabled())
                ib.getBackground().setAlpha(255);
            else
                ib.getBackground().setAlpha(30);
        }
    }

    /**
     * As the color selected is not available, we deselect any image button
     * chose.
     */
    public void setDefaultColor() {
        if(mReadyState)
            setNotReady();
        if(mCurrPaint!= null)
            mCurrPaint.setImageDrawable(getResources().getDrawable(R.drawable.paint));
        mCurrPaint = null;
    }

    /**
     * If the color of the image button is not in the list and
     * it is not the one selected, we put it as unavailable.
     * @param colors: List of available colors..
     */
    public void setAvailableColors(ArrayList<String> colors) {
        for(ImageButton ib: mColors) {

            if(ib!=mCurrPaint && !colors.contains(ib.getTag().toString().toUpperCase()))
            {
                ib.setClickable(false);
                ib.getBackground().setAlpha(30);
            }
            else{
                ib.setClickable(true);
                ib.getBackground().setAlpha(255);
            }

        }
        mSendingColor = false;
    }

    public interface setPlayerReady {
        public void setReady(boolean ready);
    }

    public interface setPlayerColor {
        public void setColor(String color);
    }

    public int getColorSelected() {
        String paint = mCurrPaint.getTag().toString();
        return Color.parseColor(paint);
    }

}
