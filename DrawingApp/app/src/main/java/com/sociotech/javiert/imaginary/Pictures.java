package com.sociotech.javiert.imaginary;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

/**
 * Drawing App created by Javier Tresaco on 15/03/15.
 * drawing.training.javi.drawingapp
 * Source code on:  https://github.com/JavierT/SocioDraw
 */
public class Pictures {

    private ArrayList<String> picturesList;
    private Activity parentActivity;
    static private Random r;
    private final SharedPreferences sharedPref;
    private final SharedPreferences.Editor editor;
    private final ArrayList<String> usedPictures;

    public Pictures(Activity activity, SharedPreferences preferences) {
        parentActivity = activity;
        sharedPref = preferences;
        editor = sharedPref.edit();

        r = new Random();

        getPicturesFromSD();

        usedPictures = getStringArrayPref(Constants.USED_PICTURES_KEY);
        if(usedPictures != null && !usedPictures.isEmpty()) {
            picturesList.removeAll(usedPictures);
//            for(String pic: usedPictures) {
//                if(picturesList.contains(pic))
//                    picturesList.remove(pic);
//
//            }
        }
    }

    private void getPicturesFromSD() {
        File path = new File(Environment.getExternalStorageDirectory(),Constants.DRAWING_FOLDER + "/" + Constants.INCOME_FOLDER);
        if(!path.exists() || (path.list().length == 0))
        {
            addDefaultPictures();
            return;
        }
        picturesList = new ArrayList<>(Arrays.asList(path.list()));
    }

    private void addDefaultPictures() {
        ArrayList<Integer> pictures = new ArrayList<>();
        pictures.add(R.drawable.picture1);
        pictures.add(R.drawable.picture2);
        pictures.add(R.drawable.picture3);
        pictures.add(R.drawable.picture4);
        pictures.add(R.drawable.picture5);
        pictures.add(R.drawable.picture6);
        pictures.add(R.drawable.picture7);
        pictures.add(R.drawable.picture8);
        pictures.add(R.drawable.picture9);
        pictures.add(R.drawable.picture10);
        pictures.add(R.drawable.picture11);
        picturesList = new ArrayList<>();


        ProgressDialog myProgressDialog = new ProgressDialog(parentActivity);
        myProgressDialog.setCancelable(false);

        myProgressDialog.setTitle("Creating pictures");

        myProgressDialog.setMessage("Please wait...");
        myProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        myProgressDialog.show();

        FileOutputStream outStream = null;
        for( int i=0; i<pictures.size(); i++ ) {
            File file = new File(Environment.getExternalStorageDirectory()+"/" + Constants.DRAWING_FOLDER + "/" + Constants.INCOME_FOLDER, "picture"+i+".png");
            try {
                picturesList.add("picture"+i+".png");
                outStream = new FileOutputStream(file);

                Bitmap bm = BitmapFactory.decodeResource(parentActivity.getResources(), pictures.get(i));
                bm.compress(Bitmap.CompressFormat.PNG, 100, outStream);
                outStream.flush();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (outStream != null) {
            try {
                outStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        myProgressDialog.dismiss();
    }

    public String getRandomPicture()
    {
        if(picturesList.size() == 0)
            getPicturesFromSD();
        if(picturesList.size()==1) {
            String pic = picturesList.get(0);
            reset();
            return pic;
        } else {
            int n = r.nextInt(picturesList.size() - 1);
            String pic = picturesList.get(n);
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
        getPicturesFromSD();
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