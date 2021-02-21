/*
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

package ch.deletescape.lawnchair

//import com.squareup.leakcanary.LeakCanary

import android.app.Activity
import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.provider.Settings
import androidx.annotation.Keep
import ch.deletescape.lawnchair.blur.BlurWallpaperProvider
import ch.deletescape.lawnchair.bugreport.BugReportClient
import ch.deletescape.lawnchair.bugreport.BugReportService
import ch.deletescape.lawnchair.flowerpot.Flowerpot
import ch.deletescape.lawnchair.iconpack.IconPackManager
import ch.deletescape.lawnchair.sesame.Sesame
import ch.deletescape.lawnchair.smartspace.LawnchairSmartspaceController
import ch.deletescape.lawnchair.theme.ThemeManager
import ch.deletescape.lawnchair.util.extensions.d
import ch.deletescape.lawnchair.util.myUtils.Constants
import ch.deletescape.lawnchair.util.myUtils.Constants.APP_OPEN_COUNT
import ch.deletescape.lawnchair.util.myUtils.Constants.getSPreferences
import com.android.launcher3.BuildConfig
import com.android.launcher3.R
import com.android.launcher3.Utilities
import com.android.quickstep.RecentsActivity
import com.facebook.ads.*
import com.google.firebase.FirebaseApp
import com.google.firebase.database.*
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import ninja.sesame.lib.bridge.v1.SesameFrontend
import ninja.sesame.lib.bridge.v1.SesameInitOnComplete
import ninja.sesame.lib.bridge.v1_1.LookFeelKeys
import java.util.*
import kotlin.collections.HashSet


class LawnchairApp : Application() {

    val activityHandler = ActivityHandler()
    val smartspace by lazy { LawnchairSmartspaceController(this) }
    val bugReporter = LawnchairBugReporter(this, Thread.getDefaultUncaughtExceptionHandler())
    val recentsEnabled by lazy { checkRecentsComponent() }
    var accessibilityService: LawnchairAccessibilityService? = null

    private val mFirebaseRemoteConfig: FirebaseRemoteConfig? = null
    private var database: FirebaseDatabase? = null
    private var myRe: DatabaseReference? = null

    private var interstitialAd: InterstitialAd? = null

    init {
        d("Hidden APIs allowed: ${Utilities.HIDDEN_APIS_ALLOWED}")
    }

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        AudienceNetworkAds.initialize(this);
//        if (BuildConfig.HAS_LEAKCANARY && lawnchairPrefs.initLeakCanary) {
//            if (LeakCanary.isInAnalyzerProcess(this)) {
//                // This process is dedicated to LeakCanary for heap analysis.
//                // You should not init your app in this process.
//                return
//            }
//            LeakCanary.install(this)
//        }

        //        Intent intent = new Intent(this, GCMRegistrationIntentService.class);
        //        startService(intent);
        database = FirebaseDatabase.getInstance()
        myRe = database!!.getReference()

        interstitialAd = InterstitialAd(this, "1238753982967772_1238768376299666")

        myRe!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val map = dataSnapshot.value as Map<String, Any>?
                APP_OPEN_COUNT = (map!!["app_open_count"].toString() + "").toInt()
                Constants.getSPreferences(this@LawnchairApp).setAppOpenedCount()
                if (getSPreferences(this@LawnchairApp).getAppOpenedCount() >= APP_OPEN_COUNT) {
                    try {
                        interstitialAd!!.loadAd(interstitialAd!!.buildLoadAdConfig().withAdListener(object : InterstitialAdListener{
                            override fun onError(p0: Ad?, p1: AdError?) {

                            }

                            override fun onAdLoaded(p0: Ad?) {
                                interstitialAd!!.show();
                            }

                            override fun onAdClicked(p0: Ad?) {

                            }

                            override fun onLoggingImpression(p0: Ad?) {

                            }

                            override fun onInterstitialDisplayed(p0: Ad?) {

                            }

                            override fun onInterstitialDismissed(p0: Ad?) {

                            }
                        }).build())
                    } catch (e: Exception) {
                    }
                    getSPreferences(this@LawnchairApp).setAppOpenedCountToZero()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    fun onLauncherAppStateCreated() {
        Thread.setDefaultUncaughtExceptionHandler(bugReporter)
        registerActivityLifecycleCallbacks(activityHandler)

        ThemeManager.getInstance(this).registerColorListener()
        BlurWallpaperProvider.getInstance(this)
        Flowerpot.Manager.getInstance(this)
        if (BuildConfig.FEATURE_BUG_REPORTER && lawnchairPrefs.showCrashNotifications) {
            BugReportClient.getInstance(this)
            BugReportService.registerNotificationChannel(this)
        }

        if (BuildConfig.FEATURE_QUINOA) {
            SesameFrontend.init(this, object : SesameInitOnComplete {
                override fun onConnect() {
                    val thiz = this@LawnchairApp
                    SesameFrontend.setIntegrationDialog(thiz, R.layout.dialog_sesame_integration,
                                                        android.R.id.button2, android.R.id.button1)
                    val ipm = IconPackManager.getInstance(thiz)
                    ipm.addListener {
                        if (thiz.lawnchairPrefs.syncLookNFeelWithSesame) {
                            runOnUiWorkerThread {
                                val pkg = ipm.packList.currentPack().packPackageName
                                Sesame.LookAndFeel[LookFeelKeys.ICON_PACK_PKG] =
                                        if (pkg == "") null else pkg
                            }
                        }
                    }
                    Sesame.setupSync(thiz)
                }

                override fun onDisconnect() {
                    // do nothing
                }

            })
        }
    }

    fun restart(recreateLauncher: Boolean = true) {
        if (recreateLauncher) {
            activityHandler.finishAll(recreateLauncher)
        } else {
            Utilities.restartLauncher(this)
        }
    }

    fun performGlobalAction(action: Int): Boolean {
        return if (accessibilityService != null) {
            accessibilityService!!.performGlobalAction(action)
        } else {
            startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                                  .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
            false
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        ThemeManager.getInstance(this).updateNightMode(newConfig)
    }

    class ActivityHandler : ActivityLifecycleCallbacks {

        val activities = HashSet<Activity>()
        var foregroundActivity: Activity? = null

        fun finishAll(recreateLauncher: Boolean = true) {
            HashSet(activities).forEach { if (recreateLauncher && it is LawnchairLauncher) it.recreate() else it.finish() }
        }

        override fun onActivityPaused(activity: Activity) {

        }

        override fun onActivityResumed(activity: Activity) {
            foregroundActivity = activity
        }

        override fun onActivityStarted(activity: Activity) {

        }

        override fun onActivityDestroyed(activity: Activity) {
            if (activity == foregroundActivity)
                foregroundActivity = null
            activities.remove(activity)
        }

        override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle?) {

        }

        override fun onActivityStopped(activity: Activity) {

        }

        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
            activities.add(activity)
        }
    }

    @Keep
    fun checkRecentsComponent(): Boolean {
        if (!Utilities.ATLEAST_P) {
            d("API < P, disabling recents")
            return false
        }
        if (!Utilities.HIDDEN_APIS_ALLOWED) {
            d("Hidden APIs not allowed, disabling recents")
            return false
        }

        val resId = resources.getIdentifier("config_recentsComponentName", "string", "android")
        if (resId == 0) {
            d("config_recentsComponentName not found, disabling recents")
            return false
        }
        val recentsComponent = ComponentName.unflattenFromString(resources.getString(resId))
        if (recentsComponent == null) {
            d("config_recentsComponentName is empty, disabling recents")
            return false
        }
        val isRecentsComponent = recentsComponent.packageName == packageName
                && recentsComponent.className == RecentsActivity::class.java.name
        if (!isRecentsComponent) {
            d("config_recentsComponentName ($recentsComponent) is not Lawnchair, disabling recents")
            return false
        }
        return true
    }
}

val Context.lawnchairApp get() = applicationContext as LawnchairApp
