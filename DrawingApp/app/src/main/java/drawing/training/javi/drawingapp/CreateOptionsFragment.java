package drawing.training.javi.drawingapp;



import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


public class CreateOptionsFragment extends Fragment {


    private String mUsername;

//    public CreateOptionsFragment (String username) {
//        CreateOptionsFragment fragment = new CreateOptionsFragment();
//        Bundle args = new Bundle();
//        args.putString(getString(R.string.username), username);
//        fragment.setArguments(args);
//    }

    public CreateOptionsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mUsername = getArguments().getString(getString(R.string.username));

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_create_options, container, false);

        TextView txtWelcome = (TextView) rootView.findViewById(R.id.txtCreateOptions);
        txtWelcome.setTypeface(MainActivity.handwritingFont);
        TextView txtScreenDesc = (TextView) rootView.findViewById(R.id.txtCreateScreenDesc);
        txtScreenDesc.setTypeface(MainActivity.handwritingFont);
        TextView txtPlayerDesc = (TextView) rootView.findViewById(R.id.txtCreatePlayerDesc);
        txtPlayerDesc.setTypeface(MainActivity.handwritingFont);

        // Event handlers for the buttons
        Button btnScreen = (Button) rootView.findViewById(R.id.btnCreateAsScreen);
        btnScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createGameAsScreen();
            }
        });
        btnScreen.setTypeface((MainActivity.handwritingFont));

        Button btnPlayer = (Button) rootView.findViewById(R.id.btnCreateAsPlayer);
        btnPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createGameAsPlayer();
            }
        });
        btnPlayer.setTypeface((MainActivity.handwritingFont));

        return rootView;

    }

    // TODO
    // Same intent to same activity but different parameters??


    private void createGameAsPlayer() {
        Intent myIntent = new Intent(getActivity(), CreateActivity.class);
        myIntent.putExtra(getString(R.string.username), mUsername); //Optional parameters
        this.startActivity(myIntent);
    }

    private void createGameAsScreen() {
        Intent myIntent = new Intent(getActivity(), CreateActivity.class);
        myIntent.putExtra(getString(R.string.username), mUsername); //Optional parameters
        this.startActivity(myIntent);
    }
}
