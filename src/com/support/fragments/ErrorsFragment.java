package com.support.fragments;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.support.main.MainActivity;
import com.example.appolissupport.R;
import com.support.adapters.ExpandableListAdapter;
import com.support.objects.ErrorChild;
import com.support.objects.ErrorParent;
import com.support.utilities.Constants;
import com.support.utilities.SharedPreferenceManager;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ErrorsFragment extends Fragment {

	private ExpandableListView lvErrors;
	private ExpandableListAdapter listAdapter;
	private ArrayList<ErrorChild> childList;
	private ArrayList<ErrorParent> theParentList = new ArrayList<>();
	private ArrayList<String> listClients = new ArrayList<String>();

	private Spinner spinner;
	private TextView tvErrorClient;
	private static TextView tvNoErrors;

	private final String METHOD_NAME = "ListClientErrors";
	private final String SOAP_ACTION = Constants.NAMESPACE+METHOD_NAME;
	private final String METHOD_NAME2 = "ListClients";
	private final String SOAP_ACTION2 = Constants.NAMESPACE+METHOD_NAME2;
	private final String METHOD_NAME6 = "ListClientSites";
	private final String SOAP_ACTION6 = Constants.NAMESPACE+METHOD_NAME6;
	private SharedPreferenceManager spm;

	public ErrorsFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		MainActivity.FragPageTitle = "Integration Errors";
		View rootView = inflater.inflate(R.layout.fragment_errors, container,
				false);
		spm = new SharedPreferenceManager(getActivity());
		tvNoErrors = (TextView) rootView.findViewById(R.id.tvNoErrors);
		lvErrors = (ExpandableListView) rootView.findViewById(R.id.attach_list);
		lvErrors.setChoiceMode(ExpandableListView.CHOICE_MODE_SINGLE);
		lvErrors.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
			int previousGroup = -1;

			@Override
			public void onGroupExpand(int groupPosition) {
				if(groupPosition != previousGroup)
					lvErrors.collapseGroup(previousGroup);
				previousGroup = groupPosition;
			}
		});

		tvErrorClient=(TextView)rootView.findViewById(R.id.tvErrorClient);
		if(spm.getBoolean("IsSupport", false)) {
			tvErrorClient.setText("Client:");
		}else{
			tvErrorClient.setText("Environment:");
		}
		spinner=(Spinner)rootView.findViewById(R.id.spinnerErrorClients);
		spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
				String client;
				if (listClients.get(position).contains("Select Client")){
					client = "Support";
				} else{
					client = listClients.get(position);
				}

				final ListErrors error = new ListErrors(getActivity(), client);
				error.execute();
			}


			@Override
			public void onNothingSelected(AdapterView<?> parentView) {
				// your code here
			}

		});
		getActivity().getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        getActivity().getActionBar().setDisplayShowTitleEnabled(true);
		getActivity().getActionBar().setTitle(MainActivity.FragPageTitle);
		final ListClients clients = new ListClients(getActivity());
		clients.execute();
		return rootView;
	}



	private class ListErrors extends AsyncTask<String, Void, Void> {

		Context context;
		ProgressDialog progressDialog;
		String client;

		public ListErrors(Context mContext, String mClient) {
			this.context = mContext;
			this.client = mClient;
		}

		@Override
		protected void onPreExecute() {

			super.onPreExecute();
			if (!isCancelled()) {
				progressDialog = new ProgressDialog(context);
				progressDialog.setMessage("Loading...");
				progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {

					@Override
					public void onCancel(DialogInterface dialog) {
						cancel(true);
					}
				});
				progressDialog.setCanceledOnTouchOutside(false);
				progressDialog.setCancelable(false);
				progressDialog.show();
				theParentList.clear();
			}

		}

		@Override
		protected Void doInBackground(String... params) {
			//Create request
			SoapObject request = new SoapObject(Constants.NAMESPACE, METHOD_NAME);

			PropertyInfo supportCasesPI = new PropertyInfo();
			supportCasesPI.setName("clientName");
			supportCasesPI.setValue(client);
			supportCasesPI.setType(String.class);
			request.addProperty(supportCasesPI);

			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);
			envelope.dotNet = true;
			//Set output SOAP object
			envelope.setOutputSoapObject(request);
			//Create HTTP call object
			HttpTransportSE androidHttpTransport = new HttpTransportSE(Constants.URL);

			try {
				androidHttpTransport.call(SOAP_ACTION, envelope);
				SoapObject response = (SoapObject) envelope.getResponse();
				for (int i = 0; i < response.getPropertyCount(); i++) {

					Object property = response.getProperty(i);
					SoapObject info = (SoapObject) property;
					String client = info.getProperty("ClientName").toString().trim();
					String object = info.getProperty("WoWObject").toString().trim();
					String objectID = info.getProperty("WoWObjectID").toString().trim();
					String objectNumber = info.getProperty("WoWObjectNumber").toString().trim();
					String description = info.getProperty("Desc").toString().trim();
					String date = info.getProperty("DateCreated").toString().trim();
					Date date2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS").parse(date);
					String formattedDate = new SimpleDateFormat("MM/dd/yyyy hh:mm a").format(date2);
					boolean rez = Boolean.valueOf(info.getProperty("ResolutionExists").toString().trim());

					ErrorChild child = new ErrorChild(description);
					child.setErrorDesc(description);
					childList = new ArrayList<ErrorChild>();
					childList.add(child);

					ErrorParent parent = new ErrorParent(client, object, objectID, objectNumber, formattedDate, rez, childList);
					parent.setClientName(client);
					parent.setObject(object);
					parent.setObjectID(objectID);
					parent.setObjectNumber(objectNumber);
					parent.setDateCreated(formattedDate);
					parent.setHasResolution(rez);
					theParentList.add(parent);


				}


			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			if (null != progressDialog && (progressDialog.isShowing())) {
				progressDialog.dismiss();
			}
			listAdapter = new ExpandableListAdapter(getActivity(), theParentList);
			listAdapter.notifyDataSetChanged();
			lvErrors.setAdapter(listAdapter);
			if(lvErrors.getCount() <= 0){
				tvNoErrors.setVisibility(View.VISIBLE);
			} else{
				tvNoErrors.setVisibility(View.GONE);
			}

		}

	}

		private class ListClients extends AsyncTask<String, Void, Void> {

			Context context;
			ProgressDialog progressDialog;

			public ListClients(Context mContext){
				this.context = mContext;
			}

			@Override
			protected void onPreExecute() {
				listClients.clear();
				super.onPreExecute();
				if(!isCancelled()){
					progressDialog = new ProgressDialog(context);
					progressDialog.setMessage("Loading...");
					progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {

						@Override
						public void onCancel(DialogInterface dialog) {
							cancel(true);
						}
					});
					progressDialog.setCanceledOnTouchOutside(false);
					progressDialog.setCancelable(false);
					progressDialog.show();
				}
			}

			@Override
			protected Void doInBackground(String... params) {
				try {
					if(spm.getBoolean("IsSupport", false)) {

						SoapObject request = new SoapObject(Constants.NAMESPACE, METHOD_NAME2);
						SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
								SoapEnvelope.VER11);
						envelope.dotNet = true;
						//Set output SOAP object
						envelope.setOutputSoapObject(request);
						//Create HTTP call object
						HttpTransportSE androidHttpTransport = new HttpTransportSE(Constants.URL);


						//Fill Client List
						androidHttpTransport.call(SOAP_ACTION2, envelope);
						SoapObject response = (SoapObject) envelope.getResponse();
						for (int i = 0; i < response.getPropertyCount(); i++) {

							Object property = response.getProperty(i);
							SoapObject info = (SoapObject) property;
							listClients.add(info.getProperty("ClientName").toString().trim());
						}

						listClients.add(0, "Select Client");
					} else {

						SoapObject request = new SoapObject(Constants.NAMESPACE, METHOD_NAME6);
						PropertyInfo supportCasesPI = new PropertyInfo();
						supportCasesPI.setName("clientName");
						supportCasesPI.setValue(spm.getString("ClientName",""));
						supportCasesPI.setType(String.class);
						request.addProperty(supportCasesPI);
						SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
								SoapEnvelope.VER11);
						envelope.dotNet = true;
						//Set output SOAP object
						envelope.setOutputSoapObject(request);
						//Create HTTP call object
						HttpTransportSE androidHttpTransport = new HttpTransportSE(Constants.URL);


						//Fill Client List
						androidHttpTransport.call(SOAP_ACTION6, envelope);
						SoapObject response = (SoapObject) envelope.getResponse();
						for (int i = 0; i < response.getPropertyCount(); i++) {

							Object property = response.getProperty(i);
							SoapObject info = (SoapObject) property;
							listClients.add(info.getProperty("ClientName").toString().trim());
						}
					}

				} catch (Exception ex) {
					ex.printStackTrace();
				}



				return null;
			}

			@Override
			protected void onPostExecute(Void result) {

				if(null != progressDialog && (progressDialog.isShowing())){
					progressDialog.dismiss();
				}
				ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_item, listClients);
				spinnerArrayAdapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );
				spinner.setAdapter(spinnerArrayAdapter);
			}
		}


	}
	
