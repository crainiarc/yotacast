package yotacast.com.yotacast;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class YotaCastService extends Service {

    AsyncTask t, jsonTask;
    SharedPreferences prefs = null;
    boolean ended = false;

    public YotaCastService() {


    }


    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d("casting", "on");
        t = new CastTask().execute();
        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        showView();


        return START_STICKY;
    }


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }


    //  Ping
    private class CastTask extends AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... v) {
            if (ended)
                return null;


            int freq = prefs.getInt("freq", 1000);

            try {
                jsonTask = new AsyncTaskParseJson().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } catch (Exception e){
            }

            try {
                Thread.sleep(freq);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }

            t = new CastTask().execute();
            return null;
        }

    }



    // you can make this class as another java file so it will be separated from your main activity.
    public class AsyncTaskParseJson extends AsyncTask<Void, Void, Void> {


        @Override
        protected Void doInBackground(Void... arg0) {
            refresh();
            return null;
        }

    }

    public void refresh(){
        String img = null;
        boolean alert = false;
        String endpoint = prefs.getString("endpoint", "http://google.com");
        try {

            // instantiate our json parser
            JSONParser jParser = new JSONParser();

            // get json string from url
            JSONObject json = jParser.getJSONFromUrl(endpoint);

            if (json == null)
                return;

            // img and alert
            img = json.getString("raw_string");
            alert = json.getBoolean("play_alert");
            byte[] decodedString = Base64.decode(img, Base64.NO_WRAP);
            InputStream inputStream  = new ByteArrayInputStream(decodedString);
            Bitmap bitmap  = BitmapFactory.decodeStream(inputStream);
            updateView(bitmap);

            if (alert)
                showAlert();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public void updateView(Bitmap img){

        RemoteViews view = new RemoteViews(getPackageName(), R.layout.yotacast);
        view.setImageViewBitmap(R.id.imageView, img);

        // Push update for this widget to the home screen
        ComponentName thisWidget = new ComponentName(YotaCastService.this, YotaCastWidget.class);
        AppWidgetManager manager = AppWidgetManager.getInstance(YotaCastService.this);
        manager.updateAppWidget(thisWidget, view);
    }


    public void hideView(){

        RemoteViews view = new RemoteViews(getPackageName(), R.layout.yotacast);
        view.setViewVisibility(R.id.imageView, View.INVISIBLE);
        view.setViewVisibility(R.id.imagePlaceholder, View.VISIBLE);

        // Push update for this widget to the home screen
        ComponentName thisWidget = new ComponentName(YotaCastService.this, YotaCastWidget.class);
        AppWidgetManager manager = AppWidgetManager.getInstance(YotaCastService.this);
        manager.updateAppWidget(thisWidget, view);
    }


    public void showView(){

        RemoteViews view = new RemoteViews(getPackageName(), R.layout.yotacast);
        view.setViewVisibility(R.id.imageView, View.VISIBLE);
        view.setViewVisibility(R.id.imagePlaceholder, View.INVISIBLE);

        // Push update for this widget to the home screen
        ComponentName thisWidget = new ComponentName(YotaCastService.this, YotaCastWidget.class);
        AppWidgetManager manager = AppWidgetManager.getInstance(YotaCastService.this);
        manager.updateAppWidget(thisWidget, view);
    }



    public void showAlert(){
        if (ended)
            return;

        Log.d("yota", "ALERT");
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
        r.play();
    }


    public void onDestroy(){
        ended = true;
        Log.d("casting", "off");
        if (jsonTask != null)
            jsonTask.cancel(true);
        if (t != null)
            t.cancel(true);
        hideView();
        super.onDestroy();
    }



    private Bitmap getImageBitmap(String url) {
        Bitmap bm = null;
        try {
            URL aURL = new URL(url);
            URLConnection conn = aURL.openConnection();
            conn.connect();
            InputStream is = conn.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);
            bm = BitmapFactory.decodeStream(bis);
            bis.close();
            is.close();
        } catch (IOException e) {
            Log.e("yo", "Error getting bitmap", e);
        }
        return bm;
    }


}
