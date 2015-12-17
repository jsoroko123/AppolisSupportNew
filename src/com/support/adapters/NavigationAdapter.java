package com.support.adapters;

import java.util.List;

import com.example.appolissupport.R;
import com.support.objects.NavigationItem;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;


public class NavigationAdapter extends ArrayAdapter<NavigationItem> {
	 
    Context context;
    List<NavigationItem> drawerItemList;
    int layoutResID;

    public NavigationAdapter(Context context, int layoutResourceID,
                List<NavigationItem> listItems) {
          super(context, layoutResourceID, listItems);
          this.context = context;
          this.drawerItemList = listItems;
          this.layoutResID = layoutResourceID;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
          // TODO Auto-generated method stub

          DrawerItemHolder drawerHolder;
          View view = convertView;

          if (view == null) {
                LayoutInflater inflater = ((Activity) context).getLayoutInflater();
                drawerHolder = new DrawerItemHolder();

                view = inflater.inflate(layoutResID, parent, false);
                drawerHolder.ItemName = (TextView) view
                            .findViewById(R.id.textview_title);
                drawerHolder.icon = (ImageView) view.findViewById(R.id.imageview_icon);

                view.setTag(drawerHolder);

          } else {
                drawerHolder = (DrawerItemHolder) view.getTag();

          }

          NavigationItem dItem = (NavigationItem) this.drawerItemList.get(position);

          drawerHolder.icon.setImageDrawable(view.getResources().getDrawable(
                      dItem.getDrawableIcon()));
          drawerHolder.ItemName.setText(dItem.getStringTitle());

          return view;
    }

    private static class DrawerItemHolder {
          TextView ItemName;
          ImageView icon;
    }
}