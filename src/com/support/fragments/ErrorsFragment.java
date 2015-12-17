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

import com.support.main.MainActivity;
import com.example.appolissupport.R;
import com.support.adapters.ExpandableListAdapter;
import com.support.objects.ErrorChild;
import com.support.objects.ErrorParent;
import com.support.utilities.Constants;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;

public class ErrorsFragment extends Fragment {

	private ExpandableListView lvErrors;
	private ExpandableListAdapter listAdapter;
	private ArrayList<ErrorChild> childList;
	private ArrayList<ErrorParent> theParentList = new ArrayList<>();
	private ArrayList<String> listClients = new ArrayList<String>();

	private Spinner spinner;

	private final String METHOD_NAME = "ListClientErrors";
	private final String SOAP_ACTION = Constants.NAMESPACE+METHOD_NAME;
	private final String METHOD_NAME2 = "ListClients";
	private final String SOAP_ACTION2 = Constants.NAMESPACE+METHOD_NAME2;

	public ErrorsFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		MainActivity.FragPageTitle = "Integration Errors";
		View rootView = inflater.inflate(R.layout.fragment_errors, container,
				false);

		lvErrors = (ExpandableListView) rootView.findViewById(R.id.attach_list);
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
					boolean rez = Boolean.valueOf(info.getProperty("ResolutionExists").toString().trim());

					ErrorChild child = new ErrorChild(description);
					child.setErrorDesc(description);
					childList = new ArrayList<ErrorChild>();
					childList.add(child);

					ErrorParent parent = new ErrorParent(client, object, objectID, objectNumber, date, rez, childList);
					parent.setClientName(client);
					parent.setObject(object);
					parent.setObjectID(objectID);
					parent.setObjectNumber(objectNumber);
					parent.setDateCreated(date);
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
				//Create request
				SoapObject request = new SoapObject(Constants.NAMESPACE, METHOD_NAME2);
				SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
						SoapEnvelope.VER11);
				envelope.dotNet = true;
				//Set output SOAP object
				envelope.setOutputSoapObject(request);
				//Create HTTP call object
				HttpTransportSE androidHttpTransport = new HttpTransportSE(Constants.URL);

				try {
					//Fill Client List
					androidHttpTransport.call(SOAP_ACTION2, envelope);
					SoapObject response = (SoapObject) envelope.getResponse();
					for (int i = 0; i < response.getPropertyCount(); i++) {

						Object property = response.getProperty(i);
						SoapObject info = (SoapObject) property;
						listClients.add(info.getProperty("ClientName").toString().trim());
					}

					listClients.add(0, "Select Client");

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
	
