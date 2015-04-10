package com.sociotech.javiert.imaginary;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;


public class MainActivity extends FragmentActivity
        implements WelcomeFragment.saveUsername, MenuFragment.createGame {

    public static Typeface handwritingFont;
    private String mUsername;

    MenuFragment menuFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // TESTING PURPOSES
//        SharedPreferences sharedPrefTesting = getPreferences(Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = sharedPrefTesting.edit();
//        editor.remove(getString(R.string.username));
//        editor.commit();


        if (savedInstanceState == null) {
            SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);

            handwritingFont = Typeface.createFromAsset(getAssets(), "FingerPaint-Regular.ttf");

            mUsername = sharedPref.getString(getString(R.string.username),"");
            if (mUsername.isEmpty()) // show the name screen
            {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.container, new WelcomeFragment())
                        .commit();
            } else {
                menuFragment = new MenuFragment();
                Bundle args = new Bundle();
                args.putString(getString(R.string.username), mUsername);
                menuFragment.setArguments(args);

                getSupportFragmentManager().beginTransaction()
                        .add(R.id.container, menuFragment)
                        .commit();

            }
        }


    }

    /////////////////////////////////////////////////////////////////////////////////////
    //                                                                                 //
    // Coming from the Welcome fragment to save the username in the shared preferences //
    // and replace the fragment with the menu fragment                                 //
    //                                                                                 //
    /////////////////////////////////////////////////////////////////////////////////////
    public void saveUsernameAndContinue(String name) {
        mUsername = name;
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.username), mUsername);
        editor.commit();

//        getFragmentManager().beginTransaction()
//                .replace(R.id.container, new MenuFragment())
//                .commit();
        MenuFragment newFragment = new MenuFragment();
        Bundle args = new Bundle();
        args.putString(getString(R.string.username), mUsername);
        newFragment.setArguments(args);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, newFragment)
                .addToBackStack(null)
                .commit();


    }

    /////////////////////////////////////////////////////////////////////////////////////
    //                                                                                 //
    // Coming from the Menu fragment to go to create activity                          //
    //                                                                                 //
    /////////////////////////////////////////////////////////////////////////////////////
    public void openCreateOptionsFragment() {

        CreateOptionsFragment createFragment = new CreateOptionsFragment();
        Bundle args = new Bundle();
        args.putString(getString(R.string.username), mUsername);
        createFragment.setArguments(args);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, createFragment)
                .addToBackStack(null)
                .commit();
    }
}


