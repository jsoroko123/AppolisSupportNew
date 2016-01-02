package com.support.main;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.v4.widget.DrawerLayout;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.appolissupport.R;
import com.support.custom.CustomProgressBar;
import com.support.fragments.CasesFragment;
import com.support.fragments.ClientsFragment;
import com.support.fragments.ErrorsFragment;
import com.support.fragments.StatsFragment;
import com.support.fragments.SubmitFragment;
import com.support.utilities.Constants;
import com.support.utilities.FileUpload;
import com.support.utilities.SharedPreferenceManager;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class MainActivity extends Activity implements FragmentManager.OnBackStackChangedListener,


		NavigationDrawerFragment.NavigationDrawerCallbacks {

	/**
	 * Fragment managing the behaviors, interactions and presentation of the
	 * navigation drawer.
	 */
	private NavigationDrawerFragment mNavigationDrawerFragment;

	/**
	 * Used to store the last screen title. For use in
	 * {@link #restoreActionBar()}.
	 */
    private CaseService s;
	private CharSequence mTitle;
	public static String FragPageTitle;
	private final String METHOD_NAME = "ListClients";
	private final String SOAP_ACTION = Constants.NAMESPACE+METHOD_NAME;
	private final String METHOD_NAME3 = "UploadImage";
	private final String SOAP_ACTION3 = Constants.NAMESPACE+METHOD_NAME3;
	private ArrayList<String> listItemInfo = new ArrayList<String>();
	private ArrayList<String> listItemInfo2 = new ArrayList<String>();
    public static SharedPreferenceManager spm2;
	SharedPreferenceManager spm4;
	public static final int SELECT_PHOTO = 100;
	public static String TAG = "PGGURU";
   // private boolean isUser;
    private String ClientName;
    public static boolean isSupport;
	private static boolean mIsInForegroundMode;
	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
	public static int clientUserID;
	public static boolean userIsInteracting = false;

	private String support;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mNavigationDrawerFragment = (NavigationDrawerFragment) getFragmentManager()
				.findFragmentById(R.id.navigation_drawer);
		mTitle = getTitle();
		// Set up the drawer.

		spm2 = new SharedPreferenceManager(this);

		spm4 = new SharedPreferenceManager(this);

		Intent intent = getIntent();
		String reset = intent.getStringExtra("Reset");
		if (reset != null){
			if (reset.contains("1")){
				spm4.remove("Search");
				spm4.remove("Status");
				spm4.remove("Client");
				spm4.remove("Type");
				spm4.remove("MyCases");
				spm4.remove("DateRange");
				spm4.remove("DtFromD");
				spm4.remove("DtFromM");
				spm4.remove("DtFromY");
				spm4.remove("DtToD");
				spm4.remove("DtToM");
				spm4.remove("DtToY");
			}
			else{
				intent.removeExtra("Reset");
			}
		}

		mNavigationDrawerFragment.setUp(R.id.navigation_drawer,
				(DrawerLayout) findViewById(R.id.drawer_layout));
		AsyncCallWS mLoadDataTask = new AsyncCallWS(getApplicationContext());
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			mLoadDataTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		} else {
			mLoadDataTask.execute();
		}
		getFragmentManager().addOnBackStackChangedListener(this);




		isSupport = spm2.getBoolean("IsSupport", false);

		if(isSupport){
			support = "1";
		} else {
			support = "0";
		}
	}




	@Override
	public void onNavigationDrawerItemSelected(int position) {
		// update the main content by replacing fragments
		Fragment fragment;
	    FragmentManager fragmentManager = getFragmentManager(); // For AppCompat use getSupportFragmentManager
	    switch(position) {
	        default:
			case 0:
				fragment = new CasesFragment();
				break;
	        case 1:
				fragment = new SubmitFragment();
				break;
	        case 2:
	            fragment = new ErrorsFragment();
				break;
	        case 3:
				fragment = new ClientsFragment();
				break;
	        case 4:
				fragment = new StatsFragment();
				break;
	            
	    }
	    fragmentManager.beginTransaction()
	        .replace(R.id.container, fragment)
	        .commit();
	}


	static Menu menu;
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (!mNavigationDrawerFragment.isDrawerOpen()) {
			// Only show items in the action bar relevant to this screen
			// if the drawer is not showing. Otherwise, let the drawer
			// decide what to show in the action bar.
			getMenuInflater().inflate(R.menu.main, menu);
			MenuItem item = menu.findItem(R.id.action_example);
			MenuItem item2 = menu.findItem(R.id.action_attach);
			MenuItem item3 = menu.findItem(R.id.action_send);
			MenuItem item4 = menu.findItem(R.id.action_logout);
			MenuItem item5 = menu.findItem(R.id.action_settings);
			if(MainActivity.FragPageTitle.equals("Support Cases")) {
				item.setVisible(true);
				item2.setVisible(false);
				item3.setVisible(false);
				item4.setVisible(true);
				item5.setVisible(true);
			} else if(MainActivity.FragPageTitle.equals("Submit a Case")) {
				item.setVisible(false);
				item2.setVisible(true);
				item3.setVisible(true);
				item4.setVisible(true);
				item5.setVisible(true);
			} else {
				item.setVisible(false);
				item2.setVisible(false);
				item3.setVisible(false);
				item4.setVisible(true);
				item5.setVisible(true);
			}
			return true;
		}
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		} else if (id == R.id.action_logout) {
			SharedPreferenceManager spm = new SharedPreferenceManager(MainActivity.this);
			spm.clearAll();
			Intent mainScreenIntent = new Intent(getApplicationContext(),LoginActivity.class);
			startActivity(mainScreenIntent);
			finish();
			return true;
		} else if (id == R.id.action_example) {
			showPopUpForScanner(this);
		} else if (id == R.id.action_send) {
			if(isSupport) {
				if (SubmitFragment.spinner.getSelectedItem().toString().contains("Select") || SubmitFragment.spinner2.getSelectedItem().toString().contains("Select") || SubmitFragment.spinner3.getSelectedItem().toString().contains("Select") || SubmitFragment.spinner4.getSelectedItem().toString().contains("Select") || SubmitFragment.etSubject.getText().toString().isEmpty() || SubmitFragment.etMainComments.getText().toString().isEmpty()) {
					Toast.makeText(getApplication(), "All information must be filled in.", Toast.LENGTH_LONG).show();
				} else {
					SubmitFragment sf = new SubmitFragment();
					sf.InsertNewCase(MainActivity.this, this, Integer.valueOf(SubmitFragment.spinUserID), String.valueOf(SubmitFragment.spinner3.getSelectedItemPosition() - 1), SubmitFragment.etSubject.getText().toString(), SubmitFragment.etMainComments.getText().toString(), SubmitFragment.uniqueID, String.valueOf(SubmitFragment.spinReasonID));
				}
			} else {
				if (SubmitFragment.spinner3.getSelectedItem().toString().contains("Select") || SubmitFragment.spinner4.getSelectedItem().toString().contains("Select") || SubmitFragment.etSubject.getText().toString().isEmpty() || SubmitFragment.etMainComments.getText().toString().isEmpty()) {
					Toast.makeText(getApplication(), "All information must be filled in.", Toast.LENGTH_LONG).show();
				} else {
					SubmitFragment sf = new SubmitFragment();
					sf.InsertNewCase(MainActivity.this, this, MainActivity.clientUserID, String.valueOf(SubmitFragment.spinner3.getSelectedItemPosition() - 1), SubmitFragment.etSubject.getText().toString(), SubmitFragment.etMainComments.getText().toString(), SubmitFragment.uniqueID, String.valueOf(SubmitFragment.spinReasonID));
				}
			}

		} else if (id == R.id.action_attach) {
			Intent pickIntent = new Intent();
			pickIntent.setType("image/*");
			pickIntent.setAction(Intent.ACTION_GET_CONTENT);

			Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

			String pickTitle = "Select Image or Take a Picture"; // Or get from strings.xml
			Intent chooserIntent = Intent.createChooser(pickIntent, pickTitle);
			chooserIntent.putExtra
					(
							Intent.EXTRA_INITIAL_INTENTS,
							new Intent[] { takePhotoIntent }
					);

			startActivityForResult(chooserIntent, SELECT_PHOTO);
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if(resultCode == RESULT_OK) {
			if (requestCode == SELECT_PHOTO){
				Uri selectedImage = data.getData();
				Bitmap bitmap = null;
				try {
					bitmap = FileUpload.RotateBitmap(FileUpload.decodeUri(selectedImage, this), 90);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				String img64 = FileUpload.encodeTobase64(bitmap);
				String currentDateandTime = sdf.format(new Date());
				UploadFile up = new UploadFile(this, img64, currentDateandTime + ".png", "", support);
				up.execute();
			}
		}

	}
	
	
	public void showPopUpForScanner(Context mContext) {
		LayoutInflater li = LayoutInflater.from(mContext);
		View promptsView = li.inflate(R.layout.dialogscanner, null);
		final LinearLayout llPanel1=(LinearLayout)promptsView.findViewById(R.id.ll1);
		final LinearLayout llPanel2=(LinearLayout)promptsView.findViewById(R.id.ll2);
		
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				mContext);
		final Spinner spinner=(Spinner)promptsView.findViewById(R.id.spinner1);
        ArrayList<String> statuses = new ArrayList<>();
        if(isSupport) {

            statuses.add("Open");
            statuses.add("Closed");
            statuses.add("WCA");
            statuses.add("CR Pending");
            statuses.add("All");
        } else {
            statuses.add("Open");
            statuses.add("Closed");
        }

	    ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
	            this, R.layout.spinner_item, statuses);
	    spinnerArrayAdapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );
	    spinner.setAdapter(spinnerArrayAdapter);
    
	    final Spinner spinner2=(Spinner)promptsView.findViewById(R.id.spinner2);
        ArrayAdapter<String> spinnerArrayAdapter2 = new ArrayAdapter<String>(
	            this, R.layout.spinner_item, listItemInfo2);
	    spinnerArrayAdapter2.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );
	    spinner2.setAdapter(spinnerArrayAdapter2);

	    
	    final Spinner spinner3=(Spinner)promptsView.findViewById(R.id.spinner3);
		String build[] = {"Support","Pre Go Live","All"};
	    ArrayAdapter<String> spinnerArrayAdapter3 = new ArrayAdapter<String>(
	            this, R.layout.spinner_item, build);
	    spinnerArrayAdapter3.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );
        spinner3.setAdapter(spinnerArrayAdapter3);
	    final EditText txt=(EditText)promptsView.findViewById(R.id.etSearch);

        final CheckBox chk1 = (CheckBox) promptsView.findViewById(R.id.checkBox2);


        final TextView tv1=(TextView)promptsView.findViewById(R.id.tvClients);
        final TextView tv2=(TextView)promptsView.findViewById(R.id.tv);
        if(spm2.getBoolean("IsSupport", false)){
            ClientName = "0";
        } else {
            ClientName = spm2.getString("ClientName", "");
            spinner2.setVisibility(View.GONE);
            tv1.setVisibility(View.GONE);
            spinner3.setVisibility(View.GONE);
            tv2.setVisibility(View.GONE);
            chk1.setVisibility(View.GONE);
        }

		txt.addTextChangedListener(new TextWatcher() {

	        @Override
	        public void onTextChanged(CharSequence s, int start, int before,
	                int count) {
	            // TODO Auto-generated method stub
				if(isSupport) {
					if (TextUtils.isEmpty(s.toString().trim())) {

						spinner.setSelection(0);
						spinner.setEnabled(true);
					} else {

						spinner.setSelection(4);
						spinner.setEnabled(false);

					}
				}
	        }
	        
	        @Override
	        public void beforeTextChanged(CharSequence s, int start, int count,
	                int after) {
	            // TODO Auto-generated method stub

	        }

	        @Override
	        public void afterTextChanged(Editable s) {

	        }
	    });
		final DatePicker dtFrom=(DatePicker)promptsView.findViewById(R.id.dpResult);
		final DatePicker dtTo=(DatePicker)promptsView.findViewById(R.id.dpResult2);

		final CheckBox[] chk = {(CheckBox) promptsView.findViewById(R.id.checkBox1)};

		if(!spm4.getString("Search", "").isEmpty()){
			txt.setText(spm4.getString("Search", ""));
		} else {
			txt.setText("");
		}

		if(!spm4.getString("Status", "").isEmpty()){
			int spinnerPosition = spinnerArrayAdapter.getPosition(spm4.getString("Status", ""));
			spinner.setSelection(spinnerPosition);
		} else {
			spinner.setSelection(0);
		}

		if(!spm4.getString("Client", "").isEmpty()){
			int spinnerPosition = spinnerArrayAdapter2.getPosition(spm4.getString("Client", ""));
			spinner2.setSelection(spinnerPosition);
		} else {
			spinner2.setSelection(0);
		}

		if(!spm4.getString("Type", "").isEmpty()){
			int spinnerPosition = spinnerArrayAdapter3.getPosition(spm4.getString("Type", ""));
			spinner3.setSelection(spinnerPosition);
		} else {
			spinner3.setSelection(0);
		}

		chk1.setChecked(spm4.getBoolean("MyCases", false));
		chk[0].setChecked(spm4.getBoolean("DateRange", false));

		if (spm4.getBoolean("DateRange", false)) {
			llPanel1.setVisibility(View.VISIBLE);
			llPanel2.setVisibility(View.VISIBLE);

			dtFrom.updateDate(spm4.getInt("DtFromY", 0), spm4.getInt("DtFromM", 0),spm4.getInt("DtFromD", 0));
			dtTo.updateDate(spm4.getInt("DtToY", 0), spm4.getInt("DtToM", 0),spm4.getInt("DtToD", 0));

		} else {
			llPanel1.setVisibility(View.GONE);
			llPanel2.setVisibility(View.GONE);
		}


		chk[0].setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					llPanel1.setVisibility(View.VISIBLE);
					llPanel2.setVisibility(View.VISIBLE);

				} else {
					llPanel1.setVisibility(View.GONE);
					llPanel2.setVisibility(View.GONE);
				}

			}
		});


	    
	    // set prompts.xml to alertdialog builder
		alertDialogBuilder.setView(promptsView);

		// set dialog message
		alertDialogBuilder
			.setCancelable(false)
			.setPositiveButton("OK",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {

							String clientList;
							if (spm4.getBoolean("IsSupport", false)) {
								if (spinner2.getSelectedItem().toString().equals("All Clients")) {
									clientList = "---Client List---";
								} else {
									clientList = spinner2.getSelectedItem().toString();
								}
							} else {
								clientList = CasesFragment.ClientName;
							}
							String fDate;
							String tDate;
							String fMonth;
							String fDay;
							String tMonth;
							String tDay;
							fDay = String.valueOf(dtFrom.getDayOfMonth());
							if (fDay.length() < 2) {
								fDay = "0" + fDay;
							}
							fMonth = String.valueOf(dtFrom.getMonth() + 1);
							if (fMonth.length() < 2) {
								fMonth = "0" + fMonth;
							}
							int fYear = dtFrom.getYear();

							tDay = String.valueOf(dtTo.getDayOfMonth());
							if (tDay.length() < 2) {
								tDay = "0" + tDay;
							}
							tMonth = String.valueOf(dtTo.getMonth() + 1);
							if (tMonth.length() < 2) {
								tMonth = "0" + tMonth;
							}
							int tYear = dtTo.getYear();

							if (chk[0].isChecked()) {
								fDate = fYear + "-" + fMonth + "-" + fDay;
								tDate = tYear + "-" + tMonth + "-" + tDay;
							} else {

								fDate = "1800-01-01";
								tDate = "2222-02-01";
							}
							spm4.saveString("Search", txt.getText().toString());
							spm4.saveString("Status", spinner.getSelectedItem().toString());
							if(spm4.getBoolean("IsSupport", false)) {
								spm4.saveString("Client", spinner2.getSelectedItem().toString());
							}
							spm4.saveString("Type", spinner3.getSelectedItem().toString());
							spm4.saveBoolean("MyCases", chk1.isChecked());
							spm4.saveBoolean("DateRange", chk[0].isChecked());
							spm4.saveInt("DtFromD", dtFrom.getDayOfMonth());
							spm4.saveInt("DtFromM", dtFrom.getMonth());
							spm4.saveInt("DtFromY", dtFrom.getYear());
							spm4.saveInt("DtToD", dtTo.getDayOfMonth());
							spm4.saveInt("DtToM", dtTo.getMonth());
							spm4.saveInt("DtToY", dtTo.getYear());
							spm4.saveString("FromDate", fDate);
							spm4.saveString("ToDate", tDate);
							CasesFragment cs = new CasesFragment();
							cs.refreshData(MainActivity.this, spinner.getSelectedItem().toString(), clientList, spinner3.getSelectedItem().toString(),
									txt.getText().toString(), fDate, tDate, spm2.getInt("UserID", 0), false, chk1.isChecked(), spm2.getBoolean("IsSupport", false));
							CasesFragment.casesAdapter.notifyDataSetChanged();
						}
					})

			.setNegativeButton("Cancel",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							dialog.cancel();
						}
					})
				.setNeutralButton("Reset",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								spm4.remove("Search");
								spm4.remove("Status");
								spm4.remove("Client");
								spm4.remove("Type");
								spm4.remove("MyCases");
								spm4.remove("DateRange");
								spm4.remove("DtFromD");
								spm4.remove("DtFromM");
								spm4.remove("DtFromY");
								spm4.remove("DtToD");
								spm4.remove("DtToM");
								spm4.remove("DtToY");
								CasesFragment cs = new CasesFragment();
								cs.refreshData(MainActivity.this, "Open", ClientName, "Support", "", "1893-01-01", "2456-07-31", spm2.getInt("UserID", 0), false, false, spm2.getBoolean("IsSupport", false));
								CasesFragment.casesAdapter.notifyDataSetChanged();
							}
						});


		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();
	}

	@Override
	public void onBackStackChanged() {
		if (!MainActivity.FragPageTitle.matches(".*\\d+.*")) {

			getActionBar().setTitle("Support Cases");

		}

	}

	private class AsyncCallWS extends AsyncTask<String, Void, Void> {

		Context context;

		public AsyncCallWS(Context mContext){
			this.context = mContext;
		}

		@Override
		protected void onPreExecute() {
			listItemInfo.clear();
			super.onPreExecute();
			if(!isCancelled()){
			}

		}

		@Override
        protected Void doInBackground(String... params) {
            //Create request
    	    SoapObject request = new SoapObject(Constants.NAMESPACE, METHOD_NAME);
    	    SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
    	            SoapEnvelope.VER11);
    	    envelope.dotNet = true;
    	    //Set output SOAP object
    	    envelope.setOutputSoapObject(request);
    	    //Create HTTP call object
    	    HttpTransportSE androidHttpTransport = new HttpTransportSE(Constants.URL);

    	    try {
    	        //Invole web service
    	        androidHttpTransport.call(SOAP_ACTION, envelope);
                SoapObject response = (SoapObject) envelope.getResponse();
                for (int i = 0; i < response.getPropertyCount(); i++) {

                    Object property = response.getProperty(i);
                    SoapObject info = (SoapObject) property;
                    listItemInfo2.add(info.getProperty("ClientName").toString().trim());
				}

                listItemInfo2.add(0, "All Clients");
			} catch (Exception e) {
    	        e.printStackTrace();
    	    }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
		}

        }


	public class UploadFile extends AsyncTask<String, Void, Void> {

		Context context;
		String image;
		String filename;
		String caseNumber;
		String support;

		public UploadFile(Context mContext, String mImage, String mFilename, String mCaseNumber, String mSupport){
			this.context = mContext;
			this.image = mImage;
			this.filename = mFilename;
			this.caseNumber = mCaseNumber;
			this.support = mSupport;

		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if(!isCancelled()){
				CustomProgressBar.showProgressBar(context, false);
			}
		}

		@Override
		protected Void doInBackground(String... params) {
			SoapObject request = new SoapObject(Constants.NAMESPACE, METHOD_NAME3);
			//Property which holds input parameters
			PropertyInfo supportCasesPI = new PropertyInfo();
			supportCasesPI.setName("image");
			supportCasesPI.setValue(image);
			supportCasesPI.setType(String.class);
			request.addProperty(supportCasesPI);

			supportCasesPI = new PropertyInfo();
			supportCasesPI.setName("filename");
			supportCasesPI.setValue(filename);
			supportCasesPI.setType(String.class);
			request.addProperty(supportCasesPI);

			supportCasesPI = new PropertyInfo();
			supportCasesPI.setName("CaseID");
			supportCasesPI.setValue("0");
			supportCasesPI.setType(String.class);
			request.addProperty(supportCasesPI);

			supportCasesPI = new PropertyInfo();
			supportCasesPI.setName("Support");
			supportCasesPI.setValue(support);
			supportCasesPI.setType(String.class);
			request.addProperty(supportCasesPI);

			supportCasesPI = new PropertyInfo();
			supportCasesPI.setName("tempID");
			supportCasesPI.setValue(SubmitFragment.uniqueID);
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
				androidHttpTransport.call(SOAP_ACTION3, envelope);

			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			Log.i(TAG, "onPostExecute");
			CustomProgressBar.hideProgressBar();
			SubmitFragment.listAttachments.add(filename);
			ArrayAdapter<String> attachArrayAdapter = new ArrayAdapter<String>(getApplication(), R.layout.spinner_item, SubmitFragment.listAttachments);
			SubmitFragment.lvAttachList.setAdapter(attachArrayAdapter);
			attachArrayAdapter.notifyDataSetChanged();
		}

	}
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		CasesFragment.start = 1;
	}

	@Override
	protected void onResume() {
		super.onResume();
		mIsInForegroundMode = true;
			AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
			Intent i = new Intent(this, CaseService.class);
			PendingIntent pi = PendingIntent.getService(this, 0, i, 0);
			am.cancel(pi);
			// by my own convention, minutes <= 0 means notifications are disabled
			if (1 > 0) {
				am.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
						SystemClock.elapsedRealtime() + 1 * 30 * 1000,
						1 * 30 * 1000, pi);
			}
	}





	@Override
	protected void onPause() {
		super.onPause();
		mIsInForegroundMode = false;
	}


	// Some function.
	public static boolean isInForeground() {
		return mIsInForegroundMode;
	}

	private Toast toast;
	private long lastBackPressTime = 0;
	@Override
	public void onBackPressed() {
		int count = getFragmentManager().getBackStackEntryCount();
		if (count == 0 || MainActivity.FragPageTitle.contains("Support Cases") || MainActivity.FragPageTitle.contains("Submit") || MainActivity.FragPageTitle.contains("Errors") || MainActivity.FragPageTitle.contains("Client List") || MainActivity.FragPageTitle.contains("Statistics")){
			if (this.lastBackPressTime < System.currentTimeMillis() - 4000) {
				toast = Toast.makeText(this, "Press back again to close application.", Toast.LENGTH_LONG);
				toast.show();
				this.lastBackPressTime = System.currentTimeMillis();
			} else {
				if (toast != null) {
					toast.cancel();
				}
				finish();
			}
		} else {
			getFragmentManager().popBackStack();
			if (MainActivity.FragPageTitle.matches(".*\\d+.*")) {
				setTitle("Support Cases");
				MainActivity.FragPageTitle="Support Cases";
				if(!isSupport) {
					CasesFragment.llCasesEnvironment.setVisibility(View.VISIBLE);
				}
			}
		}
	}

	@Override
	public void onUserInteraction() {
		super.onUserInteraction();
		userIsInteracting = true;
	}
}