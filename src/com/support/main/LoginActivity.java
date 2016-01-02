package com.support.main;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import com.example.appolissupport.R;
import com.support.custom.CustomProgressBar;
import com.support.objects.User;
import com.support.utilities.Constants;
import com.support.utilities.SharedPreferenceManager;
import com.support.utilities.Utilities;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends Activity {

	private EditText et_Username;
	private EditText et_Password;
	private Button bt_SignIn;

	private boolean isValidLogin;

	//test

	public static User user = new User();

	private final String METHOD_NAME = "ListValidUsers";
	private final String SOAP_ACTION = Constants.NAMESPACE+METHOD_NAME;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		SharedPreferenceManager spm = new SharedPreferenceManager(LoginActivity.this);

		if(!spm.getString("UserName", "").isEmpty()){
			Intent mainScreenIntent = new Intent(getApplicationContext(),MainActivity.class);
			startActivity(mainScreenIntent);
			finish();
		}

		et_Username = (EditText) findViewById(R.id.username);
		et_Password = (EditText) findViewById(R.id.password);
		bt_SignIn = (Button) findViewById(R.id.bt_sign_in);

		bt_SignIn.setOnClickListener(new OnClickListener() {


			@Override
			public void onClick(View view) {
				AsyncCallWS mLoadDataTask = new AsyncCallWS(LoginActivity.this);
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
					if (et_Username.getText().toString().isEmpty() && et_Password.getText().toString().isEmpty()) {
						Utilities.ShowDialog("Error!", "Please Enter Username and Password.", LoginActivity.this);
					} else if (et_Username.getText().toString().isEmpty() && !et_Password.getText().toString().isEmpty()) {
						Utilities.ShowDialog("Error!", "Please Enter Username", LoginActivity.this);

					} else if (!et_Username.getText().toString().isEmpty() && et_Password.getText().toString().isEmpty()) {
						Utilities.ShowDialog("Error!", "Please Enter Password.", LoginActivity.this);

					} else {

						mLoadDataTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
					}
				} else {
					if (et_Username.getText().toString().isEmpty() && et_Password.getText().toString().isEmpty()) {
						Utilities.ShowDialog("Error!", "Please Enter Username and Password.", LoginActivity.this);

					} else if (et_Username.getText().toString().isEmpty() && !et_Password.getText().toString().isEmpty()) {
						Utilities.ShowDialog("Error!", "Please Enter Username", LoginActivity.this);

					} else if (!et_Username.getText().toString().isEmpty() && et_Password.getText().toString().isEmpty()) {
						Utilities.ShowDialog("Error!", "Please Enter Password.", LoginActivity.this);

					} else {

						mLoadDataTask.execute();
					}
				}
			}
		});
	}

	private class AsyncCallWS extends AsyncTask<String, Void, Void> {

		Context context;
		Dialog d;

		public AsyncCallWS(Context mContext){
			this.context = mContext;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if(!isCancelled()){
				CustomProgressBar.showProgressBar(context, false, "Validating");
			}

		}

		@Override
		protected Void doInBackground(String... params) {
			SoapObject request = new SoapObject(Constants.NAMESPACE, METHOD_NAME);
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);
			envelope.dotNet = true;
			envelope.setOutputSoapObject(request);
			HttpTransportSE androidHttpTransport = new HttpTransportSE(Constants.URL);

			try {
				//Invole web service
				androidHttpTransport.call(SOAP_ACTION, envelope);
				SoapObject response = (SoapObject) envelope.getResponse();
				for (int i = 0; i < response.getPropertyCount(); i++) {

					Object property = response.getProperty(i);
					SoapObject info = (SoapObject) property;
					String a = info.getProperty("UserName").toString().trim().toUpperCase();
					String b = info.getProperty("Password").toString().trim().toUpperCase();
					if(et_Username.getText().toString().trim().equalsIgnoreCase(info.getProperty("UserName").toString().trim())
							&& et_Password.getText().toString().trim().equalsIgnoreCase(info.getProperty("Password").toString().trim())){
						isValidLogin = true;
						user.setUserID(Integer.valueOf(info.getProperty("UserID").toString()));
						user.setUsername(info.getProperty("UserName").toString());
						user.setClientID(Integer.valueOf(info.getProperty("ClientID").toString()));
						user.setClientName(info.getProperty("ClientName").toString());
						user.setEmail(info.getProperty("Email").toString());
						user.setAdministrator(Boolean.valueOf(info.getProperty("Administrator").toString()));
						user.setSupport(Boolean.valueOf(info.getProperty("SupportAdmin").toString()));
						break;

					}
					else {
						isValidLogin = false;
					}

				}

			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {

			CustomProgressBar.hideProgressBar();

			if(isValidLogin){
				Intent mainScreenIntent = new Intent(getApplicationContext(),MainActivity.class);
				startActivity(mainScreenIntent);
				SharedPreferenceManager spm = new SharedPreferenceManager(LoginActivity.this);
				spm.saveString("UserName", user.getUsername());
				spm.saveInt("UserID", user.getUserID());
				spm.saveString("ClientName", user.getClientName());
				spm.saveInt("ClientID", user.getClientID());
				spm.saveBoolean("IsSupport", user.isSupport());
				spm.saveBoolean("IsAdministrator", user.isAdministrator());
				finish();
			}

			else{
				Utilities.ShowDialog("Invalid User!", "Please Try Again.", LoginActivity.this);
			}
		}
	}



}