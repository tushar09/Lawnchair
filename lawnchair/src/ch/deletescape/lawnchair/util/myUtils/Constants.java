package ch.deletescape.lawnchair.util.myUtils;

import android.content.Context;
import android.os.PowerManager;

public class Constants {

    public static final String NOTIFICATION_TYPE_ADS = "-1";
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

}
