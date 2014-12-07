package yotacast.com.yotacast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;


public class autostart extends BroadcastReceiver
{
    public void onReceive(Context arg0, Intent arg1)
    {
        Log.d("yota", "asqd");
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(arg0);
        boolean running = settings.getBoolean("running", false);
        if (running){
            arg0.startService(new Intent(arg0, YotaCastService.class));
        }
    }


}