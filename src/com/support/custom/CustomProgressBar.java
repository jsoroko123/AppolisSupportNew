package com.support.custom;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import com.example.appolissupport.R;

/**
 * Created by Jeffery on 12/30/2015.
 */
public class CustomProgressBar extends Dialog {
    private static CustomProgressBar mCustomProgressbar;
    private CustomProgressBar mProgressbar;
    private OnDismissListener mOnDissmissListener;

//    ImageView img;
//    private AnimationDrawable frameAnimation;

    private CustomProgressBar(Context context) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_progressbar);
        this.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

//        img = (ImageView) findViewById(R.id.img_logo2);
//        img.setBackgroundResource(R.drawable.logo_blur);
//
//        frameAnimation = (AnimationDrawable) img.getBackground();
//        //set true if you want to animate only once
//        frameAnimation.setOneShot(true);
//        frameAnimation.start();
    }

    public CustomProgressBar(Context context, Boolean instance) {
        super(context);
        mProgressbar = new CustomProgressBar(context);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (mOnDissmissListener != null) {
            mOnDissmissListener.onDismiss(this);
        }
    }

    public static void showProgressBar(Context context, boolean cancelable) {
        showProgressBar(context, cancelable, null);
    }

    public static void showProgressBar(Context context, boolean cancelable, String message) {
        if (mCustomProgressbar != null && mCustomProgressbar.isShowing()) {
            mCustomProgressbar.cancel();
        }
        mCustomProgressbar = new CustomProgressBar(context);
        mCustomProgressbar.setCancelable(cancelable);

        mCustomProgressbar.show();

    }

    public static void showProgressBar(Context context, OnDismissListener listener) {

        if (mCustomProgressbar != null && mCustomProgressbar.isShowing()) {
            mCustomProgressbar.cancel();
        }
        mCustomProgressbar = new CustomProgressBar(context);
        mCustomProgressbar.setListener(listener);
        mCustomProgressbar.setCancelable(Boolean.TRUE);
        mCustomProgressbar.show();
    }

    public static void hideProgressBar() {
        if (mCustomProgressbar != null) {
            mCustomProgressbar.dismiss();
        }
    }

    private void setListener(OnDismissListener listener) {
        mOnDissmissListener = listener;

    }

    public static void showListViewBottomProgressBar(View view) {
        if (mCustomProgressbar != null) {
            mCustomProgressbar.dismiss();
        }

        view.setVisibility(View.VISIBLE);
    }

    public static void hideListViewBottomProgressBar(View view) {
        if (mCustomProgressbar != null) {
            mCustomProgressbar.dismiss();
        }

        view.setVisibility(View.GONE);
    }

    public void showProgress(Context context, boolean cancelable, String message) {

        if (mProgressbar != null && mProgressbar.isShowing()) {
            mProgressbar.cancel();
        }
        mProgressbar.setCancelable(cancelable);
        mProgressbar.show();
    }

}