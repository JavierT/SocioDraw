package drawing.training.javi.drawingapp;

import android.app.Activity;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;



public class ScreenFragment extends Fragment {
    private DrawingView drawView;
    private Color currPaint;

    public ScreenFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_screen, container, false);

        drawView = (DrawingView)rootView.findViewById(R.id.drawing);


        return rootView;
    }
}
