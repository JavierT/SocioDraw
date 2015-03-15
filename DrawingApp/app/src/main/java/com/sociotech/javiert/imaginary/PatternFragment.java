package com.sociotech.javiert.imaginary;


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

    public PatternFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View rootView = inflater.inflate(R.layout.fragment_pattern, container, false);

        mImgPattern = (ImageView) rootView.findViewById(R.id.imgPattern);

        return rootView;
    }

    public void setImage(int url) {
        mImgPattern.setImageResource(url);
    }


}
