package yotacast.com.yotacast;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;


/**
 * Implementation of App Widget functionality.
 */
public class YotaCastWidget extends AppWidgetProvider {

    public static final String ACTION_UPDATE_CLICK =
            "yotacast.com.yotacast.action.UPDATE_CLICK";

    public static final String ACTION_ALARM_CLICK =
            "yotacast.com.yotacast.action.ACTION_ALARM_CLICK";

    public static final String ACTION_PREV_CLICK =
            "yotacast.com.yotacast.action.PREV_CLICK";

    public static final String ACTION_FWD_CLICK =
            "yotacast.com.yotacast.action.FWD_CLICK";

    public static final String ACTION_BACK_CLICK =
            "yotacast.com.yotacast.action.BACK_CLICK";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        final int N = appWidgetIds.length;
        for (int i=0; i<N; i++) {
            updateAppWidget(context, appWidgetManager, appWidgetIds[i]);
        }
    }


    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                         int appWidgetId) {

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.yotacast);

        //  Start/Stop
        PendingIntent pendingIntent;
        Intent intent = new Intent(context, MainActivity.class);
        intent.setAction(ACTION_UPDATE_CLICK);
        pendingIntent =  PendingIntent.getActivity(context, 0, intent, 0);
        views.setOnClickPendingIntent(R.id.buttonPlay, pendingIntent);

        //  PREV
        PendingIntent pendingIntentPrev;
        Intent intentPrev = new Intent(context, MainActivity.class);
        intentPrev.setAction(ACTION_PREV_CLICK);
        pendingIntentPrev =  PendingIntent.getActivity(context, 0, intentPrev, 0);
        views.setOnClickPendingIntent(R.id.buttonPrev, pendingIntentPrev);

        //  FWD
        PendingIntent pendingIntentFwd;
        Intent intentFwd = new Intent(context, MainActivity.class);
        intentFwd.setAction(ACTION_FWD_CLICK);
        pendingIntentFwd =  PendingIntent.getActivity(context, 0, intentFwd, 0);
        views.setOnClickPendingIntent(R.id.buttonFwd, pendingIntentFwd);

        //  BACK
        PendingIntent pendingIntentBack;
        Intent intentBack = new Intent(context, MainActivity.class);
        intentBack.setAction(ACTION_BACK_CLICK);
        pendingIntentBack =  PendingIntent.getActivity(context, 0, intentBack, 0);
        views.setOnClickPendingIntent(R.id.buttonBack, pendingIntentBack);


        //   Toggle alert
        PendingIntent pendingIntentAlarm;
        Intent intentAlarm = new Intent(context, MainActivity.class);
        intentAlarm.setAction(ACTION_ALARM_CLICK);
        pendingIntentAlarm =  PendingIntent.getActivity(context, 0, intentAlarm, 0);
        views.setOnClickPendingIntent(R.id.alarm, pendingIntentAlarm);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }


}


