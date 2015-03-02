package drawing.training.javi.drawingapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class DrawingFragment extends Fragment {

    private DrawingView drawView;

    public DrawingFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_drawing, container, false);
        Bundle args = getArguments();
        int paint = args.getInt(Constants.ARGS_PAINT, 0xFF660000);

        drawView = (DrawingView)rootView.findViewById(R.id.drawing);
        drawView.setCallback(getActivity());
        drawView.setPaint(paint);
        return rootView;
    }

}