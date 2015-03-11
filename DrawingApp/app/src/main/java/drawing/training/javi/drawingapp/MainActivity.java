package drawing.training.javi.drawingapp;

import android.content.Context;
import android.content.Intent;
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


//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

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
    // Coming from the Menu fragment to change the fragment to create options fragment //
    //                                                                                 //
    /////////////////////////////////////////////////////////////////////////////////////
    public void openCreateOptionsFragment() {

        Intent myIntent = new Intent(this, CreateActivity.class);
        myIntent.putExtra(getString(R.string.username), mUsername); //Optional parameters
        this.startActivity(myIntent);

    }
}


