package com.sociotech.javiert.imaginary;

import org.alljoyn.bus.annotation.Position;

/**
 * Drawing App created by Javier Tresaco on 13/02/15.
 * drawing.training.javi.drawingapp
 * Source code on:  https://github.com/JavierT/SocioDraw
 */
public class Player {

    //public Player() { }

    @Position(0)
    public String name;

    @Position(1)
    public String color;

    @Position(2)
    public int score;

    @Position(3)
    public boolean ready;
}
