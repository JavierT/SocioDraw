package drawing.training.javi.drawingapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import org.alljoyn.bus.BusAttachment;
import org.alljoyn.bus.BusException;
import org.alljoyn.bus.BusListener;
import org.alljoyn.bus.Mutable;
import org.alljoyn.bus.ProxyBusObject;
import org.alljoyn.bus.SessionListener;
import org.alljoyn.bus.SessionOpts;
import org.alljoyn.bus.Status;
import org.alljoyn.bus.annotation.BusSignalHandler;

import java.util.ArrayList;
import java.util.Arrays;

//import org.alljoyn.bus.ProxyBusObject;


public class JoinActivity extends ActionBarActivity
    implements JoinFragment.setPlayerReady, JoinFragment.setPlayerColor {
    /* Load the native alljoyn_java library */
    static {
        System.loadLibrary("alljoyn_java");
    }

    private static final int MESSAGE_PING_REPLY = 1;
    private static final int MESSAGE_POST_TOAST = 2;
    private static final int MESSAGE_START_PROGRESS_DIALOG = 3;
    private static final int MESSAGE_STOP_PROGRESS_DIALOG = 4;
    private static final int MESSAGE_REQUEST_NEW_USERNAME = 5;
    private static final int MESSAGE_SET_NOT_READY = 6;
    private static final int MESSAGE_COLORS_UPDATE = 7;

    private String mUsername;
    private static final String TAG = "DrawingClient";

    // Handler used to make calls to Alljoyn metgods. See onCreate()
    private ClientBusHandler mBusHandler;

    private ProgressDialog mDialog;
    private JoinFragment mJoinFragment;
    private DrawingFragment mDrawingFragment;

    protected ArrayList<String> mAvailableColors;
    private boolean mPlayerReady = false;
    private boolean mCountDownStarted = false;


    private Handler mHandler = new Handler() {


        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_PING_REPLY:
                    Toast.makeText(getApplicationContext(), (String) msg.obj, Toast.LENGTH_SHORT).show();
                    break;
                case MESSAGE_POST_TOAST:
                    Toast.makeText(getApplicationContext(), (String) msg.obj, Toast.LENGTH_LONG).show();
                    break;
                case MESSAGE_START_PROGRESS_DIALOG:
                    mDialog = ProgressDialog.show(JoinActivity.this, "", "Finding Drawing Service.\n Please wait...", true, true);
                    break;
                case MESSAGE_STOP_PROGRESS_DIALOG:
                    mDialog.dismiss();
                    break;
                case MESSAGE_REQUEST_NEW_USERNAME:
                    getNewNameFromDialog();
                    break;
                case MESSAGE_SET_NOT_READY:
                    Toast.makeText(getApplicationContext(), (String) msg.obj, Toast.LENGTH_LONG).show();
                    mJoinFragment.setNotReady();
                case MESSAGE_COLORS_UPDATE:
                    //ArrayList<String> ac = (ArrayList<String>) msg.obj;
                    if(mAvailableColors != null && mAvailableColors.size()>0) {
                        if (mAvailableColors.get(0).equals(DrawingInterface.NOT_AVAILABLE)) { // Color already taken
                            mAvailableColors.remove(0);
                            Toast.makeText(getApplicationContext(), "Someone took already that color, please choose another", Toast.LENGTH_LONG).show();
                            mJoinFragment.setDefaultColor();
                        }
                        mJoinFragment.setAvailableColors(mAvailableColors);
                    }
                default:
                    break;
            }
        }
    };


    private void getNewNameFromDialog() {
        final AlertDialog alert = new AlertDialog.Builder(this).create();
        alert.setTitle("Title");

        // Set up the input
        final EditText input = new EditText(this);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        alert.setView(input);
        alert.setCancelable(false);

        // Set up the buttons
        alert.setButton(AlertDialog.BUTTON_POSITIVE,"OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String text = input.getText().toString();
                if(!text.isEmpty()) {
                    mUsername = text;
                    mBusHandler.sendEmptyMessage(ClientBusHandler.CLIENT_REQUEST_NEW_NAME);
                }

            }


        });
        alert.show();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_join);
        if (savedInstanceState == null) {
            mJoinFragment = new JoinFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.joinContainer, mJoinFragment)
                    .commit();
        }

        // Create the handler in a new thread to avoid blocking the UI
        HandlerThread busThread = new HandlerThread(ClientBusHandler.class.getSimpleName());
        busThread.start();
        mBusHandler = new ClientBusHandler(busThread.getLooper());

        // Connect to an Alljoyn object
        mBusHandler.sendEmptyMessage(ClientBusHandler.CLIENT_CONNECT);
        mHandler.sendEmptyMessage(MESSAGE_START_PROGRESS_DIALOG);

        Intent intent = getIntent();
        mUsername = intent.getStringExtra(getString(R.string.username));

        mAvailableColors = new ArrayList<>();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_join, menu);
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

        mBusHandler.sendEmptyMessage(ClientBusHandler.CLIENT_DISCONNECT);
    }

    /**
     * Interface coming from the Join fragment. Ready button clicked.
     * @param ready Set the button ready behaviour
     */
    public void setReady(boolean ready) {
        mPlayerReady = ready;
        mBusHandler.sendEmptyMessage(ClientBusHandler.CLIENT_SET_READY);
    }

    /**
     * Interface coming from the Join fragment. Color button selected.
     * @param color Set the color chosen in the fragment
     */
    public void setColor(String color) {
        Message msg = mBusHandler.obtainMessage(ClientBusHandler.CLIENT_SET_COLOR, color);
        mBusHandler.sendMessage(msg);
    }

    /**
     * If the parameter is higher than 0, a timer it's set and when it finishes,
     * the drawing fragment is shown
     * Otherwise, the drawing fragment it's shown without timer
     * @param countdown: time for the timer
     */
    private void setTimerToStart(int countdown) {
        if(countdown>0) {
            final ProgressDialog progress = ProgressDialog.show(this, "Starting the game",
                    "in " + countdown + " seconds...", true);
            new CountDownTimer(countdown*1000, 1000) {

                public void onTick(long millisUntilFinished) {
                    progress.setMessage("in "+ millisUntilFinished / 1000 + " seconds...");
                }

                public void onFinish() {
                    progress.dismiss();
                    changeToDrawingFragment();
                }
            }.start();
        } else {
            changeToDrawingFragment();
        }
    }

    private void changeToDrawingFragment() {
        mDrawingFragment = new DrawingFragment();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.joinContainer, mDrawingFragment)
                .addToBackStack(null)
                .commit();
    }



    //////////////////////////////////////////////////////////////////////////////////////////
    //                                                                                      //
    // BusHandler class that will handle all Alljoyn calls.                                 //
    //                                                                                      //
    //////////////////////////////////////////////////////////////////////////////////////////
    class ClientBusHandler extends Handler {

        //private static final String SERVICE_NAME = "drawing.training.javi.drawing";
        //private static final short CONTACT_PORT = 42;

        private BusAttachment mBus;
        private ProxyBusObject mProxyObj;
        private DrawingInterface mDrawingInterface;

        private int mSessionId;
        //private boolean mIsInASession;
        private boolean mIsConnected;
        private boolean mIsStoppingDiscovery;

        public static final int CLIENT_CONNECT = 1;
        public static final int CLIENT_JOIN_SESSION = 2;
        public static final int CLIENT_DISCONNECT = 3;
        public static final int CLIENT_REQUEST_NEW_NAME = 4;
        public static final int CLIENT_SET_READY = 5;
        public static final int CLIENT_GETPLAYERS = 6;  /* NOT NEEDED IF SIGNAL DONT WORK, DONT SHOW PLAYERS TABLE IN EACH DEVICE */
        public static final int CLIENT_WAITING = 7;
        public static final int CLIENT_SET_COLOR = 8;
        public static final int CLIENT_STARTING = 9;

        public ClientBusHandler (Looper looper) {
            super(looper);

            //mIsInASession = false;
            mIsConnected = false;
            mIsStoppingDiscovery = false;

        }

        @BusSignalHandler(iface="drawing.training.javi.drawingapp", signal="updatePlayerTables")
        public void updatePlayerTables() {
//            if(playersTable.length == 1 && playersTable[0].score == -1) // Error retrieving the list. Disconnect
//            {
            //mPlayersConnected = new ArrayList<>();
            //mHandler.sendEmptyMessage(MESSAGE_SET_PLAYERS);
//            }
            //sendUiMessage(MESSAGE_POST_TOAST, "SIGNAL RECEIVEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEED");
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CLIENT_CONNECT: {
                    org.alljoyn.bus.alljoyn.DaemonInit.PrepareDaemon(getApplicationContext());

                    /*
                    * All communication through AllJoyn begins with a BusAttachment.
                    *
                    * A BusAttachment needs a name. The actual name is unimportant except for internal
                    * security. As a default we use the class name as the name.
                    *
                    * By default AllJoyn does not allow communication between devices (i.e. bus to bus
                    * communication). The second argument must be set to Receive to allow communication
                    * between devices.
                    */
                    mBus = new BusAttachment(getPackageName(), BusAttachment.RemoteMessage.Receive);

                    mBus.useOSLogging(true);
                    mBus.setDebugLevel("ALL", 1);
                    mBus.setDebugLevel("ALLJON", 7);
                    mBus.setDaemonDebug("ALL", 7);

                    // Create a bus listener class
                    mBus.registerBusListener(new BusListener() {
                        @Override
                        public void foundAdvertisedName(String name, short transport, String namePrefix) {
                            logInfo(String.format("MyBusListener.foundAdvertisedName(%s, 0x%04x, %s)", name, transport, namePrefix));
                            /*
                            * This client will only join the first service that it sees advertising
                            * the indicated well-known name.  If the program is already a member of
                            * a session (i.e. connected to a service) we will not attempt to join
                            * another session.
                            */
                            if (!mIsConnected) {
                                Message msg = obtainMessage(CLIENT_JOIN_SESSION);
                                msg.arg1 = transport;
                                msg.obj = name;
                                sendMessage(msg);
                            }
                        }
                    });

                    /* To communicate with AllJoyn objects, we must connect the BusAttachment to the bus. */
                    Status status = mBus.connect();
                    logStatus("BusAttachment.connect()", status);
                    if (Status.OK != status) {
                        finish();
                        return;
                    }

                    /*
                    * Now find an instance of the AllJoyn object we want to call.  We start by looking for
                    * a name, then connecting to the device that is advertising that name.
                    *
                    * In this case, we are looking for the well-known SERVICE_NAME.
                    */
                    status = mBus.findAdvertisedName(DrawingInterface.SERVICE_NAME);
                    logStatus(String.format("BusAttachement.findAdvertisedName(%s)", DrawingInterface.SERVICE_NAME), status);
                    if (Status.OK != status) {
                        finish();
                        return;
                    }
                    break;
                }
                case (CLIENT_JOIN_SESSION): {
                    // If discovery is currently being stippped don't join any other sessions

                    if (mIsStoppingDiscovery || mIsConnected) {

                        break;
                    }
                    /*
                    * In order to join the session, we need to provide the well-known
                    * contact port.  This is pre-arranged between both sides as part
                    * of the definition of the chat service.  As a result of joining
                    * the session, we get a session identifier which we must use to
                    * identify the created session communication channel whenever we
                    * talk to the remote side.
                    */
                    short contactPort = DrawingInterface.CONTACT_PORT;
                    SessionOpts sessionOpts = new SessionOpts();
                    sessionOpts.transports = (short) msg.arg1;
                    Mutable.IntegerValue sessionId = new Mutable.IntegerValue();

                    Status status = mBus.joinSession((String) msg.obj, contactPort, sessionId, sessionOpts, new SessionListener() {
                        @Override
                        public void sessionLost(int sessionId, int reason) {
                            mIsConnected = false;
                            logInfo(String.format("MyBusListener.sessionLost(sessionId = %d, reason = %d)", sessionId, reason));
                            mHandler.sendEmptyMessage(MESSAGE_START_PROGRESS_DIALOG);
                        }

                    });
                    logStatus("BusAttachment.joinSession() - sessionId: " + sessionId.value, status);

                    if (status == Status.OK) {
                    /*
                     * To communicate with an AllJoyn object, we create a ProxyBusObject.
                     * A ProxyBusObject is composed of a name, path, sessionID and interfaces.
                     *
                     * This ProxyBusObject is located at the well-known SERVICE_NAME, under path
                     * "/ClientService", uses sessionID of CONTACT_PORT, and implements the SimpleInterface.
                     */
                        mProxyObj = mBus.getProxyBusObject(DrawingInterface.SERVICE_NAME, "/DrawingService", sessionId.value, new Class<?>[]{DrawingInterface.class});

                        mDrawingInterface = mProxyObj.getInterface(DrawingInterface.class);

                        mSessionId = sessionId.value;
                        mIsConnected = true;
                        mHandler.sendEmptyMessage(MESSAGE_STOP_PROGRESS_DIALOG);


                        status = mBus.registerSignalHandlers(this);
                        if (status != Status.OK) {
                            logStatus("JoinActivity.registerSignalHandlers() can't register to signals", Status.BUS_ERRORS);
                            //sendMessage(obtainMessage(CLIENT_DISCONNECT));
                            //return;
                        }

                        try {
                            if (mDrawingInterface.newPlayerConnected(mUsername)) {
                                // SEND A READY TO MYSELF TO FILL THE UI.
                                Message reply = obtainMessage(CLIENT_GETPLAYERS, mUsername);
                                sendMessage(reply);
                            }
                        } catch (BusException e) {
                            logException("DrawingInterface.newPlayerConnected()", e);
                            mHandler.sendEmptyMessage(MESSAGE_REQUEST_NEW_USERNAME);
                            return;
                        }


                    }
                    break;
                }

                case CLIENT_REQUEST_NEW_NAME: {
                    try {
                        if (mDrawingInterface.newPlayerConnected(mUsername)) {
                            // SEND A READY TO MYSELF TO FILL THE UI.
                            Message reply = obtainMessage(CLIENT_GETPLAYERS, mUsername);
                            sendMessage(reply);
                        }
                    } catch (BusException e) {
                        logException("DrawingInterface.newPlayerConnected()", e);
                        mHandler.sendEmptyMessage(MESSAGE_REQUEST_NEW_USERNAME);
                        return;
                    }
                    break;
                }

                //Release all resources acquired in the connect
                case CLIENT_DISCONNECT: {
                    mIsStoppingDiscovery = true;
                    try{
                        mDrawingInterface.setDisconnect(mUsername);
                    } catch (BusException e) {
                        logException("DrawingInterface.disconnect()", e);
                        sendUiMessage(MESSAGE_POST_TOAST,"Can't disconnect");
                        return;
                    }
                    if (mIsConnected) {
                        Status status = mBus.leaveSession(mSessionId);
                        logStatus("BusAttachment.leaveSession()", status);
                    }
                    mBus.disconnect();
                    getLooper().quit();
                    finish();
                    break;
                }

                case CLIENT_GETPLAYERS: {
                    /* Register the signals, to see if they want to work....*/
                    //SignalHandlers signalHandlers = new SignalHandlers();
                    //Status status = mBus.registerSignalHandlers(this);
                    //if (status != Status.OK) {
                        logStatus("JoinActivity.registerSignalHandlers() can't register to signals", Status.BUS_ERRORS);
                        //sendMessage(obtainMessage(DrawingInterface.DISCONNECT));
                        //return;

                    //}

                    break;
                }

                case CLIENT_SET_COLOR: {
                    Log.d("DrawingApp" , "Entrando en set color");
                    String param = (String)msg.obj;
                    try {
                        Log.d("DrawingApp" , "Enviando:" + mUsername + " y " + param);
                        String[] colors = mDrawingInterface.setPlayerColor(mUsername, param);

                        if(colors[0].equals(DrawingInterface.USERNAME_NOT_FOUND))
                            sendUiMessage(MESSAGE_POST_TOAST, "This is weird. Please exit and enter again");
                        else
                        {
                            mAvailableColors = new ArrayList<>(Arrays.asList(colors));
                            mHandler.sendEmptyMessage(MESSAGE_COLORS_UPDATE);
                        }
                    } catch (BusException e) {
                        logException("DrawingInterface.setPlayerColor()", e);
                        sendUiMessage(MESSAGE_POST_TOAST, "Color can't be sent");
                        return;
                    }
                    break;
                }

                case CLIENT_SET_READY: {
                    try {
                        if (!mDrawingInterface.setPlayerStatus(mUsername, mPlayerReady)) {
                            sendUiMessage(MESSAGE_SET_NOT_READY, "Status denied");
                        }
                    } catch (BusException e) {
                        logException("DrawingInterface.setPlayerStatus()", e);
                        sendUiMessage(MESSAGE_SET_NOT_READY, "Status can't be sent");
                        return;
                    }
                    if(mPlayerReady) {
                        // As the signals are not working, we go to waiting state.
                        sendEmptyMessage(CLIENT_WAITING);
                     }

                    break;
                }

                case CLIENT_WAITING: {
                    // If our status changed we are not longer in this state.
                    if(!mPlayerReady)
                        break;
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    try {

                        if(!mDrawingInterface.getLobbyStatus())
                            // Keep waiting if this player is ready but not the others
                            sendEmptyMessage(CLIENT_WAITING);
                        else {
                            sendUiMessage(MESSAGE_POST_TOAST, "Attention: The game will start soon");
                            sendEmptyMessage(CLIENT_STARTING);
                        }
                    } catch (BusException e) {
                        logException("DrawingInterface.getLobbyStatus()", e);
                        sendUiMessage(MESSAGE_POST_TOAST, "Status can't be retreived");
                    }
                    break;
                }

                case CLIENT_STARTING: {
                    try {
                        // If we started already the timeout, we don't do it again.
                        if(mCountDownStarted)
                            break;
                        mPlayerReady = false;
                        mCountDownStarted = true;
                        int countdown = mDrawingInterface.getCountdown();
                        if(countdown>=0)
                            setTimerToStart(countdown);
                        else
                            changeToDrawingFragment();
                    } catch (BusException e) {
                        logException("DrawingInterface.getCountdown()", e);
                        sendUiMessage(MESSAGE_POST_TOAST, "Countdown can't be retreived");
                    }
                    break;
                }
                default:
                    break;
            }
        }

        /* Helper function to send a message to the UI thread. */
        private void sendUiMessage(int what, Object obj) {
            mHandler.sendMessage(mHandler.obtainMessage(what, obj));
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

    private void logException(String msg, BusException ex) {
        String log = String.format("%s: %s", msg, ex);
        Message toastMsg = mHandler.obtainMessage(MESSAGE_POST_TOAST, log);
        mHandler.sendMessage(toastMsg);
        Log.e(TAG, log, ex);
    }

    /*
     * print the status or result to the Android log. If the result is the expected
     * result only print it to the log.  Otherwise print it to the error log and
     * Sent a Toast to the users screen.
     */
    private void logInfo(String msg) {
        Log.i(TAG, msg);
    }


}

