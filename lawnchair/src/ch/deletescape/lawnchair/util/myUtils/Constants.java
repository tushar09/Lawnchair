package ch.deletescape.lawnchair.util.myUtils;

import android.content.Context;
import android.os.PowerManager;
import android.util.Log;

public class Constants {


    public static final String NOTIFICATION_TYPE_NEWS = "0";
    public static final String NOTIFICATION_TYPE_SPECIFIC_NEWS = "1";

    public static int APP_OPEN_COUNT = 50;


    private static SPreferences sPreferences;

    public static final SPreferences getSPreferences(Context context) {
        if (sPreferences == null) {
            sPreferences = new SPreferences(context);
        }
        return sPreferences;
    }

    public static boolean isScreenOff(Context context){
        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        return powerManager.isScreenOn();
    }

    public static boolean canIShowNativeAdForAppExit(Context context) {
        if(getSPreferences(context).isPaid()){
            return false;
        }
        if(!getSPreferences(context).getShowDialogAd()){
            Log.e("getShowDialogAd", getSPreferences(context).getShowDialogAd() + "");
            getSPreferences(context).resetAppStartTime();
            return false;
        }
        if(getSPreferences(context).getShowDialogAdTimer() == -1){
            Log.e("getShowDialogAdTimer", getSPreferences(context).getShowDialogAdTimer() + "");
            getSPreferences(context).resetAppStartTime();
            return false;
        }
        if(getSPreferences(context).getAppStartTime() == -1){
            return false;
        }
        if(System.currentTimeMillis() - getSPreferences(context).getAppStartTime() >= getSPreferences(context).getShowDialogAdTimer()){
            getSPreferences(context).resetAppStartTime();
            return true;
        }

        return false;
    }
}
