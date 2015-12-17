package com.support.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.database.DataSetObserver;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.appolissupport.R;
import com.support.objects.ErrorChild;
import com.support.objects.ErrorParent;

public class ExpandableListAdapter extends BaseExpandableListAdapter {

    private Context context;
    private ArrayList<ErrorParent> parentList;
    private ArrayList<ErrorParent> originalList;

    public ExpandableListAdapter(Context context, ArrayList<ErrorParent> mParentList) {
        this.context = context;
        this.parentList = new ArrayList<>();
        this.parentList.addAll(mParentList);
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
        super.registerDataSetObserver(observer);
    }

    public Object getGroup(int groupPosition) {
        return parentList.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return parentList.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        ArrayList<ErrorChild> childList = parentList.get(groupPosition).getChildList();
        return childList.get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        if (childPosition == 0)
            return 0;

        return getChild(groupPosition, childPosition).hashCode();
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
                             View view, ViewGroup parent) {

        ErrorChild child = (ErrorChild) getChild(groupPosition, childPosition);
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = layoutInflater.inflate(R.layout.child_item, null);

        TextView tvDesc = (TextView) view.findViewById(R.id.tvErrorDesc);
        tvDesc.setText(child.getErrorDesc());

        return view;
    }

    @Override
    public int getChildrenCount(int groupPosition) {

        ArrayList<ErrorChild> ErrorList = parentList.get(groupPosition).getChildList();
        return ErrorList.size();

    }


    @Override
    public View getGroupView(final int groupPosition, boolean isLastChild, View view,
                             ViewGroup parent) {

        final ErrorParent parentRow = (ErrorParent) getGroup(groupPosition);
        if (view == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.group_item, null);
        }

        TextView tvCLient = (TextView) view.findViewById(R.id.tvErrorClientName);
        tvCLient.setText(parentRow.getClientName().trim());
        TextView tvDate = (TextView) view.findViewById(R.id.tvErrorDate);
        tvDate.setText(parentRow.getDateCreated().trim());
        TextView tvObject = (TextView) view.findViewById(R.id.tvErrorObject);
        tvObject.setText(parentRow.getObject().trim());
        TextView tvObjectID = (TextView) view.findViewById(R.id.tvErrorObjectID);
        tvObjectID.setText(parentRow.getObjectID().trim());
        TextView tvObjectNumber = (TextView) view.findViewById(R.id.tvErrorObjectNumber);
        tvObjectNumber.setText(parentRow.getObjectNumber().trim());


        return view;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }



    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}