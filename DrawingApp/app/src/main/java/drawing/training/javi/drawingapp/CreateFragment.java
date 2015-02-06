package drawing.training.javi.drawingapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;


public class CreateFragment extends Fragment {

    private ArrayAdapter<String> mListViewArrayAdapter;
    private ListView mListView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_create, container, false);

        mListViewArrayAdapter = new ArrayAdapter<>(getActivity(), R.layout.message);
        mListView= (ListView) rootView.findViewById(R.id.ListView);
        mListView.setAdapter(mListViewArrayAdapter);

        return rootView;
    }

    protected void newMessageToAdd(String msg) {
        if(mListViewArrayAdapter != null) {
            mListViewArrayAdapter.add(msg);
        }
    }

}
