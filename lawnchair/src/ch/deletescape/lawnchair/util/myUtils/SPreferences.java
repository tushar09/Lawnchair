package ch.deletescape.lawnchair.util.myUtils;

import android.content.Context;
import android.content.SharedPreferences;

public class SPreferences {
    protected final static String APP_OPENED = "appOpened";

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
}
