package yotacast.com.yotacast;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RemoteViews;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.UnsupportedEncodingException;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Log.d("yota", prefs.getBoolean("running", true)+"");

        //  Check action
        String action = getIntent().getAction();
        if (action.equals(YotaCastWidget.ACTION_UPDATE_CLICK)){
            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
            editor.putBoolean("running", !prefs.getBoolean("running", false));
            editor.commit();
            updateService();
            finish();
            return;
        } else if (action.equals(YotaCastWidget.ACTION_ALARM_CLICK)){
            toggleAlarm();
            finish();
            return;
        } else if (action.equals(YotaCastWidget.ACTION_PREV_CLICK)){
            Intent i = new Intent("yotacast.com.yotacast.cache").putExtra("type", "prev");
            this.sendBroadcast(i);
        } else if (action.equals(YotaCastWidget.ACTION_FWD_CLICK)){
            Intent i = new Intent("yotacast.com.yotacast.cache").putExtra("type", "fwd");
            this.sendBroadcast(i);
        } else if (action.equals(YotaCastWidget.ACTION_BACK_CLICK)){
            Intent i = new Intent("yotacast.com.yotacast.cache").putExtra("type", "back");
            this.sendBroadcast(i);
        }


        //  Check action, if action, then exit
        finish();
        return;
    }


    public void setService(View v){
        Switch s = (Switch) v;

        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
        editor.putBoolean("running", s.isChecked());
        editor.commit();

        updateService();
    }

    public void updateService(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if (prefs.getBoolean("running", false)){
            startService(new Intent(this, YotaCastService.class));
        } else {
            stopService(new Intent(this, YotaCastService.class));
        }
    }



    public void toggleAlarm(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        boolean alarmed = prefs.getBoolean("alarmed", false);
        if (alarmed)
            stopAlarm();
        else
            startAlarm();
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("alarmed", !alarmed);
        editor.commit();
    };

    public void stopAlarm(){
        RemoteViews view = new RemoteViews(getPackageName(), R.layout.yotacast);
        view.setTextViewText(R.id.alarm, "Play Alarm");

        // Push update for this widget to the home screen
        ComponentName thisWidget = new ComponentName(this, YotaCastWidget.class);
        AppWidgetManager manager = AppWidgetManager.getInstance(this);
        manager.updateAppWidget(thisWidget, view);

        new StopAlertTask().execute();
    }


    public void startAlarm(){
        RemoteViews view = new RemoteViews(getPackageName(), R.layout.yotacast);
        view.setTextViewText(R.id.alarm, "Stop Alarm");

        // Push update for this widget to the home screen
        ComponentName thisWidget = new ComponentName(this, YotaCastWidget.class);
        AppWidgetManager manager = AppWidgetManager.getInstance(this);
        manager.updateAppWidget(thisWidget, view);

        new AlertTask().execute();
    }


    //  Ping
    private class AlertTask extends AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... v) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            String endpoint = prefs.getString("endpoint", "http://google.com")+"/play_alert";

            try {
                // defaultHttpClient
                DefaultHttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(endpoint);
                HttpResponse httpResponse = httpClient.execute(httpPost);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

    }


    private class StopAlertTask extends AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... v) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            String endpoint = prefs.getString("endpoint", "http://google.com")+"/stop_alert";

            try {
                // defaultHttpClient
                DefaultHttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(endpoint);
                HttpResponse httpResponse = httpClient.execute(httpPost);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

    }

}
