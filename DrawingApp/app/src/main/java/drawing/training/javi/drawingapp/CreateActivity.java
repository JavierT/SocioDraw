package drawing.training.javi.drawingapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import org.alljoyn.bus.BusAttachment;
import org.alljoyn.bus.BusException;
import org.alljoyn.bus.BusListener;
import org.alljoyn.bus.BusObject;
import org.alljoyn.bus.Mutable;
import org.alljoyn.bus.SessionOpts;
import org.alljoyn.bus.SessionPortListener;
import org.alljoyn.bus.SignalEmitter;
import org.alljoyn.bus.Status;

import java.util.ArrayList;
import java.util.HashMap;

public class CreateActivity extends ActionBarActivity
    implements LobbyFragment.setStartGame {

    /* Load the native alljoyn_java library */
    static {
        System.loadLibrary("alljoyn_java");
    }

    private String mUsername;
    private static final String TAG = "DrawingService";

    private static final int MESSAGE_SET_NEW_PLAYER= 1;
    private static final int MESSAGE_POST_TOAST = 2;
    private static final int MESSAGE_UPDATE_PLAYER = 3;
    private static final int MESSAGE_COLOR_SELECTED = 4;

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
                    TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
                    mLobbyFragment.updatePlayerColor(name,mCurrentPlayers.get(name).color);
                    // Optional set name of player in that color.
                    break;
                default:
                    break;
            }
        }
    };

    private LobbyFragment mLobbyFragment;


    // The Alljoyn object that is our service
    private DrawingService mDrawingService;
    //private SignalService mSignalService;

    // Handler used to make calls to Alljoyn methods
    private Handler mBusHandler;
    private SignalEmitter mEmitter;
    //private DrawingInterface mInterface;

    protected HashMap<String, Player> mCurrentPlayers;
    private HashMap<String,Boolean> mColorsAvailable;
    private String mJoinerName;
    private int mSessionId;
    private boolean mAllPlayersReady;
    //private int mPlayersConnected;


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
        //mSignalService = new SignalService();
        mBusHandler.sendEmptyMessage(SERVICE_CONNECT);

        // Fill the colors dictionary
        mColorsAvailable = new HashMap<>();
        mColorsAvailable.put(String.format("#%08X", (0xFFFFFFFF & getResources().getColor(R.color.black))),false);
        mColorsAvailable.put(String.format("#%08X", (0xFFFFFFFF & getResources().getColor(R.color.blue))),false);
        mColorsAvailable.put(String.format("#%08X", (0xFFFFFFFF & getResources().getColor(R.color.red))),false);
        mColorsAvailable.put(String.format("#%08X", (0xFFFFFFFF & getResources().getColor(R.color.yellow))),false);
        mColorsAvailable.put(String.format("#%08X", (0xFFFFFFFF & getResources().getColor(R.color.darkblue))),false);
        mColorsAvailable.put(String.format("#%08X", (0xFFFFFFFF & getResources().getColor(R.color.orange))),false);
        mColorsAvailable.put(String.format("#%08X", (0xFFFFFFFF & getResources().getColor(R.color.purple))),false);
        mColorsAvailable.put(String.format("#%08X", (0xFFFFFFFF & getResources().getColor(R.color.green))),false);



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

    public void startGame() {
        mDrawingService.sendUiMessage(MESSAGE_POST_TOAST,"STAAART THE GAMEEEE");
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
            Message msg;
            if(mCurrentPlayers.containsKey(name))
                return false;
            else
            {

                Player p = new Player();
                p.name = name;
                p.ready = false;
                p.score = 0;
                p.color = String.format("#%08X", (0xFFFFFFFF & getResources().getColor(R.color.black)));
                mCurrentPlayers.put(name, p);
                sendUiMessage(MESSAGE_SET_NEW_PLAYER, mCurrentPlayers.get(name));

                /*Emit signal of a new player connected */
                if(mDrawingService != null && mCurrentPlayers.size()>0) {
                    Player[] result = new Player[mCurrentPlayers.size()];
                    result = mCurrentPlayers.values().toArray(result);
                    try {
                        mDrawingService.updatePlayerTables(/*result*/);
                        msg = mHandler.obtainMessage(MESSAGE_POST_TOAST, "New player connected");
                    } catch (BusException e) {
                        msg = mHandler.obtainMessage(MESSAGE_POST_TOAST, "Error sending the players");
                        logStatus("newPlayerConnected.updatePlayerTables() signal" + e.toString(), Status.BUS_ERRORS);
                        e.printStackTrace();
                        mCurrentPlayers.remove(name);
                    }
                    mHandler.sendMessage(msg);
                }
                return true;
            }
        }

        //

        /**
         * Method to get all the players connected to the server.
         * @return An array with all the players data currently connected
         * @throws BusException
         */
        public Player[] getPlayers() throws BusException {
            Message msg;
            /*Emit signal of a new player connected */
            if(mDrawingService != null && mCurrentPlayers.size()>0) {
                Player[] result = new Player[mCurrentPlayers.size()];
                result = mCurrentPlayers.values().toArray(result);
                try {
                    mDrawingService.updatePlayerTables(/*result*/);
                    msg = mHandler.obtainMessage(MESSAGE_POST_TOAST, "Get players called");
                } catch (BusException e) {
                    msg = mHandler.obtainMessage(MESSAGE_POST_TOAST, "Error getting the players");
                    logStatus("newPlayerConnected.updatePlayerTables() signal" + e.toString(), Status.BUS_ERRORS);
                    e.printStackTrace();
                }
                mHandler.sendMessage(msg);
            }
            Log.i(TAG, String.format("Client requested a list of players"));
//            Message msg;
            Player[] result;
            if(mCurrentPlayers == null)
            {
                // Fill one with the own data sent and send it back. This case shouldn't happen
                result = new Player[1];
                result[0] = new Player();
                result[0].name = "Please close the app and open again";
                result[0].ready = false;
                result[0].score = -1;
                result[0].color = String.format("#%08X", (0xFFFFFFFF & getResources().getColor(R.color.black)));
                msg = mHandler.obtainMessage(MESSAGE_POST_TOAST, "Error sending the players");
            } else {
                result = new Player[mCurrentPlayers.size()];
                result = mCurrentPlayers.values().toArray(result);
                msg = mHandler.obtainMessage(MESSAGE_POST_TOAST, "Sending players to client");
            }
            mHandler.sendMessage(msg);
            return result;
        }

        /**
         * Returns the status of the lobby. It can be WAITING = 0
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
         * Returns the status of the lobby. It can be WAITING = 0
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
                String aux = color.toUpperCase();
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
            //for(int i=0; i<colorsAvailable.size(); i++)
            //        colorTable[i]=colorsAvailable.get(i);
            colorTable = colorsAvailable.toArray(colorTable);
            Log.d("DrawingApp Server","color table values: " + colorTable.length);
            return colorTable;
        }

        /**
         * Returns the status of the lobby. It can be WAITING = 0
         *
         * @return
         * @throws BusException
         */
        public boolean getLobbyStatus() throws BusException {
            return mAllPlayersReady;
        }

        /**
         * Signal that tell the clients that a new user has joined the session
         * @throws BusException
         */
        public void updatePlayerTables() throws BusException { /* No code needed here*/}

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
        return ready;
    }

//    public class SignalService implements SignalInterface, BusObject {
//
//
//    }

    // This class will handle all Alljoyn calls
    class ServiceBusHandler extends Handler {
        //private static final String SERVICE_NAME = "drawing.training.javi.drawing";
        //private static final short CONTACT_PORT = 42;

        private BusAttachment mBus;

        // Messages sent to the BusHandler from the UI
        //public static final int CONNECT = 1;
        //public static final int DISCONNECT = 2;

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
                            if((sessionPort == DrawingInterface.CONTACT_PORT)/*&& (mPlayersConnected <= DrawingInterface.MAX_PLAYERS) */)
                            {
                                // Allow the connection
                                //mPlayersConnected++;
                                return true;
                            }
                            else
                                return false;
                        }

                        @Override
                        public void sessionJoined(short sessionPort, int id, String joiner) {
                            mSessionId = id;
                            mJoinerName = joiner;
                            // TODO This emitter does not work. It is registered but the client doesnt' find the signal
                            if(mEmitter == null) {
                                mEmitter = new SignalEmitter(mDrawingService, mSessionId, SignalEmitter.GlobalBroadcast.On);
                                //mInterface = mEmitter.getInterface(DrawingInterface.class);
                            }
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