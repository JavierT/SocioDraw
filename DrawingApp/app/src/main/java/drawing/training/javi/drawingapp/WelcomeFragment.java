package drawing.training.javi.drawingapp;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by javi on 28/01/15.
 */
public class WelcomeFragment extends Fragment {

    saveUsername mCallback;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_welcome, container, false);

        Button btn = (Button) rootView.findViewById(R.id.btnContinue);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                continueClicked(v);
            }
        });


        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (saveUsername) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement saveUsernameAndContinue");
        }
    }


    public interface saveUsername {
        public void saveUsernameAndContinue(String name);
    }

    private void continueClicked(View v) {

        EditText eUsername = (EditText) getActivity().findViewById(R.id.etxtName);
        String username = eUsername.getText().toString();
        if(!username.isEmpty()) {
            mCallback.saveUsernameAndContinue(username);

        }
    }
}
