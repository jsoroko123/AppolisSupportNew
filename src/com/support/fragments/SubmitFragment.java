package com.support.fragments;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import com.support.main.MainActivity;
import com.example.appolissupport.R;
import com.support.objects.CaseReason;
import com.support.objects.ClientUser;
import com.support.utilities.Constants;
import com.support.utilities.SharedPreferenceManager;
import com.support.utilities.Utilities;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.UUID;

public class SubmitFragment extends Fragment implements OnClickListener, OnItemClickListener {

	private final String METHOD_NAME = "ListClients";
	private final String SOAP_ACTION = Constants.NAMESPACE+METHOD_NAME;
    private final String METHOD_NAME2 = "ListClientUsers";
    private final String SOAP_ACTION2 = Constants.NAMESPACE+METHOD_NAME2;
    private final String METHOD_NAME3 = "UploadImage";
    private final String SOAP_ACTION3 = Constants.NAMESPACE+METHOD_NAME3;
    private final String METHOD_NAME4 = "InsertComments";
    private final String SOAP_ACTION4 = Constants.NAMESPACE+METHOD_NAME4;
    private final String METHOD_NAME5 = "ListCaseReasons";
    private final String SOAP_ACTION5 = Constants.NAMESPACE+METHOD_NAME5;
    private final String METHOD_NAME6 = "ListClientSites";
    private final String SOAP_ACTION6 = Constants.NAMESPACE+METHOD_NAME6;

    public static String TAG = "PGGURU";
	private ArrayList<String> listClients = new ArrayList<String>();
    private ArrayList<ClientUser> listClientUsers = new ArrayList<ClientUser>();
    private ArrayList<CaseReason> listCaseReasons = new ArrayList<CaseReason>();
    private ArrayList<String> listSeverity = new ArrayList<String>();
    public static ArrayList<String> listAttachments = new ArrayList<String>();
    public static ListView lvAttachList;
    ArrayAdapter<ClientUser> spinnerArrayAdapter;
    ArrayAdapter<CaseReason> spinnerArrayAdapter2;
    private Button btnAttach;
    public static Spinner spinner;
    public static Spinner spinner2;
    public static Spinner spinner3;
    public static Spinner spinner4;
    public static Spinner spinner5;
    private TextView tvClient;
    private TextView tvUsers;
    private TextView tvEnvironment;
    public static EditText etSubject;
    public static EditText etMainComments;
    public static String spinUserID = "0";
    public static String spinReasonID = "0";

    private String support;
    private SharedPreferenceManager spm;
    public static String uniqueID;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");

	public SubmitFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		MainActivity.FragPageTitle = "Submit a Case";
        uniqueID = UUID.randomUUID().toString();
        listAttachments.clear();
        spm = new SharedPreferenceManager(getActivity());

		View rootView = inflater.inflate(R.layout.fragment_submit, container,
				false);

        lvAttachList = (ListView)rootView.findViewById(R.id.lvAttachments);
        lvAttachList.setOnItemClickListener(this);
        tvClient=(TextView)rootView.findViewById(R.id.tvClients);
        tvUsers=(TextView)rootView.findViewById(R.id.tvUsers);
        tvEnvironment=(TextView)rootView.findViewById(R.id.tvEnvironment);

        spinner=(Spinner)rootView.findViewById(R.id.spinnerClients);

        spinner2 = (Spinner) rootView.findViewById(R.id.spinnerUsers);
        spinner2.setEnabled(false);
        spinner2.setClickable(false);
        spinner3 = (Spinner) rootView.findViewById(R.id.spSeverity);
        spinner4 = (Spinner) rootView.findViewById(R.id.spArea);
        spinner5 = (Spinner) rootView.findViewById(R.id.spinnerEnvironment);
        etSubject = (EditText)rootView.findViewById(R.id.etSubject);
        etMainComments = (EditText)rootView.findViewById(R.id.etResp);
        etMainComments.setSelection(0);
        listClientUsers.add(new ClientUser("Select User", "0"));
        spinnerArrayAdapter = new ArrayAdapter<ClientUser>(getActivity(), R.layout.spinner_item, listClientUsers);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner2.setAdapter(spinnerArrayAdapter);

