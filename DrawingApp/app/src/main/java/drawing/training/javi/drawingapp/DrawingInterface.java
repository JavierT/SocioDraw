package drawing.training.javi.drawingapp;

import org.alljoyn.bus.BusException;
import org.alljoyn.bus.annotation.BusInterface;
import org.alljoyn.bus.annotation.BusMethod;
import org.alljoyn.bus.annotation.BusSignal;

/**
 * Drawing App created by Javier Tresaco on 3/02/15.
 * ${PACKAGE_NAME}
 * Source code on:  https://github.com/JavierT/SocioDraw
 */
//@BusInterface(name = "drawing.training.javi.drawing.ClientInterface")
@BusInterface(name = "drawing.training.javi.drawingapp")
public interface DrawingInterface {


    // Constants
    public static final String SERVICE_NAME = "drawing.training.javi.drawing";
    public static final short CONTACT_PORT = 42;

    public static final int MAX_PLAYERS = 6;

    public static final String NOT_AVAILABLE = "NOT_AVAILABLE";


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

    @BusMethod
    boolean getLobbyStatus() throws BusException;

    @BusMethod
    boolean setPlayerStatus(String name, boolean status) throws BusException;

    @BusMethod (signature="ss", replySignature = "as")
    String[] setPlayerColor(String name, String color) throws BusException;

    @BusSignal
    public void updatePlayerTables() throws BusException;

}



