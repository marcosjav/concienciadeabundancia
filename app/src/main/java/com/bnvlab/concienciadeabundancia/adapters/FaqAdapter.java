package com.bnvlab.concienciadeabundancia.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.bnvlab.concienciadeabundancia.R;
import com.bnvlab.concienciadeabundancia.clases.FAQItem;

import java.util.ArrayList;

/**
 * Created by Marcos on 19/04/2017.
 */

public class FaqAdapter extends BaseExpandableListAdapter {
    // Sample data set. children[i] contains the children (String[]) for
    // groups[i].
    Context context;
    LayoutInflater inflater;
    ArrayList<FAQItem> list;

    public FaqAdapter(Context context, LayoutInflater inflater, ArrayList<FAQItem> list) {
        this.context = context;
        this.inflater = inflater;
        this.list = list;
    }

    public Object getChild(int groupPosition, int childPosition) {
        return list.get(groupPosition).getAnswer();
    }

    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    public int getChildrenCount(int groupPosition) {
        return 1;
    }

    public View getChildView(int groupPosition, int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        View view = inflater.inflate(R.layout.item_faq_answer,parent,false);

        TextView textView = (TextView) view.findViewById(R.id.text);
        textView.setText(list.get(groupPosition).getAnswer());
//        textView.setTypeface(Utils.getTypeface(context));
        return view;
    }

    public Object getGroup(int groupPosition) {
        return list.get(groupPosition).getQuestion();
    }

    public int getGroupCount() {
        return list.size();
    }

    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        View view = inflater.inflate(R.layout.item_faq_question,parent,false);
        TextView textView = (TextView) view.findViewById(R.id.text);
        textView.setText(list.get(groupPosition).getQuestion());
//        textView.setTypeface(Utils.getTypeface(context));
        return view;
    }

    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public boolean hasStableIds() {
        return true;
    }

}
