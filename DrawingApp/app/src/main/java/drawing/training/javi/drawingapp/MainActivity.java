package drawing.training.javi.drawingapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends ActionBarActivity
        implements WelcomeFragment.saveUsername {

    public static Typeface handwritingFont;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // TESTING PURPOSES
//        SharedPreferences sharedPrefTesting = getPreferences(Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = sharedPrefTesting.edit();
//        editor.remove(getString(R.string.username));
//        editor.commit();
        //

        if (savedInstanceState == null) {
            SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);

            //TODO
            //handwritingFont = Typeface.createFromAsset(getAssets(), "handwritting.ttf");

            String username = sharedPref.getString(getString(R.string.username),"");
            if (username.isEmpty()) // show the name screen
            {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.container, new WelcomeFragment())
                        .commit();
            } else {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.container, new MenuFragment())
                        .commit();
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
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.username), name);
        editor.commit();

        getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.container, new MenuFragment())
                                    .commit();

    }

}


