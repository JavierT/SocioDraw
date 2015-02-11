package drawing.training.javi.drawingapp;

import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends ActionBarActivity
        implements WelcomeFragment.saveUsername, MenuFragment.createGame {

    public static Typeface handwritingFont;
    private String mUsername;

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
                getFragmentManager().beginTransaction()
                        .add(R.id.container, new WelcomeFragment())
                        .commit();
            } else {
                MenuFragment newFragment = new MenuFragment();
                Bundle args = new Bundle();
                args.putString(getString(R.string.username), mUsername);
                newFragment.setArguments(args);

                FragmentTransaction transaction = getFragmentManager().beginTransaction();

                // Replace whatever is in the fragment_container view with this fragment,
                // and add the transaction to the back stack so the user can navigate back
                transaction.add(R.id.container, newFragment);

                // Commit the transaction
                transaction.commit();
            }
        }


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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

        FragmentTransaction transaction = getFragmentManager().beginTransaction();

        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack so the user can navigate back
        transaction.replace(R.id.container, newFragment);
        transaction.addToBackStack(null);

        // Commit the transaction
        transaction.commit();


    }

    /////////////////////////////////////////////////////////////////////////////////////
    //                                                                                 //
    // Coming from the Menu fragment to change the fragment to create options fragment //
    //                                                                                 //
    /////////////////////////////////////////////////////////////////////////////////////
    public void openCreateOptionsFragment() {

        CreateOptionsFragment newFragment = new CreateOptionsFragment();
        Bundle args = new Bundle();
        args.putString(getString(R.string.username), mUsername);
        newFragment.setArguments(args);

        FragmentTransaction transaction = getFragmentManager().beginTransaction();

        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack so the user can navigate back
        transaction.replace(R.id.container, newFragment);
        transaction.addToBackStack(null);

        // Commit the transaction
        transaction.commit();



    }
}


