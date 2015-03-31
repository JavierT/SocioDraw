package com.sociotech.javiert.imaginary;


import android.app.Activity;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class PatternFragment extends Fragment {


    private ImageView mImgPattern;
    private changeToDrawFrag mCallback;
    private AnimationDrawable frameAnimation;

    public PatternFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View rootView = inflater.inflate(R.layout.fragment_pattern, container, false);

        mImgPattern = (ImageView) rootView.findViewById(R.id.imgPattern);

        ImageView iv_arrow = (ImageView) rootView.findViewById(R.id.right_arrow);
        iv_arrow.setOnClickListener(new View.OnClickListener() {
            //@Override
            public void onClick(View v) {
                mCallback.changeToDrawingFrag();
            }
        });

        iv_arrow.setBackgroundResource(R.drawable.right_arrow_animation);
        frameAnimation = (AnimationDrawable) iv_arrow.getBackground();
        frameAnimation.start();

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (changeToDrawFrag) activity;
        }catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement changeFrag");
        }
    }

    public interface changeToDrawFrag {
        public void changeToDrawingFrag();
    }

    public void setImage(int url) {
        mImgPattern.setImageResource(url);
    }

    public void setArrowsToRed() {
        ColorFilter filter = new LightingColorFilter( Color.BLACK, Color.RED);
        frameAnimation.setColorFilter(filter);
    }

    public void setArrowsToNormal() {
        ColorFilter filter = new LightingColorFilter( Color.RED, Color.BLACK);
        frameAnimation.setColorFilter(filter);
    }
}
