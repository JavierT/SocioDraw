package drawing.training.javi.drawingapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;



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




}
