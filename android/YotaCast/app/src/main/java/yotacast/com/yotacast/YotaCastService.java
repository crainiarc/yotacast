package yotacast.com.yotacast;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
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
import java.util.ArrayList;

public class YotaCastService extends Service {

    AsyncTask t, jsonTask;
    SharedPreferences prefs = null;
    boolean ended = false;
    int position = -1;
    ArrayList<Bitmap> imageCache = new ArrayList<Bitmap>();
    private BroadcastReceiver mReceiver;
    IntentFilter intentFilter;

    public YotaCastService() {


    }



    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d("casting", "on");
        t = new CastTask().execute();
        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        showView();

        //  prev fwd back
        intentFilter = new IntentFilter(
                "yotacast.com.yotacast.cache");

        mReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                String msg = intent.getStringExtra("type");


                if (position == -1) {
                    showCache();
                    position = imageCache.size() - 1;
                }

                if (msg.equals("prev"))
                    YotaCastService.this.setCache(position-1);
                else if (msg.equals("fwd"))
                    YotaCastService.this.setCache(position+1);
                else if (msg.equals("back")) {
                    position = -1;
                    YotaCastService.this.showView();
                }

            }
        };
        //registering our receiver
        this.registerReceiver(mReceiver, intentFilter);

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
        String endpoint = prefs.getString("endpoint", "http://google.com")+"/latest_image";
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



            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("alerted", alert);
            editor.commit();


            boolean alerted = prefs.getBoolean("alerted", false);
            if (alerted)
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

        if (position != -1)
            imageCache.add(img);
        if (imageCache.size() > 5)
            imageCache.remove(0);
    }


    public void hideView(){
        Log.d("yota", "show place");

        RemoteViews view = new RemoteViews(getPackageName(), R.layout.yotacast);
        view.setViewVisibility(R.id.imageView, View.INVISIBLE);
        view.setViewVisibility(R.id.imageCache, View.INVISIBLE);
        view.setViewVisibility(R.id.imagePlaceholder, View.VISIBLE);
        view.setTextColor(R.id.buttonPlay, Color.parseColor("#80000000"));
        view.setViewVisibility(R.id.buttonBack, View.INVISIBLE);
        view.setViewVisibility(R.id.buttonFwd, View.INVISIBLE);

        // Push update for this widget to the home screen
        ComponentName thisWidget = new ComponentName(YotaCastService.this, YotaCastWidget.class);
        AppWidgetManager manager = AppWidgetManager.getInstance(YotaCastService.this);
        manager.updateAppWidget(thisWidget, view);
    }


    public void showView(){
    Log.d("yota", "show view");
        RemoteViews view = new RemoteViews(getPackageName(), R.layout.yotacast);
        view.setViewVisibility(R.id.imageView, View.VISIBLE);
        view.setViewVisibility(R.id.imagePlaceholder, View.INVISIBLE);
        view.setViewVisibility(R.id.imageCache, View.INVISIBLE);
        view.setTextColor(R.id.buttonPlay, Color.TRANSPARENT);
        view.setViewVisibility(R.id.buttonBack, View.INVISIBLE);
        view.setViewVisibility(R.id.buttonFwd, View.INVISIBLE);

        // Push update for this widget to the home screen
        ComponentName thisWidget = new ComponentName(YotaCastService.this, YotaCastWidget.class);
        AppWidgetManager manager = AppWidgetManager.getInstance(YotaCastService.this);
        manager.updateAppWidget(thisWidget, view);
    }



    void showCache(){
        Log.d("yota", "show cache");

        RemoteViews view = new RemoteViews(getPackageName(), R.layout.yotacast);
        view.setViewVisibility(R.id.imageCache, View.VISIBLE);
        view.setViewVisibility(R.id.imageView, View.INVISIBLE);
        view.setViewVisibility(R.id.imagePlaceholder, View.INVISIBLE);
        view.setViewVisibility(R.id.buttonBack, View.VISIBLE);
        view.setViewVisibility(R.id.buttonFwd, View.VISIBLE);

        // Push update for this widget to the home screen
        ComponentName thisWidget = new ComponentName(YotaCastService.this, YotaCastWidget.class);
        AppWidgetManager manager = AppWidgetManager.getInstance(YotaCastService.this);
        manager.updateAppWidget(thisWidget, view);
    }



    void setCache(int pos){
        if (pos == -1)
            pos = 0;
        if (pos >= imageCache.size())
            pos = imageCache.size() - 1;

        position = pos;

        Log.d("yota", "position "+position + " " +imageCache.size());

        if (position < 0)
            return;

        RemoteViews view = new RemoteViews(getPackageName(), R.layout.yotacast);
        view.setImageViewBitmap(R.id.imageCache, imageCache.get(position));

        // Push update for this widget to the home screen
        ComponentName thisWidget = new ComponentName(YotaCastService.this, YotaCastWidget.class);
        AppWidgetManager manager = AppWidgetManager.getInstance(YotaCastService.this);
        manager.updateAppWidget(thisWidget, view);
    }

    boolean isWhite = false;
    public void showAlert(){


        RemoteViews view = new RemoteViews(getPackageName(), R.layout.yotacast);
        view.setViewVisibility(R.id.alert, View.VISIBLE);
        if (isWhite)
            view.setTextColor(R.id.alert, Color.BLACK);
        else
            view.setTextColor(R.id.alert, Color.WHITE);
        isWhite = !isWhite;

        // Push update for this widget to the home screen
        ComponentName thisWidget = new ComponentName(YotaCastService.this, YotaCastWidget.class);
        AppWidgetManager manager = AppWidgetManager.getInstance(YotaCastService.this);
        manager.updateAppWidget(thisWidget, view);


        Log.d("yota", "ALERT");
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
        r.play();

    }

    public void hideAlert(){
        RemoteViews view = new RemoteViews(getPackageName(), R.layout.yotacast);
        view.setViewVisibility(R.id.alert, View.INVISIBLE);

        // Push update for this widget to the home screen
        ComponentName thisWidget = new ComponentName(YotaCastService.this, YotaCastWidget.class);
        AppWidgetManager manager = AppWidgetManager.getInstance(YotaCastService.this);
        manager.updateAppWidget(thisWidget, view);
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

        this.unregisterReceiver(this.mReceiver);
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
