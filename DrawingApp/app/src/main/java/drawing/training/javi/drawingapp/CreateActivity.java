package drawing.training.javi.drawingapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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

public class CreateActivity extends ActionBarActivity
    implements LobbyFragment.setStartGame {

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
                    Toast.makeText(getApplicationContext(), (String) msg.obj, Toast.LENGTH_LONG).show();
                    break;
                case MESSAGE_COLOR_SELECTED:
                    String name = (String) msg.obj;
                    Toast toast = Toast.makeText(getApplicationContext(), "The player " + name + "has taken this color", Toast.LENGTH_SHORT);
                    toast.getView().findViewById(android.R.id.message);
                    mLobbyFragment.updatePlayerColor(name,mCurrentPlayers.get(name).color);
                    // Optional set name of player in that color.
                    break;
                case MESSAGE_REMOVE_PLAYER:
                    Toast.makeText(getApplicationContext(), "The player " + msg.obj + " left the game", Toast.LENGTH_LONG).show();
                    mLobbyFragment.removePlayer((String) msg.obj);
                    break;
                case MESSAGE_SET_GAME_READY:
                    mLobbyFragment.updateStartGameButton(mAllPlayersReady);
                    break;
                default:
                    break;
            }
        }
    };

    private LobbyFragment mLobbyFragment;


    // The Alljoyn object that is our service
    private DrawingService mDrawingService;

    // Handler used to make calls to Alljoyn methods
    private Handler mBusHandler;

    protected HashMap<String, Player> mCurrentPlayers;
    private HashMap<String,Boolean> mColorsAvailable;
    private String mJoinerName;
    private int mSessionId;
    private boolean mAllPlayersReady = false;
    private ProgressDialog myProgressDialog;
    private ScreenFragment mScreenFragment;
    private int mSecondsToStart = -1;
    private String mUsername;


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

        Intent intent = getIntent();
        mUsername = intent.getStringExtra(getString(R.string.username));

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
        mColorsAvailable.put(String.format("#%08X", (getResources().getColor(R.color.yellow))),false);
        mColorsAvailable.put(String.format("#%08X", (getResources().getColor(R.color.darkblue))),false);
        mColorsAvailable.put(String.format("#%08X", (getResources().getColor(R.color.orange))),false);
        mColorsAvailable.put(String.format("#%08X", (getResources().getColor(R.color.purple))),false);
        mColorsAvailable.put(String.format("#%08X", (getResources().getColor(R.color.green))),false);

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
    protected void onDestroy() {
        super.onDestroy();
        mBusHandler.sendEmptyMessage(SERVICE_DISCONNECT);
    }

    /**
     * When the startButton is clicked, then a timer is set up to synchronize
     * with the players and the game changes to the drawing fragment
     */
    public void startGame() {
        myProgressDialog = new ProgressDialog(this);
        myProgressDialog.setTitle("Starting the game");
        myProgressDialog.setMessage("The game will start in 5 seconds...");
        myProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        myProgressDialog.show();
        mSecondsToStart = Constants.countdownTimer;
        new CountDownTimer(Constants.countdownTimer*1000, 1000) {

            public void onTick(long millisUntilFinished) {
                mSecondsToStart--;
                myProgressDialog.setMessage("The game will start in "+ millisUntilFinished / 1000 + " seconds...");
            }

            public void onFinish() {
                mSecondsToStart = 0;
                myProgressDialog.dismiss();
                openScreenFragment();
            }
        }.start();
    }

    /**
     * The Lobby fragment changes to the drawing fragment
     */
    public void openScreenFragment() {

      mScreenFragment = new ScreenFragment();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.createContainer, mScreenFragment)
                .addToBackStack(null)
                .commit();
    }

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
                    Log.d("DrawingApp Server","Color available ");
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
                        Log.d("DrawingApp Server","Imprimiendo colores disponibles: " + s);
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

            return mSecondsToStart;
        }

        // Helper function to send a message to the UI thread
        private void sendUiMessage(int what, Object obj) {
            mHandler.sendMessage(mHandler.obtainMessage(what,obj));
        }

    }

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

                        @Override
                        public void sessionJoined(short sessionPort, int id, String joiner) {
                            mSessionId = id;
                            mJoinerName = joiner;
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