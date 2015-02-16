package drawing.training.javi.drawingapp;

import android.content.Intent;
import android.os.Bundle;
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

public class CreateActivity extends ActionBarActivity {

    /* Load the native alljoyn_java library */
    static {
        System.loadLibrary("alljoyn_java");
    }

    private String mUsername;
    private static final String TAG = "DrawingService";

    private static final int MESSAGE_PING = 1;
    private static final int MESSAGE_POST_TOAST = 2;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            CreateFragment cf =  (CreateFragment) getSupportFragmentManager().findFragmentById(R.id.createContainer);
            switch (msg.what) {
                case MESSAGE_PING:
                    String ping = (String) msg.obj;
                    cf.newMessageToAdd(ping);
                    break;
                case MESSAGE_POST_TOAST:
                    Toast.makeText(getApplicationContext(), (String) msg.obj, Toast.LENGTH_LONG).show();
                    break;
                default:
                    break;
            }
        }
    };

    // The Alljoyn object that is our service
    private DrawingService mDrawingService;

    // Handler used to make calls to Alljoyn methods
    private Handler mBusHandler;

    protected ArrayList<Player> mCurrentPlayers;
    private int mPlayersConnected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.createContainer, new CreateFragment())
                    .commit();
        }

        Intent intent = getIntent();
        mUsername = intent.getStringExtra(getString(R.string.username));

        mCurrentPlayers = new ArrayList<>();

        HandlerThread busThread = new HandlerThread(ServiceBusHandler.class.getSimpleName());
        busThread.start();
        mBusHandler = new ServiceBusHandler(busThread.getLooper());

        //Start our service
        mDrawingService = new DrawingService();
        mBusHandler.sendEmptyMessage(DrawingInterface.CONNECT);

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
        mBusHandler.sendEmptyMessage(DrawingInterface.DISCONNECT);
    }



    // The class that is our Alljoyn service. It implements the DrawingInterface
    class DrawingService implements DrawingInterface, BusObject {
        /*
         * This is the code run when the client makes a call to the Ping method of the
         * DrawingInterface.  This implementation just returns the received String to the caller.
         *
         * This code also prints the string it received from the user and the string it is
         * returning to the user to the screen.
         */
        public boolean newPlayerConnected(String inStr) {
            sendUiMessage(MESSAGE_PING, "Connected: " + inStr);
            try {
                Player p = new Player();
                p.name = inStr;
                p.ready = false;
                p.score = 0;
                p.color = "#FFFFFFF";
                mCurrentPlayers.add(p);
                return true;
            }
            catch (IndexOutOfBoundsException ex) {
                Log.e(TAG,ex.toString());
                return false;
            }
        }

        @Override
        public Player[] getPlayers() throws BusException {
            Log.i(TAG, String.format("Client requested a list of players"));
            Message msg;
            Player[] result;
            if(mCurrentPlayers == null)
            {
                // Fill one with the own data sent and send it back. This case shouldn't happen
                result = new Player[1];
                result[0] = new Player();
                result[0].name = "Please close the app and open again";
                result[0].ready = false;
                result[0].score = 0;
                result[0].color = "#FFFFFF";
                msg = mHandler.obtainMessage(MESSAGE_POST_TOAST, "Error sending the players");
            } else {
                result = new Player[mCurrentPlayers.size()];
                result = mCurrentPlayers.toArray(result);
                msg = mHandler.obtainMessage(MESSAGE_POST_TOAST, "Sending players to client");
            }
            mHandler.sendMessage(msg);
            return result;

        }

        // Helper function to send a message to the UI thread
        private void sendUiMessage(int what, Object obj) {
            mHandler.sendMessage(mHandler.obtainMessage(what,obj));
        }
    }

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
                case DrawingInterface.CONNECT: {
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
                     * Our service is the SimpleService BusObject at the "/SimpleService" path.
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

                    status = mBus.bindSessionPort(contactPort, sessionOpts, new SessionPortListener() {
                        @Override
                        public boolean acceptSessionJoiner(short sessionPort, String joiner, SessionOpts sessionOpts) {
                            logStatus(String.format("BusAttachment.acceptSessionJoiner(%s)",joiner),  Status.OK);
                            if((mPlayersConnected <= DrawingInterface.MAX_PLAYERS) && (sessionPort == DrawingInterface.CONTACT_PORT))
                            {
                                // Allow the connection
                                mPlayersConnected++;
                                return true;
                            }
                            else
                                return false;
                        }
                    });
                    logStatus(String.format("BusAttachment.bindSessionPort(%d, %s)",
                            contactPort.value, sessionOpts.toString()), status);
                    if (status != Status.OK) {
                        finish();
                        return;
                    }

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

                    break;
                }

                /* Release all resources acquired in connect. */
                case DrawingInterface.DISCONNECT: {
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