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
import android.widget.TextView;

/**
 * Drawing App created by Javier Tresaco on 28/01/15.
 * ${PACKAGE_NAME}
 * Source code on:  https://github.com/JavierT/SocioDraw
 */
public class WelcomeFragment extends Fragment {

    saveUsername mCallback;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_welcome, container, false);

        Button btn = (Button) rootView.findViewById(R.id.btnContinue);
        btn.setTypeface(MainActivity.handwritingFont);

        TextView txtName = (TextView) rootView.findViewById(R.id.txtName);
        txtName.setTypeface(MainActivity.handwritingFont);

        EditText etxtName = (EditText) rootView.findViewById(R.id.etxtName);
        etxtName.setTypeface(MainActivity.handwritingFont);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText eUsername = (EditText) getActivity().findViewById(R.id.etxtName);
                String username = eUsername.getText().toString();
                if(!username.isEmpty()) {
                    mCallback.saveUsernameAndContinue(username);

                }
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

}
