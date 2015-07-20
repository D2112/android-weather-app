package com.d2112.weather.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;
import com.d2112.weather.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TextExpandableListAdapter extends BaseExpandableListAdapter {
    private Context context;
    private List<String> groups;
    private Map<String, List<String>> childListByGroupMap;

    public TextExpandableListAdapter(Context context, Map<String, List<String>> childListByGroupMap) {
        this.context = context;
        this.childListByGroupMap = childListByGroupMap;
        groups = new ArrayList<>(childListByGroupMap.keySet());
    }

    @Override
    public String getGroup(int groupPosition) {
        return groups.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return groups.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        if (convertView == null) {
            //first view creating
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_group, null);
        }
        TextView groupView = (TextView) convertView.findViewById(R.id.groupTextView); //get inner text view
        groupView.setText(getGroup(groupPosition));
        return convertView;
    }

    @Override
    public String getChild(int groupPosition, int childPosition) {
        String group = getGroup(groupPosition); //get group name
        List<String> childList = childListByGroupMap.get(group); // get child list by group name
        return childList.get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_child, null);
        }
        TextView childView = (TextView) convertView.findViewById(R.id.childTextView); //get inner text view
        childView.setText(getChild(groupPosition, childPosition));
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return getChildListByGroupPosition(groupPosition).size();
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    private List<String> getChildListByGroupPosition(int groupPosition) {
        return childListByGroupMap.get(getGroup(groupPosition));
    }
}