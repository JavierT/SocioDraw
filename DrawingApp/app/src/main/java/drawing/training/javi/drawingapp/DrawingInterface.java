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

    /*
     * The BusMethod annotation signifies that this function should be used as part of the AllJoyn
     * interface.  The runtime is smart enough to figure out what the input and output of the method
     * is based on the input/output arguments of the Ping method.
     *
     * All methods that use the BusMethod annotation can throw a BusException and should indicate
     * this fact.
     */
    @BusMethod
    String Ping(String inStr) throws BusException;
}