        if(spm.getBoolean("IsSupport", false)){
            support = "1";
            spinner.setVisibility(View.VISIBLE);
            spinner2.setVisibility(View.VISIBLE);
            spinner3.setVisibility(View.VISIBLE);
            spinner5.setVisibility(View.GONE);
            tvClient.setVisibility(View.VISIBLE);
            tvUsers.setVisibility(View.VISIBLE);
            tvEnvironment.setVisibility(View.GONE);
        } else {
            support = "0";
            spinner.setVisibility(View.GONE);
            spinner2.setVisibility(View.GONE);
            spinner5.setVisibility(View.VISIBLE);
            tvClient.setVisibility(View.GONE);
            tvUsers.setVisibility(View.GONE);
            tvEnvironment.setVisibility(View.VISIBLE);
        }

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                if (!listClients.get(position).contains("Select Client")) {
                    AsyncCallWS2 as = new AsyncCallWS2(getActivity());
                    as.execute(listClients.get(position));
                } else {
                    spinner2.setSelection(0);
                    spinner2.setEnabled(false);
                }
            }


            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });


        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                ClientUser s = (ClientUser) parentView.getItemAtPosition(position);
                Object tag = s.tag;
                spinUserID = (String) tag;

            }



            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

        spinner4.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                CaseReason s = (CaseReason) parentView.getItemAtPosition(position);
                Object tag = s.tag;
                spinReasonID = (String) tag;

            }



            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });



		getActivity().getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        getActivity().getActionBar().setDisplayShowTitleEnabled(true);
        getActivity().getActionBar().setTitle(MainActivity.FragPageTitle);

        AsyncCallWS as = new AsyncCallWS(getActivity());
        as.execute();




