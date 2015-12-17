package com.support.adapters;

import android.graphics.Color;
import android.widget.LinearLayout;
import android.widget.TextView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import com.example.appolissupport.R;

import java.util.ArrayList;

public class StatusAdapter extends ArrayAdapter<String> {
    private Context context;
    private ArrayList<String> listItemInfos;


    public StatusAdapter(Context context, ArrayList<String> list) {
        super(context, R.layout.details_item);
        this.context = context;
        this.listItemInfos = list;
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }

    @Override
    public int getCount() {
        if (null == listItemInfos) {
            return 0;
        }

        return listItemInfos.size();
    }

    @Override
    public String getItem(int position) {
        if (null == listItemInfos) {
            return null;
        }
        return listItemInfos.get(position);
    }

    public void updateListReciver(ArrayList<String> list) {
        if (null != list) {
            this.listItemInfos = new ArrayList<String>();
            this.listItemInfos = list;
        }
    }

    private class ItemDetailHolder {
        TextView status;
        LinearLayout ll_details;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ItemDetailHolder itemDetailHolder;
        View row = convertView;
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );


        if (null == convertView) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.status_item, null);
            itemDetailHolder = new ItemDetailHolder();
            itemDetailHolder.ll_details = (LinearLayout) convertView.findViewById(R.id.ll_case_status);

            itemDetailHolder.status = (TextView) convertView.findViewById(R.id.tv_status);
            convertView.setTag(itemDetailHolder);
        } else {
            itemDetailHolder = (ItemDetailHolder) convertView.getTag();
        }
        String item = listItemInfos.get(position);

        itemDetailHolder.status.setText(item);

        if(item == "WCA"){
            itemDetailHolder.ll_details.setBackground(context.getResources().getDrawable(R.drawable.appolis_blue));
            params.setMargins(65, 0, 25, 30);
            itemDetailHolder.ll_details.setLayoutParams(params);
        }





            return convertView;

        }

}
