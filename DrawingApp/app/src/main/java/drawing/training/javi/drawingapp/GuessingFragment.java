package drawing.training.javi.drawingapp;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class GuessingFragment extends Fragment {


    private ArrayList<String> mArticlesInSentence;
    private ListView mListView;
    private WordsArrayAdapter mListViewArrayAdapter;

    public GuessingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_guessing, container, false);

        TextView txt = (TextView) rootView.findViewById(R.id.txtGuessTitle);
        txt.setTypeface(MainActivity.handwritingFont);

        txt = (TextView) rootView.findViewById(R.id.txtGuessArticle);
        txt.setTypeface(MainActivity.handwritingFont);

        txt = (TextView) rootView.findViewById(R.id.etGuessWord);
        txt.setTypeface(MainActivity.handwritingFont);

        mArticlesInSentence = new ArrayList<>();
        mArticlesInSentence.add("The");
        mArticlesInSentence.add("is");
        mArticlesInSentence.add("under the");

        mListView = (ListView) rootView.findViewById(R.id.lvSentence);

        mListViewArrayAdapter = new WordsArrayAdapter(getActivity());

        mListView.setAdapter(mListViewArrayAdapter);

        return rootView;
    }

    private class WordsArrayAdapter extends ArrayAdapter<Word> {
        private final Context context;

        public WordsArrayAdapter(Context context) {
            super(context, R.layout.words);
            this.context = context;

        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View rowView = inflater.inflate(R.layout.words, parent, false);

            TextView textView = (TextView) rowView.findViewById(R.id.txtGuessArticle);
            EditText guessWord = (EditText) rowView.findViewById(R.id.etGuessWord);
            textView.setTypeface(MainActivity.handwritingFont);
            textView.setTextSize(24);

            final String article = mArticlesInSentence.get(position);
            textView.setText(article);
            return rowView;
        }


    }

    class Word {
        protected String article;
        protected String guessWord;
    }
}
