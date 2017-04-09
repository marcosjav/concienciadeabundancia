package com.bnvlab.concienciadeabundancia.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.bnvlab.concienciadeabundancia.MainActivity;
import com.bnvlab.concienciadeabundancia.R;
import com.bnvlab.concienciadeabundancia.adapters.QuizAdapter;
import com.bnvlab.concienciadeabundancia.clases.QuizItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by bort0 on 21/03/2017.
 */

public class QuizFragment extends Fragment {
    ArrayList<QuizItem> list;
    QuizAdapter adapter;
    View view;

    TextView tvTitle, tvSubTitle, tvModule, tvDescription;
    ViewSwitcher viewSwitcher;
    ListView listView;
    Button buttonOk;

    String quizId;

    public QuizFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_quiz, container, false);

        list = new ArrayList<>();
        adapter = new QuizAdapter(getContext(), R.layout.item_quiz_row, list);

        tvTitle = (TextView) view.findViewById(R.id.text_view_quiz_title);
        tvSubTitle = (TextView) view.findViewById(R.id.text_view_quiz_subtitle);
        tvModule = (TextView) view.findViewById(R.id.text_view_quiz_module);
        tvDescription = (TextView) view.findViewById(R.id.text_view_quiz_description);
        buttonOk = (Button) view.findViewById(R.id.button_quiz_ok);
        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendQuiz();
            }
        });

        listView = (ListView) view.findViewById(R.id.list_view_quiz);
        listView.setAdapter(adapter);

//        setListViewHeightBasedOnChildren(listView);

        viewSwitcher = (ViewSwitcher) view.findViewById(R.id.view_switcher_quiz_layout);

        Bundle bundle = this.getArguments();

        if (bundle != null) {
            quizId = bundle.getString("tag");
            if (!quizId.isEmpty())
                getQuiz();
        }

        this.view = view;
        return view;
    }

    private void getQuiz() {
        FirebaseDatabase.getInstance()
                .getReference(MainActivity.REFERENCE)
                .child(QuizItem.CHILD)
                .child(quizId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot data : dataSnapshot.getChildren()) {
                            if (data.getKey().equals("title"))
                                tvTitle.setText(data.getValue(String.class));
                            else if (data.getKey().equals("subtitle"))
                                tvSubTitle.setText(data.getValue(String.class));
                            else if (data.getKey().equals("module"))
                                tvModule.setText(data.getValue(String.class));
                            else if (data.getKey().equals("description"))
                                tvDescription.setText(data.getValue(String.class));
                            else {
                                QuizItem quizItem = new QuizItem(data.getValue(String.class));
                                list.add(quizItem);
                                adapter.notifyDataSetChanged();
                                setListViewHeightBasedOnChildren(listView);
//                                Toast.makeText(getContext(), quizItem.getQuiz(), Toast.LENGTH_SHORT).show();
                            }
                        }

                        showProgress(false);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        showProgress(false);
                    }
                });
    }

    private void showProgress(boolean show) {
        if (show)
            viewSwitcher.showPrevious();
        else
            viewSwitcher.showNext();
    }

    public static void setListViewHeightBasedOnChildren(ListView listView)
    {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight=0;
        View view = null;

        for (int i = 0; i < listAdapter.getCount(); i++)
        {
            view = listAdapter.getView(i, view, listView);

            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth,
                        ViewPager.LayoutParams.MATCH_PARENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();

        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + ((listView.getDividerHeight()) * (listAdapter.getCount()));

        listView.setLayoutParams(params);
        listView.requestLayout();

    }

    private void sendQuiz()
    {
        FirebaseDatabase.getInstance()
                .getReference(MainActivity.REFERENCE)
                .child("sent")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(quizId)
                .setValue(list);
    }
}
