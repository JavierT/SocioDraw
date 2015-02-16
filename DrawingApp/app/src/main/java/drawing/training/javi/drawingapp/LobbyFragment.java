package drawing.training.javi.drawingapp;



import android.content.Context;
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


public class LobbyFragment extends Fragment {


    private PlayerArrayAdapter mListViewArrayAdapter;
    private ListView mListView;
    private ImageButton currPaint;
    //private int paintColor = 0xFF660000;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_lobby, container, false);

        TextView txt = (TextView) rootView.findViewById(R.id.txtLobby);
        txt.setTypeface(MainActivity.handwritingFont);
        txt = (TextView) rootView.findViewById(R.id.txtLobbyColor);
        txt.setTypeface(MainActivity.handwritingFont);
        txt = (TextView) rootView.findViewById(R.id.txtLobbyPlayer);
        txt.setTypeface(MainActivity.handwritingFont);

        Button btn = (Button) rootView.findViewById(R.id.btnLobbyExit);
        btn.setTypeface(MainActivity.handwritingFont);
        btn = (Button) rootView.findViewById(R.id.btnLobbyReady);
        btn.setTypeface(MainActivity.handwritingFont);

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

        mListView = (ListView) rootView.findViewById(R.id.lvPlayers);

        return rootView;
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

    protected void newPlayerToAdd(Player p) {

        if(mListViewArrayAdapter != null) {
            mListViewArrayAdapter.add(p);
        }
    }

    public void setPlayers(ArrayList<Player> playerList) {

        mListViewArrayAdapter = new PlayerArrayAdapter(getActivity(), playerList);

        mListView.setAdapter(mListViewArrayAdapter);

    }

    private class PlayerArrayAdapter extends ArrayAdapter<Player> {
        private final Context context;
        private ArrayList<Player> playerList = null;

        public PlayerArrayAdapter(Context context, ArrayList<Player> values) {
            super(context, R.layout.players, values);
            this.context = context;
            this.playerList = values;
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View rowView = inflater.inflate(R.layout.players, parent, false);

            TextView textView = (TextView) rowView.findViewById(R.id.txtPlayerName);
            ImageView imageView = (ImageView) rowView.findViewById(R.id.ivPlayerStatus);
            textView.setTypeface(MainActivity.handwritingFont);

            final Player p = playerList.get(position);
            if(p!= null) {
                textView.setText(p.name);
                if(p.ready)
                    imageView.setImageResource(R.mipmap.ic_ok);
                else
                    imageView.setImageResource(R.mipmap.ic_no);
            }
            return rowView;
        }

    }


}
