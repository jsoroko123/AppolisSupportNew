
package com.support.adapters;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.example.appolissupport.MainActivity;
import com.example.appolissupport.R;
import com.support.objects.Details;
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;


public class DetailsAdapter extends ArrayAdapter<Details> {
	private Context context;
	private ArrayList<Details> listItemInfos;
	private int highResponse;


	public DetailsAdapter(Context context, ArrayList<Details> list) {
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
		if(null == listItemInfos){
			return 0;
		}
		
		return listItemInfos.size();
	}
	
	@Override
	public Details getItem(int position) {
		if(null == listItemInfos){
			return null;
		}
		return listItemInfos.get(position);
	}

	public void updateListReciver(ArrayList<Details> list){
		if(null != list){
			this.listItemInfos = new ArrayList<Details>();
			this.listItemInfos = list;
		}
	}
	
	private class ItemDetailHolder{
		TextView subject;
		TextView response;
		TextView name;
		TextView date;
		LinearLayout ll_details;
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
			convertView = inflater.inflate(R.layout.details_item, null);
			itemDetailHolder = new ItemDetailHolder();
			itemDetailHolder.ll_details = (LinearLayout) convertView.findViewById(R.id.ll_case_detail);
		
			itemDetailHolder.subject = (TextView) convertView.findViewById(R.id.tv_res);
//			itemDetailHolder.response = (TextView) convertView.findViewById(R.id.tv_status);
//			//itemDetailHolder.tvAssigned = (TextView) convertView.findViewById(R.id.tv_assignment);
//			itemDetailHolder.name = (TextView) convertView.findViewById(R.id.tv_client);
//			itemDetailHolder.date = (TextView) convertView.findViewById(R.id.tv_issue);
			convertView.setTag(itemDetailHolder);
		} else {
			itemDetailHolder = (ItemDetailHolder) convertView.getTag();
		}
			Details item = getItem(position);
			if(null != item){
//				itemDetailHolder.subject.setText(item.getSubject());
				if(item.getResponse().contains("SupportComment")){
					itemDetailHolder.ll_details.setBackground(context.getResources().getDrawable(R.drawable.appolis_blue));
					params.setMargins(65, 0, 25, 30);
					itemDetailHolder.ll_details.setLayoutParams(params);
					
				} else if(item.getResponse().contains("ClientComment")) {
						itemDetailHolder.ll_details.setBackground(context.getResources().getDrawable(R.drawable.appolis_orange));
						params.setMargins(25, 0, 65, 30);
						itemDetailHolder.ll_details.setLayoutParams(params);
				} else if(item.getResponse().contains("MainComment")) {
				itemDetailHolder.ll_details.setBackground(context.getResources().getDrawable(R.drawable.appolis_gray));
				params.setMargins(25, 40, 25, 50);
				itemDetailHolder.ll_details.setLayoutParams(params);
				}

				String resp = item.getResponse().replace("<br/>", "\n");

				if(resp.contains("Issue:")){
					resp.replace("Issue: ", "Issue: "+item.getSubject()+"\n");
				}
			
				itemDetailHolder.subject.setText(resp.replaceAll("\\<.*?>","").replace("||", "").replace("&nbsp;", " "));
//				itemDetailHolder.name.setText(item.getName());
//				itemDetailHolder.date.setText(item.getDateCreated());

			}
			
			if(item.getResponse().contains("SupportComments"))
			{
				
			}

		if(item.getResponseID() >= highResponse) {
			MainActivity.spm2.saveInt("HighResponse", Integer.valueOf(item.getResponseID()));
		}


		highResponse = Integer.valueOf(item.getResponseID());
			
			return convertView;	
	}
}
