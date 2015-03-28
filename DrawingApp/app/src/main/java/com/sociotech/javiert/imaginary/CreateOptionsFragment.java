package com.sociotech.javiert.imaginary;

/**
 * Drawing App created by Javier Tresaco on 28/03/15.
 * com.sociotech.javiert.imaginary
 * Source code on:  https://github.com/JavierT/SocioDraw
 */
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class CreateOptionsFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_create_options, container, false);
        TextView txtWelcome = (TextView) rootView.findViewById(R.id.txtCreateOptions);
        txtWelcome.setTypeface(MainActivity.handwritingFont);
        TextView txtScreenDesc = (TextView) rootView.findViewById(R.id.txtCollaborativeDesc);
        txtScreenDesc.setTypeface(MainActivity.handwritingFont);
        TextView txtPlayerDesc = (TextView) rootView.findViewById(R.id.txtCompetitiveDesc);
        txtPlayerDesc.setTypeface(MainActivity.handwritingFont);
// Event handlers for the buttons
        Button btnScreen = (Button) rootView.findViewById(R.id.btnCollaborative);
        btnScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createGameAsScreen();
            }
        });
        btnScreen.setTypeface((MainActivity.handwritingFont));
        Button btnPlayer = (Button) rootView.findViewById(R.id.btnCompetitive);
        btnPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createGameAsPlayer();
            }
        });
        btnPlayer.setTypeface((MainActivity.handwritingFont));
        return rootView;
    }

    private void createGameAsPlayer() {
        Intent myIntent = new Intent(getActivity(), CreateCollaborativeActivity.class);
        myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        this.startActivity(myIntent);
        getActivity().finish();
    }
    private void createGameAsScreen() {
        Intent myIntent = new Intent(getActivity(), CreateCompetitiveActivity.class);
        myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        this.startActivity(myIntent);
        getActivity().finish();
    }
}