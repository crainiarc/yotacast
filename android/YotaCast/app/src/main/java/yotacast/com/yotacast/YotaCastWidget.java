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

    public static final String ACTION_ALERT_CLICK =
            "yotacast.com.yotacast.action.ALERT_CLICK";

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
        CharSequence widgetText = context.getString(R.string.appwidget_text);
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.yotacast);

        //  Start/Stop
        PendingIntent pendingIntent;
        Intent intent = new Intent(context, ConfigActivity.class);
        intent.setAction(ACTION_UPDATE_CLICK);
        pendingIntent =  PendingIntent.getActivity(context, 0, intent, 0);
        views.setOnClickPendingIntent(R.id.buttonPlay, pendingIntent);


        //  Hide alert
        PendingIntent pendingIntentAlert;
        Intent intentAlert = new Intent(context, ConfigActivity.class);
        intentAlert.setAction(ACTION_ALERT_CLICK);
        pendingIntentAlert =  PendingIntent.getActivity(context, 0, intentAlert, 0);
        views.setOnClickPendingIntent(R.id.alert, pendingIntentAlert);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }


}


