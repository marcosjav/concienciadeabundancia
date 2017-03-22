package com.bnvlab.concienciadeabundancia.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.Switch;

import com.bnvlab.concienciadeabundancia.R;

/**
 * Created by bort0 on 21/03/2017.
 */

public class QuizFragment extends Fragment {

    private ListView mListView;

    public QuizFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_quiz, container, false);

        mListView = (ListView) view.findViewById(R.id.quiz_list_view);
        Switch switch_text = (Switch) view.findViewById(R.id.switch1);
        view.findViewById(R.id.quiz_list_view);
        mListView.setDivider(null);


        String[] quiz = {"te gusto el cutso?",
                    "Entendiste todo el contenido?",
                    "lo recomendarias a alguien?"};

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_checked, quiz);
        //adapter.add("Hola.");

        mListView.setAdapter(adapter);

        return view;
    }


}
