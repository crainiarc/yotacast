package yotacast.com.yotacast;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
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


public class ConfigActivity extends Activity {

    public void onResume() {
        super.onResume();  // Always call the superclass method first

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Log.d("yota", prefs.getBoolean("running", true)+"");



        //  First Time
        if (prefs.getString("endpoint", "null").equals("null")){
            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
            editor.putString("endpoint", "http://localhost:8080");
            editor.putInt("freq", 5000);
            editor.putBoolean("running", true);
            editor.commit();
        }


        //  Checked
        Switch s = (Switch) findViewById(R.id.yotaSwitch);
        if (prefs.getBoolean("running", false)){
            s.setChecked(true);
        } else
            s.setChecked(false);

        //  Set endpoint listener
        EditText endpoint = (EditText) findViewById(R.id.endpointUrl);
        endpoint.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
                editor.putString("endpoint", s.toString());
                editor.commit();
                restartService();
            }
        });
        endpoint.setText(prefs.getString("endpoint", "null"));

        //  Set frequency listener
        SeekBar freq = (SeekBar) findViewById(R.id.updateFreq);
        TextView tv = (TextView) findViewById(R.id.freqText);
        final String[] frequenciesNames = {"1 sec", "3 secs", "5 secs", "10 secs", "30 secs", "1 min", "3 mins", "5 mins", "15 mins", "30 mins", "1 hour"};
        final int[] freqencies = {1000, 3000, 5000, 10000, 30000, 60000, 180000, 300000, 900000, 1800000, 3600000};

        freq.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                TextView tv = (TextView) findViewById(R.id.freqText);
                tv.setText(frequenciesNames[progress]);

                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
                editor.putInt("freq", freqencies[progress]);
                editor.commit();
                restartService();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        int pos = 0;
        int currentFreq = prefs.getInt("freq", 1000);
        for (int i = 0; i < freqencies.length; i++){
            if (freqencies[i] == currentFreq)
                pos = i;
        }
        tv.setText(frequenciesNames[pos]);
        freq.setProgress(pos);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_config);
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

    public void restartService(){
        stopService(new Intent(this, YotaCastService.class));
        startService(new Intent(this, YotaCastService.class));
    }



}
