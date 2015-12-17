package com.support.main;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

import com.example.appolissupport.R;
import com.support.fragments.CasesFragment;
import com.support.fragments.DetailsFragment;
import com.support.utilities.Constants;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;


public class CaseService extends Service {
    private PowerManager.WakeLock mWakeLock;
    private final String METHOD_NAME = "HighCase";
    private final String SOAP_ACTION = Constants.NAMESPACE + METHOD_NAME;

    private final String METHOD_NAME2 = "HighResponse";
    private final String SOAP_ACTION2 = Constants.NAMESPACE + METHOD_NAME2;
    private int hCase = 0;
    private int hResponse = 0;


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void handleIntent(Intent intent) {
        // obtain the wake lock
        PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "TAG");
        mWakeLock.acquire();

        // check the global background data setting
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        if (!cm.getBackgroundDataSetting()) {
            stopSelf();
            return;
        }
        // jdo the actual work, in a separate thread
        //
        new PollTask().execute();
    }

    private class PollTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            if(hasNumber(MainActivity.FragPageTitle)) {
                SoapObject request = new SoapObject(Constants.NAMESPACE, METHOD_NAME2);
                PropertyInfo supportCasesPI = new PropertyInfo();
                supportCasesPI.setName("caseNum");
                supportCasesPI.setValue(DetailsFragment.caseNumber);
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
                    SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
                    hResponse = Integer.valueOf(response.toString());


                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                SoapObject request = new SoapObject(Constants.NAMESPACE, METHOD_NAME);
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                        SoapEnvelope.VER11);
                envelope.dotNet = true;
                //Set output SOAP object
                envelope.setOutputSoapObject(request);
                //Create HTTP call object
                HttpTransportSE androidHttpTransport = new HttpTransportSE(Constants.URL);

                try {
                    //Fill Client List
                    androidHttpTransport.call(SOAP_ACTION, envelope);
                    SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
                    hCase = Integer.valueOf(response.toString());


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            SharedPreferences prefs = getSharedPreferences("APL_CONFIG",
                    MODE_PRIVATE);
        if(prefs.getBoolean("IsSupport", false)) {
                if (MainActivity.isInForeground()) {
                    if (hasNumber(MainActivity.FragPageTitle)) {
                        if (prefs.getInt("HighResponse", 0) != 0 && prefs.getInt("HighResponse", 0) < hResponse) {
                            //Toast.makeText(getBaseContext(), "sgsdg" + prefs.getInt("HighResponse", 0) + "sgsg" +hResponse, Toast.LENGTH_LONG).show();
                            DetailsFragment df = new DetailsFragment();
                            df.refresh(getApplicationContext(), DetailsFragment.caseNumber, false);
                        }
                    } else {
                        if (prefs.getInt("HighCase", 0) != 0 && prefs.getInt("HighCase", 0) < hCase) {
                            Toast.makeText(getBaseContext(), "New cases have been submitted.", Toast.LENGTH_LONG).show();
                            CasesFragment cs = new CasesFragment();
                            cs.refreshData(getApplicationContext(), "Open", "", "Support", "", "1893-01-01", "2456-07-31", prefs.getInt("UserID", 0), true, false, prefs.getBoolean("IsSupport", false));
                        }
                    }
                } else {
                    if (prefs.getInt("HighCase", 0) != 0 && prefs.getInt("HighCase", 0) < hCase) {
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.putExtra("Reset", "1");
                        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);

                        PendingIntent pIntent = PendingIntent.getActivity(getApplicationContext(), (int) System.currentTimeMillis(), intent, 0);
                        Notification n = new Notification.Builder(getApplicationContext())
                                .setContentTitle("New cases have been submitted.")
                                .setContentText("Click to open.")
                                .setSmallIcon(R.drawable.ic_launcher)
                                .setContentIntent(pIntent)
                                .setAutoCancel(true).build();
//                            .addAction(R.drawable.ic_launcher, "Call", pIntent).build();


                        NotificationManager notificationManager =
                                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

                        notificationManager.notify(0, n);
                    }
                }
            }
            Log.d("Service", "Service");
            stopSelf();
        }
    }

    @Override
    public void onStart(Intent intent, int startId) {
        handleIntent(intent);
    }

    /**
     * This is called on 2.0+ (API level 5 or higher). Returning
     * START_NOT_STICKY tells the system to not restart the service if it is
     * killed because of poor resource (memory/cpu) conditions.
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        handleIntent(intent);
        return START_NOT_STICKY;
    }

    /**
     * In onDestroy() we release our wake lock. This ensures that whenever the
     * Service stops (killed for resources, stopSelf() called, etc.), the wake
     * lock will be released.
     */
    public void onDestroy() {
        super.onDestroy();
        mWakeLock.release();
    }

    private boolean hasNumber(String toCheck) {
        boolean flag = false;
        if(toCheck != null) {
            for (int i = 0; i < toCheck.length(); i++) {
                if ((int) toCheck.charAt(i) > 48 && (int) toCheck.charAt(i) < 58) {
                    flag = true;
                    break;
                }
            }
        }

        return flag;

    }



}




