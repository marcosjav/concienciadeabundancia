package com.bnvlab.concienciadeabundancia.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.bnvlab.concienciadeabundancia.R;
import com.bnvlab.concienciadeabundancia.adapters.FaqAdapter;
import com.bnvlab.concienciadeabundancia.auxiliaries.References;
import com.bnvlab.concienciadeabundancia.auxiliaries.Utils;
import com.bnvlab.concienciadeabundancia.clases.FAQItem;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by Marcos on 18/04/2017.
 */

public class FaqFragment extends Fragment {

    ExpandableListView epView;
    ArrayList<FAQItem> list;
    ExpandableListAdapter mAdapter;
    LayoutInflater inflater;
    ViewSwitcher viewSwitcher;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_faq, container, false);

        TextView title = (TextView) view.findViewById(R.id.textView);
        title.setTypeface(Utils.getTypeface(getContext()));

        this.inflater = inflater;
        epView = (ExpandableListView) view.findViewById(R.id.expandable_list_faq);
        viewSwitcher = (ViewSwitcher) view.findViewById(R.id.view_switcher);
        list = new ArrayList<>();

        getFAQ();

        epView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView arg0, View arg1,
                                        int groupPosition, long arg3) {
                if (groupPosition == 5) {

                }

                // Aqui podriamos cambiar si quisieramos el comportamiento de apertura y cierre de las listas explandibles mediante los metodos collapseGroup(int groupPos) y expandGroup(int groupPos)

                return false;
            }
        });

        epView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent,
                                        View v, int groupPosition, int childPosition,
                                        long id) {
                if (groupPosition == 0 && childPosition == 0) {

                }

                return false;
            }
        });

        return view;
    }

    private void getFAQ(){
        list = new ArrayList<>();
        FirebaseDatabase.getInstance()
                .getReference(References.REFERENCE)
                .child(References.FAQ)
                .orderByChild(References.FAQ_CHILD_INDEX)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot data : dataSnapshot.getChildren()){
                            FAQItem faqItem = data.getValue(FAQItem.class);
//                            for (int i = 0; i < 20; i++)
                            list.add(faqItem);
                        }
                        mAdapter = new FaqAdapter(getContext(), inflater, list);
                        epView.setAdapter(mAdapter);
                        viewSwitcher.showNext();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        viewSwitcher.showNext();
                    }
                });
    }
}
