package com.sociotech.javiert.imaginary;

import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;

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
    private final SharedPreferences sharedPref;
    private final SharedPreferences.Editor editor;
    private final ArrayList<String> usedPictures;

    public Pictures(SharedPreferences preferences) {
        sharedPref = preferences;
        editor = sharedPref.edit();

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
        picturesList.add(R.drawable.picture11);

        usedPictures = getStringArrayPref(Constants.USED_PICTURES_KEY);
        if(usedPictures != null && !usedPictures.isEmpty()) {
            for(String pic: usedPictures) {
                Integer picName = Integer.parseInt(pic);
                if(picturesList.contains(picName))
                    picturesList.remove(picName);

            }
        }
    }

    public int getRandomPicture()
    {
        if(picturesList.size()==1) {
            int pic = picturesList.get(0);
            reset();
            return pic;
        } else {
            int n = r.nextInt(picturesList.size() - 1);
            Integer pic = picturesList.get(n);
            picturesList.remove(n);
            usedPictures.add(String.valueOf(pic));
            setStringArrayPref(Constants.USED_PICTURES_KEY,usedPictures);
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

        usedPictures.clear();
        setStringArrayPref(Constants.USED_PICTURES_KEY, usedPictures);
    }

    public boolean isEmpty() {
        return picturesList.size() == 0;
    }



    //******************** SHAREDPREFERENCES CODE TO SOLVE SETSTRINGSET BUG ****************
    public void setStringArrayPref(String key, ArrayList<String> values) {
        JSONArray a = new JSONArray();
        for (int i = 0; i < values.size(); i++) {
            a.put(values.get(i));
        }
        if (!values.isEmpty()) {
            editor.putString(key, a.toString());
        } else {
            editor.putString(key, null);
        }
        editor.commit();
    }

    public ArrayList<String> getStringArrayPref(String key) {
        String json = sharedPref.getString(key, null);
        ArrayList<String> urls = new ArrayList<>();
        if (json != null) {
            try {
                JSONArray a = new JSONArray(json);
                for (int i = 0; i < a.length(); i++) {
                    String url = a.optString(i);
                    urls.add(url);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return urls;
    }
}