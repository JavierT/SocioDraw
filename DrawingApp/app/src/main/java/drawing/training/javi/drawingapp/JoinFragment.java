package drawing.training.javi.drawingapp;



import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


public class JoinFragment extends Fragment {

    private ImageButton currPaint;
    private boolean mReadyState = false;
    //private int paintColor = 0xFF660000;
    private Button mReadyButton;

    private setPlayerReady mCallback;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_join, container, false);

        TextView txt = (TextView) rootView.findViewById(R.id.txtJoin);
        txt.setTypeface(MainActivity.handwritingFont);
        txt = (TextView) rootView.findViewById(R.id.txtLobbyColor);
        txt.setTypeface(MainActivity.handwritingFont);

        mReadyButton = (Button) rootView.findViewById(R.id.btnJoinReady);
        mReadyButton.setTypeface(MainActivity.handwritingFont);
        mReadyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mReadyState = !mReadyState;
                if(mReadyState) {
                    mReadyButton.setText(getString(R.string.join_Ready));
                    mReadyButton.setTextColor(Color.GREEN);
                }
                else { setNotReady(); }
                mCallback.setReady(mReadyState);
            }
        });

        // Color picker
        LinearLayout paintLayout = (LinearLayout)rootView.findViewById(R.id.paint_colors);
        currPaint = (ImageButton)paintLayout.getChildAt(0);
        currPaint.setImageDrawable(getResources().getDrawable(R.drawable.paint_pressed));


        View.OnClickListener colorPickerListener = new View.OnClickListener(){
            public void onClick(View v){
                paintClicked(v);
            }
        };
        rootView.findViewById(R.id.ibColor1).setOnClickListener(colorPickerListener);
        rootView.findViewById(R.id.ibColor2).setOnClickListener(colorPickerListener);
        rootView.findViewById(R.id.ibColor3).setOnClickListener(colorPickerListener);
        rootView.findViewById(R.id.ibColor4).setOnClickListener(colorPickerListener);
        rootView.findViewById(R.id.ibColor5).setOnClickListener(colorPickerListener);
        rootView.findViewById(R.id.ibColor6).setOnClickListener(colorPickerListener);


        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (setPlayerReady) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement setReady");
        }
    }

    private void paintClicked(View view) {
        if(view!=currPaint) {
            ImageButton imgView = (ImageButton)view;
            String color = view.getTag().toString();

            // TESTING
            Toast.makeText(getActivity(), "Color picked: " + color, Toast.LENGTH_LONG).show();

            imgView.setImageDrawable(getResources().getDrawable(R.drawable.paint_pressed));
            currPaint.setImageDrawable(getResources().getDrawable(R.drawable.paint));
            currPaint=(ImageButton)view;
        }
    }

    public void setNotReady() {
        mReadyState = false;
        mReadyButton.setText(getString(R.string.join_NotReady));
        mReadyButton.setTextColor(Color.RED);
    }

    public interface setPlayerReady {
        public void setReady(boolean ready);
    }





}
