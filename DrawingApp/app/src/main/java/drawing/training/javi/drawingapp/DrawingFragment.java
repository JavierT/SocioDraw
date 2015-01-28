package drawing.training.javi.drawingapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;

public class DrawingFragment extends Fragment {

    private DrawingView drawView;
    private ImageButton currPaint;

    public DrawingFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        //drawView = new DrawingView(container.getContext(),R.id.drawing);
        drawView = (DrawingView)rootView.findViewById(R.id.drawing);

        LinearLayout paintLayout = (LinearLayout)rootView.findViewById(R.id.paint_colors);
        currPaint = (ImageButton)paintLayout.getChildAt(0);
        currPaint.setImageDrawable(getResources().getDrawable(R.drawable.paint_pressed));


        ImageButton btn = (ImageButton) rootView.findViewById(R.id.first_color);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paintClicked(v);
            }
        });



        return rootView;
    }




    public void paintClicked(View view) {
        // use chosen color
        if(view!=currPaint) {
            ImageButton imgView = (ImageButton)view;
            String color = view.getTag().toString();

            drawView.setColor(color);

            imgView.setImageDrawable(getResources().getDrawable(R.drawable.paint_pressed));
            currPaint.setImageDrawable(getResources().getDrawable(R.drawable.paint));
            currPaint=(ImageButton)view;
        }
    }
}