package com.example.fabian.swim_lab_zad4;

import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements SensorEventListener{
    private static final double VERY_GOOD_LEVEL_ACCURACY = 0.5;
    private static final double GOOD_LEVEL_ACCURACY = 2;
    private static final double MIDDLE_LEVEL_ACCURACY = 5;
    private static final double BAD_LEVEL_ACCURACY = 10;
    private static final int PROXIMITY_ACCURACY = 2;
    SensorManager sensorManager;
    Sensor proximity;
    Sensor gyroscope;
    MediaPlayer mpSound2s;
    MediaPlayer mpSound1s;
    MediaPlayer mpSound0_5s;
    MediaPlayer mpSound0_1s;
    TextView tvAxisX;
    TextView tvLevel;
    TextView tvLamp;
    TextView tvEnable;
    boolean isEnable = true;
    double angle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initViews();
        initSensors();
        initPlayers();
    }

    private void initPlayers(){
        mpSound2s = MediaPlayer.create(this, R.raw.singlesound2s);
        mpSound1s = MediaPlayer.create(this, R.raw.singlesound1s);
        mpSound0_5s = MediaPlayer.create(this, R.raw.singlesound0_5s);
        mpSound0_1s = MediaPlayer.create(this, R.raw.singlesound0_1s);
    }

    private  void initSensors(){
        sensorManager =  (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        assert sensorManager != null;
        proximity = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    private void initViews(){
        tvAxisX = findViewById(R.id.axis_x);
        tvLevel = findViewById(R.id.level);
        tvEnable = findViewById(R.id.enable);
        tvLamp = findViewById(R.id.lamp);
    }

    @Override
    protected void onResume(){
        super.onResume();
        sensorManager.registerListener(this, proximity, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause(){
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType() == Sensor.TYPE_PROXIMITY){
            if(isEnable && event.values[0] < PROXIMITY_ACCURACY) {
                isEnable = false;
                tvEnable.setText(getString(R.string.disable));
            }
            else if(event.values[0] < PROXIMITY_ACCURACY) {
                isEnable = true;
                tvEnable.setText(getString(R.string.enable));
            }
        }

        if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER && isEnable){
            angle = Math.atan2(event.values[0],event.values[1])/(Math.PI/180) - 90;
            String angletoshow = String.format("%.2f", angle);
            tvAxisX.setText(getString(R.string.axis_x) + angletoshow);

            if(angle < VERY_GOOD_LEVEL_ACCURACY && angle > -VERY_GOOD_LEVEL_ACCURACY) {
                tvLamp.setBackgroundColor(Color.GREEN);
                if (!mpSound0_1s.isPlaying())
                    mpSound0_1s.start();
                if (mpSound0_5s.isPlaying())
                    mpSound0_5s.pause();
            }
            else if(angle < GOOD_LEVEL_ACCURACY && angle > -GOOD_LEVEL_ACCURACY) {
                tvLamp.setBackgroundColor(Color.YELLOW);
                if (!mpSound0_5s.isPlaying())
                    mpSound0_5s.start();
                if(mpSound1s.isPlaying())
                    mpSound1s.pause();
                if(mpSound0_1s.isPlaying())
                    mpSound0_1s.pause();
            }
            else if(angle < MIDDLE_LEVEL_ACCURACY && angle > -MIDDLE_LEVEL_ACCURACY){
                tvLamp.setBackgroundColor(Color.parseColor("#FFA500"));
                if(!mpSound1s.isPlaying())
                    mpSound1s.start();
                if(mpSound2s.isPlaying())
                    mpSound2s.pause();
                if(mpSound0_5s.isPlaying())
                    mpSound0_5s.pause();
            }
            else if(angle < BAD_LEVEL_ACCURACY && angle > -BAD_LEVEL_ACCURACY){
                tvLamp.setBackgroundColor(Color.MAGENTA);
                if(!mpSound2s.isPlaying())
                    mpSound2s.start();
                if(mpSound1s.isPlaying())
                    mpSound1s.pause();
            }
            else{
                tvLamp.setBackgroundColor(Color.RED);
                if(mpSound2s.isPlaying())
                    mpSound2s.pause();
                if(mpSound1s.isPlaying())
                    mpSound1s.pause();
                if (mpSound0_5s.isPlaying())
                    mpSound0_5s.pause();
                if(mpSound0_1s.isPlaying())
                    mpSound0_1s.pause();
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy){}
}