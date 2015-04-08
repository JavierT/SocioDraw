package com.sociotech.javiert.imaginary;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.sociotech.javiert.imaginary.createPictures.DrawingActivity;

/**
 * Drawing App created by Javier Tresaco on 2/02/15.
 * ${PACKAGE_NAME}
 * Source code on:  https://github.com/JavierT/SocioDraw
 */
public class MenuFragment extends Fragment {

    private String mUsername;
    private createGame mCallback;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_menu, container, false);

        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        mUsername = sharedPref.getString(getString(R.string.username),"");

        

        TextView txtWelcome = (TextView) rootView.findViewById(R.id.txtWelcome);
        txtWelcome.setTypeface(MainActivity.handwritingFont);
        txtWelcome.setText(getString(R.string.welcome) + " " + mUsername);

        // Event handlers for the buttons
        Button btnJoin = (Button) rootView.findViewById(R.id.btnJoin);
        btnJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                joinGame();
            }
        });
        btnJoin.setTypeface((MainActivity.handwritingFont));

        Button btnCreate = (Button) rootView.findViewById(R.id.btnCreate);
        btnCreate.setTypeface((MainActivity.handwritingFont));
        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.openCreateOptionsFragment();
            }
        });

        Button btnDrawing = (Button) rootView.findViewById(R.id.btnDrawing);
        btnDrawing.setTypeface((MainActivity.handwritingFont));
        btnDrawing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createDrawingActivity();
            }
        });
        return rootView;
    }

    private void createDrawingActivity() {
        Intent myIntent = new Intent(getActivity(), DrawingActivity.class);
        this.startActivity(myIntent);
        getActivity().finish();
    }

    private void joinGame() {
        Intent myIntent = new Intent(getActivity(), JoinActivity.class);
        myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        myIntent.putExtra(getString(R.string.username), mUsername); //Optional parameters
        this.startActivity(myIntent);
        getActivity().finish();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (createGame) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement openCreateOptionsFragment");
        }
    }


    public interface createGame {
        public void openCreateOptionsFragment();
    }

}
