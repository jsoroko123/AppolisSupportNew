
package com.support.adapters;

import java.util.ArrayList;

import com.example.appolissupport.R;
import com.support.objects.Attachments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;



public class AttachAdapter extends ArrayAdapter<Attachments> {
    private Context context;
    private ArrayList<Attachments> listItemInfos;


    public AttachAdapter(Context context, ArrayList<Attachments> list) {
        super(context, R.layout.details_item);
        this.context = context;
        this.listItemInfos = list;
    }



    @Override
    public int getCount() {
        if(null == listItemInfos){
            return 0;
        }

        return listItemInfos.size();
    }



    @Override
    public Attachments getItem(int position) {
        if(null == listItemInfos){
            return null;
        }
        return listItemInfos.get(position);
    }


    /**
     * update list
     * @param list
     */
    public void updateListReciver(ArrayList<Attachments> list){
        if(null != list){
            this.listItemInfos = new ArrayList<Attachments>();
            this.listItemInfos = list;
        }
    }

    private class ItemDetailHolder{
        TextView filename;
        TextView date;
        LinearLayout ll_attach;

    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ItemDetailHolder itemDetailHolder;
        String response;
        LayoutParams params = new LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT
        );


        if(null == convertView){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.attach_item, null);
            itemDetailHolder = new ItemDetailHolder();
            itemDetailHolder.ll_attach = (LinearLayout) convertView.findViewById(R.id.ll_attach);

            itemDetailHolder.filename = (TextView) convertView.findViewById(R.id.tv_filename);
            itemDetailHolder.date = (TextView) convertView.findViewById(R.id.tv_date3);
//			itemDetailHolder.response = (TextView) convertView.findViewById(R.id.tv_status);
//			//itemDetailHolder.tvAssigned = (TextView) convertView.findViewById(R.id.tv_assignment);
//			itemDetailHolder.name = (TextView) convertView.findViewById(R.id.tv_client);
//			itemDetailHolder.date = (TextView) convertView.findViewById(R.id.tv_issue);
            convertView.setTag(itemDetailHolder);
        } else {
            itemDetailHolder = (ItemDetailHolder) convertView.getTag();
        }
        Attachments item = getItem(position);
        if(null != item){
                int ind = item.getFileName().indexOf('_');
                String file = item.getFileName().substring(ind + 1, item.getFileName().length());
				itemDetailHolder.filename.setText(file);
                itemDetailHolder.filename.setTag(item.getFileName());
				itemDetailHolder.date.setText(item.getDateName());

            if (item.getFileName()=="Add Attachment"){
                itemDetailHolder.ll_attach.setBackgroundColor(context.getResources().getColor(R.color.OrangeAppolis));

            }


        }

        return convertView;
    }
}
