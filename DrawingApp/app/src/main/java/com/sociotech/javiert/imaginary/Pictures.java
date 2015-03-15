package com.sociotech.javiert.imaginary;

import java.util.ArrayList;
import java.util.Random;

/**
 * Drawing App created by Javier Tresaco on 15/03/15.
 * drawing.training.javi.drawingapp
 * Source code on:  https://github.com/JavierT/SocioDraw
 */
public class Pictures {

    static private ArrayList<Integer> picturesList;
    static private Random r;

    public Pictures() {
        r = new Random();
        picturesList = new ArrayList<>();
        picturesList.add(R.drawable.picture1);
        picturesList.add(R.drawable.picture2);
        picturesList.add(R.drawable.picture3);
        picturesList.add(R.drawable.picture4);
        picturesList.add(R.drawable.picture5);
        picturesList.add(R.drawable.picture6);
        picturesList.add(R.drawable.picture7);
        picturesList.add(R.drawable.picture8);
        picturesList.add(R.drawable.picture9);
        picturesList.add(R.drawable.picture10);
    }

    public int getRandomPicture()
    {
        if(picturesList.size()==1) {
            int pic = picturesList.get(0);
            reset();
            return pic;
        } else {
            int n = r.nextInt(picturesList.size() - 1);
            int pic = picturesList.get(n);
            picturesList.remove(n);
            return pic;
        }
    }

    public void reset() {
        if(picturesList==null)
            picturesList = new ArrayList<>();
        else
            picturesList.clear();
        picturesList.add(R.drawable.picture1);
        picturesList.add(R.drawable.picture2);
        picturesList.add(R.drawable.picture3);
        picturesList.add(R.drawable.picture4);
        picturesList.add(R.drawable.picture5);
        picturesList.add(R.drawable.picture6);
        picturesList.add(R.drawable.picture7);
        picturesList.add(R.drawable.picture8);
        picturesList.add(R.drawable.picture9);
        picturesList.add(R.drawable.picture10);

    }

    public boolean isEmpty() {
        return picturesList.size() == 0;
    }
}
