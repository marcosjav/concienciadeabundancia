package com.bnvlab.concienciadeabundancia.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.bnvlab.concienciadeabundancia.R;
import com.bnvlab.concienciadeabundancia.clases.MessageItem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Marcos on 19/04/2017.
 */

public class MessageAdapter extends BaseExpandableListAdapter {
    // Sample data set. children[i] contains the children (String[]) for
    // groups[i].
    Context context;
    LayoutInflater inflater;
    ArrayList<MessageItem> list;
    SimpleDateFormat formatter = new SimpleDateFormat("hh:mm dd/MM/yyyy");

    public MessageAdapter(Context context, LayoutInflater inflater, ArrayList<MessageItem> list) {
        this.context = context;
        this.inflater = inflater;
        this.list = list;
    }

    public Object getChild(int groupPosition, int childPosition) {
        return list.get(groupPosition).getMessage();
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
        textView.setText(list.get(groupPosition).getMessage());
//        textView.setTypeface(Utils.getTypeface(context));
        return view;
    }

    public Object getGroup(int groupPosition) {
        return list.get(groupPosition).getTitle();
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
        textView.setText(list.get(groupPosition).getTitle());
        TextView time = (TextView) view.findViewById(R.id.time);
        String sTime = formatter.format(new Date(list.get(groupPosition).getTime()));
        time.setText(sTime);
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
