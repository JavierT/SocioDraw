package com.sociotech.javiert.imaginary;

import org.alljoyn.bus.BusException;
import org.alljoyn.bus.annotation.BusInterface;
import org.alljoyn.bus.annotation.BusMethod;

/**
 * Drawing App created by Javier Tresaco on 3/02/15.
 * ${PACKAGE_NAME}
 * Source code on:  https://github.com/JavierT/SocioDraw
 */

@BusInterface(name = "com.sociotech.javiert.imaginary.app")
public interface DrawingInterface {

    // Constants
    public static final String SERVICE_NAME = "drawing.training.javi.drawing";
    public static final short CONTACT_PORT = 42;

    public static final String NOT_AVAILABLE = "NOT_AVAILABLE";
    public static final String USERNAME_NOT_FOUND = "USERNAME_NOT_FOUND";

    @BusMethod(signature="s", replySignature = "b")
    boolean newPlayerConnected(String inStr) throws BusException;

    @BusMethod
    int getLobbyStatus() throws BusException;

    @BusMethod
    boolean setPlayerStatus(String name, boolean status) throws BusException;

    @BusMethod(signature="ss", replySignature = "as")
    String[] setPlayerColor(String name, String color) throws BusException;

    @BusMethod
    boolean setDisconnect(String mUsername) throws BusException;

    @BusMethod(signature = "r", replySignature = "b")
    boolean sendPoint(DrawingPath points) throws BusException;
}



