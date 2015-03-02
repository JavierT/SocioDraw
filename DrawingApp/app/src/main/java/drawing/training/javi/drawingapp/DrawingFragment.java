package drawing.training.javi.drawingapp;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class DrawingFragment extends Fragment {

    private DrawingView drawView;
    private Color currPaint;

    public DrawingFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_drawing, container, false);

//        //drawView = new DrawingView(container.getContext(),R.id.drawing);
        drawView = (DrawingView)rootView.findViewById(R.id.drawing);





        return rootView;
    }

}