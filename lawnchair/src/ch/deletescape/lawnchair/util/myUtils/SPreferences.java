package ch.deletescape.lawnchair.util.myUtils;

import android.content.Context;
import android.content.SharedPreferences;

public class SPreferences {
    protected final static String APP_OPENED = "appOpened";
    protected final static String APP_START_TIME = "appStartTime";
    public static final String SHOW_DIALOG_AD_TIMER = "showDialogAdTimer";
    public static final String SHOW_DIALOG_AD = "showDialogAd";
    public static final String IS_PAID = "isPaid";

    protected SharedPreferences sp;

    public SPreferences(Context context){
        sp = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
    }

    public void setAppOpenedCount(){
        int count = sp.getInt(APP_OPENED, 0);
        sp.edit().putInt(APP_OPENED, count + 1).commit();
    }

    public void setAppOpenedCountToZero(){
        sp.edit().putInt(APP_OPENED, 0).commit();
    }

    public int getAppOpenedCount(){
        return sp.getInt(APP_OPENED, 0);
    }

    public void setAppStartTime(){
        sp.edit().putLong(APP_START_TIME, System.currentTimeMillis()).commit();
    }

    public long getAppStartTime(){
        return sp.getLong(APP_START_TIME, -1);
    }

    public void resetAppStartTime(){
        sp.edit().putLong(APP_START_TIME, -1).commit();
    }


    public void setShowDialogAdTimer(long timer){
        sp.edit().putLong(SHOW_DIALOG_AD_TIMER, timer).commit();
    }

    public long getShowDialogAdTimer(){
        return sp.getLong(SHOW_DIALOG_AD_TIMER, -1);
    }

    public void setShowDialogAd(boolean show){
        sp.edit().putBoolean(SHOW_DIALOG_AD, show).commit();
    }

    public boolean getShowDialogAd(){
        return sp.getBoolean(SHOW_DIALOG_AD, false);
    }

    public void setIsPaid(boolean show){
        sp.edit().putBoolean(IS_PAID, show).commit();
    }

    public boolean isPaid(){
        return sp.getBoolean(IS_PAID, false);
    }

}
