package com.sociotech.javiert.imaginary;


import android.app.Activity;
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
        AnimationDrawable frameAnimation = (AnimationDrawable) iv_arrow.getBackground();
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


}
