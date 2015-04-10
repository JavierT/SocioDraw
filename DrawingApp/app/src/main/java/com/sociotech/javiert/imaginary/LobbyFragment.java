package com.sociotech.javiert.imaginary;


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
//        txt = (TextView) rootView.findViewById(R.id.txtLobbyPlayer);
//        txt.setTypeface(MainActivity.handwritingFont);

        mReadyButton = (Button) rootView.findViewById(R.id.btnLobbyStart);
        mReadyButton.setTypeface(MainActivity.handwritingFont);
        mReadyButton.setTextColor(Color.RED);
        mReadyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // True as parameter to say that it's the first round
                mCallback.startGame(true);
            }
        });
        mReadyButton.setClickable(false);

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

    /**
     * Sets that player with the new status (ready / not ready)
     * @param name of the player
     * @param status new status
     * @return true if it was sucessful
     */
    public boolean setReady(String name, boolean status) {

        int position = getPlayer(name);
        if(position > mListViewArrayAdapter.getCount())
        {
            // Out of bounds
            return false;
        }
        else {
                mPlayersConnected.get(position).ready = status;
                mListViewArrayAdapter.getItem(position).ready = status;
                mListViewArrayAdapter.notifyDataSetChanged();
                return true;
        }
    }

    /**
     * Remove the player with the given name from the list view
     * @param name of the player
     */
    public void removePlayer(String name) {
        int pos = getPlayer(name);
        mListViewArrayAdapter.remove(mPlayersConnected.get(pos));
        mPlayersConnected.remove(pos);
        mListViewArrayAdapter.notifyDataSetChanged();
    }

    /**
     * If all the players are ready, the button is enabled
     * Otherwise, is disabled and put in red.
     * @param status:
     */
    public void updateStartGameButton(boolean status) {
        mReadyButton.setClickable(status);
        if(!status)
            mReadyButton.setTextColor(Color.RED);
        else
            mReadyButton.setTextColor(Color.parseColor("#006400"));
    }


    /**
     * Interface to communicate with the activity when the start
     * button has been clicked
     */
    public interface setStartGame {
        public void startGame(boolean isFirstRound);
    }

    /**
     * Adds a new player to the list view
     * @param p: new player
     */
    public void newPlayerToAdd(Player p) {
        mPlayersConnected.add(p);
        mListViewArrayAdapter.add(p);
        mListViewArrayAdapter.notifyDataSetChanged();
    }

    /**
     * Update the color chosed by the player given in name
     * @param name: of the player
     * @param color selected
     * @return if the process was successful
     */
    public boolean updatePlayerColor(String name, String color) {
        int position = getPlayer(name);
        if(position > mListViewArrayAdapter.getCount())
        {
            // Out of bounds
            return false;
        }
        else {
            mPlayersConnected.get(position).color = color;
            mListViewArrayAdapter.notifyDataSetChanged();
            return true;
        }
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
            textView.setTextSize(18);

            final Player p = mPlayersConnected.get(position);
            textView.setTextColor(Color.parseColor(p.color));
            textView.setText(p.name);
            if(p.ready)
                imageView.setImageResource(R.mipmap.ic_ok);
            else
                imageView.setImageResource(R.mipmap.ic_no);
            return rowView;
        }


    }

    private int getPlayer(String name) {
        int position = 0;
        for (int i=0; i<mPlayersConnected.size(); i++)
        {
            if(mPlayersConnected.get(i).name.equals(name))
                position = i;

        }
        if(position > mPlayersConnected.size())
        {
            // Out of bounds
            return -1;
        }
        else {
            return position;
        }
    }


}
