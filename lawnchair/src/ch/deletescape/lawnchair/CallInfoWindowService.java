/*
 *     Copyright (C) 2021 Lawnchair Team.
 *
 *     This file is part of Lawnchair Launcher.
 *
 *     Lawnchair Launcher is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Lawnchair Launcher is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with Lawnchair Launcher.  If not, see <https://www.gnu.org/licenses/>.
 */

package ch.deletescape.lawnchair;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.app.NotificationCompat;
import com.android.launcher3.R;

public class CallInfoWindowService extends Service {
    private WindowManager windowManager;
    private View binding;
    private String name = "", number = "";

    private TextView tvName;

    private LinearLayout adView;

    public void onCreate(){
        super.onCreate();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String CHANNEL_ID = "my_channel_01";
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    "Caller id",
                    NotificationManager.IMPORTANCE_DEFAULT);

            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);

            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("")
                    .setContentText("").build();

            startForeground(1, notification);
        }
    }

    @Override
    public IBinder onBind(Intent intent){
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId){

        binding = LayoutInflater.from(this).inflate(R.layout.content_call_info_floating, null, true);


//        nativeAd = new NativeAd(this, "716982881822998_1046642802190336");
//        nativeAd.setAdListener(new NativeAdListener() {
//            @Override
//            public void onMediaDownloaded(Ad ad) {
//
//            }
//
//            @Override
//            public void onError(Ad ad, AdError adError) {
//                Answers.getInstance().logCustom(new CustomEvent("FAN adError").putCustomAttribute(adError.getErrorMessage(), adError.toString()));
//            }
//
//            @Override
//            public void onAdLoaded(Ad ad) {
//                // Race condition, load() called again before last ad was displayed
//                if (nativeAd == null || nativeAd != ad) {
//                    return;
//                }
//                // Inflate Native Ad into Container
//                inflateAd(nativeAd);
//
//            }
//
//            @Override
//            public void onAdClicked(Ad ad) {
//                Answers.getInstance().logCustom(new CustomEvent("FAN Clicked"));
//            }
//
//            @Override
//            public void onLoggingImpression(Ad ad) {
//                Answers.getInstance().logCustom(new CustomEvent("FAN Impression"));
//                Answers.getInstance().logCustom(new CustomEvent("FAN Impression from caller id"));
//            }
//        });
//        // Request an ad
//        nativeAd.loadAd();

//        binding.ibClose.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v){
//                //Answers.getInstance().logCustom(new CustomEvent("Caller Info Closed")
//                //        .putCustomAttribute("Closed By", Constants.getSharedPreferences(CallInfoWindowService.this).getNumber()));
//                stopSelf();
//            }
//        });


        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        LayoutParams myParams = null;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            myParams = new WindowManager.LayoutParams(
                    LayoutParams.MATCH_PARENT,
                    LayoutParams.WRAP_CONTENT,
                    LayoutParams.TYPE_PHONE,
                    LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT);
        }else {
            myParams = new WindowManager.LayoutParams(
                    LayoutParams.MATCH_PARENT,
                    LayoutParams.WRAP_CONTENT,
                    LayoutParams.TYPE_APPLICATION_OVERLAY,
                    LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT);
        }

        myParams.gravity = Gravity.CENTER | Gravity.CENTER;
        myParams.x = 0;
        myParams.y = 100;
        // add a floatingfacebubble icon in window
        try{

            windowManager.addView(binding, myParams);
            tvName.setText("Loading");

            //for moving the picture on touch and slide
            final LayoutParams finalMyParams = myParams;
            binding.setOnTouchListener(new View.OnTouchListener(){
                WindowManager.LayoutParams paramsT = finalMyParams;
                private int initialX;
                private int initialY;
                private float initialTouchX;
                private float initialTouchY;
                private long touchStartTime = 0;

                @Override
                public boolean onTouch(View v, MotionEvent event){
                    //remove face bubble on long press
                    if(System.currentTimeMillis() - touchStartTime > ViewConfiguration.getLongPressTimeout() && initialTouchX == event.getX()){
                        try{
                            windowManager.removeView(binding);
                        }catch(Exception e){

                        }
                        stopSelf();
                        return false;
                    }
                    switch(event.getAction()){
                        case MotionEvent.ACTION_DOWN:
                            touchStartTime = System.currentTimeMillis();
                            initialX = finalMyParams.x;
                            initialY = finalMyParams.y;
                            initialTouchX = event.getRawX();
                            initialTouchY = event.getRawY();
                            break;
                        case MotionEvent.ACTION_UP:
                            break;
                        case MotionEvent.ACTION_MOVE:
                            finalMyParams.x = initialX + (int) (event.getRawX() - initialTouchX);
                            finalMyParams.y = initialY + (int) (event.getRawY() - initialTouchY);
                            try{
                                windowManager.updateViewLayout(v, finalMyParams);
                            }catch(IllegalArgumentException e){

                            }

                            break;
                    }
                    return false;
                }
            });
        }catch(Exception e){
            Log.e("err", e.toString());
            e.printStackTrace();
        }


        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public void onDestroy(){
        super.onDestroy();
        try{
            windowManager.removeViewImmediate(binding);
        }catch(Exception e){

        }
    }
}
