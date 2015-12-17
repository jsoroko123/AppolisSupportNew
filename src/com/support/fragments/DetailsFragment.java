package com.support.fragments;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.appolissupport.MainActivity;
import com.example.appolissupport.R;
import com.support.adapters.AttachAdapter;
import com.support.adapters.DetailsAdapter;
import com.support.objects.Attachments;
import com.support.objects.Details;
import com.support.utilities.Constants;
import com.support.utilities.FileUpload;
import com.support.utilities.SharedPreferenceManager;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class DetailsFragment extends Fragment implements OnClickListener, OnItemClickListener, CompoundButton.OnCheckedChangeListener {
    private final String METHOD_NAME = "GetCaseDetails";
    private final String SOAP_ACTION = Constants.NAMESPACE+METHOD_NAME;
    private final String METHOD_NAME2 = "GetCaseAttachments";
    private final String SOAP_ACTION2 = Constants.NAMESPACE+METHOD_NAME2;
    private final String METHOD_NAME3 = "UploadImage";
    private final String SOAP_ACTION3 = Constants.NAMESPACE+METHOD_NAME3;
    private final String METHOD_NAME4 = "insSupportCaseResponse";
    private final String SOAP_ACTION4 = Constants.NAMESPACE+METHOD_NAME4;
    private final String METHOD_NAME5 = "SendEmail";
    private final String SOAP_ACTION5 = Constants.NAMESPACE+METHOD_NAME5;
    public static String TAG = "PGGURU";
    public static String caseNumber;
    private String clientContact;
    private static ListView lvDetailsList;
    private static ListView lvAttachList;
    private Button btnSubmit;
    private ImageView img;
    private ArrayList<Details> listItemInfo = new ArrayList<Details>();
    private ArrayList<Attachments> listAttachments = new ArrayList<Attachments>();
    private DetailsAdapter detailsAdapter = null;
    private AttachAdapter attachAdapter = null;
    public static final int DIALOG_DOWNLOAD_PROGRESS = 0;
    private ProgressDialog mProgressDialog;
    private ViewGroup hiddenPanel;
    private int attachcount = 0;
    private boolean isUP = true;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
    private SharedPreferenceManager spm;
    private String support;
    private String severity;
    private static final int SELECT_PHOTO = 100;
    private EditText etResponse;
    private TextView tvClientUser;
    private TextView tvSeverity;
    private int caseUserID;
    /**
     * Returns a new instance of this fragment for the given section number.
     */

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);
        getActivity().getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        getActivity().getActionBar().setDisplayShowTitleEnabled(true);
        getActivity().getActionBar().setTitle(MainActivity.FragPageTitle);

    }

    public DetailsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.activity_details_fragment, container,
                false);

        hiddenPanel = (ViewGroup)rootView.findViewById(R.id.hidden_panel);

        lvDetailsList = (ListView)rootView.findViewById(R.id.case_details_list2);
        lvDetailsList.setOnItemClickListener(this);

        img = (ImageView)rootView.findViewById(R.id.img_ex);
        img.setOnClickListener(this);

        etResponse = (EditText)rootView.findViewById(R.id.txt_submit_response);

        tvClientUser = (TextView)rootView.findViewById(R.id.tv_client_user);
        tvSeverity = (TextView)rootView.findViewById(R.id.tv_sever);


        lvAttachList = (ListView)rootView.findViewById(R.id.attach_list);
        lvAttachList.setOnItemClickListener(this);

        // ListView Item Click Listener
        lvAttachList.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {

                String value = attachAdapter.getItem(position).getFileName();

                if (value == "Add Attachment") {
                    Intent pickIntent = new Intent();
                    pickIntent.setType("image/*");
                    pickIntent.setAction(Intent.ACTION_GET_CONTENT);

                    Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                    String pickTitle = "Select or take a new Picture"; // Or get from strings.xml
                    Intent chooserIntent = Intent.createChooser(pickIntent, pickTitle);
                    chooserIntent.putExtra
                            (
                                    Intent.EXTRA_INITIAL_INTENTS,
                                    new Intent[] { takePhotoIntent }
                            );

                    startActivityForResult(chooserIntent, SELECT_PHOTO);

                } else {
                    DownloadFile(value, "http://wowsupport.appolis.com/attachments/" + value);
                }
            }
        });

        btnSubmit = (Button)rootView.findViewById(R.id.btn_submit_response2);
        btnSubmit.setOnClickListener(this);

        caseNumber = getArguments().getString("CaseID");
        severity = getArguments().getString("Severity");
        clientContact = getArguments().getString("ClientContact");
        tvClientUser.setText(clientContact);
        tvSeverity.setText(severity.substring(0,7));

        MainActivity.FragPageTitle = "Case "+caseNumber;
        setHasOptionsMenu(true);



        spm = new SharedPreferenceManager(getActivity());
        if(spm.getBoolean("IsSupport", false)){
            support = "1";
        } else {
            support = "0";
        }


        refresh(getActivity(), caseNumber, true);
        return rootView;
    }

    private static final int FILE_SELECT_CODE = 0;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == getActivity().RESULT_OK) {
            if (requestCode == SELECT_PHOTO){
                Uri selectedImage = data.getData();
                Bitmap bitmap = null;
                try {
                    bitmap = FileUpload.RotateBitmap(FileUpload.decodeUri(selectedImage, getActivity()), 90);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                String img64 = FileUpload.encodeTobase64(bitmap);
                String currentDateandTime = sdf.format(new Date());
                UploadFile up = new UploadFile(getActivity(), img64, currentDateandTime + ".png", caseNumber, support);
                up.execute();
            }
        }

    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.btn_submit_response2:
                if(!etResponse.getText().toString().isEmpty()) {
                    StringBuilder sb = new StringBuilder();
                    for(Details dets : listItemInfo){
                        sb.append(dets.getResponse());
                    }


                    InsertResponse inr = new InsertResponse(getActivity(), Integer.valueOf(caseNumber), spm.getInt("UserID", 0), Integer.valueOf(support), etResponse.getText().toString(),sb.toString(), false);
                    inr.execute();
                    InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
                    //Find the currently focused view, so we can grab the correct window token from it.
                    View view = getActivity().getCurrentFocus();
                    //If no view currently has focus, create a new one, just so we can grab a window token from it
                    if(view == null) {
                        view = new View(getActivity());
                    }
                    inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
                } else {
                    Toast.makeText(getActivity(), "Response Cannot Be Blank.", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.img_ex:
                if(attachcount>0) {
                    if (isUP) {
                        isUP=false;
                        Animation bottomUp = AnimationUtils.loadAnimation(getActivity(),
                                R.anim.bottom_up);

                        hiddenPanel.startAnimation(bottomUp);
                        hiddenPanel.setVisibility(View.VISIBLE);
                    } else{
                        isUP = true;
                        Animation bottomUp = AnimationUtils.loadAnimation(getActivity(),
                                R.anim.bottom_down);

                        hiddenPanel.startAnimation(bottomUp);
                        hiddenPanel.setVisibility(View.GONE);
                    }

                } else {
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
                break;

            default:
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(getActivity(), "Response Cannot Be Blank.", Toast.LENGTH_SHORT).show();

    }  //Insert Response
    private class InsertResponse extends AsyncTask<String, Void, Void> {

        Context context;
        ProgressDialog progressDialog;
        int caseNumber;
        int userID;
        int support;
        String comments;
        boolean retain;
        String builder;

        public InsertResponse(Context mContext, int mCaseNumber, int mUserID, int mSupport, String mComments, String mBuilder, boolean mRetain){
            this.context = mContext;
            this.caseNumber = mCaseNumber;
            this.userID = mUserID;
            this.support = mSupport;
            this.comments = mComments;
            this.retain = mRetain;
            this.builder = mBuilder;
        }



        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if(!isCancelled()){
                progressDialog = new ProgressDialog(context);
                progressDialog.setMessage("Submitting to Case...");
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

            String currentDateandTime = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a z").format(new Date());

            //call 1
            SoapObject request = new SoapObject(Constants.NAMESPACE, METHOD_NAME4);
            //Property which holds input parameters
            PropertyInfo supportCasesPI = new PropertyInfo();
            supportCasesPI.setName("iCaseID");
            supportCasesPI.setValue(caseNumber);
            supportCasesPI.setType(Integer.class);
            request.addProperty(supportCasesPI);

            supportCasesPI = new PropertyInfo();
            supportCasesPI.setName("iUserID");
            supportCasesPI.setValue(userID);
            supportCasesPI.setType(Integer.class);
            request.addProperty(supportCasesPI);

            supportCasesPI = new PropertyInfo();
            supportCasesPI.setName("isAppolisUser");
            supportCasesPI.setValue(support);
            supportCasesPI.setType(Integer.class);
            request.addProperty(supportCasesPI);

            supportCasesPI = new PropertyInfo();
            supportCasesPI.setName("sComments");
            supportCasesPI.setValue(currentDateandTime + "\n" + spm.getString("UserName", "") + " Wrote:\n" + comments);
            supportCasesPI.setType(String.class);
            request.addProperty(supportCasesPI);

            supportCasesPI = new PropertyInfo();
            supportCasesPI.setName("bRetainActionNeededByAppolis");
            supportCasesPI.setValue(retain);
            supportCasesPI.setType(Boolean.class);
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

            //Call 2


            SoapObject request2 = new SoapObject(Constants.NAMESPACE, METHOD_NAME5);
            //Property which holds input parameters
            PropertyInfo supportCasesPI2 = new PropertyInfo();
            supportCasesPI2.setName("iUserID");
            supportCasesPI2.setValue(caseUserID);
            supportCasesPI2.setType(Integer.class);
            request2.addProperty(supportCasesPI2);

            supportCasesPI2 = new PropertyInfo();
            supportCasesPI2.setName("iCommentID");
            supportCasesPI2.setValue(caseNumber);
            supportCasesPI2.setType(Integer.class);
            request2.addProperty(supportCasesPI2);

            supportCasesPI2 = new PropertyInfo();
            supportCasesPI2.setName("comments");
            supportCasesPI2.setValue("Issue: "+listItemInfo.get(0).getSubject()+builder.toString().replace("Issue:","")+"\n" +currentDateandTime + "\n" + spm.getString("UserName", "") + " Wrote:\n" + comments+"\n");
            supportCasesPI2.setType(String.class);
            request2.addProperty(supportCasesPI2);

            supportCasesPI2 = new PropertyInfo();
            supportCasesPI2.setName("severity");
            supportCasesPI2.setValue(severity);
            supportCasesPI2.setType(String.class);
            request2.addProperty(supportCasesPI2);

            SoapSerializationEnvelope envelope2 = new SoapSerializationEnvelope(
                    SoapEnvelope.VER11);
            envelope2.dotNet = true;
            //Set output SOAP object
            envelope2.setOutputSoapObject(request2);
            //Create HTTP call object
            HttpTransportSE androidHttpTransport2 = new HttpTransportSE(Constants.URL);

            try {
                //Invole web service
                androidHttpTransport2.call(SOAP_ACTION5, envelope2);

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

            etResponse.setText("");

            refresh(getActivity(), String.valueOf(caseNumber), true);

        }

    }

    private class UploadFile extends AsyncTask<String, Void, Void> {

        Context context;
        ProgressDialog progressDialog;
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
                progressDialog = new ProgressDialog(context);
                progressDialog.setMessage("Uploading... Please Wait.");
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
            supportCasesPI.setValue(caseNumber);
            supportCasesPI.setType(String.class);
            request.addProperty(supportCasesPI);

            supportCasesPI = new PropertyInfo();
            supportCasesPI.setName("Support");
            supportCasesPI.setValue(support);
            supportCasesPI.setType(String.class);
            request.addProperty(supportCasesPI);

            supportCasesPI = new PropertyInfo();
            supportCasesPI.setName("tempID");
            supportCasesPI.setValue("");
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
            if(null != progressDialog && (progressDialog.isShowing())){
                progressDialog.dismiss();
            }

            listAttachments.clear();
            AsyncCallWS2 mLoadDataTask = new AsyncCallWS2(context);
            mLoadDataTask.execute();
            attachAdapter.notifyDataSetChanged();
            Animation bottomUp = AnimationUtils.loadAnimation(getActivity(),
                    R.anim.bottom_up);

            hiddenPanel.startAnimation(bottomUp);
            hiddenPanel.setVisibility(View.VISIBLE);
            isUP = false;

        }

    }

    private class AsyncCallWS extends AsyncTask<String, Void, Void> {

        Context context;
        ProgressDialog progressDialog;
        String caseNum;
        boolean showProgress = false;

        public AsyncCallWS(Context mContext, String mCaseNum, boolean mShowProgress){
            this.context = mContext;
            this.caseNum = mCaseNum;
            this.showProgress = mShowProgress;

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
                if(showProgress) {
                    progressDialog.show();
                }
            }
        }

        @Override
        protected Void doInBackground(String... params) {
            SoapObject request = new SoapObject(Constants.NAMESPACE, METHOD_NAME);
            //Property which holds input parameters
            PropertyInfo supportCasesPI = new PropertyInfo();
            supportCasesPI.setName("iCaseID");
            supportCasesPI.setValue(caseNum);
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
                androidHttpTransport.call(SOAP_ACTION, envelope);
                SoapObject response = (SoapObject) envelope.getResponse();
                for (int i = 0; i < response.getPropertyCount(); i++) {

                    Object property = response.getProperty(i);
                    SoapObject info = (SoapObject) property;
                    Details cds = new Details();
                    cds.setUserID(Integer.valueOf(info.getProperty("UserID").toString().trim()));
                    cds.setSupportUserID(Integer.valueOf(info.getProperty("SupportUserID").toString().trim()));
                    cds.setResponseID(Integer.valueOf(info.getProperty("ResponseID").toString().trim()));
                    cds.setDateCreated(info.getProperty("DateCreated").toString().trim());
                    cds.setName(info.getProperty("Name").toString().trim());
                    String strippedResponse = info.getProperty("Response").toString().trim().replaceAll("\\<.*?>","").trim();
                    cds.setResponse(strippedResponse);
                    cds.setResponse(info.getProperty("Response").toString().trim());
                    cds.setSubject(info.getProperty("Subject").toString().trim());
                    cds.setPhone(info.getProperty("Phone").toString().trim());
                    cds.setClientPhone(info.getProperty("ClientPhone").toString().trim());
                    listItemInfo.add(cds);

                    caseUserID = cds.getUserID();


                }



            } catch (Exception e) {

                e.printStackTrace();
            }

            SoapObject request2 = new SoapObject(Constants.NAMESPACE, METHOD_NAME2);
            //Property which holds input parameters
            PropertyInfo supportCasesPI2 = new PropertyInfo();
            supportCasesPI2.setName("iCaseID");
            supportCasesPI2.setValue(caseNumber);
            supportCasesPI2.setType(Integer.class);
            request2.addProperty(supportCasesPI2);

            SoapSerializationEnvelope envelope2 = new SoapSerializationEnvelope(
                    SoapEnvelope.VER11);
            envelope2.dotNet = true;
            envelope2.setOutputSoapObject(request2);
            HttpTransportSE androidHttpTransport2 = new HttpTransportSE(Constants.URL);

            try {
                androidHttpTransport2.call(SOAP_ACTION2, envelope2);
                SoapObject response2 = (SoapObject) envelope2.getResponse();
                for (int i = 0; i < response2.getPropertyCount(); i++) {
                    attachcount++;
                    Object property2 = response2.getProperty(i);
                    SoapObject info2 = (SoapObject) property2;
                    Attachments att = new Attachments();
                    att.setFileName(info2.getProperty("FileName").toString().trim());
                    att.setDateName(info2.getProperty("Date").toString().trim());

                    listAttachments.add(att);
                }

                Attachments att2 = new Attachments();
                att2.setFileName("Add Attachment");
                att2.setDateName("");
                listAttachments.add(listAttachments.size(), att2);


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
            detailsAdapter = new DetailsAdapter(context,
                    listItemInfo);

            lvDetailsList.setAdapter(detailsAdapter);
            detailsAdapter.notifyDataSetChanged();
            lvDetailsList.setSelection(lvDetailsList.getCount() - 1);


            attachAdapter = new AttachAdapter(context,
                    listAttachments);

            lvAttachList.setAdapter(attachAdapter);
            attachAdapter.notifyDataSetChanged();
            lvAttachList.setSelection(lvAttachList.getCount() - 1);
        }
    }

    private class AsyncCallWS2 extends AsyncTask<String, Void, Void> {

        Context context;
        ProgressDialog progressDialog;

        public AsyncCallWS2(Context mContext){
            this.context = mContext;

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
                progressDialog.show();
            }
        }

        @Override
        protected Void doInBackground(String... params) {

            SoapObject request2 = new SoapObject(Constants.NAMESPACE, METHOD_NAME2);
            //Property which holds input parameters
            PropertyInfo supportCasesPI2 = new PropertyInfo();
            supportCasesPI2.setName("iCaseID");
            supportCasesPI2.setValue(caseNumber);
            supportCasesPI2.setType(Integer.class);
            request2.addProperty(supportCasesPI2);

            SoapSerializationEnvelope envelope2 = new SoapSerializationEnvelope(
                    SoapEnvelope.VER11);
            envelope2.dotNet = true;
            //Set output SOAP object
            envelope2.setOutputSoapObject(request2);
            //Create HTTP call object
            HttpTransportSE androidHttpTransport2 = new HttpTransportSE(Constants.URL);

            try {
                //Invole web service
                androidHttpTransport2.call(SOAP_ACTION2, envelope2);
                SoapObject response2 = (SoapObject) envelope2.getResponse();
                for (int i = 0; i < response2.getPropertyCount(); i++) {
                    attachcount++;
                    Object property2 = response2.getProperty(i);
                    SoapObject info2 = (SoapObject) property2;
                    Attachments att = new Attachments();
                    att.setFileName(info2.getProperty("FileName").toString().trim());
                    att.setDateName(info2.getProperty("Date").toString().trim());

                    listAttachments.add(att);
                }

                Attachments att2 = new Attachments();
                att2.setFileName("Add Attachment");
                att2.setDateName("");
                listAttachments.add(listAttachments.size(), att2);


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
            attachAdapter = new AttachAdapter(context,
                    listAttachments);

            lvAttachList.setAdapter(attachAdapter);
            attachAdapter.notifyDataSetChanged();
            lvAttachList.setSelection(lvAttachList.getCount() - 1);
        }
    }

    public void refresh(Context context, String caseNum, boolean showProgress) {
        AsyncCallWS mLoadDataTask = new AsyncCallWS(context,caseNum, showProgress);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            mLoadDataTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            mLoadDataTask.execute();
        }
    }


    private void DownloadFile(String filename, String url) {

        new DownloadFileAsync(getActivity(), filename).execute(url, filename);
    }
}

    class DownloadFileAsync extends AsyncTask<String, String, String> {
        ProgressDialog progressDialog;
        Context context;
        String filepath;

        public DownloadFileAsync(Context mContext, String mfile){
            this.context = mContext;
            this.filepath = mfile;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //showDialog(DIALOG_DOWNLOAD_PROGRESS);
            if (!isCancelled()) {
                progressDialog = new ProgressDialog(context);
                progressDialog.setMessage("Downloading...");
                progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {

                    @Override
                    public void onCancel(DialogInterface dialog) {
                        cancel(true);
                    }
                });
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.setCancelable(false);
                progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                progressDialog.show();

            }

        }

        @Override
        protected String doInBackground(String... aurl) {
            int count;

            try {

                URL url = new URL(aurl[0].replace(" ","%20").replace("#",""));
                String file = aurl[1];
                File root = Environment.getExternalStorageDirectory();
                File dir = new File(root.getAbsolutePath());
                URLConnection conexion = url.openConnection();
                conexion.connect();

                int lenghtOfFile = conexion.getContentLength();
                Log.d("ANDRO_ASYNC", "Lenght of file: " + lenghtOfFile);

                InputStream input = new BufferedInputStream(url.openStream());
                OutputStream output = new FileOutputStream(dir+"/"+file);


                byte data[] = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    publishProgress(""+(int)((total*100)/lenghtOfFile));
                    output.write(data, 0, count);
                }

                output.flush();
                output.close();
                input.close();
            } catch (Exception e) {

                String a = e.getMessage();
                a=e.getLocalizedMessage();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(String... progress) {
            Log.d("ANDRO_ASYNC", progress[0]);
           progressDialog.setProgress(Integer.parseInt(progress[0]));
        }

        @Override
        protected void onPostExecute(String unused) {
            String filetype;
           progressDialog.dismiss();
            File file = new File(Environment.getExternalStorageDirectory(),
                    filepath);
            Uri path = Uri.fromFile(file);
            Intent pdfOpenintent = new Intent(Intent.ACTION_VIEW);
            pdfOpenintent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            pdfOpenintent.setDataAndType(path, "image/*");
            try {
                context.startActivity(pdfOpenintent);
            } catch (ActivityNotFoundException e) {

            }
        }


     }











