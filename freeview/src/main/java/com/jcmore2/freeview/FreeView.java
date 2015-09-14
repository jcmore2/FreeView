package com.jcmore2.freeview;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

/**
 * Created by jcmore2 on 30/7/15.
 *
 * FreeView create a floating view that you can use inside and outside your app
 */
public class FreeView {

    private static final String TAG = "FreeView";

    private static Application application;
    private static FreeView sInstance;
    protected static Context mContext;


    private static Intent service;
    protected static FreeViewListener mListener;

    protected static View contentView;
    protected static boolean dismissOnBackground = true;


    /**
     * Init the FreeView instance
     *
     * @param context
     */
    public static FreeView init(Context context) {

        if (sInstance == null) {
            sInstance = new FreeView(context);
        }

        return sInstance;
    }

    /**
     * get the FreeView instance
     * @return
     */
    public static FreeView get(){
        if (sInstance == null) {
            throw new IllegalStateException(
                    "FreeView is not initialised - invoke " +
                            "at least once with parameterised init/get");
        }
        return sInstance;
    }

    /**
     * Set respurce view
     * @param resourceLayout
     * @return
     */
    public static FreeView withView(int resourceLayout){
        LayoutInflater layoutInflater = (LayoutInflater) application.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        contentView = layoutInflater.inflate(resourceLayout, null);
        return sInstance;
    }

    /**
     * Set view
     * @param view
     * @return
     */
    public static FreeView withView(View view){
        contentView = view;
        return sInstance;
    }

    /**
     * Dismiss when app goes to background (Default true)
     * @param dismiss
     * @return
     */
    public static FreeView dismissOnBackground(boolean dismiss){
        dismissOnBackground = dismiss;
        return sInstance;
    }

    /**
     * Constructor
     *
     * @param context
     */
    private FreeView(Context context) {
        try {
            if (context == null) {
                Log.e(TAG, "Cant init, context must not be null");
            } else {
                mContext = context;
                application = (Application) context.getApplicationContext();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Show view
     */
    public static void showFreeView(){

        showFreeView(null);

    }

    /**
     * Show view with callback
     */
    public static void showFreeView(FreeViewListener listener){

        if(contentView == null)
            throw new RuntimeException("It´ necessary call '.withView()' method");

        mListener = listener;
        service = new Intent(application, FreeViewService.class);
        application.startService(service);

    }

    /**
     * Dismiss View
     */
    public static void dismissFreeView(){
        if(service!=null)
            application.stopService(service);
    }


    public interface FreeViewListener {
        void onShow();
        void onDismiss();
        void onClick();
    }
}
