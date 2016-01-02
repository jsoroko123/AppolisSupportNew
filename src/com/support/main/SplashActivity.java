package com.support.main;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.ImageView;

import com.example.appolissupport.R;
import com.support.utilities.SharedPreferenceManager;

public class SplashActivity extends Activity {

    private SharedPreferenceManager spm;
    private int sleepTime;
    private int countForSplash;
    ImageView img;
    private AnimationDrawable frameAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash);
        spm = new SharedPreferenceManager(this);
        img = (ImageView) findViewById(R.id.img_logo);
        img.setBackgroundResource(R.drawable.logo_blur);

        frameAnimation = (AnimationDrawable) img.getBackground();
        //set true if you want to animate only once
        frameAnimation.setOneShot(true);
        frameAnimation.start();


        Thread WelcomeScreen = new Thread() {

            public void run() {

                try {

                    if(spm.getInt("SplashCount", 0) == 0) {

                        sleepTime = 6000;
                    }
                    else if(spm.getInt("SplashCount", 0) == 5){
                            spm.saveInt("SplashCount",0);
                        sleepTime = 6000;

                    } else {
                        sleepTime = 6000;
                    }

                    spm.saveInt("SplashCount", spm.getInt("SplashCount", 0) + 1);

                    sleep(sleepTime);

                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                finally {
                    finish();
                }
            }
        };
        WelcomeScreen.start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_splash, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
