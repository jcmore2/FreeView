package com.jcmore2.freeview;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;


public class FreeViewService extends Service {

    private static final String TAG = "FreeViewService";

    private WindowManager windowManager;

    private WindowManager.LayoutParams params;
    private WindowManager.LayoutParams paramsF;

    private View mView;

    boolean isViewVisible = false;

    /**
     * Max allowed duration for a "click", in milliseconds.
     */
    private static final int MAX_CLICK_DURATION = 1000;

    /**
     * Max allowed distance to move during a "click", in DP.
     */
    private static final int MAX_CLICK_DISTANCE = 15;

    private long pressStartTime;
    private float pressedX;
    private float pressedY;
    private boolean stayedWithinClickDistance;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        createView();

            BackgroundManager.init(getApplication()).registerListener(new BackgroundManager.Listener() {
                @Override
                public void onBecameForeground(Activity activity) {

                }

                @Override
                public void onBecameBackground(Activity activity) {
                    if (FreeView.dismissOnBackground) {

                        removeView();
                        stopService();
                    }
                }
            });
    }

    /**
     * Create view
     */
    private void createView() {

        mView = FreeView.contentView;

        try {
            mView.setOnTouchListener(new View.OnTouchListener() {
                private int initialX;
                private int initialY;
                private float initialTouchX;
                private float initialTouchY;

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:

                            initialX = paramsF.x;
                            initialY = paramsF.y;
                            initialTouchX = event.getRawX();
                            initialTouchY = event.getRawY();

                            pressStartTime = System.currentTimeMillis();
                            pressedX = event.getX();
                            pressedY = event.getY();
                            stayedWithinClickDistance = true;

                            break;
                        case MotionEvent.ACTION_UP:

                            long pressDuration = System.currentTimeMillis() - pressStartTime;
                            if (pressDuration < MAX_CLICK_DURATION && stayedWithinClickDistance) {
                                FreeView.mListener.onClick();
                            }

                        case MotionEvent.ACTION_POINTER_UP:
                            break;
                        case MotionEvent.ACTION_MOVE:

                            if (stayedWithinClickDistance && distance(pressedX, pressedY, event.getX(), event.getY()) > MAX_CLICK_DISTANCE) {
                                stayedWithinClickDistance = false;
                            }

                            paramsF.x = initialX + (int) (event.getRawX() - initialTouchX);
                            paramsF.y = initialY + (int) (event.getRawY() - initialTouchY);
                            windowManager.updateViewLayout(mView, paramsF);

                            break;
                    }
                    return true;
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "ON TOUCH ERROR--->" + e.getMessage());
        }

        setViewPosition();
        addView();

    }

    private static float distance(float x1, float y1, float x2, float y2) {
        float dx = x1 - x2;
        float dy = y1 - y2;
        float distanceInPx = (float) Math.sqrt(dx * dx + dy * dy);
        return pxToDp(distanceInPx);
    }

    private static float pxToDp(float px) {
        return px / FreeView.mContext.getResources().getDisplayMetrics().density;
    }

    /**
     * Remove view from window
     */
    private void removeView() {
        Log.i(TAG, "removeView");

        if (mView != null && isViewVisible) {
            isViewVisible = false;
            windowManager.removeView(mView);
            if (FreeView.mListener != null) {
                FreeView.dismissOnBackground = true;
                FreeView.mListener.onDismiss();
            }
        }
    }

    /**
     * Add view to window
     */
    private void addView() {
        Log.i(TAG, "addView");

        if (mView != null && !isViewVisible) {
            isViewVisible = true;
            setViewPosition();
            windowManager.addView(mView, params);

            if (FreeView.mListener != null) {
                FreeView.mListener.onShow();
            }
        }
    }

    /**
     * Set initial position
     */
    private void setViewPosition() {

        params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        params.gravity = Gravity.CENTER;
        params.x = 0;
        params.y = 0;

        paramsF = params;
    }

    /**
     * Stop service
     */
    private void stopService() {

        this.stopSelf();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        removeView();
    }


}

