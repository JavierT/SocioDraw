package com.sociotech.javiert.imaginary;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Calendar;


public class ScreenFragment extends Fragment {
    private ScreenView screenView;

    public ScreenFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_screen, container, false);

        screenView = (ScreenView)rootView.findViewById(R.id.screen);


        return rootView;
    }

    public void paintPoints(DrawingPath points) {
        screenView.paintPoints(points);
    }

    public void savePicture()
    {
        String uniqueId = getTodaysDate() + "_" + getCurrentTime() + ".png";

        File f = new File(Environment.getExternalStorageDirectory(),
                Constants.DRAWING_FOLDER);
        if (!f.exists()) //noinspection ResultOfMethodCallIgnored
            f.mkdirs();

        File file= new File(Environment.getExternalStorageDirectory()+ "/"+ Constants.DRAWING_FOLDER,uniqueId);

        Bitmap mBitmap =  Bitmap.createBitmap (Constants.WIDTH, Constants.HEIGHT, Bitmap.Config.RGB_565);

        Canvas canvas = new Canvas(mBitmap);
        screenView.prepareToSave();
        try
        {
            FileOutputStream mFileOutStream = new FileOutputStream(file);
            screenView.draw(canvas);
            mBitmap.compress(Bitmap.CompressFormat.PNG, 100, mFileOutStream);
            mFileOutStream.flush();
            mFileOutStream.close();
            String url = MediaStore.Images.Media.insertImage(getActivity().getContentResolver(),
                    Environment.getExternalStorageDirectory()+ "/"+ Constants.DRAWING_FOLDER+"/"+uniqueId,uniqueId, null);
            Log.v("log_tag", "url: " + url);

        }
        catch(Exception e)
        {
            Log.v("log_tag", e.toString());
        }
        screenView.restoreAfterSave();
    }

    private String getTodaysDate() {

        final Calendar c = Calendar.getInstance();
        int todaysDate =     (c.get(Calendar.YEAR) * 10000) +
                ((c.get(Calendar.MONTH) + 1) * 100) +
                (c.get(Calendar.DAY_OF_MONTH));
        Log.w("DATE:",String.valueOf(todaysDate));
        return(String.valueOf(todaysDate));

    }

    private String getCurrentTime() {

        final Calendar c = Calendar.getInstance();
        int currentTime =     (c.get(Calendar.HOUR_OF_DAY) * 10000) +
                (c.get(Calendar.MINUTE) * 100);
        Log.w("TIME:",String.valueOf(currentTime));
        return(String.valueOf(currentTime));

    }


    public void clearPicture() {
        screenView.clearCanvas();
    }
}
