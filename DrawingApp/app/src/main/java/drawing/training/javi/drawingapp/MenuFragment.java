package drawing.training.javi.drawingapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/**
 * Drawing App created by Javier Tresaco on 2/02/15.
 * ${PACKAGE_NAME}
 * Source code on:  https://github.com/JavierT/SocioDraw
 */
public class MenuFragment extends Fragment {

    private String mUsername;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_menu, container, false);

        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        mUsername = sharedPref.getString(getString(R.string.username),"");

        

        TextView txtWelcome = (TextView) rootView.findViewById(R.id.txtWelcome);
        //txtWelcome.setTypeface(MainActivity.handwritingFont);
        txtWelcome.setText(getString(R.string.welcome) + " " + mUsername);

        // Event handlers for the buttons
        Button btnJoin = (Button) rootView.findViewById(R.id.btnJoin);
        btnJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                joinGame();
            }
        });

        Button btnCreate = (Button) rootView.findViewById(R.id.btnCreate);
        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createGame();
            }
        });

        return rootView;
    }

    private void joinGame() {
        Intent myIntent = new Intent(getActivity(), JoinActivity.class);
        myIntent.putExtra(getString(R.string.username), mUsername); //Optional parameters
        this.startActivity(myIntent);
    }

    private void createGame() {
        Intent myIntent = new Intent(getActivity(), CreateActivity.class);
        myIntent.putExtra(getString(R.string.username), mUsername); //Optional parameters
        this.startActivity(myIntent);
    }
}
