package com.support.fragments;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.support.main.MainActivity;
import com.example.appolissupport.R;
import com.fortysevendeg.swipelistview.BaseSwipeListViewListener;
import com.fortysevendeg.swipelistview.SwipeListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshSwipeListView;
import com.support.adapters.CasesAdapter;
import com.support.objects.SupportCases;
import com.support.utilities.Constants;
import com.support.utilities.SharedPreferenceManager;
import com.support.utilities.Utilities;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;
import java.util.Date;

public class CasesFragment extends Fragment implements OnClickListener, OnItemClickListener {
	private final String METHOD_NAME = "ListSupportCases";
	private final String SOAP_ACTION = Constants.NAMESPACE+METHOD_NAME;
	private final String METHOD_NAME2 = "UpdateCaseStatus";
	private final String SOAP_ACTION2 = Constants.NAMESPACE+METHOD_NAME2;
	private final String METHOD_NAME3 = "ListSupportUsers";
	private final String SOAP_ACTION3 = Constants.NAMESPACE+METHOD_NAME3;
    private final String METHOD_NAME4 = "UpdateCaseAssignment";
    private final String SOAP_ACTION4 = Constants.NAMESPACE+METHOD_NAME4;
	private final String METHOD_NAME5 = "DisplayReminder";
	private final String SOAP_ACTION5 = Constants.NAMESPACE+METHOD_NAME5;
	private final String METHOD_NAME6 = "InsertReminder";
	private final String SOAP_ACTION6 = Constants.NAMESPACE+METHOD_NAME6;
	private final String METHOD_NAME7 = "DeleteReminder";
	private final String SOAP_ACTION7 = Constants.NAMESPACE+METHOD_NAME7;

	public static String TAG = "PGGURU";
	private static PullToRefreshSwipeListView lvSupportCasesList;
	private SwipeListView swipeList;
	private ArrayList<SupportCases> listItemInfo = new ArrayList<SupportCases>();
	public static ArrayList<String> listItemSupportUsers = new ArrayList<String>();
	public static CasesAdapter casesAdapter = null;
    public static SharedPreferenceManager spm;
	public static String ClientName;
	public static int start;
	public static String caseStatus = "";
    public static String SupportUserName = "";
	public static String phoneNum = "";
	private String search;
	private String status;
	private String client;
	private String type;
	private String fDate;
	private String tDate;
	private static String notes;
	private boolean isUser;
	private EditText etNotes;
	private static TextView noCases;

