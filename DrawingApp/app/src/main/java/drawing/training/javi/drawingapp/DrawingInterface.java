package drawing.training.javi.drawingapp;

import org.alljoyn.bus.BusException;
import org.alljoyn.bus.annotation.BusInterface;
import org.alljoyn.bus.annotation.BusMethod;

/**
 * Drawing App created by Javier Tresaco on 3/02/15.
 * ${PACKAGE_NAME}
 * Source code on:  https://github.com/JavierT/SocioDraw
 */
//@BusInterface(name = "org.alljoyn.bus.drawing.ClientInterface")
@BusInterface(name = "drawing.training.javi.drawing.ClientInterface")
public interface DrawingInterface {


    // Constants
    public static final String SERVICE_NAME = "drawing.training.javi.drawing";
    public static final short CONTACT_PORT = 42;

    public static final int CONNECT = 1;
    public static final int JOIN_SESSION = 2;
    public static final int DISCONNECT = 3;
    public static final int READY = 4;

    public static final int MAX_PLAYERS = 6;


    /*
     * The BusMethod annotation signifies that this function should be used as part of the AllJoyn
     * interface.  The runtime is smart enough to figure out what the input and output of the method
     * is based on the input/output arguments of the Ping method.
     *
     * All methods that use the BusMethod annotation can throw a BusException and should indicate
     * this fact.
     */
    @BusMethod(signature="s", replySignature = "b")
    boolean newPlayerConnected(String inStr) throws BusException;

    @BusMethod(replySignature = "ar")
    Player[] getPlayers() throws BusException;

}



