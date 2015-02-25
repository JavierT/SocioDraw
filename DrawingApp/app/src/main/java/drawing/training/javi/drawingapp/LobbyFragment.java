package drawing.training.javi.drawingapp;


import android.app.Activity;
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
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;


public class LobbyFragment extends Fragment {


    private PlayerArrayAdapter mListViewArrayAdapter;
    private ListView mListView;

    private Button mReadyButton;
    private ArrayList<Player> mPlayersConnected;

    private setStartGame mCallback;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_lobby, container, false);

        TextView txt = (TextView) rootView.findViewById(R.id.txtLobby);
        txt.setTypeface(MainActivity.handwritingFont);
        txt = (TextView) rootView.findViewById(R.id.txtLobbyPlayer);
        txt.setTypeface(MainActivity.handwritingFont);

        mReadyButton = (Button) rootView.findViewById(R.id.btnLobbyStart);
        mReadyButton.setTypeface(MainActivity.handwritingFont);
        mReadyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mCallback.startGame();
            }
        });


        mListView = (ListView) rootView.findViewById(R.id.lvPlayers);

        mPlayersConnected = new ArrayList<>();
        mListViewArrayAdapter = new PlayerArrayAdapter(getActivity());

        mListView.setAdapter(mListViewArrayAdapter);
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (setStartGame) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement setStartGame");
        }
    }


    public boolean setReady(String name, boolean status) {
        int position = 0;
        for (int i=0; i<mPlayersConnected.size(); i++)
        {
            if(mPlayersConnected.get(i).name.equals(name))
                position = i;

        }
        if(position > mListViewArrayAdapter.getCount())
        {
            // Out of bounds
            return false;
        }
        else {
            Player p = mListViewArrayAdapter.getItem(position);
            if(p!= null) {
                p.ready = status;
                mListViewArrayAdapter.notifyDataSetChanged();
                return true;
            }
        }
        return false;
    }

    public interface setStartGame {
        public void startGame();
    }

    public void newPlayerToAdd(Player p) {
        mPlayersConnected.add(p);
        mListViewArrayAdapter.add(p);
        mListViewArrayAdapter.notifyDataSetChanged();
    }

    private class PlayerArrayAdapter extends ArrayAdapter<Player> {
        private final Context context;
        //private ArrayList<Player> playerList = null;

        public PlayerArrayAdapter(Context context) {
            //super(context, R.layout.players, values);
            super(context, R.layout.players);
            this.context = context;
            //this.playerList = values;
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View rowView = inflater.inflate(R.layout.players, parent, false);

            TextView textView = (TextView) rowView.findViewById(R.id.txtPlayerName);
            ImageView imageView = (ImageView) rowView.findViewById(R.id.ivPlayerStatus);
            textView.setTypeface(MainActivity.handwritingFont);

            final Player p = mPlayersConnected.get(position);
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
