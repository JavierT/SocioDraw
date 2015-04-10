package com.sociotech.javiert.imaginary;

import org.alljoyn.bus.annotation.Position;

/**
 * Drawing App created by Javier Tresaco on 2/03/15.
 * drawing.training.javi.drawingapp
 * Source code on:  https://github.com/JavierT/SocioDraw
 */
public class DrawingPath {

    @Position(0)
    public double fromX;

    @Position(1)
    public double fromY;

    @Position(2)
    public double toX;

    @Position(3)
    public double toY;

    @Position(4)
    public int color;

    @Position(5)
    public int stroke;

    @Position(6)
    public String username;
//
//    public DrawingPath() {
//        this.fromX= 0.0f;
//        this.fromY = 0.0f;
//        this.toX= 0.0f;
//        this.toY = 0.0f;
//        this.color = Color.BLACK;
//    }
//
//    public DrawingPath(PointF from, PointF to) {
//        this.fromX= from.x;
//        this.fromY = from.y;
//        this.toX= to.x;
//        this.toY = to.y;
//        this.color = Color.BLACK;
//        this.color = Color.BLACK;
//    }
//
//    public DrawingPath(PointF from, PointF to, int color) {
//        this.fromX= from.x;
//        this.fromY = from.y;
//        this.toX= to.x;
//        this.toY = to.y;
//        this.color = color;
//    }


//    public float getFromX() {
//        return fromX;
//    }
//
//    public float getFromY() {
//        return fromY;
//    }
//
//    public int getColor() {
//        return color;
//    }
//
//    public float getToX() {
//        return toX;
//    }
//
//    public float getToY() {
//        return toY;
//    }
}