    private static final String ARG_SECTION_NUMBER = "section_number";

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
    	MainActivity.FragPageTitle = "Support Cases";
        super.onActivityCreated(savedInstanceState);
        getActivity().getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        getActivity().getActionBar().setDisplayShowTitleEnabled(true);
        getActivity().getActionBar().setTitle(MainActivity.FragPageTitle);
		final ListSupportUsers up = new ListSupportUsers(getActivity());
		up.execute();
		start = 1;
    }

	public CasesFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
        spm = new SharedPreferenceManager(getActivity());
		View rootView = inflater.inflate(R.layout.fragment_cases, container,
				false);
		start = 1;

		lvSupportCasesList = (PullToRefreshSwipeListView) rootView.findViewById(R.id.lv_support_cases);
		lvSupportCasesList.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
		lvSupportCasesList.setOnItemClickListener(this);
		swipeList = lvSupportCasesList.getRefreshableView();

		if(spm.getBoolean("IsSupport", false)){
			ClientName = "0";

		} else {
			ClientName = spm.getString("ClientName", "");
		}

		noCases = (TextView) rootView.findViewById(R.id.tvNoCases);

		setListData();
		lvSupportCasesList
				.setOnRefreshListener(new OnRefreshListener2<SwipeListView>() {

					@Override
					public void onPullDownToRefresh(
							PullToRefreshBase<SwipeListView> refreshView) {
						// TODO Auto-generated method stub
						String label = DateUtils.formatDateTime(
								getActivity(),
								System.currentTimeMillis(),
								DateUtils.FORMAT_SHOW_TIME
										| DateUtils.FORMAT_SHOW_DATE
										| DateUtils.FORMAT_ABBREV_ALL);
						//  Update the LastUpdatedLabel
						refreshView.getLoadingLayoutProxy()
								.setLastUpdatedLabel(label);


						if(!spm.getString("Search", "").isEmpty()){
							search = spm.getString("Search", "");
						} else {
							search = "";
						}

						if(!spm.getString("Status", "").isEmpty()){
							status = spm.getString("Status", "");
						} else {
							status = "Open";
						}

						if(!spm.getString("Client", "").isEmpty()){
							client = spm.getString("Client", "");
						} else {
							client = ClientName;
						}

						if(!spm.getString("Type", "").isEmpty()){
							type = spm.getString("Type", "");
						} else {
							type = "Support";
						}


						if (spm.getBoolean("MyCases", false)) {
							isUser = true;
						} else {
							isUser = false;
						}

						if (spm.getBoolean("DateRange", false)) {
							fDate = spm.getString("FromDate", "");
							tDate = spm.getString("ToDate", "");
						} else {

							fDate = "1800-01-01";
							tDate = "2222-02-01";
						}

						refreshData(getActivity(), status, client, type, search, fDate, tDate, spm.getInt("UserID", 0), true, isUser, spm.getBoolean("IsSupport", false));
					}

					@Override
					public void onPullUpToRefresh(
							PullToRefreshBase<SwipeListView> refreshView) {
					}
				});
        return rootView;
	}


	private class AsyncCallWS extends AsyncTask<String, Void, Void> {

		Context context;
		ProgressDialog progressDialog;
		String caseStatus;
		String client;
		String type;
		String search;
		String from;
		String to;
        int userID;
        boolean isRefresh;
        boolean isUserCases;
        int loggedUser;
        boolean isSupport;

		public AsyncCallWS(Context mContext, String mcaseStatus, String mClient, String mType, String mSearch, String mFrom, String mTo, int mUserID, boolean mIsRefresh, boolean mIsUserCases, boolean mSupport){
			this.context = mContext;
			this.caseStatus = mcaseStatus;
			this.client = mClient;
			this.type = mType;
			this.search = mSearch;
			this.from = mFrom;
			this.to = mTo;
            this.userID = mUserID;
            this.isRefresh = mIsRefresh;
            this.isUserCases = mIsUserCases;
            this.isSupport = mSupport;
		}

		@Override
		protected void onPreExecute() {
			listItemInfo.clear();
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
                if(!isRefresh) {
                    progressDialog.show();
                }
			}

		}

		@Override
        protected Void doInBackground(String... params) {
            Log.i(TAG, "doInBackground");
            //Create request
    	    SoapObject request = new SoapObject(Constants.NAMESPACE, METHOD_NAME);
			//Property which holds input parameters
			PropertyInfo supportCasesPI = new PropertyInfo();
			supportCasesPI.setName("ClientName");
			supportCasesPI.setValue(client);
			supportCasesPI.setType(String.class);
			request.addProperty(supportCasesPI);

    	    supportCasesPI = new PropertyInfo();
    	    supportCasesPI.setName("status");
    	    supportCasesPI.setValue(caseStatus);
    	    supportCasesPI.setType(String.class);
    	    request.addProperty(supportCasesPI);

    	    supportCasesPI = new PropertyInfo();
    	    supportCasesPI.setName("viewOption");
    	    supportCasesPI.setValue(type);
    	    supportCasesPI.setType(String.class);
    	    request.addProperty(supportCasesPI);

    	    supportCasesPI = new PropertyInfo();
    	    supportCasesPI.setName("search");
    	    supportCasesPI.setValue(search);
    	    supportCasesPI.setType(String.class);
    	    request.addProperty(supportCasesPI);

            if(isUserCases){
                loggedUser = userID;
            } else {
                loggedUser = 0;
            }

    	    supportCasesPI = new PropertyInfo();
    	    supportCasesPI.setName("supportUserID");
    	    supportCasesPI.setValue(loggedUser);
    	    supportCasesPI.setType(int.class);
    	    request.addProperty(supportCasesPI);

    	    supportCasesPI = new PropertyInfo();
    	    supportCasesPI.setName("isSupport");
    	    supportCasesPI.setValue(isSupport);
    	    supportCasesPI.setType(Boolean.class);
    	    request.addProperty(supportCasesPI);

    	    supportCasesPI = new PropertyInfo();
    	    supportCasesPI.setName("loggedUserId");
    	    supportCasesPI.setValue(userID);
    	    supportCasesPI.setType(int.class);
    	    request.addProperty(supportCasesPI);

    	    supportCasesPI = new PropertyInfo();
    	    supportCasesPI.setName("begin");
    	    supportCasesPI.setValue(from);
    	    supportCasesPI.setType(Date.class);
    	    request.addProperty(supportCasesPI);

    	    supportCasesPI = new PropertyInfo();
    	    supportCasesPI.setName("end");
    	    supportCasesPI.setValue(to);
    	    supportCasesPI.setType(Date.class);
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
    	        androidHttpTransport.call(SOAP_ACTION, envelope);
                SoapObject response = (SoapObject) envelope.getResponse();
                for (int i = 0; i < response.getPropertyCount(); i++) {

                    Object property = response.getProperty(i);
                    SoapObject info = (SoapObject) property;
                    SupportCases cases = new SupportCases();
                    cases.setCaseNumber(info.getProperty("CaseNumber").toString().trim());
                    cases.setIssue(info.getProperty("Issue").toString().trim());
                    cases.setClient(info.getProperty("Client").toString().trim());
                    cases.setClientContact(info.getProperty("ClientContact").toString().trim());
                    cases.setDateCreated(info.getProperty("DateCreated").toString().trim());
                    cases.setSeverity(info.getProperty("Severity").toString().trim());
                    cases.setStatus(info.getProperty("Status").toString().trim());
                    cases.setAssigned(info.getProperty("SupportUser").toString().trim());
                    cases.setReplied(info.getProperty("Replied").toString().trim());
                    cases.setAttachment(Boolean.valueOf(info.getProperty("HasAttachment").toString().trim()));
					cases.setNotes(Boolean.valueOf(info.getProperty("HasNotes").toString().trim()));
					cases.setPhone(info.getProperty("Phone").toString().trim());
                    listItemInfo.add(cases);
                }

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
                lvSupportCasesList.onRefreshComplete();
                casesAdapter = new CasesAdapter(context,
                        listItemInfo);

				lvSupportCasesList.setAdapter(casesAdapter);
				start+=1;

    		casesAdapter.notifyDataSetChanged();

			if(listItemInfo.isEmpty()){
				noCases.setVisibility(View.VISIBLE);
			} else{
				noCases.setVisibility(View.GONE);
			}
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            Log.i(TAG, "onProgressUpdate");
        }

    }

	public void refreshData(Context context, String caseStatus, String client, String type, String search, String from, String to, int userID, boolean isrefresh, boolean isUser, boolean isSupport) {
		AsyncCallWS mLoadDataTask = new AsyncCallWS(context, caseStatus, client, type, search, from, to, userID, isrefresh, isUser, isSupport);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			mLoadDataTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		} else {
			mLoadDataTask.execute();
		}
	}


	private void setListData() {
		String search;
		String status;
		String client;
		String type;
		String fDate;
		String tDate;
		boolean isUser;

		if(!spm.getString("Search", "").isEmpty()){
			search = spm.getString("Search", "");
		} else {
			search = "";
		}

		if(!spm.getString("Status", "").isEmpty()){
			status = spm.getString("Status", "");
		} else {
			status = "Open";
		}

		if(!spm.getString("Client", "").isEmpty()){
			client = spm.getString("Client", "");
		} else {
			client = ClientName;
		}

		if(!spm.getString("Type", "").isEmpty()){
			type = spm.getString("Type", "");
		} else {
			type = "Support";
		}

		if (spm.getBoolean("MyCases", false)) {
			isUser = true;
		} else {
			isUser = false;
		}


		if (spm.getBoolean("DateRange", false)) {
			fDate = spm.getString("FromDate", "");
			tDate = spm.getString("ToDate", "");
		} else {

			fDate = "1800-01-01";
			tDate = "2222-02-01";
		}



		refreshData(getActivity(), status ,client, type, search, fDate,tDate, spm.getInt("UserID",0), false, isUser, spm.getBoolean("IsSupport", false));
		swipeList.setSwipeListViewListener(new BaseSwipeListViewListener() {
			@Override
			public void onClickFrontView(final int position) {
				Fragment newFragment = new DetailsFragment();

				Bundle args = new Bundle();
				args.putString("CaseID", casesAdapter.getItem(position - 1).getCaseNumber());
				args.putString("ClientContact", casesAdapter.getItem(position - 1).getClientContact());
				args.putString("Severity", casesAdapter.getItem(position - 1).getSeverity());
				newFragment.setArguments(args);
				FragmentTransaction transaction = getFragmentManager().beginTransaction();
				transaction.replace(R.id.linear, newFragment);
				transaction.addToBackStack(null);
				transaction.commit();
            }

			@Override
			public void onClickBackView(final int position) {

			}

			@Override
			public void onOpened(int position, boolean toRight) {
				// TODO Auto-generated method stub
				super.onOpened(position - 1, toRight);
			}

			@Override
			public void onMove(int position, float x) {
				// TODO Auto-generated method stub
				super.onMove(position - 1, x);
			}

			@Override
			public int onChangeSwipeMode(int position) {
				// TODO Auto-generated method stub
				return SwipeListView.SWIPE_MODE_DEFAULT;
			}

			@Override
			public void onStartOpen(int position, int action, boolean right) {
				// TODO Auto-generated method stub
				super.onStartOpen(position - 1, action, right);

			}
		});
		try{
			Display display = getActivity().getWindowManager().getDefaultDisplay();
			Point size = new Point();
			display.getSize(size);
			int width = size.x;
			swipeList.setOffsetLeft(0-width);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		lvSupportCasesList.setLongClickable(true);
		swipeList.setSwipeOpenOnLongPress(false);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

	}

	@Override
	 public void onClick(View v) {

	}


	public void showPopUpForStatus(final Context mContext) {
		LayoutInflater li = LayoutInflater.from(mContext);
		View promptsView = li.inflate(R.layout.dialogscanner3, null);
		caseStatus = "";

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				mContext);

		alertDialogBuilder.setTitle("Update Case Status");

		final ListView lv=(ListView)promptsView.findViewById(R.id.list_status);
		ArrayList<String> statuses = new ArrayList<>();
		statuses.add("Open");
		statuses.add("Closed");
		statuses.add("WCA");
		statuses.add("CR Pending");

		final ArrayAdapter adapter = new ArrayAdapter(mContext,
				android.R.layout.simple_list_item_activated_1, statuses);
		lv.setAdapter(adapter);

		final UpdateCaseStatus up = new UpdateCaseStatus(mContext, CasesAdapter.caseID, spm.getInt("UserID",0));

		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {

				Object o = lv.getItemAtPosition(position);
				caseStatus = o.toString();
							}
		});

		// set prompts.xml to alertdialog builder
		alertDialogBuilder.setView(promptsView)
				.setPositiveButton("OK",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								if (!caseStatus.isEmpty()) {
									up.execute(caseStatus);
								} else {
									Toast.makeText(mContext," Please select case status.", Toast.LENGTH_LONG).show();
								}

							}
						})

				.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						});

				AlertDialog alertDialog = alertDialogBuilder.create();
				alertDialog.show();
	}

	public void showPopUpForSupportUsers(final Context mContext) {
		LayoutInflater li = LayoutInflater.from(mContext);
		View promptsView = li.inflate(R.layout.dialogscanner4, null);
		SupportUserName = "";
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				mContext);

		alertDialogBuilder.setTitle("Assign User");

		final ListView lv=(ListView)promptsView.findViewById(R.id.list_users);




		final ArrayAdapter adapter = new ArrayAdapter(mContext,
				android.R.layout.simple_list_item_activated_1, listItemSupportUsers);
		lv.setAdapter(adapter);

        final UpdateCaseAssignment up = new UpdateCaseAssignment(mContext, CasesAdapter.caseID, spm.getInt("UserID",0));

		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {

				Object o = lv.getItemAtPosition(position);
                SupportUserName = o.toString();
			}
		});

		// set prompts.xml to alertdialog builder
		alertDialogBuilder.setView(promptsView)
				.setPositiveButton("OK",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								if (!SupportUserName.isEmpty()) {
									if (SupportUserName.contains("Assign to Me")) {
										up.execute(spm.getString("UserName", ""));
									} else {
										up.execute(SupportUserName);
									}
								} else {
									Toast.makeText(mContext, " Please select user to assign to case.", Toast.LENGTH_LONG).show();
								}

							}
						})

				.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						});

		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();
	}


	public void showPopUpForPhoneNumbers(final Context mContext, ArrayList<String> phones, String contact) {

		LayoutInflater li = LayoutInflater.from(mContext);
		View promptsView = li.inflate(R.layout.dialogscanner5, null);
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				mContext);
		phoneNum = "";

		final ListView lv=(ListView)promptsView.findViewById(R.id.list_phone);

		final ArrayAdapter adapter = new ArrayAdapter(mContext,
				android.R.layout.simple_list_item_activated_1, phones);
		lv.setAdapter(adapter);


		if(phones.size()>0) {
			lv.setClickable(true);
			alertDialogBuilder.setTitle("Contact " + contact + "?");

			alertDialogBuilder.setView(promptsView)
					.setPositiveButton("OK",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int id) {
									if (!phoneNum.isEmpty()) {
										Intent intent = new Intent("android.intent.action.DIAL");
										intent.setData(Uri.parse("tel: " + phoneNum.substring(0, 10) + ""));
										mContext.startActivity(intent);
									} else {
									Toast.makeText(mContext," Please select phone number.", Toast.LENGTH_LONG).show();
									}
								}
							})

					.setNegativeButton("Cancel",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int id) {
									dialog.cancel();
								}
							});
		} else {
			lv.setClickable(false);
			phones.add("No contact #'s found for \n" + contact + ".");

			alertDialogBuilder.setView(promptsView)

					.setNegativeButton("Cancel",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int id) {
									dialog.cancel();
								}
							});
		}
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {

					Object o = lv.getItemAtPosition(position);
					phoneNum = Utilities.stripNonDigits(o.toString());


				}
			});
        AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();
	}


	public void showPopUpForNotes(final Context mContext, int CaseNumber) {
		CaseNotes cn = new CaseNotes(mContext, CaseNumber);
		cn.execute();


	}

	private class UpdateCaseStatus extends AsyncTask<String, Void, Void> {

		Context context;
		ProgressDialog progressDialog;
		int caseNumber;
		int userID;


		public UpdateCaseStatus(Context mContext, int mCaseNumber, int mUserID) {
			this.context = mContext;
			this.caseNumber = mCaseNumber;
			this.userID = mUserID;
		}


		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if (!isCancelled()) {
				progressDialog = new ProgressDialog(context);
				progressDialog.setMessage("Updating Case Status...");
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

			SoapObject request = new SoapObject(Constants.NAMESPACE, METHOD_NAME2);

			PropertyInfo supportCasesPI = new PropertyInfo();
			supportCasesPI.setName("CaseID");
			supportCasesPI.setValue(caseNumber);
			supportCasesPI.setType(Integer.class);
			request.addProperty(supportCasesPI);

			supportCasesPI = new PropertyInfo();
			supportCasesPI.setName("UserID");
			supportCasesPI.setValue(userID);
			supportCasesPI.setType(Integer.class);
			request.addProperty(supportCasesPI);

			supportCasesPI = new PropertyInfo();
			supportCasesPI.setName("Comments");
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
				//Invole web service
				androidHttpTransport.call(SOAP_ACTION2, envelope);

			} catch (Exception e) {
				e.printStackTrace();
			}


			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			Log.i(TAG, "onPostExecute");
			if (null != progressDialog && (progressDialog.isShowing())) {
				progressDialog.dismiss();
			}


			if(!spm.getString("Search", "").isEmpty()){
				search = spm.getString("Search", "");
			} else {
				search = "";
			}

			if(!spm.getString("Status", "").isEmpty()){
				status = spm.getString("Status", "");
			} else {
				status = "Open";
			}

			if(!spm.getString("Client", "").isEmpty()){
				client = spm.getString("Client", "");
			} else {
				client = ClientName;
			}

			if(!spm.getString("Type", "").isEmpty()){
				type = spm.getString("Type", "");
			} else {
				type = "Support";
			}


			if (spm.getBoolean("MyCases", false)) {
				isUser = true;
			} else {
				isUser = false;
			}

			if (spm.getBoolean("DateRange", false)) {
				fDate = spm.getString("FromDate", "");
				tDate = spm.getString("ToDate", "");
			} else {

				fDate = "1800-01-01";
				tDate = "2222-02-01";
			}

			refreshData(context, status, client, type, search, fDate, tDate, spm.getInt("UserID", 0), true, isUser, spm.getBoolean("IsSupport", false));

		}
	}


	private class ListSupportUsers extends AsyncTask<String, Void, Void> {

		Context context;

		public ListSupportUsers(Context mContext){
			this.context = mContext;
		}

		@Override
		protected void onPreExecute() {
			listItemSupportUsers.clear();
			super.onPreExecute();
			if(!isCancelled()){
			}

		}

		@Override
		protected Void doInBackground(String... params) {
			//Create request
			SoapObject request = new SoapObject(Constants.NAMESPACE, METHOD_NAME3);
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
				SoapObject response = (SoapObject) envelope.getResponse();
				for (int i = 0; i < response.getPropertyCount(); i++) {

					Object property = response.getProperty(i);
					SoapObject info = (SoapObject) property;
					listItemSupportUsers.add(info.getProperty("UserName").toString().trim());
				}

				listItemSupportUsers.add(0, "Assign to Me");



			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {


		}

	}


	private class CaseNotes extends AsyncTask<String, Void, Void> {

		Context context;
		int CaseID;
		ProgressDialog progressDialog;

		public CaseNotes(Context mContext, int mCaseID){
			this.context = mContext;
			this.CaseID = mCaseID;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if(!isCancelled()){
			}
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

		@Override
		protected Void doInBackground(String... params) {
			//Create request
			SoapObject request = new SoapObject(Constants.NAMESPACE, METHOD_NAME5);

			PropertyInfo supportCasesPI = new PropertyInfo();
			supportCasesPI.setName("caseID");
			supportCasesPI.setValue(CaseID);
			supportCasesPI.setType(Integer.class);
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
				androidHttpTransport.call(SOAP_ACTION5, envelope);
				SoapObject response = (SoapObject) envelope.getResponse();
				notes="";
				for (int i = 0; i < response.getPropertyCount(); i++) {

					Object property = response.getProperty(i);
					SoapObject info = (SoapObject) property;
					notes = info.getProperty("ReminderText").toString().trim();
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
			LayoutInflater li = LayoutInflater.from(context);
			View promptsView = li.inflate(R.layout.dialogscanner6, null);

			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
					context);

			alertDialogBuilder.setTitle("Case Notes");

			etNotes=(EditText)promptsView.findViewById(R.id.etNotes);
			etNotes.setText(notes);




			// set prompts.xml to alertdialog builder
			alertDialogBuilder.setView(promptsView)
					.setPositiveButton("Save",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int id) {

									InsertReminder ir = new InsertReminder(context, CasesAdapter.caseID, spm.getInt("UserID", 0), etNotes.getText().toString());
									ir.execute();


								}
							})

					.setNeutralButton("Delete",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int id) {

									DeleteReminder ir = new DeleteReminder(context, CasesAdapter.caseID);
									ir.execute();
								}
							})

					.setNegativeButton("Cancel",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int id) {
									dialog.cancel();
								}
							});

			AlertDialog alertDialog = alertDialogBuilder.create();
			alertDialog.show();


		}

	}

    private class UpdateCaseAssignment extends AsyncTask<String, Void, Void> {

        Context context;
        ProgressDialog progressDialog;
        int caseNumber;
        int userID;


        public UpdateCaseAssignment(Context mContext, int mCaseNumber, int mUserID) {
            this.context = mContext;
            this.caseNumber = mCaseNumber;
            this.userID = mUserID;
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (!isCancelled()) {
                progressDialog = new ProgressDialog(context);
                progressDialog.setMessage("Updating Case Status...");
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

            SoapObject request = new SoapObject(Constants.NAMESPACE, METHOD_NAME4);

            PropertyInfo supportCasesPI = new PropertyInfo();
            supportCasesPI.setName("CaseID");
            supportCasesPI.setValue(caseNumber);
            supportCasesPI.setType(Integer.class);
            request.addProperty(supportCasesPI);

            supportCasesPI = new PropertyInfo();
            supportCasesPI.setName("SupportUserID");
            supportCasesPI.setValue(userID);
            supportCasesPI.setType(Integer.class);
            request.addProperty(supportCasesPI);

            supportCasesPI = new PropertyInfo();
            supportCasesPI.setName("AssignedUser");
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
            if (null != progressDialog && (progressDialog.isShowing())) {
                progressDialog.dismiss();
            }


            if(!spm.getString("Search", "").isEmpty()){
                search = spm.getString("Search", "");
            } else {
                search = "";
            }

            if(!spm.getString("Status", "").isEmpty()){
                status = spm.getString("Status", "");
            } else {
                status = "Open";
            }

            if(!spm.getString("Client", "").isEmpty()){
                client = spm.getString("Client", "");
            } else {
                client = ClientName;
            }

            if(!spm.getString("Type", "").isEmpty()){
                type = spm.getString("Type", "");
            } else {
                type = "Support";
            }


            if (spm.getBoolean("MyCases", false)) {
                isUser = true;
            } else {
                isUser = false;
            }

            if (spm.getBoolean("DateRange", false)) {
                fDate = spm.getString("FromDate", "");
                tDate = spm.getString("ToDate", "");
            } else {

                fDate = "1800-01-01";
                tDate = "2222-02-01";
            }

            refreshData(context, status, client, type, search, fDate, tDate, spm.getInt("UserID", 0), true, isUser, spm.getBoolean("IsSupport", false));

        }
    }

	public class InsertReminder extends AsyncTask<String, Void, Void> {

		Context context;
		ProgressDialog progressDialog;
		int caseID;
		int userID;
		String notes;


		public InsertReminder(Context mContext, int mCaseID, int mUserID, String mNotes){
			this.context = mContext;
			this.caseID = mCaseID;
			this.userID = mUserID;
			this.notes = mNotes;
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
			SoapObject request = new SoapObject(Constants.NAMESPACE, METHOD_NAME6);
			PropertyInfo supportCasesPI = new PropertyInfo();
			supportCasesPI.setName("CaseID");
			supportCasesPI.setValue(caseID);
			supportCasesPI.setType(Integer.class);
			request.addProperty(supportCasesPI);

			supportCasesPI = new PropertyInfo();
			supportCasesPI.setName("UserID");
			supportCasesPI.setValue(userID);
			supportCasesPI.setType(Integer.class);
			request.addProperty(supportCasesPI);

			supportCasesPI = new PropertyInfo();
			supportCasesPI.setName("reminderText");
			supportCasesPI.setValue(notes);
			supportCasesPI.setType(String.class);
			request.addProperty(supportCasesPI);

			supportCasesPI = new PropertyInfo();
			supportCasesPI.setName("sentText");
			supportCasesPI.setValue(false);
			supportCasesPI.setType(Boolean.class);
			request.addProperty(supportCasesPI);

			supportCasesPI = new PropertyInfo();
			supportCasesPI.setName("sendEmail");
			supportCasesPI.setValue(true);
			supportCasesPI.setType(Boolean.class);
			request.addProperty(supportCasesPI);

			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);
			envelope.dotNet = true;
			envelope.setOutputSoapObject(request);
			HttpTransportSE androidHttpTransport = new HttpTransportSE(Constants.URL);

			try {
				androidHttpTransport.call(SOAP_ACTION6, envelope);

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
		}

	}

	public class DeleteReminder extends AsyncTask<String, Void, Void> {

		Context context;
		ProgressDialog progressDialog;
		int caseID;

		public DeleteReminder(Context mContext, int mCaseID){
			this.context = mContext;
			this.caseID = mCaseID;
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
			SoapObject request = new SoapObject(Constants.NAMESPACE, METHOD_NAME7);
			PropertyInfo supportCasesPI = new PropertyInfo();
			supportCasesPI.setName("CaseID");
			supportCasesPI.setValue(caseID);
			supportCasesPI.setType(Integer.class);
			request.addProperty(supportCasesPI);

			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);
			envelope.dotNet = true;
			envelope.setOutputSoapObject(request);
			HttpTransportSE androidHttpTransport = new HttpTransportSE(Constants.URL);

			try {
				androidHttpTransport.call(SOAP_ACTION7, envelope);

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
		}

	}
}