//        AsyncCallWS2 as2 = new AsyncCallWS2(getActivity());
//        as2.execute("");
		return rootView;
	}


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    private class AsyncCallWS extends AsyncTask<String, Void, Void> {

		Context context;
        ProgressDialog progressDialog;

		public AsyncCallWS(Context mContext){
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
            try {
            if(spm.getBoolean("IsSupport", false)) {
                SoapObject request = new SoapObject(Constants.NAMESPACE, METHOD_NAME);
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                        SoapEnvelope.VER11);
                envelope.dotNet = true;
                //Set output SOAP object
                envelope.setOutputSoapObject(request);
                //Create HTTP call object
                HttpTransportSE androidHttpTransport = new HttpTransportSE(Constants.URL);


                //Fill Client List
                androidHttpTransport.call(SOAP_ACTION, envelope);
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





                //Add Severities
                listSeverity.add(Constants.LEVEL1);
                listSeverity.add(Constants.LEVEL2);
                listSeverity.add(Constants.LEVEL3);
                listSeverity.add(Constants.LEVEL4);
                listSeverity.add(0, "Select Severity");


                try{
                    /////////////////////////////////////
                    SoapObject request2 = new SoapObject(Constants.NAMESPACE, METHOD_NAME5);

                    SoapSerializationEnvelope envelope2 = new SoapSerializationEnvelope(
                            SoapEnvelope.VER11);
                    envelope2.dotNet = true;
                    //Set output SOAP object
                    envelope2.setOutputSoapObject(request2);
                    //Create HTTP call object
                    HttpTransportSE androidHttpTransport2 = new HttpTransportSE(Constants.URL);


                        //Fill Client List
                        androidHttpTransport2.call(SOAP_ACTION5, envelope2);
                        SoapObject response2 = (SoapObject) envelope2.getResponse();
                        for (int i = 0; i < response2.getPropertyCount(); i++) {

                            Object property2 = response2.getProperty(i);
                            SoapObject info2 = (SoapObject) property2;
                            listCaseReasons.add(new CaseReason(info2.getProperty("Reason").toString().trim(), info2.getProperty("ReasonID").toString().trim()));
                        }
                    listCaseReasons.remove(0);
                    listCaseReasons.add(0,new CaseReason("Select Area", "0"));

                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                //////////////////////////////////////


			return null;
		}

		@Override
		protected void onPostExecute(Void result) {

            if(null != progressDialog && (progressDialog.isShowing())){
                progressDialog.dismiss();
            }
            if(spm.getBoolean("IsSupport", false)) {
                ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_item, listClients);
                spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(spinnerArrayAdapter);
            } else {
                ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_item, listClients);
                spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner5.setAdapter(spinnerArrayAdapter);
            }

            ArrayAdapter<String> spinnerArrayAdapter3 = new ArrayAdapter<>(getActivity(), R.layout.spinner_item, listSeverity);
            spinnerArrayAdapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );
            spinner3.setAdapter(spinnerArrayAdapter3);

            ArrayAdapter<CaseReason> spinnerArrayAdapter4 = new ArrayAdapter<CaseReason>(getActivity(), R.layout.spinner_item, listCaseReasons);
            spinnerArrayAdapter4.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );
            spinner4.setAdapter(spinnerArrayAdapter4);

        }
    }

    private class AsyncCallWS2 extends AsyncTask<String, Void, Void> {

        Context context;

        public AsyncCallWS2(Context mContext){
            this.context = mContext;
        }

        @Override
        protected void onPreExecute() {
            listClientUsers.clear();
            super.onPreExecute();
            if(!isCancelled()){
            }
         }

        @Override
        protected Void doInBackground(String... params) {
            //Create request
            SoapObject request = new SoapObject(Constants.NAMESPACE, METHOD_NAME2);
            PropertyInfo supportCasesPI = new PropertyInfo();
            supportCasesPI.setName("clientName");
            supportCasesPI.setValue(params[0]);
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
                //Fill Client List
                androidHttpTransport.call(SOAP_ACTION2, envelope);
                SoapObject response = (SoapObject) envelope.getResponse();
                for (int i = 0; i < response.getPropertyCount(); i++) {

                    Object property = response.getProperty(i);
                    SoapObject info = (SoapObject) property;
                    listClientUsers.add(new ClientUser(info.getProperty("UserName").toString().trim(), info.getProperty("UserID").toString().trim()));
                }

                listClientUsers.add(0,new ClientUser("Select User", "0"));

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            ArrayAdapter<ClientUser> spinnerArrayAdapter = new ArrayAdapter<ClientUser>(getActivity(), R.layout.spinner_item, listClientUsers);
            spinnerArrayAdapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );
            spinner2.setAdapter(spinnerArrayAdapter);
            spinner2.setEnabled(true);
            spinner2.setClickable(true);
        }

    }

    public void InsertNewCase(Context mContext, Activity act, int mUserID, String mSeverity, String mCommentTitle, String mComments, String mTempID, String mReason) {
        InsertCase mLoadDataTask = new InsertCase(mContext, act, mUserID, mSeverity, mCommentTitle, mComments, mTempID, mReason);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            mLoadDataTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            mLoadDataTask.execute();
        }
    }

    public class InsertCase extends AsyncTask<String, Void, Void> {

        Context context;
        ProgressDialog progressDialog;
        int userID;
        String severity;
        String commentTitle;
        String comments;
        String tempID;
        String reason;
        Activity abc;

        public InsertCase(Context mContext, Activity mABC, int mUserID, String mSeverity, String mCommentTitle, String mComments, String mTempID, String mReason){
            this.context = mContext;
            this.userID = mUserID;
            this.severity = mSeverity;
            this.commentTitle = mCommentTitle;
            this.comments = mComments;
            this.tempID = mTempID;
            this.reason = mReason;
            this.abc = mABC;
        }



        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if(!isCancelled()){
                progressDialog = new ProgressDialog(context);
                progressDialog.setMessage("Submitting Case...");
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

           // String currentDateandTime = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a z").format(new Date());

            //call 1
            SoapObject request = new SoapObject(Constants.NAMESPACE, METHOD_NAME4);
            //Property which holds input parameters
            PropertyInfo supportCasesPI = new PropertyInfo();
            supportCasesPI.setName("iUserID");
            supportCasesPI.setValue(userID);
            supportCasesPI.setType(Integer.class);
            request.addProperty(supportCasesPI);

            supportCasesPI = new PropertyInfo();
            supportCasesPI.setName("sSeverity");
            supportCasesPI.setValue(severity);
            supportCasesPI.setType(String.class);
            request.addProperty(supportCasesPI);

            supportCasesPI = new PropertyInfo();
            supportCasesPI.setName("CommentTitle");
            supportCasesPI.setValue(commentTitle);
            supportCasesPI.setType(String.class);
            request.addProperty(supportCasesPI);

            supportCasesPI = new PropertyInfo();
            supportCasesPI.setName("sComments");
            supportCasesPI.setValue(comments);
            supportCasesPI.setType(String.class);
            request.addProperty(supportCasesPI);

            supportCasesPI = new PropertyInfo();
            supportCasesPI.setName("tempCommentID");
            supportCasesPI.setValue(tempID);
            supportCasesPI.setType(String.class);
            request.addProperty(supportCasesPI);

            supportCasesPI = new PropertyInfo();
            supportCasesPI.setName("reason");
            supportCasesPI.setValue(reason);
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
                //Invole web service
                androidHttpTransport.call(SOAP_ACTION4, envelope);

            } catch (Exception e) {
                e.printStackTrace();
            }



        return null;
    }


        @Override
        protected void onPostExecute(Void result) {
            Log.i(TAG, "onPostExecute");
            if(null != progressDialog && (progressDialog.isShowing())){
                progressDialog.dismiss();
            }
            Utilities.ShowDialog("Case Submitted", "Case Successfully Submitted to Appolis Support", context);

            spinner.setSelection(0);
            spinner2.setSelection(0);
            spinner2.setEnabled(false);
            spinner3.setSelection(0);
            spinner4.setSelection(0);
            etSubject.setText("");
            etMainComments.setText("");

        }

    }


}