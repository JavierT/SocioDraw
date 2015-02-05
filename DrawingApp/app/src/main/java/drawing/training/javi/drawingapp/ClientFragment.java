package drawing.training.javi.drawingapp;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Drawing App created by Javier Tresaco on 4/02/15.
 * ${PACKAGE_NAME}
 * Source code on:  https://github.com/JavierT/SocioDraw
 */
public class ClientFragment extends Fragment {

    private EditText mEditText;
    private ArrayAdapter<String> mListViewArrayAdapter;
    private ListView mListView;

    sendMessageListener mCallback;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_join, container, false);

        mListViewArrayAdapter = new ArrayAdapter<>(getActivity(), R.layout.message);
        mListView= (ListView) rootView.findViewById(R.id.ListView);
        mListView.setAdapter(mListViewArrayAdapter);

        mEditText = (EditText) rootView.findViewById(R.id.EditText);
        mEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_NULL && event.getAction() == KeyEvent.ACTION_UP) {

                    mCallback.sendMessage(view.getText().toString());

                }
                return true;
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
            mCallback = (sendMessageListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement sendMessage");
        }
    }

    public interface sendMessageListener {
        public void sendMessage(String name);
    }

    protected void newMessageToAdd(String msg) {
        if(mListViewArrayAdapter != null) {
            mListViewArrayAdapter.add(msg);
        }
    }

    protected void setText(String msg) {
        if(mEditText != null) {
            mEditText.setText(msg);
        }
    }

}