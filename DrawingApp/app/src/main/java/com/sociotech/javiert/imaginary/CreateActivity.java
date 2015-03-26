package com.sociotech.javiert.imaginary;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.alljoyn.bus.BusAttachment;
import org.alljoyn.bus.BusException;
import org.alljoyn.bus.BusListener;
import org.alljoyn.bus.BusObject;
import org.alljoyn.bus.Mutable;
import org.alljoyn.bus.SessionOpts;
import org.alljoyn.bus.SessionPortListener;
import org.alljoyn.bus.Status;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

public class CreateActivity extends FragmentActivity
    implements LobbyFragment.setStartGame, PatternFragment.changeToDrawFrag,
    ScreenFragment.changeToPattFrag {

    /* Load the native alljoyn_java library */
    static {
        System.loadLibrary("alljoyn_java");
    }

    private static final String TAG = "DrawingService";

    private static final int MESSAGE_SET_NEW_PLAYER= 1;
    private static final int MESSAGE_POST_TOAST = 2;
    private static final int MESSAGE_UPDATE_PLAYER = 3;
    private static final int MESSAGE_COLOR_SELECTED = 4;
    private static final int MESSAGE_REMOVE_PLAYER = 5;
    private static final int MESSAGE_SET_GAME_READY = 6;
    private static final int MESSAGE_PAINT_POINTS = 7;
    private static final int MESSAGE_UPDATE_DRAWING_COUNTER = 8;

    public static final int SERVICE_CONNECT = 1;
    public static final int SERVICE_DISCONNECT = 2;


    private Handler mHandler = new Handler() {


        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_SET_NEW_PLAYER:
                    mLobbyFragment.newPlayerToAdd((Player) msg.obj);
                    break;
                case MESSAGE_UPDATE_PLAYER:
                    String playerName = (String) msg.obj;
                    mLobbyFragment.setReady(playerName,mCurrentPlayers.get(playerName).ready);
                    break;
                case MESSAGE_POST_TOAST:
                    Toast.makeText(getApplicationContext(), (String) msg.obj,
                                    Toast.LENGTH_LONG).show();
                    break;
                case MESSAGE_COLOR_SELECTED:
                    String name = (String) msg.obj;
                    Toast toast = Toast.makeText(getApplicationContext(), "The player " + name
                                                + "has taken this color", Toast.LENGTH_SHORT);
                    toast.getView().findViewById(android.R.id.message);
                    mLobbyFragment.updatePlayerColor(name,mCurrentPlayers.get(name).color);
                    // Optional set name of player in that color.
                    break;
                case MESSAGE_REMOVE_PLAYER:
                    Toast.makeText(getApplicationContext(), "The player " + msg.obj
                                    + " left the game", Toast.LENGTH_LONG).show();
                    mLobbyFragment.removePlayer((String) msg.obj);
                    break;
                case MESSAGE_SET_GAME_READY:
                    mLobbyFragment.updateStartGameButton(mAllPlayersReady);
                    break;
                case MESSAGE_PAINT_POINTS:
                    //mPagerAdapter.getItem(Constants.SCREEN_ID)
                    mScreenFragment.paintPoints((DrawingPath) msg.obj);
                    break;
                case MESSAGE_UPDATE_DRAWING_COUNTER:
                    mDrawingCounterView.setText((String) msg.obj);
                    break;
                default:
                    break;
            }
        }
    };

    /** maintains the pager adapter*/
    private PagerAdapter mPagerAdapter;

    // Fragments associated to this activity
    private LobbyFragment mLobbyFragment;
    private ScreenFragment mScreenFragment;
    private PatternFragment mPatternFragment;

    // The Alljoyn object that is our service
    private DrawingService mDrawingService;

    // Handler used to make calls to Alljoyn methods
    private Handler mBusHandler;

    protected HashMap<String, Player> mCurrentPlayers;
    private HashMap<String,Boolean> mColorsAvailable;
    private boolean mAllPlayersReady = false;
    private ProgressDialog myProgressDialog;

    private int mSecondsRemaining = -1;

    private TextView mDrawingCounterView;
    private boolean mDrawingStatus = false;
    private ViewPager mPager;
    private Pictures mPatternPictures;
    private int roundsRemaining = 3; // minimum 3 players to start the game

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);
        if (savedInstanceState == null) {
            mLobbyFragment = new LobbyFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.createContainer, mLobbyFragment)
                    .commit();
        }


        mCurrentPlayers = new HashMap<>();

        HandlerThread busThread = new HandlerThread(ServiceBusHandler.class.getSimpleName());
        busThread.start();
        mBusHandler = new ServiceBusHandler(busThread.getLooper());

        //Start our service
        mDrawingService = new DrawingService();
        mBusHandler.sendEmptyMessage(SERVICE_CONNECT);

        // Fill the colors dictionary
        mColorsAvailable = new HashMap<>();
        mColorsAvailable.put(String.format("#%08X", (getResources().getColor(R.color.black))),false);
        mColorsAvailable.put(String.format("#%08X", (getResources().getColor(R.color.blue))),false);
        mColorsAvailable.put(String.format("#%08X", (getResources().getColor(R.color.red))),false);
        mColorsAvailable.put(String.format("#%08X", (getResources().getColor(R.color.orange))),false);
        mColorsAvailable.put(String.format("#%08X", (getResources().getColor(R.color.purple))),false);
        mColorsAvailable.put(String.format("#%08X", (getResources().getColor(R.color.green))),false);

        mPatternPictures = new Pictures(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_create, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case R.id.action_settings:
                return true;
            case R.id.quit:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(R.string.quit)
                .setMessage(R.string.really_quit)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mBusHandler.sendEmptyMessage(SERVICE_DISCONNECT);
                    }

                })
                .setNegativeButton(R.string.no, null)
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBusHandler.sendEmptyMessage(SERVICE_DISCONNECT);
    }

    /**
     * Ask to the create game player if they want to continue playing another rounds or
     * if they want to left the game.
     */
    public void finishGame() {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(R.string.gameOver)
                .setMessage(R.string.gameOverDesc)
                .setNegativeButton(R.string.exit, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mBusHandler.sendEmptyMessage(SERVICE_DISCONNECT);
                    }

                })
                .setPositiveButton(R.string.playAgain, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        roundsRemaining = mCurrentPlayers.size() +1;
                        startNextRound();
                    }

                })

                .show();
    }

    /**
     * When the startButton is clicked, then a timer is set up to synchronize
     * with the players and the game changes to the drawing fragment
     */
    public void startGame(final boolean isFirstRound) {
        myProgressDialog = new ProgressDialog(this);
        myProgressDialog.setCancelable(false);
        if(isFirstRound) {
            myProgressDialog.setTitle("Starting the game");
            // Number of rounds (pictures) that is gonna be is the number of players connected + server player
            roundsRemaining = mCurrentPlayers.size()+1;
        }
        else
            myProgressDialog.setTitle("Starting the next round");
        myProgressDialog.setMessage("The game will start in " + Constants.countdownTimer + " seconds...");
        myProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        myProgressDialog.show();
        mSecondsRemaining = Constants.countdownTimer;
        new CountDownTimer(Constants.countdownTimer*1000, 1000) {

            public void onTick(long millisUntilFinished) {
                mSecondsRemaining--;
                myProgressDialog.setMessage("The game will start in "+
                        millisUntilFinished / 1000 + " seconds...");
            }

            public void onFinish() {
                mSecondsRemaining = 0;
                myProgressDialog.dismiss();
                if(isFirstRound)
                    openScreenFragment();
                else {
                    initDrawingCounter();
                    mPager.setCurrentItem(Constants.PATTERN_ID);
                }
            }
        }.start();
    }

    /**
     * The Lobby fragment changes to the drawing fragment
     */
    public void openScreenFragment() {

        getSupportFragmentManager().beginTransaction().remove(mLobbyFragment).commit();
        mScreenFragment = new ScreenFragment();
        mPatternFragment = new PatternFragment();

        this.initialisePaging();

        mPatternFragment.setImage(mPatternPictures.getRandomPicture());

//        getSupportFragmentManager().beginTransaction()
//                .replace(R.id.createContainer, mScreenFragment)
//                .addToBackStack(null)
//                .commit();
    }

    /**
     * Initialise the fragments to be paged
     */
    private void initialisePaging() {

        List<Fragment> fragments = new Vector<>();
        fragments.add(Constants.PATTERN_ID, mPatternFragment);
        fragments.add(Constants.SCREEN_ID, mScreenFragment);



        this.mPagerAdapter  = new PagerAdapter(getSupportFragmentManager(), fragments);
        //
        mPager = (ViewPager)findViewById(R.id.viewpager);
        mPager.setAdapter(this.mPagerAdapter);

        mPager.setCurrentItem(Constants.PATTERN_ID);

        mDrawingCounterView = (TextView)findViewById(R.id.txtDrawiningCounter);
        mDrawingCounterView.setTypeface(MainActivity.handwritingFont);
        initDrawingCounter();
    }

    /**
     * This function sets up the counter for the drawing time. Once the time is done, it shows
     * a toast and shows the Continue button that when is clicked the next round is prepared.
     */
    private void initDrawingCounter() {
        mSecondsRemaining = Constants.DRAWING_TIME;
        mDrawingStatus = true;
        new CountDownTimer(Constants.DRAWING_TIME *1000, 1000) {

            public void onTick(long millisUntilFinished) {
                mSecondsRemaining--;
                long minutes = TimeUnit.SECONDS.toMinutes(mSecondsRemaining);
                long seconds = mSecondsRemaining - TimeUnit.MINUTES.toSeconds(minutes);
                String counterString;
                if(seconds < 10)
                    counterString = "Time remaining "+ String.format("%d:0%d", minutes, seconds);

                else
                    counterString = "Time remaining "+ String.format("%d:%d", minutes, seconds);
                mHandler.sendMessage(mHandler.obtainMessage(MESSAGE_UPDATE_DRAWING_COUNTER, counterString));
            }

            public void onFinish() {
                //mSecondsRemaining to -1 to tell the client that the counter to the next round did
                // not started yet.
                mSecondsRemaining = -1;
                mDrawingStatus = false;
                mHandler.sendMessage(mHandler.obtainMessage(MESSAGE_UPDATE_DRAWING_COUNTER, "Time is up!"));
                mHandler.sendMessage(mHandler.obtainMessage(MESSAGE_POST_TOAST, "Time is up! Please, show the picture to the others"));
                mScreenFragment.savePicture();
                final Button btnContinue =(Button)findViewById(R.id.btnContinue);
                btnContinue.setTypeface(MainActivity.handwritingFont);
                btnContinue.setVisibility(View.VISIBLE);
                btnContinue.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        v.setVisibility(View.GONE);
                        roundsRemaining--;
                        if(roundsRemaining>0)
                            startNextRound();
                        else
                            finishGame();
                    }
                });
            }
        }.start();
    }

    private void startNextRound() {
        startGame(false);
        if(mPatternPictures.isEmpty())
            mPatternPictures.reset();
        mPatternFragment.setImage(mPatternPictures.getRandomPicture());
        mScreenFragment.clearPicture();
    }


    public void changeToDrawingFrag() {
        mPager.setCurrentItem(Constants.SCREEN_ID);
    }

    public void changeToPatternFrag() {
        mPager.setCurrentItem(Constants.PATTERN_ID);
    }

    /**********************************************************************************************/
    public class PagerAdapter extends FragmentPagerAdapter {

        private List<Fragment> fragments;
        /**
         * @param fm: Fragment manager
         * @param fragments: fragments in the pager
         */
        public PagerAdapter(FragmentManager fm, List<Fragment> fragments) {
            super(fm);
            this.fragments = fragments;
        }
        /* (non-Javadoc)
         * @see android.support.v4.app.FragmentPagerAdapter#getItem(int)
         */
        @Override
        public Fragment getItem(int position) {
            if(position == Constants.SCREEN_ID)
            return this.fragments.get( Constants.SCREEN_ID);
            else
                return this.fragments.get(Constants.PATTERN_ID);
        }

        /* (non-Javadoc)
         * @see android.support.v4.view.PagerAdapter#getCount()
         */
        @Override
        public int getCount() {
            return this.fragments.size();
        }

    }
    /**********************************************************************************************/


    /**********************************************************************************************/
    // The class that is our Alljoyn service. It implements the DrawingInterface
    public class DrawingService implements DrawingInterface, BusObject {

        /**
         * Method send by the client to add the information of a new player
         * to the server. This method will raise the signal "UpdatePlayersTable"
         * that will make others players to update his table.
         * @param name: name of the new player connected
         * @return true if the players has been added correctly. Returns false if the
         * name is already registered.
         */
        public boolean newPlayerConnected(String name) {
            if(mCurrentPlayers.containsKey(name))
                return false;
            else
            {

                Player p = new Player();
                p.name = name;
                p.ready = false;
                p.score = 0;
                p.color = String.format("#%08X", (getResources().getColor(R.color.black)));
                mCurrentPlayers.put(name, p);
                sendUiMessage(MESSAGE_SET_NEW_PLAYER, mCurrentPlayers.get(name));

                return true;
            }
        }

        /**
         * Sets the new status of the player and returns true if it was changed successfully
         *
         * @return Returns false if the players is not found in the Current Players List.
         * @throws BusException
         */
        public boolean setPlayerStatus(String name, boolean status) throws BusException {
            if(!mCurrentPlayers.containsKey(name))
                return false;
            else
            {
                mCurrentPlayers.get(name).ready = status;
                sendUiMessage(MESSAGE_UPDATE_PLAYER,name);
                mAllPlayersReady = checkStatus();
                return true;
            }
        }

        /**
         * Sets the color of the player. Updates the table of colors with colors used
         * As well, puts the previous color of that player as available
         * If the color chosen is already taken, the list of available colors is returned
         * with a first item that indicates that the color is not available.
         *
         * @return Returns a list of the available colors.
         * @throws BusException
         */
        public String[] setPlayerColor(String name, String color) throws BusException {
            Log.d("DrawingApp Server","Method setPlayerColor called: " + name + " / " + color);
            ArrayList<String> colorsAvailable = new ArrayList<>();
            if(!mCurrentPlayers.containsKey(name)) {
                Log.d("DrawingApp Server","Username not found: " + name);
                colorsAvailable.add(DrawingInterface.USERNAME_NOT_FOUND);
            }
            else
            {
                if(mColorsAvailable.get(color.toUpperCase()))
                {
                    // Color already taken. Return array with -1 in first item
                    Log.d("DrawingApp Server","Color already taken ");
                    colorsAvailable.add(DrawingInterface.NOT_AVAILABLE);
                }
                else {
                    // Get previous color get by the player:
                    String oldColor = mCurrentPlayers.get(name).color;
                    mColorsAvailable.put(oldColor, false);
                    mCurrentPlayers.get(name).color = color.toUpperCase();
                    sendUiMessage(MESSAGE_COLOR_SELECTED,name);
                    mColorsAvailable.put(color.toUpperCase(), true);
                }
                // Return table with available colors
                for(String s : mColorsAvailable.keySet()) {
                    if(!mColorsAvailable.get(s)) {
                        colorsAvailable.add(s);
                        Log.d("DrawingApp Server","Available colors: " + s);
                    }
                }
                if(mColorsAvailable.isEmpty())
                    colorsAvailable.add(DrawingInterface.NOT_AVAILABLE);
            }
            String[] colorTable = new String[colorsAvailable.size()];
            colorTable = colorsAvailable.toArray(colorTable);
            Log.d("DrawingApp Server","color table values: " + colorTable.length);
            return colorTable;
        }

        /**
         * Disconnect a player from the service
         * @param name of the player
         * @return true if the disconnection was successful
         */
        public boolean setDisconnect(String name) throws BusException {
            if(!mCurrentPlayers.containsKey(name))
                return false;
            String color = mCurrentPlayers.get(name).color;
            if(mColorsAvailable.containsKey(color))
                mColorsAvailable.put(color,false);
            mCurrentPlayers.remove(name);
            sendUiMessage(MESSAGE_REMOVE_PLAYER, name);
            return true;
        }

        /**
         * Returns the status of the lobby. False if we are still waiting for
         * players.
         * @return Return true if the game enters in the countdown state
         * @throws BusException
         */
        public int getLobbyStatus() throws BusException {

            return mSecondsRemaining;
        }

        /**
         * Send to the UI the points to paint.
         * @param points Structure with the starting point and the end point
         *               as well the color chosen by the player
         * @return true if received, false if the time is up
         * @throws BusException
         */
        public boolean sendPoint(DrawingPath points) throws BusException {
            //sendUiMessage(MESSAGE_POST_TOAST,"RECEIVED POINTS  "+ points.fromX+", " + points.fromY );
            if(!mDrawingStatus && mSecondsRemaining<=0) // if the time is up
                return false;
            sendUiMessage(MESSAGE_PAINT_POINTS, points);
            return true;
        }

        // Helper function to send a message to the UI thread
        private void sendUiMessage(int what, Object obj) {
            mHandler.sendMessage(mHandler.obtainMessage(what,obj));
        }

    }
    /**********************************************************************************************/

    private boolean checkStatus() {
        if(mCurrentPlayers.size()<2)
            return false;
        boolean ready = true;
        for(Player p: mCurrentPlayers.values()) {
            ready = ready && p.ready;
        }
        mHandler.sendEmptyMessage(MESSAGE_SET_GAME_READY);
        return ready;
    }


    // This class will handle all Alljoyn calls
    class ServiceBusHandler extends Handler {

        private BusAttachment mBus;

        public ServiceBusHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SERVICE_CONNECT: {
                    org.alljoyn.bus.alljoyn.DaemonInit.PrepareDaemon(getApplicationContext());

                    /*
                     * All communication through AllJoyn begins with a BusAttachment.
                     *
                     * A BusAttachment needs a name. The actual name is unimportant except for internal
                     * security. As a default we use the class name as the name.
                     *
                     * By default AllJoyn does not allow communication between devices (i.e. bus to bus
                     * communication).  The second argument must be set to Receive to allow
                     * communication between devices.
                     */
                     mBus = new BusAttachment(getPackageName(), BusAttachment.RemoteMessage.Receive);

                    /*
                     * Create a bus listener class
                     */
                     mBus.registerBusListener(new BusListener());

                    /*
                     * To make a service available to other AllJoyn peers, first register a BusObject with
                     * the BusAttachment at a specific path.
                     *
                     * Our service is the DrawingService BusObject at the "/DrawingService" path.
                     */
                    Status status = mBus.registerBusObject(mDrawingService, "/DrawingService");
                    logStatus("BusAttachment.registerBusObject()", status);
                    if (status != Status.OK) {
                        finish();
                        return;
                    }

                    /*
                     * The next step in making a service available to other AllJoyn peers is to connect the
                     * BusAttachment to the bus with a well-known name.
                     */
                    /*
                     * connect the BusAttachement to the bus
                     */
                    status = mBus.connect();
                    logStatus("BusAttachment.connect()", status);
                    if (status != Status.OK) {
                        finish();
                        return;
                    }

                    /*
                     * Create a new session listening on the contact port of the chat service.
                     */
                    Mutable.ShortValue contactPort = new Mutable.ShortValue(DrawingInterface.CONTACT_PORT);

                    SessionOpts sessionOpts = new SessionOpts();
                    sessionOpts.traffic = SessionOpts.TRAFFIC_MESSAGES;
                    sessionOpts.isMultipoint = false;
                    sessionOpts.proximity = SessionOpts.PROXIMITY_ANY;

                    /*
                     * Explicitly add the Wi-Fi Direct transport into our
                     * advertisements.  This sample is typically used in a "cable-
                     * replacement" scenario and so it should work well over that
                     * transport.  It may seem odd that ANY actually excludes Wi-Fi
                     * Direct, but there are topological and advertisement/
                     * discovery problems with WFD that make it problematic to
                     * always enable.
                     */
                    sessionOpts.transports = SessionOpts.TRANSPORT_ANY + SessionOpts.TRANSPORT_WFD;


                    /*
                     * request a well-known name from the bus
                     */
                    int flag = BusAttachment.ALLJOYN_REQUESTNAME_FLAG_REPLACE_EXISTING | BusAttachment.ALLJOYN_REQUESTNAME_FLAG_DO_NOT_QUEUE;

                    status = mBus.requestName(DrawingInterface.SERVICE_NAME, flag);
                    logStatus(String.format("BusAttachment.requestName(%s, 0x%08x)", DrawingInterface.SERVICE_NAME, flag), status);
                    if (status == Status.OK) {
                    /*
                     * If we successfully obtain a well-known name from the bus
                     * advertise the same well-known name
                     */
                        status = mBus.advertiseName(DrawingInterface.SERVICE_NAME, sessionOpts.transports);
                        logStatus(String.format("BusAttachement.advertiseName(%s)", DrawingInterface.SERVICE_NAME), status);
                        if (status != Status.OK) {
                        /*
                         * If we are unable to advertise the name, release
                         * the well-known name from the local bus.
                         */
                            status = mBus.releaseName(DrawingInterface.SERVICE_NAME);
                            logStatus(String.format("BusAttachment.releaseName(%s)", DrawingInterface.SERVICE_NAME), status);
                            finish();
                            return;
                        }
                    }

                    status = mBus.bindSessionPort(contactPort, sessionOpts, new SessionPortListener() {
                        @Override
                        public boolean acceptSessionJoiner(short sessionPort, String joiner, SessionOpts sessionOpts) {
                            logStatus(String.format("BusAttachment.acceptSessionJoiner(%s)",joiner),  Status.OK);

                            return (sessionPort == DrawingInterface.CONTACT_PORT);
                        }

                    });
                    logStatus(String.format("BusAttachment.bindSessionPort(%d, %s)",
                            contactPort.value, sessionOpts.toString()), status);
                    if (status != Status.OK) {
                        finish();
                        return;
                    }

                    break;
                }
                /* Release all resources acquired in connect. */
                case SERVICE_DISCONNECT: {
                /*
                 * It is important to unregister the BusObject before disconnecting from the bus.
                 * Failing to do so could result in a resource leak.
                 */
                    mBus.unregisterBusObject(mDrawingService);
                    mBus.disconnect();
                    mBusHandler.getLooper().quit();
                    finish();
                    break;
                }

                default:
                    break;
            }
        }
    }

    private void logStatus(String msg, Status status) {
        String log = String.format("%s: %s", msg, status);
        if (status == Status.OK) {
            Log.i(TAG, log);
        } else {
            Message toastMsg = mHandler.obtainMessage(MESSAGE_POST_TOAST, log);
            mHandler.sendMessage(toastMsg);
            Log.e(TAG, log);
        }
    }
}